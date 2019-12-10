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
package org.sonarsource.sonarlint.core.container.standalone.rule;

import org.picocontainer.injectors.ProviderAdapter;
import org.sonar.api.batch.rule.Rules;
import org.sonar.api.batch.rule.internal.NewRule;
import org.sonar.api.batch.rule.internal.RulesBuilder;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Param;

public class StandaloneRulesProvider extends ProviderAdapter {
  private Rules singleton = null;

  private static Rules createRules(StandaloneRuleDefinitionsLoader pluginRulesLoader) {
    RulesBuilder builder = new RulesBuilder();

    for (RulesDefinition.Repository repoDef : pluginRulesLoader.getContext().repositories()) {
      for (org.sonar.api.server.rule.RulesDefinition.Rule ruleDef : repoDef.rules()) {
        NewRule newRule = builder.add(RuleKey.of(ruleDef.repository().key(), ruleDef.key()))
          .setInternalKey(ruleDef.internalKey())
          .setDescription(ruleDef.htmlDescription() != null ? ruleDef.htmlDescription() : ruleDef.markdownDescription())
          .setSeverity(ruleDef.severity())
          .setType(ruleDef.type() != null ? ruleDef.type().toString() : null)
          .setName(ruleDef.name());
        for (Param p : ruleDef.params()) {
          newRule.addParam(p.key())
            .setDescription(p.description());
        }
      }
    }

    return builder.build();
  }

  public Rules provide(StandaloneRuleDefinitionsLoader pluginRulesLoader) {
    if (singleton == null) {
      singleton = createRules(pluginRulesLoader);
    }
    return singleton;
  }
}
