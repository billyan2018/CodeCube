package codecube.core;

import lombok.RequiredArgsConstructor;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.highlighting.internal.SyntaxHighlightingRule;
import org.sonarsource.sonarlint.core.StandaloneSonarLintEngineImpl;
import org.sonarsource.sonarlint.core.client.api.common.LogOutput;
import org.sonarsource.sonarlint.core.client.api.common.analysis.*;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneGlobalConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FileBasedAnalyzerExecutor implements AnalyzerExecutor {

  private final File baseDir;
  private final LanguagePlugin languagePlugin;
  private final List<String> paths;

  @Override
  public void execute(IssueListener listener) {
    StandaloneGlobalConfiguration globalConfig =
            StandaloneGlobalConfiguration.builder()
                    .addPlugin(languagePlugin.getUrl())
                    .build();
    StandaloneSonarLintEngine engine = new StandaloneSonarLintEngineImpl(globalConfig);

    List<ClientInputFile> inputFiles = paths
            .stream()
            .map(FileBasedAnalyzerExecutor:: buildInputFile)
            .collect(Collectors.toList());
    Map<String, String> extraProperties = Collections.emptyMap();//Collections.singletonMap("sonar.java.binaries", "**/classes");
    StandaloneAnalysisConfiguration config = new StandaloneAnalysisConfiguration(
            baseDir.toPath(),
            inputFiles,
            extraProperties);

    LogOutput logOutput = (formattedMessage, level) -> {
    };


    HighlightingListener highlightingListener = new HighlightingListener() {
      public void handle(List<SyntaxHighlightingRule> highlighting) {
      }
    };

    Map<TextRange, Set<TextRange>> symbolRefs = new HashMap<>();
    SymbolRefsListener symbolRefsListener = symbolRefs::putAll;

    List<AnalysisError> errors = new ArrayList<>();
    AnalysisErrorsListener analysisErrorsListener = (message, location) -> errors.add(new AnalysisErrorImpl(message, location));

    engine.analyze(
            config,
            listener,
            highlightingListener,
            symbolRefsListener,
            analysisErrorsListener,
            logOutput);
  }

  private static ClientInputFile buildInputFile(String path) {
    return new BufferedInputFile(path);
  }
}
