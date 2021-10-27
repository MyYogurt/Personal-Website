package org.moisiadis.website;

import org.apache.tika.Tika;
import org.moisiadis.glide.Glide;
import org.moisiadis.glide.util.network.HTTPExchange;
import org.moisiadis.glide.util.network.HTTPRequest;
import org.moisiadis.website.email.Email;
import org.moisiadis.website.email.SMTPCredentials;
import org.moisiadis.website.util.XMLReader;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Personal Website
 * @author Panos Moisiadis
 */
public class Website {

    private static final Logger logger = Logger.getLogger(Website.class.getName());

    private static final HashSet<InetAddress> recentAddresses = new HashSet<InetAddress>();

    private static int IP_BLOCK_TIME;

    /**
     * Main. Initializes HTTP server
     */
    public static void main(String[] args) {
        try {
            FileHandler fileHandler = new FileHandler("logs/website log " + LocalDateTime.now() + ".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            logger.info("Server startup");

            logger.info("Parsing XML...");

            XMLReader xml = new XMLReader("configuration.xml");
            IP_BLOCK_TIME = Integer.parseInt(xml.getElementsByName("ip-block-time").item(0).getTextContent());

            final SMTPCredentials credentials = new SMTPCredentials(xml.getElementsByName("smtp-username").item(0).getTextContent(), xml.getElementsByName("smtp-password").item(0).getTextContent());

            logger.info("XML successfully parsed");

            logger.info("Starting server...");

            Glide glide = new Glide(80, 4);
            glide.setContext("/", new MainHandler());
            glide.setContext("/formsubmission", new FormHandler(credentials));
            glide.setContext("/block-time", new BlockHandler());
            glide.start();

            logger.info("Server started.");
        } catch (Exception exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    static final class MainHandler implements HTTPExchange {

        private static final Logger logger = Logger.getLogger(MainHandler.class.getName());

        @Override
        public void handle(HTTPRequest request) {
            logger.info("New connection: " + request.getRemoteAddress());
            if (!request.getRequestMethod().equals("GET")) {
                logger.warning("Non-GET request sent to root.");
                try {
                    request.sendResponse(418);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                return;
            }

            final String requestedFile = request.getRequestPath();
            File file;
            if (requestedFile.equals("/")) {
                file = new File("html/index.html");
            } else if (requestedFile.equals("/docs")) {
                file = new File("docs/index.html");
            } else {
                file = new File("html" + requestedFile);
            }


            if (!file.exists()) {
                file = new File("docs" + requestedFile);
                if (!file.exists()) {
                    final String message = "File not found.";
                    logger.warning("Requested root file (" + file.getName() + ") not found.");
                    try {
                        request.sendResponse(404, "text/plain", message.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }

            Tika tika = new Tika();
            String mimeType = null;
            try {
                mimeType = tika.detect(file);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            try {
                request.sendResponse(200, mimeType, file);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    static final  class FormHandler implements HTTPExchange {

        private final SMTPCredentials credentials;

        public FormHandler(SMTPCredentials credentials) {
            this.credentials = credentials;
        }

        private static final Logger logger = Logger.getLogger(FormHandler.class.getName());

        @Override
        public void handle(HTTPRequest request) {
            final String requestMethod = request.getRequestMethod();

            if (!requestMethod.equals("POST")) {
                logger.warning("Invalid request sent to form submission");
                try {
                    request.sendResponse(418);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                return;
            }

            final InetAddress ip = request.getRemoteAddress();

            if (recentAddresses.contains(ip)) {
                try {
                    request.sendResponse(429);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            } else {
                recentAddresses.add(ip);
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(IP_BLOCK_TIME);
                    } catch (InterruptedException e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                    recentAddresses.remove(ip);
                });
                thread.start();
            }
            final String in;
            if (request.hasPayload())
                in = new String(request.getPayload());
            else {
                System.out.println(request.getRequestHeaders());
                System.out.println(Arrays.toString(request.getPayload()));
                logger.log(Level.WARNING, "Form submission request without payload");
                try {
                    request.sendResponse(400);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                return;
            }
            final Email email = new Email(credentials.getSMTPUsername(), credentials.getSMTPPassword(), in);
            new Thread(email).start();
            try {
                request.sendResponse(200);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    static final class BlockHandler implements HTTPExchange {

        @Override
        public void handle(HTTPRequest httpRequest) {
            if (!httpRequest.getRequestMethod().equals("GET")) {
                try {
                    httpRequest.sendResponse(418);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            final String response = String.valueOf(IP_BLOCK_TIME);
            try {
                httpRequest.sendResponse(200, "text/plain", response.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}