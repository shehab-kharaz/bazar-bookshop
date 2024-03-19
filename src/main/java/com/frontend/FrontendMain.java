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
        //The port that the Frontend service will listen on it
        Spark.port(4569);

        //Define the endpoints and create the request that will be forwarded for catalog/order
        Spark.get("/search/:topic", (req, res) -> {
            String topic = req.params(":topic");
            String replace = "http://localhost:" + CATALOG_PORT + "/search/" + topic.replace(" ", "%20");
            return forwardRequest( replace,"GET");
        });

        Spark.get("/searchItem/:itemName", (req, res) -> {
            String itemName = req.params(":itemName");
            return forwardRequest("http://localhost:" + CATALOG_PORT + "/searchItem/" + itemName.replace(" ", "%20"), "GET");
        });

        Spark.get("/info/:itemNumber", (req, res) -> {
            String itemNumber = req.params(":itemNumber");
            return forwardRequest("http://localhost:" + CATALOG_PORT + "/info/" + itemNumber, "GET");
        });

        Spark.post("/purchase/:itemNumber", (req, res) -> {
            String itemNumber = req.params(":itemNumber");
            return forwardRequest("http://localhost:" + ORDER_PORT + "/purchase/" + itemNumber, "POST");
        });


        Spark.post("/updateCost/:itemNumber/:newCost", (req, res) -> {
            String itemNumber = req.params(":itemNumber");
            String newCost = req.params(":newCost");
            return forwardRequest("http://localhost:" + CATALOG_PORT + "/update/cost/" + itemNumber + "/" + newCost, "POST");
        });

        Spark.post("/increaseStock/:itemNumber/:amount", (req, res) -> {
            String itemNumber = req.params(":itemNumber");
            String amount = req.params(":amount");
            return forwardRequest("http://localhost:" + CATALOG_PORT + "/update/increaseStock/" + itemNumber + "/" + amount, "POST");
        });

        Spark.post("/decreaseStock/:itemNumber/:amount", (req, res) -> {
            String itemNumber = req.params(":itemNumber");
            String amount = req.params(":amount");
            return forwardRequest("http://localhost:" + CATALOG_PORT + "/update/decreaseStock/" + itemNumber + "/" + amount, "POST");
        });
    }




    //Forwarding requests over HTTP using HttpClient to the desired services (order, catalog)
    private static String forwardRequest(String url, String method) {
        try {

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .method(method, HttpRequest.BodyPublishers.noBody());

            //since POST, so we need to change a resource and this to make the service expect JSON
            //I used POST rather than PUT for update/purchase
            //PATCH would be the most appropriate verb
            if (method.equals("POST")) {
                requestBuilder.header("Content-Type", "application/json");
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            //return the response if status code = 200 and return error otherwise
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return response.body() + "\n";
            } else {
                return "Error forwarding request: " + response.statusCode() + " " + response.body() + "\n";
            }
        } catch (Exception e) {
            return "Error forwarding request: " + e.getMessage() + "\n";
        }
    }

}
