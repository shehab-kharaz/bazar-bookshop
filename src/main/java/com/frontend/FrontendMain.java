package com.frontend;

import spark.Spark;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FrontendMain {

    public static final int CATALOG_PORT = 4567;
    private static final int ORDER_PORT = 4568;

    public static void main(String[] args) {
        Spark.port(4569);

        Spark.get("/search/:topic", (req, res) -> {
            String topic = req.params(":topic");
            String replace = "http://localhost:" + CATALOG_PORT + "/search/" + topic.replace(" ", "%20");
            return forwardRequest( replace,"GET");
        });

        Spark.get("/info/:itemNumber", (req, res) -> {
            String itemNumber = req.params(":itemNumber");
            return forwardRequest("http://localhost:" + CATALOG_PORT + "/info/" + itemNumber, "GET");
        });

        Spark.post("/purchase/:itemNumber", (req, res) -> {
            String itemNumber = req.params(":itemNumber");
            return forwardRequest("http://localhost:" + ORDER_PORT + "/purchase/" + itemNumber, "POST");
        });
    }




    private static String forwardRequest(String url, String method) {
        try {
            // Debugging: Print the URL before making the request
            System.out.println("Forwarding request to URL: " + url);

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .method(method, HttpRequest.BodyPublishers.noBody());

            if (method.equals("POST")) {
                requestBuilder.header("Content-Type", "application/json"); // Adjust content type if needed
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return response.body();
            } else {
                return "Error forwarding request: " + response.statusCode() + " " + response.body();
            }
        } catch (Exception e) {
            return "Error forwarding request: " + e.getMessage();
        }
    }


}
