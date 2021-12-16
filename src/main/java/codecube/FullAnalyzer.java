package codecube;

import codecube.core.AnalyzerResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class FullAnalyzer {

    private final List<String> files;
    private final Gson gson = new Gson();

    private static final Map<String, BaseAnalyzer> ANALYZERS = ImmutableMap.of("java", new JavaAnalyzer());


    private void proceed() throws IOException {
        List<AnalyzerResult> results = new ArrayList<>();
        for (String path : files) {
            String fileExtension = FilenameUtils.getExtension(path);
            BaseAnalyzer analyzer = ANALYZERS.get(fileExtension);
            if (null == analyzer) {
                continue;
            }

            String source = FileUtils.readFileToString(new File(path), "utf-8");
            AnalyzerResult result = analyzer.analyze(source);
            results.add(result);


        }
        System.out.print(gson.toJson(results));
    }

    public static void main(String[] args) throws IOException {
        List<String> files = //Arrays.asList(args);
                ImmutableList.of("/Users/biyan/Desktop/CodeCube/src/main/java/codecube/FullAnalyzer.java",
                        "/Users/biyan/Desktop/CodeCube/src/main/java/codecube/BaseAnalyzer.java");
        FullAnalyzer analyzer = new FullAnalyzer(files);
        analyzer.proceed();
    }
}
