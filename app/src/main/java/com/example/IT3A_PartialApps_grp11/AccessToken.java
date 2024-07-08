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
                    "  \"project_id\": \"finalprojbrgymanagementnotifs\",\n" +
                    "  \"private_key_id\": \"b3debdc081414cc0b693f17bf9d66bd80d28b6e2\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQ7O8/F0BcHkBx\\nEdb/y/sp5xN/tHwsKgCh63nWMBf8Sr2dFfHpKt9hmjxtVlwROsynSiPV3/UtRpD8\\nKuQd4LYeBv3r8OAvtsTOBP4cbT7g55unokb8+gtQbakBWqw4OQWz8kvqmztkwa5l\\ndqf+T5s5F4aBvR50aFet06QDJiTTFM8GgRfsyS2NW8DNk00fyKo4LvqTAIRuBs77\\nK4p4ultsVfMMAlMoOIdRGW3x08dkqyjQDSzLKuutYxxQqic3jw4cBiGlRbvKIm2s\\nu+T0sXag+fxkfX1JcWDJ9gkMnHEBe4HKWqA5Yu2mPfUBlccFL7z8qUkuAo8J7qmn\\nXlWfEYlxAgMBAAECggEABjXpIMqFywzO1SmfDZI3vrl01/WrlpbsNQQq0fd4p6qq\\nSoeMuOHJ9dxOt57KTkC3UXn3ZCx3TMvn3a6W3QLquwmwqwJFETZjY7LKEvzJg4jZ\\nXksxgJjUkEtKwVCZ7cTfzjDVeQXS+0PjjdySYW8W51DvRjMNEY8BJuVZc3sYe+Up\\nYbIFVt88McXhhzf3LiJsjnSHG3vNOBl4haNGUpgHWBcmsaoELzAc9/PRSAXTAtpK\\na12C3eXjp2YkGC1SymjaQi4rfKCo3ssjjeHPkYG7O90qpGA94FTO/MjSXAt09CJc\\nFHaBQMBrtP6Ba+O1TYnfTif3pTxoP6MAARZPc+EBuQKBgQDB4dJdnJirAuvIgXjY\\npuU+nzoZCJ9OM325NaffbyPoZ95uwkrH9tWGaw45Zfy7I+w++jW+zStkVub3EHfv\\nUCu2vmXoVGjA4ieuTaVXqxZ8WsHET3rpTOSJZogG3vHyZu0Y/3p8LgH8ckvUzrzD\\nAUh/d2VO2YrXii6K8o44J7YsFQKBgQC/W7OZ6yQPx6OUehcLxnL3knojq5E6+kGQ\\nubqLjnSAOT6FWmDq7ehA1e3smK7NHTbvhKGsqtgldcTLTOtZyoaCtJdd7orl5mIs\\nV3GSUUfHlc3jzuzFEK2JK++FrVdF5iNgpos2kgorXZU0QvhDY40a+K88cSz5Qtaf\\nIZ6dsxxS7QKBgEpCAL2D9eFB7CbOmAd63MDAYZMm/0UHabbfOx3TxpAEAGixl8zA\\n2IV0SW1oh4TxvFFeqekbUexLcIjeUOFi9ms2v3ddpQWDWSlkadiLGcClTiOvQWdL\\n4RhTIfRnvYlXLKZva21WfcI/0JWAXUHfIvJXO4EYxxnIvgzP2/LcGuUFAoGALgjL\\nfh25QSqPyapNXQHSqohMZcRiXcPSfhSQMf6FPEuJJN+HLT0qgF52rFJWyvVaP+iy\\nVC2NbgWU4CuS+rjj/xGe0HQQahowR+aJZd+z2unI7CLdhtLqTHOLXadDtYcVv6yR\\nFQGw/Jm4ySfLPbr3OSPWVD4NybEwnHukAuAR0kkCgYEAhk4zpNdfh87fPf5+z69F\\n6+4PByXzeBnB3r/ctvxiWEcxphjozm6nDWSwQxp2Y6aCE3pwNGyF/VDQ9FCrq5I7\\n4CMacvlKQnF8o4h/bwrKeocL9XQPeDNfyzkTIqb4n8IEiGsvpHGyaTX42n717uI5\\nDkw1BANVv1NnCL97rchdGzA=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-9ja85@finalprojbrgymanagementnotifs.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"107663118826576265462\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-9ja85%40finalprojbrgymanagementnotifs.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";

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
