package codecube.core;

import java.io.IOException;

public interface AnalyzerExecutor {
  AnalyzerResult execute(LanguagePlugin languagePlugin, String path) throws IOException;
}
