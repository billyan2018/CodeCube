package org.sonarsource.sonarlint.core.client.api.common.analysis;

import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;

public interface Highlighting {

  TypeOfText type();

  TextRange textRange();
}
