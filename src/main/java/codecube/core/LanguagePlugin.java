package codecube.core;

import java.net.URL;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class LanguagePlugin {

  private final URL url;
  private final String inputFileExtension;

  public LanguagePlugin(URL url, String inputFileExtension) {
    this.url = url;
    this.inputFileExtension = inputFileExtension;
  }

  URL getUrl() {
    return url;
  }

  String getInputFileExtension() {
    return inputFileExtension;
  }
}
