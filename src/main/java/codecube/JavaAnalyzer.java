package codecube;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
        ensurePlugin();
        return Paths.get(new File(PLUG_IN_FILE).getAbsolutePath());
    }

    @Override
    String fileExtension() {
        return "java";
    }


    private void ensurePlugin() {
        File dest = new File(PLUG_IN_FILE);
        if (dest.isFile()) {
            return;
        }
        URL inputUrl = JavaAnalyzer.class.getClassLoader().getResource(PLUG_IN_FILE);
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            log.error("Error with init plugin", ex);
        }
    }
}
