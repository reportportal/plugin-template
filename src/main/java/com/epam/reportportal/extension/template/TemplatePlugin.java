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

package com.epam.reportportal.extension.template;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 * @author Andrei Piankouski
 */
public class TemplatePlugin extends Plugin {

  /**
   * Constructor to be used by plugin manager for plugin instantiation. Your plugins have to provide constructor with
   * this exact signature to be successfully loaded by manager.
   *
   * @param wrapper - A wrapper over plugin instance.
   */
  public TemplatePlugin(PluginWrapper wrapper) {
    super(wrapper);
  }
}
