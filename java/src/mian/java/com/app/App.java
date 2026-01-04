package com.app;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(8081);

        post("/process", (req, res) -> {
            res.type("application/json");
            return "{ \"message\": \"Java service processed file\" }";
        });
    }
}
