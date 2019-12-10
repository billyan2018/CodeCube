package codecube.core;

import org.junit.Ignore;

public class PythonScriptAnalyzerExecutorTest extends AbstractAnalyzerExecutorTest {

  @Override
  String languageCode() {
    return "python";
  }

  @Override
  String validExampleCode() {
    return "def addNumbers(a, b):\n" +
      "    return a + b";
  }

  @Override
  String invalidExampleCode() {
    return "def add(";
  }

  @Override
  int issueCount() {
    return 1;
  }

  @Override
  int highlightingCount() {
    return 2;
  }

  @Override
  int symbolRefCount() {
    return -1;
  }

  @Ignore
  @Override
  public void should_report_symbol_refs() {
    // TODO SonarPython doesn't report symbol refs!!! :-(
  }

  @Ignore
  @Override
  public void should_report_analysis_failed() {
    // TODO SonarPython doesn't report analysis errors !!! :-(
  }
}
