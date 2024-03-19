package com.order;

import spark.Spark;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderMain {

    private static final String CATALOG_SERVICE_URL = "http://localhost:4567";
    private static final Path ORDER_LOG_FILE = Path.of("src/main/java/com/order/order_log.txt");

    public static void main(String[] args) {
        //specify the port to listen on it, define the endpoint and calling the actual functionality
        Spark.port(4568);
        Spark.post("/purchase/:itemNumber", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            String requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return purchaseBook(itemNumber, req.ip(), requestTime);
        });
    }

    private static String purchaseBook(int itemNumber, String sourceIP, String requestTime) {
        try {
            // build and send a request to catalog service to check if the item is available or out of stock
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest stockCheckRequest = HttpRequest.newBuilder()
                    .uri(new URI(CATALOG_SERVICE_URL + "/checkStock/" + itemNumber))
                    .GET()
                    .build();

            HttpResponse<String> stockCheckResponse = httpClient.send(stockCheckRequest, HttpResponse.BodyHandlers.ofString());

            //if the item is available
            if (stockCheckResponse.statusCode() == HttpURLConnection.HTTP_OK && stockCheckResponse.body().equals("available")) {

                // build and send a request to catalog service to decrease the item stock by 1
                HttpRequest decreaseStockRequest = HttpRequest.newBuilder()
                        .uri(new URI(CATALOG_SERVICE_URL + "/update/decreaseStock/" + itemNumber + "/1"))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> decreaseStockResponse = httpClient.send(decreaseStockRequest, HttpResponse.BodyHandlers.ofString());
                // for log file (to log the request)
                logPurchase(itemNumber, requestTime, sourceIP, decreaseStockResponse.statusCode(), "Item purchased successfully");

                //return that the item is purchased successfully AFTER checking that the response is OK
                if (decreaseStockResponse.statusCode() == HttpURLConnection.HTTP_OK) {
                    return "Item purchased successfully\n";
                } else {
                    return "Error purchasing item: Stock decrease failed\n";
                }
            } else {
                // for log file (to log the request)
                logPurchase(itemNumber, requestTime, sourceIP, stockCheckResponse.statusCode(), "Item out of stock");

                return "Item out of stock\n";
            }
        } catch (Exception e) {
            // for log file (to log the request)
            logPurchase(itemNumber, requestTime, sourceIP, HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal Server Error");
            return "Error purchasing item: " + e.getMessage() + "\n";
        }
    }


    //to be more precise, the time of the request is saved in a variable at the instant
    //when the request received (in the main thread), I do not use .currentTime in the method
    private static void logPurchase(int itemNumber, String requestTime, String sourceIP, int statusCode, String statusMessage) {
        try {
            String logEntry = String.format("Book ID: %d, Request Time: %s, Source IP: %s, Status Code: %d, Status Message: %s%n",
                    itemNumber, requestTime, sourceIP, statusCode, statusMessage);
            Files.write(ORDER_LOG_FILE, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Error logging purchase: " + e.getMessage());
        }
    }
}
