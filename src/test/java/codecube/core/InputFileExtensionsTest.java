package codecube.core;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InputFileExtensionsTest {
  @Test
  public void should_return_js_for_javascript() {
    Assertions.assertThat(InputFileExtensions.fromLanguageCode("javascript")).isEqualTo("js");
  }

  @Test
  public void should_return_self_for_nonexistent() {
    Assertions.assertThat(InputFileExtensions.fromLanguageCode("foo")).isEqualTo("foo");
    Assertions.assertThat(InputFileExtensions.fromLanguageCode("bar")).isEqualTo("bar");
    Assertions.assertThat(InputFileExtensions.fromLanguageCode("baz")).isEqualTo("baz");
  }
}
