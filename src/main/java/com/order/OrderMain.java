package com.order;

import spark.Spark;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class OrderMain {

    private static final String CATALOG_FILE_PATH = "src/main/resources/catalog.txt";

    public static void main(String[] args) {
        Spark.port(4568);

        Spark.post("/purchase/:itemNumber", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            return purchaseBook(itemNumber);
        });
    }

    private static String purchaseBook(int itemNumber) {
        try {
            Path catalogPath = Paths.get(CATALOG_FILE_PATH);
            List<String> catalogData = Files.readAllLines(catalogPath);

            for (int i = 0; i < catalogData.size(); i++) {
                String line = catalogData.get(i);
                String[] parts = line.split(",");
                int itemId = Integer.parseInt(parts[0].trim());
                int stock = Integer.parseInt(parts[3].trim());

                if (itemId == itemNumber) {
                    if (stock > 0) {
                        stock--;
                        parts[3] = String.valueOf(stock);
                        catalogData.set(i, String.join(",", parts));
                        Files.write(catalogPath, catalogData);
                        return "Purchased item successfully";
                    } else {
                        return "Item out of stock";
                    }
                }
            }
            return "Item not found";
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error purchasing item: " + e.getMessage());
            return "Error purchasing item: " + e.getMessage();
        }
    }
}
