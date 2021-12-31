package codecube.core;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonarsource.sonarlint.core.StandaloneSonarLintEngineImpl;
import org.sonarsource.sonarlint.core.client.api.common.LogOutput;
import org.sonarsource.sonarlint.core.client.api.common.analysis.*;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneGlobalConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FileBasedAnalyzerExecutor implements AnalyzerExecutor {

  private final File baseDir;
  private final LanguagePlugin languagePlugin;
  private final List<String> paths;

  @Override
  public AnalyzerResult execute() {
    StandaloneGlobalConfiguration globalConfig =
            StandaloneGlobalConfiguration.builder()
            .addPlugin(languagePlugin.getUrl())
            .build();
    StandaloneSonarLintEngine engine = new StandaloneSonarLintEngineImpl(globalConfig);

    List<ClientInputFile> inputFiles = paths
            .stream()
            .map(FileBasedAnalyzerExecutor:: buildInputFile)
            .collect(Collectors.toList());
    Map<String, String> extraProperties = new HashMap<>();
    extraProperties.put("sonar.java.binaries", "**/classes");



    StandaloneAnalysisConfiguration config = new StandaloneAnalysisConfiguration(
            baseDir.toPath(),
            inputFiles,
            extraProperties);

    List<Issue> issues = new ArrayList<>();
    IssueListener issueListener = issues::add;

    LogOutput logOutput = (formattedMessage, level) -> {
    };

    List<Highlighting> highlightings = new ArrayList<>();
    HighlightingListener highlightingListener = highlighting -> highlighting.forEach(hl -> highlightings.add(new Highlighting() {
      @Override
      public TypeOfText type() {
        return hl.getTextType();
      }

      @Override
      public TextRange textRange() {
        return hl.range();
      }
    }));

    Map<TextRange, Set<TextRange>> symbolRefs = new HashMap<>();
    SymbolRefsListener symbolRefsListener = symbolRefs::putAll;

    List<AnalysisError> errors = new ArrayList<>();
    AnalysisErrorsListener analysisErrorsListener = (message, location) -> errors.add(new AnalysisErrorImpl(message, location));

    engine.analyze(
      config,
      issueListener,
      highlightingListener,
      symbolRefsListener,
      analysisErrorsListener,
      logOutput);

    return new AnalyzerResultImpl(issues, highlightings, symbolRefs, errors);
  }
  private static String loadFile(String path) {
    try {
      return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    } catch (IOException ex) {
      return "";
    }
  }
  private static ClientInputFile buildInputFile(String path) {
    final String code = loadFile(path);

    return new ClientInputFile() {
      @Override
      public String getPath() {
        return path;
      }

      @Override
      public boolean isTest() {
        return path.contains("/test/");
      }

      @Override
      public Charset getCharset() {
        return StandardCharsets.UTF_8;
      }

      @Override
      public InputStream inputStream() {
        return new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
      }

      @Override
      public String contents() {
        return code;
      }
    };
  }
}
