package com.example.IT3A_PartialApps_grp11;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendNotification {
    private final String userFCMtoken;
    private final String title;
    private final String body;
    private final Context context;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/finalprojbrgymanagementnotifs/messages:send";

    public SendNotification(String userFCMtoken, String title, String body, Context context) {
        this.userFCMtoken = userFCMtoken;
        this.title = title;
        this.body = body;
        this.context = context;
    }

    public void SendNotifications() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject messageObject = new JSONObject();
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);
            messageObject.put("token", userFCMtoken);
            messageObject.put("notification", notificationObject);
            mainObj.put("message", messageObject);

            Log.d("SendNotification", "Request JSON: " + mainObj.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {
                Log.d("SendNotification", "Response: " + response.toString());
                Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_SHORT).show();
            }, volleyError -> {
                Log.e("SendNotification", "Failed to send notification: " + volleyError.getMessage());
                if (volleyError.networkResponse != null) {
                    Log.e("SendNotification", "Volley Error Code: " + volleyError.networkResponse.statusCode);
                    Log.e("SendNotification", "Volley Error Data: " + new String(volleyError.networkResponse.data));
                }
                Toast.makeText(context, "Failed to send notification: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    AccessToken accessToken = new AccessToken();
                    String accessKey = accessToken.getAccessToken();
                    Log.d("SendNotification", "Access Token: " + accessKey);
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "Bearer " + accessKey);
                    return header;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            Log.e("SendNotification", "Failed to create JSON request: " + e.getMessage());
            Toast.makeText(context, "Failed to create JSON request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
