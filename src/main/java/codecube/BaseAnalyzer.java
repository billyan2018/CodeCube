package codecube;

import codecube.core.Analyzer;
import codecube.core.AnalyzerResult;
import codecube.core.InputFileExtensions;
import codecube.core.LanguagePlugin;
import java.io.IOException;
import java.nio.file.Path;

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

}
