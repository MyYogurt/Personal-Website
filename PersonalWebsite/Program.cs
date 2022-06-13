using System.Xml;
using Microsoft.AspNetCore.HttpOverrides;
using Microsoft.OpenApi.Models;
using PersonalWebsite.Controllers;

XmlDocument doc = new XmlDocument();
doc.Load("configuration.xml");
var element = doc.DocumentElement;

if (element is null)
{
	Console.Error.WriteLine("Error reading XML");
	Environment.Exit(1);
}

var node = element.SelectSingleNode("smtp-username");

if (node is null)
{
	Console.Error.WriteLine("Error reading XML");
	Environment.Exit(1);
}

string smtpUsername = node.InnerText;
node = element.SelectSingleNode("smtp-password");

if (node is null)
{
	Console.Error.WriteLine("Error reading XML");
	Environment.Exit(1);
}

string smtpPassword = node.InnerText;

Api.SmtpUsername = smtpUsername;
Api.SmtpPassword = smtpPassword;


var builder = WebApplication.CreateBuilder(args);
builder.Services.AddControllers(o =>
	o.InputFormatters.Insert(o.InputFormatters.Count, new Api.TextPlainInputFormatter()));

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
	{
		options.SwaggerDoc("v1", new OpenApiInfo
		{
			Version = "v1",
			Title = "Panos Moisiadis Web API",
			Description = "API providing functionality to panosmoisiadis.com",
		});

		// using System.Reflection;
		var xmlFilename = "docs.xml";
		options.IncludeXmlComments(Path.Combine(AppContext.BaseDirectory, xmlFilename));
	});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
	app.UseSwagger();
	app.UseSwaggerUI();
	app.UseHttpsRedirection();
}

app.UseForwardedHeaders(new ForwardedHeadersOptions()
{
	ForwardedHeaders = ForwardedHeaders.XForwardedFor | ForwardedHeaders.XForwardedProto
});

app.UseAuthorization();

app.MapControllers();

app.Run();