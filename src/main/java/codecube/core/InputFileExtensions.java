package codecube.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InputFileExtensions {

  private static final Map<String, String> LANGUAGE_MAP;

  static {
    Map<String, String> mappings = new HashMap<>();
    mappings.put("java", "java");
    mappings.put("javascript", "js");
    mappings.put("php", "php");
    mappings.put("python", "py");
    LANGUAGE_MAP = Collections.unmodifiableMap(mappings);
  }

  public static String fromLanguageCode(String languageCode) {
    return LANGUAGE_MAP.getOrDefault(languageCode, languageCode);
  }
}
