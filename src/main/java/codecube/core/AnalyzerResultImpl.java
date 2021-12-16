package codecube.core;

import lombok.RequiredArgsConstructor;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Highlighting;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class AnalyzerResultImpl implements AnalyzerResult {

    private final List<Issue> issues;
    private final List<Highlighting> highlightings;
    private final Map<TextRange, Set<TextRange>> symbolRefs;
    private final List<AnalysisError> errors;


    @Override
    public List<Issue> issues() {
        return issues;
    }

    @Override
    public List<Highlighting> highlightings() {
        return highlightings;
    }

    @Override
    public Map<TextRange, Set<TextRange>> symbolRefs() {
        return symbolRefs;
    }

    @Override
    public List<AnalysisError> errors() {
        return errors;
    }

    @Override
    public boolean success() {
        return errors.isEmpty();
    }
}
