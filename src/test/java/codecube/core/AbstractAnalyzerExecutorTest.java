package codecube.core;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public abstract class AbstractAnalyzerExecutorTest {
  

  private  AnalyzerExecutor executor = new AnalyzerExecutorImpl();

   final LanguagePlugin languagePlugin = newLanguagePlugin();

  private LanguagePlugin newLanguagePlugin() {
    try {
      String languageCode = languageCode();
      String inputFileExtension = InputFileExtensions.fromLanguageCode(languageCode);
      return new LanguagePlugin(findPluginFile().toUri().toURL(), inputFileExtension);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    // unreachable
    return null;
  }

  abstract Path findPluginFile();

  abstract String languageCode();

  abstract String validExampleCode();

  abstract String invalidExampleCode();

  abstract int issueCount();

  abstract int highlightingCount();

  abstract int symbolRefCount();

  @Test
  @Ignore
  public void should_report_issues() {
    AnalyzerResult result = execute(validExampleCode());
    assertThat(result.success()).isTrue();
    assertThat(result.errors()).isEmpty();
    assertThat(result.issues()).hasSize(issueCount());
  }

  @Test
  @Ignore
  public void should_report_highlightings() {
    AnalyzerResult result = execute(validExampleCode());
    assertThat(result.success()).isTrue();
    assertThat(result.errors()).isEmpty();
    assertThat(result.highlightings()).hasSize(highlightingCount());
  }

  @Test
  @Ignore
  public void should_report_symbol_refs() {
    AnalyzerResult result = execute(validExampleCode());
    assertThat(result.success()).isTrue();
    assertThat(result.errors()).isEmpty();
    assertThat(result.symbolRefs()).hasSize(symbolRefCount());
  }

  @Test
  @Ignore
  public void should_report_analysis_failed() {
    AnalyzerResult result = execute(invalidExampleCode() + validExampleCode());
    assertThat(result.success()).isFalse();
    assertThat(result.errors()).isNotEmpty();
    assertThat(result.errors().get(0).message()).isNotNull();
    // TODO let implementations return expected location of first parsing error
    //assertThat(result.errors().get(0).location()).isNotNull();
    //assertThat(result.errors().get(0).location().line()).isGreaterThanOrEqualTo(1);
    //assertThat(result.errors().get(0).location().lineOffset()).isGreaterThanOrEqualTo(0);

    assertThat(result.issues()).isEmpty();
    assertThat(result.highlightings()).isEmpty();
    assertThat(result.symbolRefs()).isEmpty();
  }

  private AnalyzerResult execute(String code) {
    return executor.execute(languagePlugin, code);
  }
}
