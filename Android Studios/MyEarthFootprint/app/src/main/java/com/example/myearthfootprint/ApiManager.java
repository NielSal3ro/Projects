package com.example.myearthfootprint;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiManager {
    private static final String BASE_URL = "https://capstone2api-rowv.onrender.com/";

    /** Callback for JSON Object responses */
    public interface JsonCallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }

    /** Callback for JSON Array responses */
    public interface JsonArrayCallback {
        void onSuccess(JSONArray result);
        void onError(String error);
    }
    /** Login */
    public static void authenticate(String username, String password, JsonCallback callback) {
        new AsyncTask<Void, Void, JSONObject>() {
            String errorMsg;
            @Override protected JSONObject doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "users/authenticate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    JSONObject payload = new JSONObject();
                    payload.put("Username", username);
                    payload.put("Password", password);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(payload.toString().getBytes("UTF-8"));
                    }

                    InputStream is = conn.getInputStream();
                    String resp = readStream(is);
                    return new JSONObject(resp);

                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    return null;
                }
            }
            @Override protected void onPostExecute(JSONObject result) {
                if (errorMsg != null) callback.onError(errorMsg);
                else callback.onSuccess(result);
            }
        }.execute();
    }

    /** Fetch products by category */
    public static void getProducts(String category, JsonArrayCallback callback) {
        new AsyncTask<String, Void, JSONArray>() {
            String errorMsg;
            @Override protected JSONArray doInBackground(String... params) {
                try {
                    URL url = new URL(BASE_URL + "products?category=" + params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    InputStream is = conn.getInputStream();
                    String resp = readStream(is);
                    return new JSONArray(resp);
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    return null;
                }
            }
            @Override protected void onPostExecute(JSONArray result) {
                if (errorMsg != null) callback.onError(errorMsg);
                else callback.onSuccess(result);
            }
        }.execute(category);
    }

    /** Fetch product details by ID */
    public static void getProductDetails(int productId, JsonCallback callback) {
        new AsyncTask<Integer, Void, JSONObject>() {
            String errorMsg;
            @Override protected JSONObject doInBackground(Integer... params) {
                try {
                    URL url = new URL(BASE_URL + "productdetails?id=" + params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    InputStream is = conn.getInputStream();
                    String resp = readStream(is);
                    return new JSONObject(resp);
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    return null;
                }
            }
            @Override protected void onPostExecute(JSONObject result) {
                if (errorMsg != null) callback.onError(errorMsg);
                else callback.onSuccess(result);
            }
        }.execute(productId);
    }

    /** Fetch alternate products */
    public static void getAlternateProducts(JsonArrayCallback callback) {
        new AsyncTask<Void, Void, JSONArray>() {
            String errorMsg;
            @Override protected JSONArray doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "alternateproducts");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    InputStream is = conn.getInputStream();
                    String resp = readStream(is);
                    return new JSONArray(resp);
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    return null;
                }
            }
            @Override protected void onPostExecute(JSONArray result) {
                if (errorMsg != null) callback.onError(errorMsg);
                else callback.onSuccess(result);
            }
        }.execute();
    }

    /** Fetch all users */
    public static void getUsers(JsonArrayCallback callback) {
        new AsyncTask<Void, Void, JSONArray>() {
            String errorMsg;
            @Override protected JSONArray doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "users");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    InputStream is = conn.getInputStream();
                    String resp = readStream(is);
                    return new JSONArray(resp);
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    return null;
                }
            }
            @Override protected void onPostExecute(JSONArray result) {
                if (errorMsg != null) callback.onError(errorMsg);
                else callback.onSuccess(result);
            }
        }.execute();
    }

    /** Register a new user */
    public static void registerUser(
            String firstName,
            String lastName,
            String username,
            String password,
            JsonCallback callback
    ) {
        new AsyncTask<Void,Void,String>(){
            String error;
            @Override protected String doInBackground(Void... params) {
                try {
                    URL url = new URL(BASE_URL + "users");
                    HttpURLConnection c = (HttpURLConnection)url.openConnection();
                    c.setRequestMethod("POST");
                    c.setRequestProperty("Content-Type","application/json");
                    c.setDoOutput(true);

                    JSONObject body = new JSONObject();
                    body.put("User_FName", firstName);
                    body.put("User_LName", lastName);
                    body.put("Username", username);
                    body.put("Password", password);
                    try (OutputStream os = c.getOutputStream()) {
                        os.write(body.toString().getBytes("utf-8"));
                    }
                    int code = c.getResponseCode();
                    InputStream in = (code < 400 ? c.getInputStream() : c.getErrorStream());
                    String resp = readStream(in);
                    if (code >= 400) {
                        error = resp;
                        return null;
                    }
                    return resp;
                } catch(Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }
            @Override protected void onPostExecute(String s) {
                if (s != null) {
                    try {
                        callback.onSuccess(new JSONObject(s));
                    } catch(JSONException e) {
                        callback.onError(e.getMessage());
                    }
                } else {
                    callback.onError(error);
                }
            }
        }.execute();
    }

    /** Record one impact event for a user */
    public static void addImpact(int userID, double ghg, double water) {
        JSONObject body = new JSONObject();
        try {
            body.put("userID", userID);
            body.put("ghg",     ghg);
            body.put("water",   water);
        } catch (JSONException ignored) {}

        postJson("/impact", body, new JsonCallback() {
            @Override public void onSuccess(JSONObject res) { /* no-op */ }
            @Override public void onError(String err) {
                Log.e("ApiManager", "addImpact failed: " + err);
            }
        });
    }

    /** Fetch last-24h impact summary for a user */
    public static void getImpactSummary(int userID, JsonCallback cb) {
        String url = BASE_URL + "impact/summary?userID=" + userID;
        new AsyncTask<Void,Void,String>() {
            String error;
            @Override protected String doInBackground(Void... params) {
                try {
                    HttpURLConnection c = (HttpURLConnection)new URL(url).openConnection();
                    c.setRequestMethod("GET");
                    InputStream in = c.getInputStream();
                    return readStream(in);
                } catch(Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }
            @Override protected void onPostExecute(String s) {
                if (s != null) {
                    try { cb.onSuccess(new JSONObject(s)); }
                    catch (JSONException e) { cb.onError(e.getMessage()); }
                } else cb.onError(error);
            }
        }.execute();
    }

    /** Helper to POST a JSON body to BASE_URL + path */
    private static void postJson(
            String path,
            JSONObject body,
            JsonCallback cb
    ) {
        new AsyncTask<Void,Void,String>() {
            String error;
            @Override protected String doInBackground(Void... params) {
                try {
                    URL url = new URL(BASE_URL + path);
                    HttpURLConnection c = (HttpURLConnection)url.openConnection();
                    c.setRequestMethod("POST");
                    c.setRequestProperty("Content-Type","application/json");
                    c.setDoOutput(true);

                    try (OutputStream os = c.getOutputStream()) {
                        os.write(body.toString().getBytes("utf-8"));
                    }

                    int code = c.getResponseCode();
                    InputStream in = (code < 400 ? c.getInputStream() : c.getErrorStream());
                    String resp = readStream(in);
                    if (code >= 400) {
                        error = resp;
                        return null;
                    }
                    return resp;
                } catch(Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }
            @Override protected void onPostExecute(String s) {
                if (s != null) {
                    try { cb.onSuccess(new JSONObject(s)); }
                    catch (JSONException e) { cb.onError(e.getMessage()); }
                } else cb.onError(error);
            }
        }.execute();
    }

    /** Utility: read an InputStream fully into a String */
    private static String readStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
}
