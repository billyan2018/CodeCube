package codecube.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface AnalyzerExecutor {
  AnalyzerResult execute(File baseDir,
                         LanguagePlugin languagePlugin,
                         List<String> paths);
}
