package codecube;

import codecube.core.AnalyzerResult;
import codecube.domain.PullFile;
import codecube.utils.GitHubRetriever;
import com.google.common.collect.ImmutableMap;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        List<PullFile> files = retrieveFiles();
        for (String lang: ANALYZERS.keySet()) {

            List<PullFile> filesWithLanguage = files.stream().filter(file ->
                    lang.equals(FilenameUtils.getExtension(file.getFilename())))
                    .collect(Collectors.toList());
            if (!filesWithLanguage.isEmpty()) {
                BaseAnalyzer analyzer = ANALYZERS.get(lang);
                List<String> filePaths = filesWithLanguage
                        .stream()
                        .map(file -> file.getFilename())
                        .collect(Collectors.toList());
                AnalyzerResult result = analyzer.analyze("/tmp/", filePaths);
                for (PullFile file: filesWithLanguage) {
                    List<Issue> issues = result
                            .issues()
                            .stream()
                            .filter(item -> item.getInputFile().getPath().equals(file.getFilename()))
                            .collect(Collectors.toList());

                    Set<Integer> changedLines = file.changedLines();

                    if (!issues.isEmpty()) {
                        System.out.println("======" + file.getFilename() + ":" + issues.size());
                        issues.forEach(
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

                                });
                    }
                }


            }
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
        PrRunner prAnalyzer = new PrRunner(args[0], args[1]);
        prAnalyzer.proceed();
    }
}
