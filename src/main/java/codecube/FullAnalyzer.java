package codecube;

import codecube.core.AnalyzerResult;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class FullAnalyzer {

    private final List<String> files;
    private final Gson gson = new Gson();

    private static final Map<String, BaseAnalyzer> ANALYZERS = ImmutableMap.of("java", new JavaAnalyzer());


    private void proceed() throws IOException {
        List<Map<String, String>> results = new ArrayList<>();
        for (String path : files) {
            String fileExtension = FilenameUtils.getExtension(path);
            BaseAnalyzer analyzer = ANALYZERS.get(fileExtension);
            if (null == analyzer) {
                continue;
            }


            AnalyzerResult result = analyzer.analyze(path);
            Map<String, String> info = new HashMap<>();
            result.issues().forEach(issue -> {
                info.put("path", path);
                info.put("severity", issue.getSeverity());
                info.put("code", issue.getRuleKey());
                info.put("message", issue.getMessage());
                info.put("start_line", issue.getStartLine().toString());
                info.put("end_line", issue.getEndLine().toString());
                results.add(info);
            });
        }
        System.out.print(gson.toJson(results));
    }

    public static void main(String[] args) throws IOException {
        List<String> files = Arrays.asList(args);
        FullAnalyzer analyzer = new FullAnalyzer(files);
        analyzer.proceed();
    }
}
