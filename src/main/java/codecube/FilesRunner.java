package codecube;

import codecube.core.BufferedInputFile;
import codecube.core.CodeBlock;
import codecube.core.FileBasedAnalyzerExecutor;
import com.google.common.collect.ImmutableMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.sonar.api.internal.apachecommons.codec.digest.DigestUtils;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

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

        for (Map.Entry<String, BaseAnalyzer> entry: ANALYZERS.entrySet()) {
            String lang = entry.getKey();
            List<String> filesWithLanguage = files.stream().filter(file ->
                    lang.equals(FilenameUtils.getExtension(file))).collect(Collectors.toList());
            if (!filesWithLanguage.isEmpty()) {
                BaseAnalyzer analyzer = entry.getValue();
                FileBasedAnalyzerExecutor executor = new FileBasedAnalyzerExecutor(new File(baseDir),
                        analyzer.getLanguagePlugin(),
                        filesWithLanguage);
                //AnalyzerResult result = executor.execute();
                executor.execute(issue -> {
                    String path = issue.getInputFile().getPath();
                    if (path.startsWith(baseDir)) {
                        path = path.substring(baseDir.length() + 1);
                    }

                    String json = buildJson(path, issue, lang);
                    System.out.print(json + '\uffff');
                });
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

    private static String generateFingerPrint(String path, String ruleCode,String code ) {
        String content = path + ruleCode + code;
        return DigestUtils.sha256Hex(content).toUpperCase(Locale.ROOT);
    }

    private static String safeMessage(String message) {
        int[] jsonSpecialChars = {'\n', '\r', '\"', '\\', '\b', '\f', '\t'};
        boolean containsAny = Arrays.stream(jsonSpecialChars).anyMatch(ch -> message.indexOf(ch) > -1);
        if (containsAny) {
            return StringEscapeUtils.escapeJson(message);
        } else {
            return message;
        }
    }

    private static String buildJson(String path, Issue issue, String lang) {
        String docUrl = generateDocUrl(issue.getRuleKey());
        String desc = safeMessage(issue.getMessage());
        BufferedInputFile inputFile = (BufferedInputFile)issue.getInputFile();
        int begin = issue.getStartLine() == null ? 1 : issue.getStartLine();
        CodeBlock codeBlock = new CodeBlock(inputFile, begin);
        String code = codeBlock.getHighlightedLine();

        String fingerPrint = generateFingerPrint(path, issue.getRuleKey(), code);
        return String.format("{ \"type\":\"issue\",\"engine_name\": \"sonar-%s\","
                + "\"engine_link\": \"https://www.sonarlint.org\","
                + "\"severity\":\"%s\",\"check_name\":\"%s\","
                + "\"check_link\":\"%s\","
                + "\"description\":\"%s\","
                + "\"fingerprint\":\"%s\","
                + "\"code\":%s,"
                + "\"location\":{\"path\":\"%s\","
                + "                \"lines\":{\n"
                + "                    \"begin\":%d,\n"
                + "                    \"end\":%d\n"
                + "                }}}",
                lang,
                issue.getSeverity().toLowerCase(),
                issue.getRuleKey(),
                docUrl,
                desc,
                fingerPrint,
                codeBlock.toJson(),
                path,
                issue.getStartLine(),
                issue.getEndLine()
        );
    }


    public static void main(String[] args) throws IOException {
        List<String> files = new ArrayList<>();

        File listFile = new File("/tmp/sonar.txt");
        try (Scanner scanner = new Scanner(listFile)) {
            while (scanner.hasNextLine()) {
                files.add(scanner.nextLine());
            }
        }
        FilesRunner analyzer = new FilesRunner(files);
        analyzer.proceed(args[0]);
    }
}
