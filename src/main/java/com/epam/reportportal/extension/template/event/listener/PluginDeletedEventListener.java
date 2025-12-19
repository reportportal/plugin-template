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

package com.epam.reportportal.extension.template.event.listener;

import com.epam.reportportal.core.events.domain.PluginDeletedEvent;
import com.epam.reportportal.extension.template.event.handler.PluginDeletedEventHandler;
import java.util.Objects;
import org.springframework.context.ApplicationListener;

/**
 * @author Andrei Piankouski
 */
public class PluginDeletedEventListener implements ApplicationListener<PluginDeletedEvent> {

  private final String pluginId;
  private final PluginDeletedEventHandler pluginDeletedEventHandler;

  public PluginDeletedEventListener(String pluginId,
      PluginDeletedEventHandler pluginDeletedEventHandler) {
    this.pluginId = pluginId;
    this.pluginDeletedEventHandler = pluginDeletedEventHandler;
  }

  @Override
  public void onApplicationEvent(PluginDeletedEvent event) {
    if (supports(event)) {
      pluginDeletedEventHandler.handle(event);
    }
  }

  private boolean supports(PluginDeletedEvent event) {
    return Objects.nonNull(event.getBefore()) && pluginId.equals(event.getBefore().getName());
  }
}
