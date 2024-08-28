package com.example;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -cp lib/json-20210307.jar;build DestinationHashGenerator <PRN Number> <JSON file path>");
            return;
        }

        String prn = args[0].toLowerCase().replaceAll("\\s+", "");
        String jsonFilePath = args[1];

        try {
            FileReader reader = new FileReader(jsonFilePath);
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));

            String destinationValue = findDestination(jsonObject);
            if (destinationValue == null) {
                System.out.println("No 'destination' key found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);
            String combinedString = prn + destinationValue + randomString;

            String md5Hash = generateMD5Hash(combinedString);
            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String findDestination(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (key.equals("destination")) {
                return value.toString();
            }

            if (value instanceof JSONObject) {
                String result = findDestination((JSONObject) value);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
