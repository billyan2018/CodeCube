package codecube.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GitHubRetriever {

    public static String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization",
                "token " + GitHubToken.TOKEN);
        conn.setRequestProperty("Accept", "application/json");
        conn.addRequestProperty("User-Agent", "Mozilla/4.0");
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("charset", "UTF-8");
        conn.connect();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        } else {
            StringBuilder response = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine() + "\n");
                }
            }
            return response.toString();
        }
    }
}
