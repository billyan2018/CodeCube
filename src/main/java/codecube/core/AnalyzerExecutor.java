package codecube.core;

import org.sonarsource.sonarlint.core.client.api.common.analysis.IssueListener;

public interface AnalyzerExecutor {
  void execute(IssueListener listener);
}
