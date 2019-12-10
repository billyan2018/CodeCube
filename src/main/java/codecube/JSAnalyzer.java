package codecube;

import codecube.core.AnalyzerResult;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSAnalyzer extends BaseAnalyzer {

    public static void main(String[] args) {
        final JSAnalyzer analyzer = new JSAnalyzer();
        AnalyzerResult result =  analyzer.analyze(sampleCode());

        result.issues().forEach(
                item -> System.out.println(
                        "" + item.getStartLine() + ":" + item.getRuleName()
                )
        );
    }

    @Override
    String language() {
        return "javascript";
    }

    @Override
    Path findPluginFile() {
        return Paths.get("/Users/work/Documents/Tools/java-libs/sonar-java/"
                + "sonar-javascript-plugin-3.3.0.5702.jar");
    }

    @Override
    String fileExtension() {
        return "js";
    }

    private static String sampleCode() {
        return "const items: <any>[] = [];\n" + "items.push('aaa');\n" + "console.log(items)";
    }
}
