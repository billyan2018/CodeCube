package codecube;

import codecube.domain.PullFile;
import codecube.utils.GitHubRetriever;
import com.google.common.collect.ImmutableMap;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class PrRunner {

    private final Gson gson = new Gson();
    private static final Map<String, BaseAnalyzer> ANALYZERS = ImmutableMap.of("java", new JavaAnalyzer());


    private final String githubToken;
    private final String prUrl;



    List<PullFile> retrieveFiles() throws IOException {
        String host = prUrl.replace("https://github.com/", "https://api.github.com/repos/")
                .replace("/pull/","/pulls/");
        if (!host.endsWith("/files")) {
            host = host + "/files";
        }
        String body = sendGetRequest(host);

        PullFile[] array = gson.fromJson(body, PullFile[].class);
        return Arrays.asList(array);
    }

    String preparePullRequestFile(PullFile file) throws IOException {

        String rawUrl = file.getRawUrl()
                .replace("github.com", "raw.githubusercontent.com")
                .replace("/raw/", "/");
        return sendGetRequest(rawUrl);
    }

    private void proceed() throws IOException {
        /*List<PullFile> files = retrieveFiles();
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
        }*/
    }

    private static boolean isInChange(Collection<Integer> changedLines, int start, int end) {
        return changedLines.stream().anyMatch(item -> item >= start && item <= end);
    }

    private String sendGetRequest(String url) throws IOException {
        return new GitHubRetriever(githubToken, url).sendGetRequest();
    }



    public static void main(String[] args) throws IOException {
        log.warn("PR:" + args[1]);
        PrRunner prAnalyzer = new PrRunner(args[0], args[1]);
        prAnalyzer.proceed();
    }
}
