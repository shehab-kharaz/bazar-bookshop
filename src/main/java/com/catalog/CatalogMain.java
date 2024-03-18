package com.catalog;

import org.json.JSONArray;
import org.json.JSONObject;
import spark.Spark;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class CatalogMain {

    private static final String CATALOG_FILE_PATH = "src/main/resources/catalog.txt";
    private static final Path path = Paths.get(CATALOG_FILE_PATH);


    public static void main(String[] args) {

        //Define the port to listen on it, defile routes and calling the actual functionalities
        Spark.port(4567);
        Spark.get("/search/:topic", (req, res) -> {
            String topic = req.params(":topic");
            return searchBooks(topic, 2);
        });

        Spark.get("/searchItem/:itemName", (req, res) -> {
            String itemName = req.params(":itemName");
            return searchBooks(itemName, 1);
        });

        Spark.get("/info/:itemNumber", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            return getBookInfo(itemNumber);
        });

        Spark.post("/update/cost/:itemNumber/:newCost", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            double newCost = Double.parseDouble(req.params(":newCost"));
            return updateCost(itemNumber, newCost);
        });

        Spark.post("/update/increaseStock/:itemNumber/:amount", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            int increaseAmount = Integer.parseInt(req.params(":amount"));
            return increaseStock(itemNumber, increaseAmount);
        });

        Spark.post("/update/decreaseStock/:itemNumber/:amount", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            int decreaseAmount = Integer.parseInt(req.params(":amount"));
            return decreaseStock(itemNumber, decreaseAmount);
        });
    }

    //Using BufferReader to read the data file and get the needed data
    //topic argument can be the topic or the book name, since the two is the same functionality but difference just
    //in the thing we search based on it, so the part argument will specify if we search based on the
    //topic or the book name
    private static String searchBooks(String topic, int part) {

        JSONArray jsonArray = new JSONArray();
        try (BufferedReader reader = new BufferedReader(new FileReader(CATALOG_FILE_PATH))) {
            jsonArray = new JSONArray(reader.lines()
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length >= 3 && parts[part].trim().equalsIgnoreCase(topic))
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

    private static String updateCost(int itemNumber, double newCost) {
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts.length >= 5 && Integer.parseInt(parts[0].trim()) == itemNumber) {
                    parts[4] = String.valueOf(newCost);
                    lines.set(i, String.join(",", parts));
                    break;
                }
            }
            Files.write(path, lines);
            return "Cost for item " + itemNumber + " updated to " + newCost + " successfully.\n";
        } catch (Exception e) {
            return "Error updating cost for item " + itemNumber + ": " + e.getMessage() + "\n";
        }
    }

    private static String increaseStock(int itemNumber, int increaseAmount) {
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts.length >= 5 && Integer.parseInt(parts[0].trim()) == itemNumber) {
                    int currentStock = Integer.parseInt(parts[3].trim());
                    parts[3] = String.valueOf(currentStock + increaseAmount);
                    lines.set(i, String.join(",", parts));
                    break;
                }
            }
            Files.write(Paths.get(CATALOG_FILE_PATH), lines);
            return "Stock for item " + itemNumber + " increased by " + increaseAmount + " units successfully.\n";
        } catch (Exception e) {
            return "Error increasing stock for item " + itemNumber + ": " + e.getMessage() + "\n";
        }
    }

    private static String decreaseStock(int itemNumber, int decreaseAmount) {
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts.length >= 5 && Integer.parseInt(parts[0].trim()) == itemNumber) {
                    int currentStock = Integer.parseInt(parts[3].trim());
                    if (currentStock < decreaseAmount) {
                        return "Error: Insufficient stock for item " + itemNumber + ".";
                    }
                    parts[3] = String.valueOf(currentStock - decreaseAmount);
                    lines.set(i, String.join(",", parts));
                    break;
                }
            }
            Files.write(Paths.get(CATALOG_FILE_PATH), lines);
            return "Stock for item " + itemNumber + " decreased by " + decreaseAmount + " units successfully.\n";
        } catch (Exception e) {
            return "Error decreasing stock for item " + itemNumber + ": " + e.getMessage() + "\n";
        }
    }

}
