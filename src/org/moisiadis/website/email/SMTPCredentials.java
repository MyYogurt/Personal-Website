package org.moisiadis.website.email;

/**
 * Object that contains the username and password of an SMTP server
 */
public class SMTPCredentials {
    private final String SMTPUsername, SMTPPassword;

    /**
     * @param SMTPUsername Username of SMTP server
     * @param SMTPPassword Password of SMTP server
     */
    public SMTPCredentials(final String SMTPUsername, final String SMTPPassword) {
        this.SMTPUsername = SMTPUsername;
        this.SMTPPassword = SMTPPassword;
    }

    /**
     * @return SMTP server username
     */
    public String getSMTPUsername() {
        return SMTPUsername;
    }

    /**
     * @return SMTP server password
     */
    public String getSMTPPassword() {
        return SMTPPassword;
    }
}
