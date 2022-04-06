package codecube;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

// another line
@Slf4j
class JavaAnalyzer extends BaseAnalyzer {

    private static final String  PLUGIN_FILE = "sonar-java-plugin-4.14.0.11784.jar";
    @Override
    String language() {
        return "java";
    }

    @Override
    String pluginFilePath() {
        return PLUGIN_FILE;
    }


}
