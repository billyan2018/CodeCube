package codecube.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisErrorImpl;

/**
 * A helper class to translate original AnalysisError objects
 * into a form more suitable for display on Instalint
 */
class AnalysisErrorTranslator {

  private static final Pattern PARSE_ERROR_PATTERN = Pattern.compile("^Parse error at line (\\d+) column (\\d+):.*",
          Pattern.DOTALL);

  AnalysisError translate(AnalysisError analysisError) {
    String origMessage = analysisError.message();
    if (origMessage == null) {
      return analysisError;
    }

    Matcher matcher = PARSE_ERROR_PATTERN.matcher(origMessage);
    if (!matcher.matches()) {
      return analysisError;
    }

    int line = Integer.parseInt(matcher.group(1));
    int column = Integer.parseInt(matcher.group(2));
    String message = String.format("Parse error at line %d column %d", line, column);
    TextPointer location = new DefaultTextPointer(line, column - 1);
    return new AnalysisErrorImpl(message, location);
  }
}
