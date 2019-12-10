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
package org.sonarsource.sonarlint.core.container.analysis.filesystem;

import java.util.HashSet;
import java.util.Set;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;

/**
 * Index input files into {@link InputPathCache}.
 */
@SonarLintSide
public class FileIndexer {

  private static final Logger LOG = Loggers.get(FileIndexer.class);

  private final InputFileBuilder inputFileBuilder;
  private final StandaloneAnalysisConfiguration analysisConfiguration;

  private final Set<SonarLintInputFile> indexed = new HashSet<SonarLintInputFile>();

  public FileIndexer(InputFileBuilder inputFileBuilder, StandaloneAnalysisConfiguration analysisConfiguration) {
    this.inputFileBuilder = inputFileBuilder;
    this.analysisConfiguration = analysisConfiguration;
  }

  void index(SonarLintFileSystem fileSystem) {
    try {
      indexFiles(fileSystem, analysisConfiguration.inputFiles());
    } catch (Exception e) {
      throw e;
    }
  }

  private void indexFiles(SonarLintFileSystem fileSystem, Iterable<ClientInputFile> inputFiles) {
    for (ClientInputFile file : inputFiles) {
      indexFile(fileSystem, file);
    }
  }

  private void indexFile(SonarLintFileSystem fileSystem, ClientInputFile file) {
    SonarLintInputFile inputFile = inputFileBuilder.create(file);
    indexFile(fileSystem, inputFile);
  }

  private void indexFile(final SonarLintFileSystem fs, final SonarLintInputFile inputFile) {
    if (!indexed.add(inputFile)) {
      return;
    }
    fs.add(inputFile);
    if (indexed.size() == 1) {
      LOG.debug("Setting filesystem encoding: " + inputFile.charset());
      fs.setEncoding(inputFile.charset());
    }
  }
}
