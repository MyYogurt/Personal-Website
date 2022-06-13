package org.moisiadis;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.FileUtils;
import org.moisiadis.service.EmailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class Main {
	public static void main(String[] args) {
		try {
			loadCredentials();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		SpringApplication.run(Main.class);
	}

	private static void loadCredentials() throws IOException {
		final XmlMapper mapper = new XmlMapper();
		final String data = FileUtils.readFileToString(new File("configuration.xml"), "UTF-8");
		final SMTPCredentials credentials = mapper.readValue(data, SMTPCredentials.class);
		EmailService.setCredentials(credentials);
	}
}
