/*
 * Copyright 2025 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.reportportal.extension.template.event.plugin;

import static java.util.Optional.ofNullable;

import com.epam.reportportal.core.events.domain.PluginUploadedEvent;
import com.epam.reportportal.extension.template.event.EventHandlerFactory;
import org.springframework.context.ApplicationListener;

/**
 * @author Andrei Piankouski
 */
public class PluginEventListener implements ApplicationListener<PluginUploadedEvent> {

  private final String pluginId;
  private final EventHandlerFactory<PluginUploadedEvent> pluginEventHandlerFactory;

  public PluginEventListener(String pluginId,
      EventHandlerFactory<PluginUploadedEvent> pluginEventHandlerFactory) {
    this.pluginId = pluginId;
    this.pluginEventHandlerFactory = pluginEventHandlerFactory;
  }

  @Override
  public void onApplicationEvent(PluginUploadedEvent event) {
    if (supports(event)) {
      ofNullable(pluginEventHandlerFactory.getEventHandler(event.getPluginActivityResource().getName()))
          .ifPresent(pluginEventEventHandler ->
              pluginEventEventHandler.handle(event));
    }
  }

  private boolean supports(PluginUploadedEvent event) {
    return pluginId.equals(event.getPluginActivityResource().getName());
  }
}
