package com.frontend;

import org.json.JSONException;
import spark.Spark;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Main {
    static final String CATALOG_FILE_PATH = "src/main/resources/catalog.txt";

    public static void main(String[] args) {

        Spark.get("/search/:topic", (req, res) -> {
            String topic = req.params(":topic");
            return searchBooksByTopic(topic);
        });

        Spark.get("/info/:itemNumber", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            return getBookInfo(itemNumber);
        });

        Spark.post("/purchase/:itemNumber", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            return purchaseBook(itemNumber);
        });

    }

    private static String searchBooksByTopic(String topic) {
    JSONArray jsonArray = new JSONArray();

    try (BufferedReader reader = new BufferedReader(new FileReader(CATALOG_FILE_PATH))) {
        reader.lines()
                .map(line -> line.split(","))
                .filter(parts -> parts.length >= 3 && parts[2].trim().equalsIgnoreCase(topic))
                .forEach(parts -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", Integer.parseInt(parts[0].trim()));
                    jsonObject.put("title", parts[1].trim());
                    jsonArray.put(jsonObject);
                });
    } catch (IOException e) {
        System.out.println("Exception from searchBookByTopic in frontend service");
    }

    return jsonArray.toString();
}
    private static String getBookInfo(int itemNumber) throws IOException {
        JSONObject response = new JSONObject();

        try (BufferedReader reader = new BufferedReader(new FileReader(CATALOG_FILE_PATH))) {
            JSONArray booksArray = new JSONArray();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && Integer.parseInt(parts[0].trim()) == itemNumber) {
                    JSONObject bookInfo = new JSONObject();
                    bookInfo.put("title", parts[1]);
                    bookInfo.put("quantity", Integer.parseInt(parts[3]));
                    bookInfo.put("price", Double.parseDouble(parts[4]));
                    booksArray.put(bookInfo);
                    break;
                }
            }
            response.put("book", booksArray);
        } catch (IOException | NumberFormatException | JSONException e) {
            // Handle IOException, NumberFormatException, or JSONException
            System.err.println("Error reading catalog file: " + e.getMessage());
            throw new IOException(e); // Re-throw the exception to propagate it up the call stack
        }

        return response.toString();
    }
    private static String purchaseBook(int itemNumber) {
    try {
        Path catalogPath = Paths.get(CATALOG_FILE_PATH);
        BufferedReader reader = new BufferedReader(new FileReader(catalogPath.toFile()));
        String line;
        List<String> updatedCatalogData = new ArrayList<>();
        boolean itemFound = false;


        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            int itemId = Integer.parseInt(parts[0].trim());
            int stock = Integer.parseInt(parts[3].trim());

            if (itemId == itemNumber) {
                itemFound = true;
                if (stock > 0) {
                    stock--;
                    parts[3] = String.valueOf(stock);
                } else {
                    return "Item out of stock";
                }
            }
            updatedCatalogData.add(String.join(",", parts));
        }

        if (!itemFound) {
            return "Item not found";
        }

        FileWriter writer = new FileWriter(catalogPath.toFile());
        for (String updatedLine : updatedCatalogData) {
            writer.write(updatedLine + "\n");
        }
        writer.close();

        return "Purchased item successfully";

    } catch (IOException | NumberFormatException e) {
        System.err.println("Error purchasing item: " + e.getMessage());
        return "Error purchasing item: " + e.getMessage();
    }
}

}
