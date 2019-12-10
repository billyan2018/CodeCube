package codecube.core;

public class PhpScriptAnalyzerExecutorTest extends AbstractAnalyzerExecutorTest {

  @Override
  String languageCode() {
    return "php";
  }

  @Override
  String validExampleCode() {
    return "<?php\n" +
      "    // var arr = [1, 2, 3];\n" +
      "    function add($a, $b) {\n" +
      "        $sum = $a + $b;\n" +
      "        return $sum;\n" +
      "    }\n" +
      "    ?>";
  }

  @Override
  String invalidExampleCode() {
    return "<?php";
  }

  @Override
  int issueCount() {
    return 2;
  }

  @Override
  int highlightingCount() {
    return 3;
  }

  @Override
  int symbolRefCount() {
    return 4;
  }
}
