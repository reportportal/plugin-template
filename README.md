# Template Plugin for ReportPortal

This is a template for creating a ReportPortal plugin.

## How to Implement a ReportPortal Plugin

This guide provides step-by-step instructions on how to implement a Report Portal application plugin.

### Prerequisites

Make sure you have the following software installed:

- JDK version 21
- Gradle

### Step 1: Implement the plugin

This template uses an event-driven architecture. You can implement your custom logic by creating event handlers.

- **`TemplatePluginExtension`**: An extension that includes a listener for the `pluginLoaded` event. Use
  this if you need to perform actions when the plugin is loaded, for example, creating a default integration.

**Note:** If your plugin does not need to handle events, you can remove the event listener and the `DisposableBean`
implementation from the `TemplatePluginExtension` class.

### Step 2: Configure the plugin

Modify the `gradle.properties` file to set the version number and other properties of your plugin.

**Note:** Versions in branches other than the _main_ branch are not release versions and must be updated following the
pattern: `NEXT_RELEASE_VERSION-SNAPSHOT-NUMBER_OF_BUILD`.
(Example: `6.0.1-SNAPSHOT-1`)

### Step 3: Build the plugin

To build the plugin, run the following command in your terminal:

`./gradlew build`

This will generate a `.jar` file in the `build/libs` directory. This file is your plugin package.

### Step 4: Deploy the plugin

1. Navigate to the **Administrate** section in your ReportPortal instance.
2. Go to the **Plugins** tab.
3. Click on the **Upload** button and select the `.jar` file generated in the previous step.
4. After the plugin is uploaded, it will appear in the list of plugins.

Bundled UI (webpack, `metadata.json`, extension points): see [`ui/README.md`](ui/README.md).
