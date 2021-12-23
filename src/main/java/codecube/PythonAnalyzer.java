package codecube;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

// another line
@Slf4j
class PythonAnalyzer extends BaseAnalyzer {

    private static final String  PLUG_IN_FILE= "sonar-python-plugin-1.8.0.1496.jar";
    @Override
    String language() {
        return "python";
    }

    @Override
    Path findPluginFile() {
        ensurePlugin(PLUG_IN_FILE);
        return Paths.get(new File(PLUG_IN_FILE).getAbsolutePath());
    }

}
