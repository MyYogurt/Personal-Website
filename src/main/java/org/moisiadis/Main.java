package org.moisiadis;

import org.moisiadis.service.EmailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    private static String SMTPUsername, SMTPPassword;

    public static void main(String[] args) {
        try {
            XMLReader reader = new XMLReader("configuration.xml");
            EmailService.setSMTPUsername(reader.getElementsByName("smtp-username").item(0).getTextContent());
            EmailService.setSMTPPassword(reader.getElementsByName("smtp-password").item(0).getTextContent());
            reader = null;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        SpringApplication.run(Main.class);
    }
}
