package com.example.IT3A_PartialApps_grp11;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InfobipSmsUtils {

    private static final String BASE_URL = "https://xlvg4e.api.infobip.com/sms/2/text/advanced";
    private static final String API_KEY = "27b080a561b453165de964b5b8c34f76-ebadca4e-45df-4be0-8eaa-158d07e2debd";

    public static void sendSms(String phoneNumber, String message) {
        new SendSmsTask(phoneNumber, message).execute();
    }

    private static class SendSmsTask extends AsyncTask<Void, Void, Void> {

        private String phoneNumber;
        private String message;

        SendSmsTask(String phoneNumber, String message) {
            this.phoneNumber = phoneNumber;
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(BASE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "App " + API_KEY);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String payload = "{"
                        + "\"messages\":[{"
                        + "\"from\":\"Barangay Tatalon Subsidy\","
                        + "\"destinations\":[{\"to\":\"" + phoneNumber + "\"}],"
                        + "\"text\":\"" + message + "\""
                        + "}]"
                        + "}";

                OutputStream os = connection.getOutputStream();
                os.write(payload.getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                Log.d("InfobipApiClient", "Response Code: " + responseCode);

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
