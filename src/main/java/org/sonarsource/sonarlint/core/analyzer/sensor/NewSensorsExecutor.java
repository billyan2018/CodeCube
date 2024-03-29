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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonarsource.sonarlint.core.util.StringUtils;

import static java.util.Arrays.asList;
import static org.sonarsource.sonarlint.core.analyzer.sensor.ScannerExtensionDictionnary.sort;

/**
 * Execute only new Sensors.
 */
public class NewSensorsExecutor implements SensorsExecutor {

  private static final Logger logger = LoggerFactory.getLogger(NewSensorsExecutor.class);

  private final SensorOptimizer sensorOptimizer;
  private final Sensor[] sensors;
  private final DefaultSensorContext context;

  public NewSensorsExecutor(DefaultSensorContext context, SensorOptimizer sensorOptimizer) {
    this(context, sensorOptimizer, new Sensor[0]);
  }

  public NewSensorsExecutor(DefaultSensorContext context, SensorOptimizer sensorOptimizer, Sensor[] sensors) {
    this.context = context;
    this.sensors = sensors;
    this.sensorOptimizer = sensorOptimizer;
  }

  private static void executeSensor(SensorContext context, Sensor sensor, DefaultSensorDescriptor descriptor) {
    if (logger.isDebugEnabled()) {
      logger.debug("Execute Sensor: {}", descriptor.name() != null ? descriptor.name() : StringUtils.describe(sensor));
    }
    sensor.execute(context);
  }

  @Override
  public void execute() {
    for (Sensor sensor : sort(asList(sensors))) {
      DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
      sensor.describe(descriptor);
      if (sensorOptimizer.shouldExecute(descriptor)) {
        executeSensor(context, sensor, descriptor);
      }
    }
  }
}
