package codecube;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
class EmptyAnalyzer extends BaseAnalyzer {
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
        String notUsed = "something new";
        return "java";
    }
}
