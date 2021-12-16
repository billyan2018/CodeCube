package codecube.core;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.net.URL;

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
