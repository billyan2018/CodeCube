package codecube.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullFile {

    private static final Pattern SEPERATOR_COMMA = Pattern.compile(",");
    private static final Pattern SEPERATOR_SPACE = Pattern.compile(" ");

    private final String sha;
    private final String filename;
    private final String rawUrl;
    private final String patch;

    @JsonCreator
    public PullFile(@JsonProperty("sha") final String sha,
            @JsonProperty("filename") final String filename,
            @JsonProperty("raw_url") final String rawUrl,
            @JsonProperty("patch") String patch) {
        this.sha = sha;
        this.filename = filename;
        this.rawUrl = rawUrl;
        this.patch = patch;
    }

    public Set<Integer> changedLines() {
        String[] lines = patch.split("\n");
        Set<Integer> changedLines = new HashSet<>();
        int start = 0;
        for (String line: lines) {

            if (line.startsWith("@@")) {
                String trimmed = line.replaceAll("@", "").trim();
                String[] parts = SEPERATOR_SPACE.split(trimmed);
                if (parts.length < 2) {
                    continue;
                }
                String lastPart = parts[1];
                String[] numbers = SEPERATOR_COMMA.split(lastPart);

                start = Integer.parseInt(numbers[0].substring(1)) ;
                continue;
            } else if (line.startsWith("+")) {
                changedLines.add(start);
            } else if (line.startsWith("-")) {
                continue;
            }
            start ++;
        }
        return changedLines;
    }
}

