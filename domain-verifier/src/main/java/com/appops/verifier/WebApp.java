package com.appops.verifier;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebApp {

   
    private static final Map<String, String> verificationTokens = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        
        port(8080);

       
        staticFiles.location("/"); 

        Gson gson = new Gson();

       
        post("/start-verification", (req, res) -> {
            Map<String, String> requestPayload = gson.fromJson(req.body(), Map.class);
            String domain = requestPayload.get("domain");

            if (domain == null || domain.trim().isEmpty()) {
                res.status(400); // Bad Request
                return "{\"error\":\"Domain is required\"}";
            }

            String token = DomainVerifier.generateVerificationToken();
            verificationTokens.put(domain, token); // Store the token for this specific domain

            System.out.println("Generated token '" + token + "' for domain '" + domain + "'");
            
            res.type("application/json");
            return "{\"token\":\"" + token + "\"}";
        });

       
        get("/check-status/:domain", (req, res) -> {
            String domain = req.params(":domain");
            String storedToken = verificationTokens.get(domain);

            if (storedToken == null) {
                res.status(404); // Not Found
                return "{\"status\":\"error\", \"message\":\"Verification not started for this domain.\"}";
            }

            boolean isVerified = DomainVerifier.verifyTxtRecord(domain, storedToken);

            res.type("application/json");
            if (isVerified) {
                verificationTokens.remove(domain);
                return "{\"status\":\"verified\"}";
            } else {
                return "{\"status\":\"pending\"}";
            }
        });

        System.out.println("✅ Server is running on http://localhost:8080");
    }
}