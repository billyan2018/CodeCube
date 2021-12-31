package codecube;

import codecube.core.AnalyzerResult;
import codecube.core.FileBasedAnalyzerExecutor;
import com.google.common.collect.ImmutableMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.sonar.api.internal.apachecommons.codec.digest.DigestUtils;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;


import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class FilesRunner {

    private final List<String> files;
    private static final Map<String, BaseAnalyzer> ANALYZERS = ImmutableMap.of(
            "java", new JavaAnalyzer(),
            "py", new PythonAnalyzer()
    );

    private void proceed(String baseDir) {

        for (String lang: ANALYZERS.keySet()) {

            List<String> filesWithLanguage = files.stream().filter(file ->
                    lang.equals(FilenameUtils.getExtension(file))).collect(Collectors.toList());
            if (!filesWithLanguage.isEmpty()) {
                BaseAnalyzer analyzer = ANALYZERS.get(lang);
                FileBasedAnalyzerExecutor executor = new FileBasedAnalyzerExecutor(new File(baseDir),
                        analyzer.getLanguagePlugin(),
                        filesWithLanguage);
                AnalyzerResult result = executor.execute();

                for (Issue issue : result.issues()) {

                    String path = issue.getInputFile().getPath();
                    if (path.startsWith(baseDir)) {
                        path = path.substring(baseDir.length() + 1);
                    }

                    String json = buildJson(path, issue);
                    System.out.print(json + '\uffff');
                }
            }
        }
    }

    private static String generateDocUrl(String issueCode) {
        String docUrl = "";
        if (issueCode.startsWith("python:S")) {
            String code = issueCode.substring("python:S".length());
            docUrl = "https://rules.sonarsource.com/python/RSPEC-" + code;
        } else if (issueCode.startsWith("java:S")) {
            String code = issueCode.substring("java:S".length());
            docUrl = "https://rules.sonarsource.com/java/RSPEC-" + code;
        } else if (issueCode.startsWith("squid:S")) {
            String code = issueCode.substring("squid:S".length());
            docUrl = "https://rules.sonarsource.com/java/RSPEC-" + code;
        }
        return docUrl;
    }

    private static String generateFingerPrint(String path, String startLine, String code) {
        String content = path + startLine + code;
        return DigestUtils.md5Hex(content).toUpperCase(Locale.ROOT);
    }

    private static String buildJson(String path, Issue issue) {
        String docUrl = generateDocUrl(issue.getRuleKey());
        String desc = docUrl
                + " "
                + issue.getMessage()
                .replaceAll("\"","'")
                .replaceAll("\\\\","\\\\\\\\");
        String fingerPrint = generateFingerPrint(path, ""+ issue.getStartLine(), issue.getRuleKey());
        return String.format("{ \"type\":\"issue\",\"engine_name\": \"sonarlint\","
                   + "\"severity\":\"%s\",\"check_name\":\"%s\","
                        + "\"description\":\"%s\","
                        + "\"fingerprint\":\"%s\",\"location\":{\"path\":\"%s\"," +
                        "                \"lines\":{\n" +
                        "                    \"begin\":%d,\n" +
                        "                    \"end\":%d\n" +
                        "                }}}"
                    ,
                issue.getSeverity().toLowerCase(),
                issue.getRuleKey(),
                desc,
                fingerPrint,
                path,
                issue.getStartLine(),
                issue.getEndLine()
        );
    }


    public static void main(String[] args) throws IOException {
        List<String> files = new ArrayList<>();

        File listFile = new File("/tmp/sonar.txt");
        Scanner scanner = new Scanner(listFile);

        while (scanner.hasNextLine()) {
            files.add(scanner.nextLine());
        }
        FilesRunner analyzer = new FilesRunner(files);
        analyzer.proceed(args[0]);
    }
}
