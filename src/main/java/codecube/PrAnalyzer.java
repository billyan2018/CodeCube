package codecube;

import codecube.utils.GitHubRetriever;
import com.fasterxml.jackson.databind.ObjectMapper;
import codecube.core.AnalyzerResult;
import codecube.domain.PullFile;
import java.io.IOException;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

@Slf4j
@RequiredArgsConstructor
public class PrAnalyzer {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, BaseAnalyzer> ANALYZERS;

    private final String githubToken;
    private final String prUrl;

    static {
        Map<String, BaseAnalyzer> temp = new HashMap<>();
        temp.put("java", new JavaAnalyzer());

        ANALYZERS = Collections.unmodifiableMap(temp);
    }

    List<PullFile> retrieveFiles() throws IOException {
        String host = prUrl.replace("https://github.com/", "https://api.github.com/repos/")
                .replace("/pull/","/pulls/");
        if (!host.endsWith("/files")) {
            host = host + "/files";
        }
        String body = sendGetRequest(host);

        PullFile[] array = mapper.readValue(body, PullFile[].class);
        return Arrays.asList(array);
    }

    String preparePullRequestFile(PullFile file) throws IOException {

        String rawUrl = file.getRawUrl()
                .replace("github.com", "raw.githubusercontent.com")
                .replace("/raw/", "/");
        return sendGetRequest(rawUrl);
    }

    private void proceed() throws IOException {
        List<PullFile> files = retrieveFiles();
        for (PullFile file : files) {
            System.out.println("======" + file.getFilename());
            String fileExtension = FilenameUtils.getExtension(file.getFilename());
            BaseAnalyzer analyzer = ANALYZERS.get(fileExtension);
            if (null == analyzer) {
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
                       if (!"Package declaration should match source file directory".equals(issue.getRuleName())) {
                           System.out.println(symbol
                                   + "[" + issue.getStartLine()
                                   + "~" + issue.getEndLine() + "]:"
                                   + issue.getSeverity() + ":"
                                   + issue.getType() + ":"
                                   + issue.getRuleName());

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

    private String sendGetRequest(String url) throws IOException {
        return new GitHubRetriever(githubToken, url).sendGetRequest();
    }



    public static void main(String[] args) throws IOException {
        log.warn("PR:" + args[1]);
        PrAnalyzer prAnalyzer = new PrAnalyzer(args[0], args[1]);
        prAnalyzer.proceed();
    }
}
