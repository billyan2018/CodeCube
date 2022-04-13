package codecube.core;

import lombok.RequiredArgsConstructor;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.highlighting.internal.SyntaxHighlightingRule;
import org.sonarsource.sonarlint.core.StandaloneSonarLintEngineImpl;
import org.sonarsource.sonarlint.core.client.api.common.LogOutput;
import org.sonarsource.sonarlint.core.client.api.common.analysis.*;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneGlobalConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;

import javax.annotation.Nullable;
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
    Map<String, String> extraProperties =
            Collections.singletonMap("sonar.java.binaries",".");
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


    SymbolRefsListener symbolRefsListener = new SymbolRefsListener() {
      @Override
      public void handle(Map<TextRange, Set<TextRange>> referencesBySymbol) {
        // ignore
      }
    };

    AnalysisErrorsListener analysisErrorsListener = new AnalysisErrorsListener() {
      @Override
      public void handle(@Nullable String message, @Nullable TextPointer location) {
        // ignore
      }
    };

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
