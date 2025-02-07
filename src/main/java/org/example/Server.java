package org.example;

import static spark.Spark.*;
import com.google.gson.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;

public class Server {
    private static final byte[] SECRET_DATA = "Super Secret Message".getBytes();

    public static void main(String[] args) {
        port(4567);

        get("/", (req, res) -> {
            res.type("text/html");
            return new String(Files.readAllBytes(Paths.get("index.html")));
        });

        post("/encrypt", (req, res) -> {
            try {
                JsonObject requestBody = JsonParser.parseString(req.body()).getAsJsonObject();
                String publicKeyPem = requestBody.get("publicKey").getAsString()
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");

                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPem);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

                // FIXED: Explicit OAEP parameters (Matches Python)
                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                        "SHA-256",
                        "MGF1",
                        new MGF1ParameterSpec("SHA-256"),
                        PSource.PSpecified.DEFAULT
                );
                cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
                // cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                var params = cipher.getParameters();

                byte[] encryptedBytes = cipher.doFinal(SECRET_DATA);

                JsonObject response = new JsonObject();
                response.addProperty("encryptedData", Base64.getEncoder().encodeToString(encryptedBytes));
                res.type("application/json");
                return response.toString();
            } catch (Exception e) {
                res.status(400);
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        });
    }
}
