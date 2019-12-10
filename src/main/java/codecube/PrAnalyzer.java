package codecube;

import static codecube.utils.GitHubRetriever.sendGetRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import codecube.core.AnalyzerResult;
import codecube.domain.PullFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrAnalyzer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JavaAnalyzer analyzer = new JavaAnalyzer();

    List<PullFile> retrieveFiles(String prUrl) throws IOException {
        String host = prUrl.replace("https://github.com/", "https://api.github.com/repos/")
                .replace("/pull/","/pulls/");
        if (!host.endsWith("/files")) {
            host = host + "/files";
        }
        String body = sendGetRequest(host);

        PullFile[] array = mapper.readValue(body, PullFile[].class);
        return Arrays.asList(array);
    }

    static String preparePullRequestFile(PullFile file) throws IOException {

        String rawUrl = file.getRawUrl()
                .replace("github.com", "raw.githubusercontent.com")
                .replace("/raw/", "/");
        return sendGetRequest(rawUrl);
    }

    private void proceed(String prUrl) throws IOException {
        List<PullFile> files = retrieveFiles(prUrl);
        for (PullFile file : files) {
            System.out.println("======" + file.getFilename());
            if (!file.getFilename().endsWith(analyzer.fileExtension())) {
                continue;
            }

            String source = preparePullRequestFile(file);
            AnalyzerResult result = analyzer.analyze(source);
            Set<Integer> changedLines = file.changedLines();
            System.out.println("======" + file.getFilename() + ":" + result.issues().size());
            result.issues().forEach(
                   issue -> {
                       boolean inScope = isInChange(changedLines, issue.getStartLine(), issue.getEndLine());

                       String symbol = inScope ? "Y" : "N";
                       if (inScope) {
                           System.out.println(symbol + "[" + issue.getStartLine() + "~" + issue.getEndLine() + "]:" + issue.getRuleName());
                       }
                   }
            );
            result.errors().forEach(analysisError ->
                    System.err.println("" + analysisError.location() + ":" + analysisError.message())

            );
        }
    }

    private static boolean isInChange(Collection<Integer> changedLines, int start, int end) {
        return changedLines.stream().anyMatch(item -> item >= start && item <= end);
    }

    public static void main(String[] args) throws IOException {

        PrAnalyzer prAnalyzer = new PrAnalyzer();
        prAnalyzer.proceed("https://github.com/kingland-systems/mfps/pull/676/files");
    }
}
