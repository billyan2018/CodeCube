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

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.events.SensorsPhaseHandler;
import org.sonar.api.resources.Project;
import org.sonarsource.sonarlint.core.util.StringUtils;

/**
 * Execute new and old Sensors.
 */
public class AllSensorsExecutor implements SensorsExecutor {

  private static final Logger LOG = LoggerFactory.getLogger(AllSensorsExecutor.class);

  private final Project module;
  private final ScannerExtensionDictionnary selector;
  private final SensorsPhaseHandler[] handlers;
  private final SensorContext context;

  public AllSensorsExecutor(SensorContext context, ScannerExtensionDictionnary selector, Project project) {
    this(context, selector, project, new SensorsPhaseHandler[0]);
  }

  public AllSensorsExecutor(SensorContext context, ScannerExtensionDictionnary selector, Project project, SensorsPhaseHandler[] handlers) {
    this.context = context;
    this.selector = selector;
    this.module = project;
    this.handlers = handlers;
  }

  @Override
  public void execute() {
    Collection<Sensor> sensors = selector.select(Sensor.class, module, true);

    for (SensorsPhaseHandler h : handlers) {
      h.onSensorsPhase(new DefaultSensorsPhaseEvent(sensors, true));
    }

    for (Sensor sensor : sensors) {
      executeSensor(context, sensor);
    }

    for (SensorsPhaseHandler h : handlers) {
      h.onSensorsPhase(new DefaultSensorsPhaseEvent(sensors, false));
    }
  }

  private void executeSensor(SensorContext context, Sensor sensor) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Execute Sensor: {}", StringUtils.describe(sensor));
    }
    sensor.analyse(module, context);
  }
}
