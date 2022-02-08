package codecube.core;

import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CodeBlock {
    private static final int CONTEXT_LINES = 2;
    private static final int MAX_LINES = 10;

    private final BufferedInputFile inputFile;

    @Getter
    private final int highlight;
    @Getter
    private final int start;
    @Getter
    private final int end;
    @Getter
    private final String allLines;
    @Getter
    private final String highlightedLine;

    public CodeBlock(BufferedInputFile inputFile,
                     int begin) {
        this.inputFile = inputFile;
        this.highlight = begin;

        this.start = Math.max(begin - CONTEXT_LINES, 1);
        this.end = Math.min(begin + MAX_LINES + CONTEXT_LINES, begin + CONTEXT_LINES);
        if (inputFile != null) {
            List<String> lines = Collections.unmodifiableList(inputFile.extractBlock(this.start - 1, this.end - 1));
            this.allLines = lines.stream().collect(Collectors.joining("\n"));
            this.highlightedLine = lines.get(this.highlight - this.start);
        } else {
            this.allLines = "";
            this.highlightedLine = "";
        }
    }

    public String toJson() {
        return  "{\n" +
                "      \"highlight\": " + this.highlight + ",\n" +
                "      \"start\": "+ this.start +",\n" +
                "      \"allLines\": \" "+ StringEscapeUtils.escapeJson(this.getAllLines()) + "  \"\n" +
                "    }";
    }
}
