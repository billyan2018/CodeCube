package codecube;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JSAnalyzer extends BaseAnalyzer {

    private static final String  PLUG_IN_FILE= "sonar-javascript-plugin-3.3.0.5702.jar";
    @Override
    String language() {
        return "javascript";
    }

    @Override
    Path findPluginFile() {
        ensurePlugin(PLUG_IN_FILE);
        return Paths.get(PLUG_IN_FILE);
    }

    @Override
    String fileExtension() {
        return "js";
    }

}
