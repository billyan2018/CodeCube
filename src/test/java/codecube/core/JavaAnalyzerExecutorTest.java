package codecube.core;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaAnalyzerExecutorTest extends AbstractAnalyzerExecutorTest {

  @Override
  String languageCode() {
    return "java";
  }

  @Override
  String validExampleCode() {
    return "public class Hello {\n" 
    +   "  private int add(int a, int b) {\n"
    +  "    return a + b;\n"
    +  "  }\n"
    +  "}\n";
  }

  @Override
  String invalidExampleCode() {
    return "function hello(";
  }

  @Override
  int issueCount() {
    return 2;
  }

  @Override
  int highlightingCount() {
    return 7;
  }

  @Override
  int symbolRefCount() {
    return 4;
  }

  @Override
  Path findPluginFile() {

    return Paths.get("sonar-java-plugin-4.9.0.9858.jar");
  }
}
