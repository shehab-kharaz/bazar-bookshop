package com.catalog;

import org.json.JSONArray;
import org.json.JSONObject;
import spark.Spark;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;


public class CatalogMain {

    private static final String CATALOG_FILE_PATH = "src/main/resources/catalog.txt";

    public static void main(String[] args) {

        Spark.port(4567);

        Spark.get("/search/:topic", (req, res) -> {
            String topic = req.params(":topic");
            return searchBooksByTopic(topic);
        });


        Spark.get("/info/:itemNumber", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            return getBookInfo(itemNumber);
        });
    }

    private static String searchBooksByTopic(String topic) {
        JSONArray jsonArray = new JSONArray();
        try (BufferedReader reader = new BufferedReader(new FileReader(CATALOG_FILE_PATH))) {
            jsonArray = new JSONArray(reader.lines()
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length >= 3 && parts[2].trim().equalsIgnoreCase(topic))
                    .map(parts -> new JSONObject().put("id", Integer.parseInt(parts[0].trim())).put("title", parts[1].trim()))
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            System.out.println("Exception from searchBooksByTopic in catalog service");
        }
        return jsonArray.toString();
    }

    private static String getBookInfo(int itemNumber) {
        JSONObject response = new JSONObject();
        try (BufferedReader reader = new BufferedReader(new FileReader(CATALOG_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && Integer.parseInt(parts[0].trim()) == itemNumber) {
                    response.put("title", parts[1]);
                    response.put("quantity", Integer.parseInt(parts[3]));
                    response.put("price", Double.parseDouble(parts[4]));
                    break;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading catalog file: " + e.getMessage());
        }
        return response.toString();
    }
}
