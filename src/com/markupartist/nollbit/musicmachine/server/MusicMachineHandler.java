package com.markupartist.nollbit.musicmachine.server;

import com.google.gson.Gson;
import com.markupartist.nollbit.musicmachine.server.json.GeneralObjectDeserializer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.felixbruns.jotify.gateway.util.URIUtilities;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

class Params {
    String key;
    String value;
}

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 14, 2010
 * Time: 10:27:33 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class MusicMachineHandler implements HttpHandler {
    HttpExchange httpExchange;
    Gson gson = GeneralObjectDeserializer.generalGson;

    @SuppressWarnings("unchecked")
    public void handle(HttpExchange exchange) throws IOException {
        /* Get request method and query. */
        httpExchange = exchange;

        String requestMethod = exchange.getRequestMethod();
        String requestQuery  = exchange.getRequestURI().getQuery();

        /* Get request parameters. */
        Map<String, String> params = new HashMap<String, String>();

        if(requestMethod.equalsIgnoreCase("GET")){
            params = URIUtilities.parseQuery(requestQuery);
        }
        else if(requestMethod.equalsIgnoreCase("POST")){
            InputStream input = null;
            try {
                input = exchange.getRequestBody();
                params = (Map<String, String>) GeneralObjectDeserializer.fromJson(new InputStreamReader(input));
            } finally {
                /* Close input stream. */
                if(input != null) {
                    input.close();
                }
            }
        }

        /* Get response body and headers. */
        OutputStream responseBody    = exchange.getResponseBody();
        Headers responseHeaders = exchange.getResponseHeaders();
        int responseCode = 200;
        String       responseString;
        byte[]       responseBytes;

        /* Set Access-Control headers.*/
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        responseHeaders.set("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
        responseHeaders.set("Access-Control-Allow-Headers", "X-Requested-With");
        responseHeaders.set("Access-Control-Max-Age", "1728000");

        /* Get response string depending on request method. */

        try {
            if(requestMethod.equalsIgnoreCase("OPTIONS")){
                responseString = "";
            }
            else if(requestMethod.equalsIgnoreCase("GET")){
                responseString = this.handleGet(params);
            }
            else if(requestMethod.equalsIgnoreCase("POST")){
                responseString = this.handlePost(params);
            }
            else if(requestMethod.equalsIgnoreCase("PUT")){
                responseString = this.handlePut(params);
            }
            else{
                responseString = "huh?";
            }
            responseHeaders.set("Content-Type", "application/json");
        }
        catch (BadRequestException e) {
            responseCode = 406;
            responseString = e.getMessage();
            responseHeaders.set("Content-Type", "text/plain");
        }
        catch (InternalServerErrorException e) {
            responseCode = 500;
            responseString = e.getMessage();
            responseHeaders.set("Content-Type", "text/plain");
        }
        catch (ConflictException e) {
            responseCode = 409;
            responseString = e.getMessage();
            responseHeaders.set("Content-Type", "text/plain");
        }

        /* Get response bytes. */
        responseBytes = responseString.getBytes(Charset.forName("UTF-8"));

        /* Send response code, length and headers. */
        exchange.sendResponseHeaders(responseCode, responseBytes.length);

        /* Write response string to output stream. */
        responseBody.write(responseBytes);
        responseBody.close();
    }

    public String handleGet(Map<String, String> params) throws BadRequestException, InternalServerErrorException, ConflictException {
        throw new BadRequestException("Not implemented");
    }
    public String handlePost(Map<String, String> params) throws BadRequestException, InternalServerErrorException, ConflictException {
        throw new BadRequestException("Not implemented");
    }
    public String handlePut(Map<String, String> params) throws BadRequestException, InternalServerErrorException, ConflictException {
        throw new BadRequestException("Not implemented");
    }

    public class BadRequestException extends Exception {
        public BadRequestException(String message) {
            super(message);
        }
    }
    public class InternalServerErrorException extends Exception {
        public InternalServerErrorException(String message) {
            super(message);
        }
    }
    public class ConflictException extends Exception {
        public ConflictException(String message) {
            super(message);
        }
    }

    /**
     * Get the encapsulated HTTP request received and the response in one exchange.
     * @return the httpExchange
     */
    public HttpExchange getHttpExchange() {
        return httpExchange;
    }
}
