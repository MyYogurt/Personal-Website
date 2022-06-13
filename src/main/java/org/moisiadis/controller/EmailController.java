package org.moisiadis.controller;

import org.moisiadis.SMTPCredentials;
import org.moisiadis.service.EmailService;
import org.moisiadis.service.IPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

@RestController
public class EmailController {
	private final Logger logger = LoggerFactory.getLogger(EmailController.class);

	@PostMapping("/form-submission")
	public ResponseEntity<?> sendEmail(@RequestBody String messageBody, HttpServletRequest request) {
		final String ipAddress = request.getRemoteAddr();
		if (IPService.contains(ipAddress)) {
			return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
		}
		IPService.add(ipAddress);

		logger.info("New message from: " + ipAddress);

		final Properties properties = System.getProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.port", 587);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");

		final Session session = Session.getDefaultInstance(properties);

		try {
			final MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("noreply@panosmoisiadis.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("panosmoisiadis@pm.me"));
			message.setSubject("New Contact Form Submission");
			message.setText(messageBody);

			final Transport transport = session.getTransport();
			final SMTPCredentials credentials = EmailService.getCredentials();
			transport.connect("email-smtp.us-east-2.amazonaws.com", credentials.SMTPUsername(), credentials.SMTPPassword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("Message sent successfully");
		} catch (MessagingException messagingException) {
			logger.error("Error sending message", messagingException);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
}
