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
package org.sonarsource.sonarlint.core;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.client.api.common.LogOutput;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisErrorsListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.HighlightingListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.IssueListener;
import org.sonarsource.sonarlint.core.client.api.common.analysis.SymbolRefsListener;
import org.sonarsource.sonarlint.core.client.api.exceptions.SonarLintWrappedException;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneGlobalConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;
import org.sonarsource.sonarlint.core.container.standalone.StandaloneGlobalContainer;
import org.sonarsource.sonarlint.core.log.SonarLintLogging;

import static com.google.common.base.Preconditions.checkNotNull;

public final class StandaloneSonarLintEngineImpl implements StandaloneSonarLintEngine {

  private final StandaloneGlobalConfiguration globalConfig;
  private final ReadWriteLock rwl = new ReentrantReadWriteLock();
  private StandaloneGlobalContainer globalContainer;
  private LogOutput logOutput = null;

  public StandaloneSonarLintEngineImpl(StandaloneGlobalConfiguration globalConfig) {
    this.globalConfig = globalConfig;
    logOutput = globalConfig.getLogOutput();
    start();
  }

  public void start() {
    setLogging(null);
    rwl.writeLock().lock();
    globalContainer = StandaloneGlobalContainer.create(globalConfig);
    try {
      globalContainer.startComponents();
    } catch (RuntimeException e) {
      throw SonarLintWrappedException.wrap(e);
    } finally {
      rwl.writeLock().unlock();
    }
  }

  @Override
  public void analyze(StandaloneAnalysisConfiguration configuration,
    IssueListener issueListener,
    HighlightingListener highlightingListener,
    SymbolRefsListener symbolRefsListener,
    AnalysisErrorsListener analysisErrorsListener,
    @Nullable LogOutput logOutput) {
    checkNotNull(configuration);
    checkNotNull(issueListener);
    setLogging(logOutput);
    rwl.readLock().lock();
    try {
      globalContainer.analyze(configuration, issueListener, highlightingListener, symbolRefsListener, analysisErrorsListener);
    } catch (RuntimeException e) {
      throw SonarLintWrappedException.wrap(e);
    } finally {
      rwl.readLock().unlock();
    }
  }

  private void setLogging(@Nullable LogOutput logOutput) {
    if (logOutput != null) {
      SonarLintLogging.set(logOutput);
    } else {
      SonarLintLogging.set(this.logOutput);
    }
  }
}
