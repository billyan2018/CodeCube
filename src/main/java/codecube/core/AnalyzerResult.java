package codecube.core;

import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Highlighting;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AnalyzerResult {

  List<Issue> issues();

  List<Highlighting> highlightings();

  Map<TextRange, Set<TextRange>> symbolRefs();

  List<AnalysisError> errors();

  boolean success();
}
