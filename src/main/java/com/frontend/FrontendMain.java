package com.frontend;

import spark.Spark;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class FrontendMain {

    public static final int CATALOG_PORT = 4567;
    private static final int ORDER_PORT = 4568;

    public static void main(String[] args) {
        Spark.port(4569);

        Spark.get("/search/:topic", (req, res) -> {
            String topic = req.params(":topic");
            return forwardRequest("http://localhost:" + CATALOG_PORT + "/search/" + topic, "GET");
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

    public static String forwardRequest(String urlString, String method) {

        urlString = urlString.replace(":topic", URLEncoder.encode(":topic", StandardCharsets.UTF_8));

        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            if (method.equalsIgnoreCase("POST")) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                OutputStream os = connection.getOutputStream();
                os.write("".getBytes()); // Empty payload for POST request
                os.flush();
                os.close();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } else {
                response.append("HTTP response code: ").append(responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }
}
