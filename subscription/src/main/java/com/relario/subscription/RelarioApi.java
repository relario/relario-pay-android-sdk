package com.relario.subscription;


import static com.relario.subscription.SubscriptionUtils.API_KEY;
import static com.relario.subscription.SubscriptionUtils.SHARED_PREFS_FILE;
import static com.relario.subscription.SubscriptionUtils.HttpClient;
import static com.relario.subscription.SubscriptionUtils.JsonUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.relario.subscription.models.NewTransaction;
import com.relario.subscription.models.Transaction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RelarioApi {
    private static final String BASE_URL = "https://payment.relario.com/api/web";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final String TAG = RelarioApi.class.getSimpleName();


    public static Transaction getTransaction(Context context, String transactionId) throws Exception {
        String apiKey = getApiKey(context);
        Request request = new Request.Builder()
                .url(BASE_URL + "/transactions/" + transactionId)
                .addHeader("Authorization", "Bearer " + apiKey)
                .get().build();
        try (Response response = HttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);
            if (response.body() == null) throw new Exception("Empty response " + response);
            return JsonUtil.fromJson(response.body().string(), Transaction.class);
        } catch (Exception e) {
            Log.e(TAG, "Exception when getTransaction", e);
            throw e;
        }
    }

    public static Transaction createTransaction(Context context, NewTransaction newTransaction) throws Exception {
        Log.i(TAG, "Create transaction: " + newTransaction.toString());
        String json = JsonUtil.toJson(newTransaction);
        Log.i(TAG, "New Transaction: " + json);
        RequestBody body = RequestBody.create(json, JSON);
        String apiKey = getApiKey(context);
        Request request = new Request.Builder().url(BASE_URL + "/transactions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body).build();
        try (Response response = HttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);
            if (response.body() == null) throw new Exception("Empty response " + response);
            return JsonUtil.fromJson(response.body().string(), Transaction.class);
        } catch (Exception e) {
            Log.e(TAG, "Exception when createTransaction", e);
            throw e;
        }
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
    public static String getApiKey(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(API_KEY, "");
    }

}
