package com.frontend;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        // Define routes
        get("/search", (req, res) -> {
            // Simulate search operation
            return "Search results for topic: " + req.queryParams("topic");
        });

        get("/info", (req, res) -> {
            // Simulate info operation
            return "Item number: " + req.queryParams("item_number") + ", Info: Details of the item";
        });

        get("/purchase", (req, res) -> {
            // Simulate purchase operation
            return "Purchased item number: " + req.queryParams("item_number");
        });
    }
}
