package codecube.core;


import codecube.domain.PullFileContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PullFileBasedAnalyzerExecutor implements AnalyzerExecutor {

  private final LanguagePlugin languagePlugin;
  private final List<PullFileContent> files;

  public void execute(IssueListener issueListener) {
    StandaloneGlobalConfiguration globalConfig =
            StandaloneGlobalConfiguration.builder()
            .addPlugin(languagePlugin.getUrl())
            .build();
    StandaloneSonarLintEngine engine = new StandaloneSonarLintEngineImpl(globalConfig);

    List<ClientInputFile> inputFiles = files
            .stream()
            .map(PullFileBasedAnalyzerExecutor:: buildInputFile)
            .collect(Collectors.toList());
    Map<String, String> extraProperties = new HashMap<>();
    extraProperties.put("sonar.java.binaries", "**/classes");



    StandaloneAnalysisConfiguration config = new StandaloneAnalysisConfiguration(
            FileSystems.getDefault().getPath("/tmp"),
            inputFiles,
            extraProperties);



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
      null,
      logOutput);

  }



  private static ClientInputFile buildInputFile(PullFileContent file) {


    return new ClientInputFile() {
      @Override
      public String getPath() {
        return file.getFilename();
      }

      @Override
      public boolean isTest() {
        return file.getFilename().contains("/test/");
      }

      @Override
      public Charset getCharset() {
        return StandardCharsets.UTF_8;
      }

      @Override
      public InputStream inputStream() {
        return new ByteArrayInputStream(file.getContent().getBytes(StandardCharsets.UTF_8));
      }

      @Override
      public String contents() {
        return file.getContent();
      }
    };
  }
}
