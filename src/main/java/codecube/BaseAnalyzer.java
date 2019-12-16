package codecube;

import codecube.core.Analyzer;
import codecube.core.AnalyzerResult;
import codecube.core.InputFileExtensions;
import codecube.core.LanguagePlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

@Slf4j
abstract class BaseAnalyzer {

    private final LanguagePlugin languagePlugin = newLanguagePlugin();
    private final Analyzer analyzer = new Analyzer(languagePlugin);

    public AnalyzerResult analyze(String source) {
        return analyzer.apply(source);
    }

    private LanguagePlugin newLanguagePlugin() {
        try {
            String inputFileExtension = InputFileExtensions.fromLanguageCode(language());
            return new LanguagePlugin(findPluginFile().toUri().toURL(), inputFileExtension);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // unreachable
        return null;
    }

    abstract String language();

    abstract Path findPluginFile();

    abstract String fileExtension();

    void ensurePlugin(String pluginFile) {
        File dest = new File(pluginFile);
        if (dest.isFile()) {
            return;
        }
        URL inputUrl = JavaAnalyzer.class.getClassLoader().getResource(pluginFile);
        try {
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (IOException ex) {
            log.error("Error with init plugin", ex);
        }
    }

}
