package codecube.core;

import lombok.RequiredArgsConstructor;
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
    Map<String, String> extraProperties =
            Collections.singletonMap("sonar.java.binaries",".");
    StandaloneAnalysisConfiguration config = new StandaloneAnalysisConfiguration(
            baseDir.toPath(),
            inputFiles,
            extraProperties);

    LogOutput logOutput = (formattedMessage, level) -> {
    };


    HighlightingListener highlightingListener = highlighting -> {
      // ignore
    };


    SymbolRefsListener symbolRefsListener = referencesBySymbol -> {
      // ignore
    };

    AnalysisErrorsListener analysisErrorsListener = (message, location) -> {
      // ignore
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
