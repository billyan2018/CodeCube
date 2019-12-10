/*
 * SonarLint Core - Implementation (trimmed)
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.sonarlint.core.analyzer.sensor;

import com.google.common.base.Strings;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.Rule;
import org.sonar.api.batch.rule.Rules;
import org.sonar.api.batch.rule.internal.DefaultRule;
import org.sonar.api.batch.sensor.coverage.internal.DefaultCoverage;
import org.sonar.api.batch.sensor.cpd.internal.DefaultCpdTokens;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonar.api.batch.sensor.highlighting.internal.DefaultHighlighting;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.batch.sensor.symbol.internal.DefaultSymbolTable;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.MessageException;
import org.sonarsource.sonarlint.core.analyzer.issue.DefaultClientIssue;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisErrorsListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.HighlightingListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.IssueListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.SymbolRefsListener;
import org.sonarsource.sonarlint.core.container.analysis.filesystem.SonarLintInputFile;

public class DefaultSensorStorage implements SensorStorage {

  private final ActiveRules activeRules;
  private final Rules rules;
  private final IssueListener issueListener;
  private final HighlightingListener highlightingListener;
  private final SymbolRefsListener symbolRefsListener;
  private final AnalysisErrorsListener analysisErrorsListener;

  public DefaultSensorStorage(ActiveRules activeRules, Rules rules,
    IssueListener issueListener,
    HighlightingListener highlightingListener,
    SymbolRefsListener symbolRefsListener,
    AnalysisErrorsListener analysisErrorsListener) {
    this.activeRules = activeRules;
    this.rules = rules;
    this.issueListener = issueListener;
    this.highlightingListener = highlightingListener;
    this.symbolRefsListener = symbolRefsListener;
    this.analysisErrorsListener = analysisErrorsListener;
  }

  @Override
  public void store(Measure newMeasure) {
    // NO-OP
  }

  @Override
  public void store(Issue issue) {
    InputComponent inputComponent = issue.primaryLocation().inputComponent();

    DefaultRule rule = validateRule(issue);
    ActiveRule activeRule = activeRules.find(issue.ruleKey());
    if (activeRule == null) {
      // rule does not exist or is not enabled -> ignore the issue
      return;
    }

    String primaryMessage = Strings.isNullOrEmpty(issue.primaryLocation().message()) ? rule.name() : issue.primaryLocation().message();
    org.sonar.api.batch.rule.Severity overriddenSeverity = issue.overriddenSeverity();
    String severity = overriddenSeverity != null ? overriddenSeverity.name() : activeRule.severity();
    String type = rule.type();

    DefaultClientIssue newIssue = new DefaultClientIssue(severity, type, activeRule, rules.find(activeRule.ruleKey()), primaryMessage, issue.primaryLocation().textRange(),
      inputComponent.isFile() ? ((SonarLintInputFile) inputComponent).getClientInputFile() : null, issue.flows());
    issueListener.handle(newIssue);
  }

  private DefaultRule validateRule(Issue issue) {
    RuleKey ruleKey = issue.ruleKey();
    Rule rule = rules.find(ruleKey);
    if (rule == null) {
      throw MessageException.of(String.format("The rule '%s' does not exist.", ruleKey));
    }
    if (Strings.isNullOrEmpty(rule.name()) && Strings.isNullOrEmpty(issue.primaryLocation().message())) {
      throw MessageException.of(String.format("The rule '%s' has no name and the related issue has no message.", ruleKey));
    }
    return (DefaultRule) rule;
  }

  @Override
  public void store(DefaultHighlighting highlighting) {
    highlightingListener.handle(highlighting.getSyntaxHighlightingRuleSet());
  }

  @Override
  public void store(DefaultCoverage defaultCoverage) {
    // NO-OP
  }

  @Override
  public void store(DefaultCpdTokens defaultCpdTokens) {
    // NO-OP
  }

  @Override
  public void store(DefaultSymbolTable symbolTable) {
    symbolRefsListener.handle(symbolTable.getReferencesBySymbol());
  }

  @Override
  public void store(AnalysisError analysisError) {
    analysisErrorsListener.handle(analysisError.message(), analysisError.location());
  }

  @Override
  public void storeProperty(String key, String value) {
    // NO-OP
  }

}
