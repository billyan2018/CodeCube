package codecube;

import codecube.core.InputFileExtensions;
import codecube.core.LanguagePlugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

//add a comment line
@Slf4j
abstract class BaseAnalyzer {

    @Getter
    private final LanguagePlugin languagePlugin = newLanguagePlugin();

    private LanguagePlugin newLanguagePlugin() {
        try {

            String inputFileExtension = InputFileExtensions.fromLanguageCode(language());
            return new LanguagePlugin(findPluginFile(), inputFileExtension);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        // unreachable
        return null;
    }

    abstract String language();

    abstract String pluginFilePath();

    URL findPluginFile() throws URISyntaxException, MalformedURLException {
        String jarPath = pluginFilePath();
        String destPath = ensurePlugin(jarPath);


        return new URL("file://" + destPath);
    }
    private  String ensurePlugin(String pluginFile) {
        String destPath = "/tmp/" + pluginFile;
        File dest = new File(destPath);
        if (dest.isFile()) {
            return destPath;
        }
        URL inputUrl = BaseAnalyzer.class.getClassLoader().getResource(pluginFile);
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            log.error("Error with init plugin", ex);
        }
        return destPath;
    }


}
