package com.example.IT3A_PartialApps_grp11;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InfobipSmsUtils {

    private static final String BASE_URL = "https://gg8ep6.api.infobip.com/sms/2/text/advanced";
    private static final String API_KEY = "f83d5263c50f49fa31b44d67adaebdc5-199f0de7-6fb2-4124-b967-c22d30ccf637";

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
