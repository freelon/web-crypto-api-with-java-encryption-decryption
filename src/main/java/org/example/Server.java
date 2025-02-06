package org.example;

import static spark.Spark.*;
import org.bouncycastle.util.encoders.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;

public class Server {
    private static final byte[] SECRET_DATA = "Super Secret Message".getBytes();

    public static void main(String[] args) {
        port(5000);

        get("/", (req, res) -> {
            res.type("text/html");
            return new String(Files.readAllBytes(Paths.get("../index.html")));
        });

        post("/encrypt", (req, res) -> {
            try {
                String publicKeyPem = req.body().replace("-----BEGIN PUBLIC KEY-----", "")
                                                .replace("-----END PUBLIC KEY-----", "")
                                                .replace("\n", "");
                byte[] publicKeyBytes = Base64.decode(publicKeyPem);
                
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey = keyFactory.generatePublic(keySpec);

                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] encryptedBytes = cipher.doFinal(SECRET_DATA);
                
                return "{\"encryptedData\": \"" + Base64.toBase64String(encryptedBytes) + "\"}";
            } catch (Exception e) {
                res.status(400);
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        });
    }
}
