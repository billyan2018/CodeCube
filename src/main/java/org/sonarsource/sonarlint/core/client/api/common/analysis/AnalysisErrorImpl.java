package org.sonarsource.sonarlint.core.client.api.common.analysis;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.sensor.error.AnalysisError;

public class AnalysisErrorImpl implements AnalysisError {
  private final String message;
  private final TextPointer location;

  public AnalysisErrorImpl(@Nullable String message, @Nullable TextPointer location) {
    this.message = message;
    this.location = location;
  }

  @Override
  public InputFile inputFile() {
    return null;
  }

  @CheckForNull
  @Override
  public String message() {
    return message;
  }

  @CheckForNull
  @Override
  public TextPointer location() {
    return location;
  }
}
