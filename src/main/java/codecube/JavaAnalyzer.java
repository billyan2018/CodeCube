package codecube;

import codecube.core.AnalyzerResult;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaAnalyzer extends BaseAnalyzer {


    public static void main(String[] args) {
        final JavaAnalyzer javaAnalyzer = new JavaAnalyzer();
        AnalyzerResult result =  javaAnalyzer.analyze(sampleCode());

        result.issues().forEach(
                item -> System.out.println(
                        "" + item.getStartLine() + ":" + item.getRuleName()
                )
        );
    }

    @Override
    String language() {
        return "java";
    }

    @Override
    Path findPluginFile() {
        return Paths.get("C:/Users/biyan/Documents/tools/CodeCube/codecube/plugins/"
                + "sonar-java-plugin-4.9.0.9858.jar");
    }

    @Override
    String fileExtension() {
        return "java";
    }

    private static String sampleCode() {
        return "public class Hello {\n"
                + "  private int add_number(int a, int b) {\n"
                + " int c = 100; "
                + "    return a + b;\n"
                + "  }\n"
                + "}\n";
    }
}
