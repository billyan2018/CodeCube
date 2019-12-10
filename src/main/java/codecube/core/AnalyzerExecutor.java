package codecube.core;

public interface AnalyzerExecutor {
  AnalyzerResult execute(LanguagePlugin languagePlugin, String code);
}
