package org.moisiadis.website.email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Class to send email
 */
public class Email implements Runnable {
    private final String SMTPUsername, SMTPPassword, messageBody;

    /**
     * Creates new Email
     * @param SMTPUsername Username of SMTP server
     * @param SMTPPassword Password of SMTP server
     * @param messageBody Body of email
     */
    public Email(final String SMTPUsername, final String SMTPPassword, final String messageBody) {
        this.SMTPUsername = SMTPUsername;
        this.SMTPPassword = SMTPPassword;
        this.messageBody = messageBody;
    }

    /**
     * Sends the email in a thread
     */
    public void run() {
        Properties properties = System.getProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.port", 587);
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
            transport.connect("email-smtp.us-east-2.amazonaws.com", SMTPUsername, SMTPPassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Message sent successfully");
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
            System.exit(1);
        }
    }
}
