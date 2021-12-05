package org.moisiadis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    private static String SMTPUsername, SMTPPassword;
    private static final int IP_BLOCK_TIME = 60000;

    public static void main(String[] args) {
        try {
            XMLReader reader = new XMLReader("configuration.xml");
            SMTPUsername = reader.getElementsByName("smtp-username").item(0).getTextContent();
            SMTPPassword = reader.getElementsByName("smtp-password").item(0).getTextContent();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        SpringApplication.run(Main.class);
    }

    public static int getBlockTime() {
        return IP_BLOCK_TIME;
    }

    public static String getSMTPUsername() {
        return SMTPUsername;
    }

    public static String getSMTPPassword() {
        return SMTPPassword;
    }

    public int getIpBlockTime() {
        return IP_BLOCK_TIME;
    }
}
