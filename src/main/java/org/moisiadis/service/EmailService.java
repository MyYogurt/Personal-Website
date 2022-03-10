package org.moisiadis.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static String SMTPUsername, SMTPPassword;

    public static String getSMTPUsername() {
        return SMTPUsername;
    }

    public static void setSMTPUsername(String SMTPUsername) {
        EmailService.SMTPUsername = SMTPUsername;
    }

    public static String getSMTPPassword() {
        return SMTPPassword;
    }

    public static void setSMTPPassword(String SMTPPassword) {
        EmailService.SMTPPassword = SMTPPassword;
    }
}
