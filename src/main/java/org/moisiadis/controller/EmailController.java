package org.moisiadis.controller;

import org.moisiadis.service.EmailService;
import org.moisiadis.service.IPService;
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

    @PostMapping("/formsubmission")
    public ResponseEntity<Object> sendEmail(@RequestBody String messageBody, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        if (IPService.contains(ipAddress)) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }
        IPService.add(ipAddress);
        Properties properties = System.getProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.portN", 587);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@panosmoisiadis.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("panosmoisiadis@pm.me"));
            message.setSubject("New Contact Form Submission");
            message.setText(messageBody);

            Transport transport = session.getTransport();
            transport.connect("email-smtp.us-east-2.amazonaws.com", EmailService.getSMTPUsername(), EmailService.getSMTPPassword());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Message sent successfully");
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
