using System.Net;
using System.Net.Mail;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Formatters;

namespace PersonalWebsite.Controllers;

/// <summary>
/// Main API Controller
/// </summary>
[ApiController]
[Route("api")]
public class Api : ControllerBase
{
	/// <summary>
	/// How often an email can be sent when requested by the same IP address
	/// </summary>
	private const int BlockTime = 60000;
	
	/// <summary>
	/// Set containing Ip addresses that have sent messages recently
	/// </summary>
	private static readonly HashSet<IPAddress> IpSet = new HashSet<IPAddress>();
	
	/// <summary>
	/// Default logger
	/// </summary>
	private readonly ILogger<Api> _logger;

	/// <summary>
	/// Credentials for SMTP server
	/// </summary>
	private static string? _smtpUsername, _smtpPassword;

	/// <summary>
	/// Set the SMTP Username
	/// </summary>
	public static string SmtpUsername
	{
		set => _smtpUsername = value;
	}

	/// <summary>
	/// Set the SMTP Password
	/// </summary>
	public static string SmtpPassword
	{
		set => _smtpPassword = value;
	}

	/// <summary>
	/// Create API controller with a logger
	/// </summary>
	/// <param name="logger"></param>
	public Api(ILogger<Api> logger)
	{
		_logger = logger;
	}
	
	/// <summary>
	/// How long a user must wait to send another email in milliseconds
	/// </summary>
	/// <returns>How long a user must wait to send another email in milliseconds</returns>
	[HttpGet("block-time")]
	public int GetBlockTime()
	{
		return BlockTime;
	}

	/// <summary>
	/// Sends email from contact form
	/// </summary>
	/// <param name="messageBody">Email message body</param>
	/// <response code="202">Successfully Sent</response>
	/// <response code="400">Bad request</response>
	/// <response code="429">Too many requests sent</response>
	/// <response code="500">Server error sending message</response>
	[HttpPost("form-submission")]
	[Consumes("text/plain")]
	public IActionResult SendEmail([FromBody] string messageBody)
	{
		IPAddress? ip = HttpContext.Connection.RemoteIpAddress;
		if (ip == null)
		{
			return new BadRequestResult();
		}

		if (IpSet.Contains(ip))
		{
			_logger.LogInformation("IP {ip} has tried to send too mange messages in a short amount of time", ip);
			return new StatusCodeResult(429);
		}

		IpSet.Add(ip);
		Thread thread = new Thread(() =>
		{
			Thread.Sleep(BlockTime);
			IpSet.Remove(ip);
		});
		thread.Start();

		const string from = "noreply@panosmoisiadis.com", fromname = "Contact Form [panosmoisiadis.com]";
		const string to = "panosmoisiadis@pm.me";
		const string subject = "New Contact Form Submission";
		
		
		const string host = "email-smtp.us-east-2.amazonaws.com";
		const int port = 587;
		
		MailMessage message = new MailMessage();
		message.IsBodyHtml = false;
		message.From = new MailAddress(from, fromname);
		message.To.Add(new MailAddress(to));
		message.Subject = subject;
		message.Body = messageBody;

		using SmtpClient client = new SmtpClient(host, port);
		client.Credentials = new NetworkCredential(_smtpUsername, _smtpPassword);
		client.EnableSsl = true;
		
		try
		{
			_logger.LogInformation("Sending...");
			client.Send(message);
			_logger.LogInformation("Message Sent");
		}
		catch (Exception e)
		{
			_logger.LogTrace(e, "Error sending message");
			return new StatusCodeResult(500);
		}

		return new AcceptedResult();
	}
	
	public class TextPlainInputFormatter : InputFormatter
	{
		private const string ContentType = "text/plain";

		public TextPlainInputFormatter()
		{
			SupportedMediaTypes.Add(ContentType);
		}

		public override async Task<InputFormatterResult> ReadRequestBodyAsync(InputFormatterContext context)
		{
			var request = context.HttpContext.Request;
			using (var reader = new StreamReader(request.Body))
			{
				var content = await reader.ReadToEndAsync();
				return await InputFormatterResult.SuccessAsync(content);
			}
		}

		public override bool CanRead(InputFormatterContext context)
		{
			var contentType = context.HttpContext.Request.ContentType;
			return contentType.StartsWith(ContentType);
		}
	}
}