package codecube.core;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AnalyzerExecutorImpl implements AnalyzerExecutor {



  @Override
  public AnalyzerResult execute(LanguagePlugin languagePlugin, String path) throws IOException {
    StandaloneGlobalConfiguration globalConfig = StandaloneGlobalConfiguration.builder()
            .addPlugin(languagePlugin.getUrl())
            .build();
    StandaloneSonarLintEngine engine = new StandaloneSonarLintEngineImpl(globalConfig);

    final String code = FileUtils.readFileToString(new File(path), "utf-8");
    ClientInputFile clientInputFile = new ClientInputFile() {
      @Override
      public String getPath() {
        return path;
      }

      @Override
      public boolean isTest() {
        return false;
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

    Iterable<ClientInputFile> inputFiles = Collections.singleton(clientInputFile);

    Map<String, String> extraProperties = new HashMap<>();
    StandaloneAnalysisConfiguration config = new StandaloneAnalysisConfiguration(Files.createTempDirectory("sonarlint-"), inputFiles, extraProperties);

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
}
