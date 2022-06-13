package org.moisiadis.service;

import org.moisiadis.SMTPCredentials;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private static SMTPCredentials credentials;

	public static SMTPCredentials getCredentials() {
		return credentials;
	}

	public static void setCredentials(final SMTPCredentials credentials) {
		EmailService.credentials = credentials;
	}
}
