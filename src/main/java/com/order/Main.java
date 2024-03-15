package com.order;

import spark.Spark;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        Spark.post("/purchase", (req, res) -> {
            int itemNumber = Integer.parseInt(req.queryParams("item_number"));
            return purchaseBook(itemNumber);
        });
    }

    private static String purchaseBook(int itemNumber) throws IOException {
        String result = "";
        Path path = Paths.get("../data/sh.txt");
        for (String line : Files.readAllLines(path)) {
            String[] parts = line.split(",");
            if (Integer.parseInt(parts[0].trim()) == itemNumber) {
                int stock = Integer.parseInt(parts[2].trim());
                if (stock > 0) {
                    stock--;
                    String updatedLine = parts[0] + "," + parts[1] + "," + stock + "," + parts[3];
                    Files.write(path, (updatedLine + System.lineSeparator()).getBytes());
                    result = "Purchased item: " + parts[1];
                } else {
                    result = "Item out of stock";
                }
                break;
            }
        }
        return result;
    }
}
