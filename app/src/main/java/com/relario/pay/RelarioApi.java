package com.relario.pay;

import static com.relario.pay.SubscriptionUtils.API_KEY;
import static com.relario.pay.SubscriptionUtils.SHARED_PREFS_FILE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.relario.pay.models.NewTransaction;
import com.relario.pay.models.Transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RelarioApi {
    public static final String BASE_URL = "https://payment.relario.com/api/web";

    private static final Gson gson = new Gson();

    public static Transaction getTransaction(Context context, String transactionId) {
        String jsonResponse = makeGetRequest(context, BASE_URL + "/transactions/" + transactionId);
        return gson.fromJson(jsonResponse, Transaction.class);
    }

    public static Transaction createTransaction(Context context, NewTransaction newTransaction) {
        String request = gson.toJson(newTransaction);
        String jsonResponse = makePostRequest(context, BASE_URL + "/transactions", request);
        return gson.fromJson(jsonResponse, Transaction.class);
    }

    public static String getCurrentIP() {
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL("https://checkip.amazonaws.com/");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            br.close();

            return result.toString().trim(); // Trim to remove any newline characters
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    // Method to store the API key in SharedPreferences
    public static void storeApiKey(Context context, String apiKey) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(API_KEY, apiKey);
        editor.apply();
    }

    // Method to retrieve the API key from SharedPreferences
    private static String getApiKey(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(API_KEY, "");
    }



    private static String makeGetRequest(Context context, String urlString) {
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();


        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            String apiKey = getApiKey(context);
            urlConnection.setRequestProperty("Authorization", "Bearer " + apiKey);
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle error appropriately
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result.toString();
    }

    // Method to make a generic POST request
    public static String makePostRequest(Context context, String urlString, String jsonInputString) {
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            String apiKey = getApiKey(context);
            urlConnection.setRequestProperty("Authorization", "Bearer " + apiKey);
            urlConnection.setDoOutput(true);

            try (OutputStream os = urlConnection.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))) {
                writer.write(jsonInputString);
                writer.flush();
            }

            // Read the response
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // If response code is OK, read the response
                result.append(readStream(urlConnection.getInputStream()));
            } else {
                // If response code is not OK, read the error stream
                result.append(readStream(urlConnection.getErrorStream()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result.toString();
    }

    // Method to read an InputStream into a String
    private static String readStream(InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


}
