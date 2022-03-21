package codecube.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public class PullFile {

    private static final Pattern SEPARATOR_COMMA = Pattern.compile(",");
    private static final Pattern SEPARATOR_SPACE = Pattern.compile(" ");

    private final String sha;
    private final String filename;

    @SerializedName("raw_url")
    private final String rawUrl;
    private final String patch;

    public Set<Integer> changedLines() {
        if (patch == null) {
            return Collections.emptySet();
        }
        String[] lines = patch.split("\n");
        Set<Integer> changedLines = new HashSet<>();
        int start = 0;
        for (String line: lines) {

            if (line.startsWith("@@")) {
                String trimmed = line.replaceAll("@", "").trim();
                String[] parts = SEPARATOR_SPACE.split(trimmed);
                if (parts.length < 2) {
                    continue;
                }
                String lastPart = parts[1];
                String[] numbers = SEPARATOR_COMMA.split(lastPart);

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

