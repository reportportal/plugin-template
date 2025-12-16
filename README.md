# Template Plugin for ReportPortal

## UI

Install the dependencies: `npm install`

Build the UI source code: `npm run build` 

## How to Implement a ReportPortal Plugin

This guide provides step-by-step instructions on how to implement a Report Portal application plugin.

### Prerequisites

Make sure you have the following software installed:

- JDK version 21
- Gradle

### Step 1: Choose an extension type

This template provides two types of extensions:

- **`TemplatePluginExtension`**: A simple extension without event listeners. Use this to create a basic plugin that
  provides information defined in the manifest file.
- **`TemplatePluginExtensionWithListener`**: An extension that includes a listener for the `pluginLoaded` event. Use
  this if you need to perform actions when the plugin is loaded, for example, creating a default integration.

Choose the extension type that fits your needs and delete the other one.

### Step 2: Implement your plugin

Open the Java file for the extension type you chose and add your custom logic.

- For `TemplatePluginExtension`, you can add methods that will be exposed through the plugin's API.
- For `TemplatePluginExtensionWithListener`, you can add your logic to the `onPluginLoaded` method, which is triggered
  when the plugin is loaded.

### Step 3: Configure the plugin

Modify the `gradle.properties` file to set the version number and other properties of your plugin.

**Note:** Versions in branches other than the _main_ branch are not release versions and must be updated following the
pattern: `NEXT_RELEASE_VERSION-SNAPSHOT-NUMBER_OF_BUILD`.
(Example: `6.0.1-SNAPSHOT-1`)

### Step 4: Build the plugin

To build the plugin, run the following command in your terminal:

```bash
./gradlew build
```

This will generate a `.jar` file in the `build/libs` directory. This file is your plugin package.

### Step 5: Deploy the plugin

1. Navigate to the **Administrate** section in your ReportPortal instance.
2. Go to the **Plugins** tab.
3. Click on the **Upload** button and select the `.jar` file generated in the previous step.
4. After the plugin is uploaded, it will appear in the list of plugins.
