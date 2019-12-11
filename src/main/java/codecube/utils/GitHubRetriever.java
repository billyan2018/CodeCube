package codecube.utils;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;


@RequiredArgsConstructor
public class GitHubRetriever {

    private final String githubToken;
    private final String urlString;

    public String sendGetRequest() throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization",
                "token " + githubToken);
        conn.setRequestProperty("Accept", "application/json");
        conn.addRequestProperty("User-Agent", "Mozilla/4.0");
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("charset", "UTF-8");
        conn.connect();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode()
                    + "Check your github token and ensure you have access to the repo");
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
