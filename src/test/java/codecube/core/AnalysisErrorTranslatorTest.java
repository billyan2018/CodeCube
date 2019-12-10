package codecube.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.junit.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.sensor.error.AnalysisError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AnalysisErrorTranslatorTest {

  private final AnalysisErrorTranslator translator = new AnalysisErrorTranslator();

  @Test
  public void should_do_nothing_if_message_is_null() {
    AnalysisError notParseError = newAnalysisError(null, mock(TextPointer.class));
    assertThat(translator.translate(notParseError)).isEqualTo(notParseError);
  }

  @Test
  public void should_do_nothing_if_not_parse_error() {
    AnalysisError notParseError = newAnalysisError("not a parse error", mock(TextPointer.class));
    assertThat(translator.translate(notParseError)).isEqualTo(notParseError);
  }

  @Test
  public void should_translate_parse_errors() {
    int line = 2;
    int column = 14;
    String originalMessage = String.format("Parse error at line %d column %d:whatever", line, column);
    String expectedMessage = String.format("Parse error at line %d column %d", line, column);

    AnalysisError parseError = newAnalysisError(originalMessage, mock(TextPointer.class));
    AnalysisError expected = newAnalysisError(expectedMessage, new DefaultTextPointer(line, column - 1));

    AnalysisError translated = translator.translate(parseError);
    assertThat(translated.message()).isEqualTo(expected.message());
    TextPointer location = translated.location();
    assertThat(location.line()).isEqualTo(expected.location().line());
    assertThat(location.lineOffset()).isEqualTo(expected.location().lineOffset());
  }

  private AnalysisError newAnalysisError(@Nullable String message, TextPointer location) {
    return new AnalysisError() {
      @Override
      public InputFile inputFile() {
        // TODO create new interface that removes this unnecessary field
        return null;
      }

      @CheckForNull
      @Override
      public String message() {
        return message;
      }

      @Override
      public TextPointer location() {
        return location;
      }
    };
  }
}
