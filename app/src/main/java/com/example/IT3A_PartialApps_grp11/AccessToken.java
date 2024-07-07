package com.example.IT3A_PartialApps_grp11;

import android.util.Log;

import com.google.api.client.util.Lists;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AccessToken  {
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken(){
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"finalprojbrgymanagement\",\n" +
                    "  \"private_key_id\": \"249c35248eed1a6a6c5e661512fcf50b010c82ad\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCp+72/eM0hNq5Q\\npExlbGX01VWmfRxhrlB6XmdX10BYpfTMYa/Sm875K64eix0wGl9nL6gMqHASEtbW\\n5ZDqTh7wot4NkSGHtykDqOilg5rgtSSk5ElXH7HzBuEskPFsGj6NWOMn8PjhgmHJ\\npRzIAagIQoBeZO0Tf386SlrdKtml2TIBRoyojNzV3DlaA+oypy6M3C52Aw2SVE+f\\nRRZ6N7nuNWN5H5ICmc2ClZY4RW580oUPF8Yxi0gjtjMD+sKz3EVODa51SC3lhkBI\\nbzOyd6fHMEg8jBcEMiWLzBuVPuEywRLiRwx5hejPxCg77fa198bj1EgM8SlGtA9k\\ndVh8V5zZAgMBAAECggEAGPwUbk3SZ4Tdh03hoBttkFiWZR5zlan6UJo1T2nR8+sP\\nZHEhV5qo3Tf946W9TbeP+UnocKqGBSoOuPnM3Juh8ZXG1LCrKy27/LqO2NvTX8ml\\npV+b+2SAZ+vFciMAe/a5kKtq1rvqdl8PLNqKNJ2wYJJsbYjLzfdfWyaDB4odAULX\\nE+4tnnYnxSBr3jpDrKVdRDHLObjZ+jfaH9dfyjIKGR5fc0dL7moD8EZ1U1/ZJoWU\\nGzHbct+Xbt8qldWezH855l7+NZnEdQHjMcXL2jttQYRg/Wuvv87ttpy1nCQnOJIA\\n2PHEqBQKGpqvUmu2T/fdajiSEJp6qlt8TDG0EnWFvwKBgQDtkEEXt/JG+T4Eyb1b\\nVJVuXfjzuVkYyWK+6HP5xHetSKauNzjXURCxzfOiej0CJazTHITwzz9xNejcAojX\\nndlmbEvmghb5morTQX8VZ6Q70KrWhuUrYYYdYE66mzhbpoa7wJvq5Pp1Z4BA9idh\\niIcgvSiP8cKyZb2TXNdi3flqowKBgQC3LNoh7l+AVRS085zCWyElFLjfzHYNhm70\\nN7ERMADXmgh/oL+C2imZS4PQAJnAjbtzW9h52LC0Rmu6aq8cIyb7Ij3SQ2hJE6NF\\n6qX205YTiJiJ5z3+qfCvd7zahB10rxW9JN4nau9lv3mOs5lkrkdzaUP/5oL2uLAK\\nkY3bNzhuUwKBgQCxSRIKrU+0/5P05sjVO444yxBaclu3T7LNXZQPNTtKqgkZwkQ9\\nC1Y2GshS3H5i99K9yW68dy2VQvIAHjQZFMeE1BTgdPzIUTiA50P7qVi6+sJDTm//\\nq8v2aETYpbWtT90EiHzTEwaGTzM7R3ekJcsR95gps/XkRGDxdDuYoKbdmQKBgQCL\\nwJOYSbbhQYhv2mGiQ0bRdLLsRKFZBlUGzKqyc05hMCsFvM4AdpRAWcXyX1Lu/vOi\\nOtO5euKuV///FUAOlp86AVDrZ/RbtLjgMft3isy4kIHYzO2Wq29vYEpc6WlTyfUq\\n/sLjjgmiNmj2v/3rO7BV1F1GE0eeFyBwn8Z8FvBd8wKBgQCMcRMm9Xf5cbHR/BJz\\npdk/av5FzT9GP21ejIx9ifPd/jP72hXU25G3isqR9rtWXa05jO/khWynNfkSCar2\\nPTqOUPKz0kn9NN7ObrOvtlMZ0ugUYGN+eGnM0q/QFHtJDHADRSJefqcp7P2kFW5K\\nHkwekPOtz0HCJKlq0zUGHnwP1A==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-2hezu@finalprojbrgymanagement.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"106121211043735043466\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-2hezu%40finalprojbrgymanagement.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(Arrays.asList(firebaseMessagingScope));

            googleCredentials.refresh();

            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e){
            Log.d("Error", "" + e.getMessage());
            return null;
        }
    }
}
