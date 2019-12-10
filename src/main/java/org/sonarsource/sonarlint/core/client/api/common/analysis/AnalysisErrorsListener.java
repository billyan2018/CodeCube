package org.sonarsource.sonarlint.core.client.api.common.analysis;

import javax.annotation.Nullable;
import org.sonar.api.batch.fs.TextPointer;

@FunctionalInterface
public interface AnalysisErrorsListener {
  void handle(@Nullable String message, @Nullable TextPointer location);
}
