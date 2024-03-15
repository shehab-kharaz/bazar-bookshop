package com.catalog;

import spark.Spark;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        Spark.get("/query-by-subject", (req, res) -> {
            String topic = req.queryParams("topic");
            return queryBooksBySubject(topic);
        });

        Spark.get("/query-by-item", (req, res) -> {
            int itemNumber = Integer.parseInt(req.queryParams("item_number"));
            return queryBookByItem(itemNumber);
        });

        Spark.put("/update", (req, res) -> {
            int itemNumber = Integer.parseInt(req.queryParams("item_number"));
            String newCost = req.queryParams("new_cost");
            int newStock = Integer.parseInt(req.queryParams("new_stock"));
            return updateBook(itemNumber, newCost, newStock);
        });
    }

    private static String queryBooksBySubject(String topic) throws IOException {
        StringBuilder result = new StringBuilder();
        //noinspection resource
        Files.lines(Paths.get("sh.txt"))
                .filter(line -> line.contains(topic))
                .forEach(line -> result.append(line).append("\n"));
        return result.toString();
    }

    private static String queryBookByItem(int itemNumber) throws IOException {
        String result = "";
        for (String line : Files.readAllLines(Paths.get("../data/sh.txt"))) {
            String[] parts = line.split(",");
            if (Integer.parseInt(parts[0].trim()) == itemNumber) {
                result = "Item number: " + parts[0] + ", Title: " + parts[1] + ", Stock: " + parts[2] + ", Cost: " + parts[3];
                break;
            }
        }
        return result;
    }

    private static String updateBook(int itemNumber, String newCost, int newStock) throws IOException {
        String result = "";
        StringBuilder updatedLines = new StringBuilder();
        Path path = Paths.get("../data/sh.txt");
        for (String line : Files.readAllLines(path)) {
            String[] parts = line.split(",");
            if (Integer.parseInt(parts[0].trim()) == itemNumber) {
                String updatedLine = itemNumber + "," + parts[1] + "," + newStock + "," + newCost;
                updatedLines.append(updatedLine).append("\n");
                result = "Update successful for item: " + parts[1];
            } else {
                updatedLines.append(line).append("\n");
            }
        }
        Files.write(path, updatedLines.toString().getBytes());
        return result;
    }
}
