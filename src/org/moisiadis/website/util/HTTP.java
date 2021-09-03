package org.moisiadis.website.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class to send responses
 */
public class HTTP {
    /**
     * Send HTTP Error Responses
     * @param exchange HTTP Exchange
     * @param type Response type
     * @throws IOException
     */
    public static void sendResponse(HttpExchange exchange, ResponseType type) throws IOException {
        final int code;
        final String response;
        switch (type) {
            case FILE_NOT_FOUND -> {
                code = 404;
                response = "File not found";
            }
            case TOO_MANY_REQUESTS -> {
                code = 429;
                response = "Too many requests. Please try again later";
            }
            case INVALID_REQUEST -> {
                code = 418;
                response = "Invalid request";
            }
            default -> {
                code = 200;
                response = "HTTP/1.1";
            }
        }
        exchange.sendResponseHeaders(code, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());

        exchange.close();
    }
}
