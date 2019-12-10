package codecube.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonarsource.sonarlint.core.StandaloneSonarLintEngineImpl;
import org.sonarsource.sonarlint.core.client.api.common.LogOutput;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisErrorImpl;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisErrorsListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Highlighting;
import org.sonarsource.sonarlint.core.client.api.common.analysis.HighlightingListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;
import org.sonarsource.sonarlint.core.client.api.common.analysis.IssueListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.SymbolRefsListener;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneGlobalConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;

public class AnalyzerExecutorImpl implements AnalyzerExecutor {

  private static Path newDir(Path path) throws IOException {
    return Files.createDirectories(path);
  }

  private static Path newTempDir() throws IOException {
    return Files.createTempDirectory("sonarlint-");
  }

  @Override
  public AnalyzerResult execute(LanguagePlugin languagePlugin, String code) {
    StandaloneGlobalConfiguration globalConfig = StandaloneGlobalConfiguration.builder()
            .addPlugin(languagePlugin.getUrl())
            .build();
    StandaloneSonarLintEngine engine = new StandaloneSonarLintEngineImpl(globalConfig);

    Path tmp;
    try {
      tmp = newTempDir();
    } catch (IOException e) {
      throw new IllegalStateException("Could not create temp dir");
    }

    Path workDir;
    try {
      workDir = newDir(tmp.resolve("work"));
    } catch (IOException e) {
      throw new IllegalStateException("Could not create workdir");
    }

    Path path = tmp.resolve("code." + languagePlugin.getInputFileExtension());

    ClientInputFile clientInputFile = new ClientInputFile() {
      @Override
      public String getPath() {
        return path.toString();
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
    StandaloneAnalysisConfiguration config = new StandaloneAnalysisConfiguration(workDir, inputFiles, extraProperties);

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

    return new AnalyzerResult() {
      @Override
      public List<Issue> issues() {
        return issues;
      }

      @Override
      public List<Highlighting> highlightings() {
        return highlightings;
      }

      @Override
      public Map<TextRange, Set<TextRange>> symbolRefs() {
        return symbolRefs;
      }

      @Override
      public List<AnalysisError> errors() {
        return errors;
      }

      @Override
      public boolean success() {
        return errors.isEmpty();
      }
    };
  }
}
