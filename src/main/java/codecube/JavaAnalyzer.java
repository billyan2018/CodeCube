package codecube;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
class JavaAnalyzer extends BaseAnalyzer {

    private static final String  PLUG_IN_FILE= "sonar-java-plugin-4.9.0.9858.jar";
    @Override
    String language() {
        return "java";
    }

    @Override
    Path findPluginFile() {
        ensurePlugin(PLUG_IN_FILE);
        return Paths.get(new File(PLUG_IN_FILE).getAbsolutePath());
    }

    @Override
    String fileExtension() {
        return "java";
    }

}
