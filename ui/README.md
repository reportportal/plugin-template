# Plugin template for Epam Report Portal

## UI

Preconditions:
- Install Node.js (version 20 is recommended).

Install the dependencies: `npm install`

Run in dev mode:
```bash
npm run dev # Run webpack in dev watch mode
npm run start # Serve built files
```

**Option 1** — override via browser console (resets on page reload):

_Available only from RP v24.1_: use
```javascript
window.RP.overrideExtension(pluginName, url);
```
function call in browser to override the plugin UI assets in favor of your local development changes, f.e.
```javascript
window.RP.overrideExtension('plugin name', 'http://localhost:9090');
```

**Option 2** — override via service-ui webpack proxy (persists across reloads):

In `service-ui/app/webpack/dev.config.js`, add the following entry **before** the existing `/api/` proxy rule:
```javascript
{
  context: ['/api/v1/plugin/public/{pluginName}/'],
  target: 'http://localhost:9090',
  changeOrigin: true,
  pathRewrite: { '^/api/v1/plugin/public/{pluginName}/file': '' },
},
```
Replace `{pluginName}` with the actual plugin name. Then restart the service-ui dev server. All plugin file requests will be redirected to your local dev server on port 9090.

Build the UI source code: `npm run build`

**How UI plugin works** (need to be updated): [UI plugin docs](https://github.com/reportportal/service-ui/blob/master/docs/14-plugins.md).

## Build the plugin

Preconditions:
- Install JDK version 11.
- Specify version number in gradle.properties file.

**Note:** Versions in the _develop_ branch are not release versions and must be postfixed with `NEXT_RELEASE_VERSION-SNAPSHOT-NUMBER_OF_BUILD (Example: 5.3.6-SNAPSHOT-1)`

Build the plugin: `gradlew build`
