package org.moisiadis.website;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.tika.Tika;
import org.moisiadis.website.email.Email;
import org.moisiadis.website.email.SMTPCredentials;
import org.moisiadis.website.util.HTTP;
import org.moisiadis.website.util.IO;
import org.moisiadis.website.util.ResponseType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * Personal Website
 * @author Panos Moisiadis
 */
public class Website {
    private static SMTPCredentials credentials;

    private final static String SUCCESSFUL_MESSAGE = "Message sent successfully";

    private static final Logger logger = Logger.getLogger(Website.class.getName());

    private static final HashSet<String> recentAddresses = new HashSet<String>();

    private static int IP_BLOCK_TIME;

    /**
     * Main. Initializes HTTP server
     */
    public static void main(String[] args) {
        logger.info("Server startup");
        try {
            logger.info("Parsing XML...");
            IP_BLOCK_TIME = Integer.parseInt(Objects.requireNonNull(IO.parseXML("configuration.xml", "ip-block-time")));
            logger.info("XML successfully parsed");

            FileHandler fileHandler = new FileHandler("logs/website" + LocalDateTime.now() + ".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            logger.info("Loading credentials...");

            credentials = IO.loadSESCredentials("configuration.xml");

            logger.info("Credentials successfully loaded.");
            logger.info("Starting server...");

            HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
            server.createContext("/", new PageHandler());
            server.createContext("/formsubmission", new FormHandler());
            server.setExecutor(null);
            server.start();

            logger.info("Server started.");
        } catch (Exception exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    static class PageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            final String ip = exchange.getRemoteAddress().getAddress().getHostAddress();

            logger.info("New root connection from: " + exchange.getRemoteAddress());

            if (!exchange.getRequestMethod().equals("GET")) {
                logger.warning("Non-GET request sent to root.");
                HTTP.sendResponse(exchange, ResponseType.INVALID_REQUEST);
                return;
            }

            final String requestedFile = exchange.getRequestURI().toString();
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
                    logger.warning("Requested root file (" + file.getName() + ") not found. File requested by: " + ip);
                    HTTP.sendResponse(exchange, ResponseType.FILE_NOT_FOUND);
                    return;
                }
            }

            Tika tika = new Tika();
            final String mimeType = tika.detect(file);
            exchange.getResponseHeaders().put("Content-Type", Collections.singletonList(mimeType));
            exchange.sendResponseHeaders(200, file.length());
            OutputStream os = exchange.getResponseBody();
            Files.copy(file.toPath(), os);

            exchange.close();
        }
    }

    static class FormHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logger.info("New form connection from: " + exchange.getRemoteAddress());

            final String requestMethod = exchange.getRequestMethod();

            if (!requestMethod.equals("POST")) {
                logger.warning("Invalid request sent to form submission");
                HTTP.sendResponse(exchange, ResponseType.INVALID_REQUEST);
                return;
            }

            final String ip = exchange.getRemoteAddress().getAddress().getHostAddress();

            if (recentAddresses.contains(ip)) {
                HTTP.sendResponse(exchange, ResponseType.TOO_MANY_REQUESTS);
                return;
            } else {
                recentAddresses.add(ip);
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(IP_BLOCK_TIME);
                        } catch (InterruptedException e) {
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                        recentAddresses.remove(ip);
                    }
                };
                thread.start();
            }

            final String in = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).lines().collect(Collectors.joining("\n"));
            final Email email = new Email(credentials.getSMTPUsername(), credentials.getSMTPPassword(), in);
            new Thread(email).start();
            exchange.sendResponseHeaders(200, SUCCESSFUL_MESSAGE.length());
            OutputStream os = exchange.getResponseBody();
            os.write(SUCCESSFUL_MESSAGE.getBytes());

            exchange.close();
        }
    }
}
