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

_Available only from RP v24.1_: use
```javascript
window.RP.overrideExtension(pluginName, url);
```
function call in browser to override the plugin UI assets in favor of your local development changes, f.e.
```javascript
window.RP.overrideExtension('plugin name', 'http://localhost:9090');
```

Build the UI source code: `npm run build`

**How UI plugin works** (need to be updated): [UI plugin docs](https://github.com/reportportal/service-ui/blob/master/docs/14-plugins.md).

## Build the plugin

Preconditions:
- Install JDK version 11.
- Specify version number in gradle.properties file.

**Note:** Versions in the _develop_ branch are not release versions and must be postfixed with `NEXT_RELEASE_VERSION-SNAPSHOT-NUMBER_OF_BUILD (Example: 5.3.6-SNAPSHOT-1)`

Build the plugin: `gradlew build`

---

## Where the UI appears (extensions)

ReportPortal opens your plugin UI in fixed **places** (instance admin, project settings, sidebar link, and so on). For each place you:

1. Add an entry in **`src/metadata.json`** (`type` + `name`; see the shipped examples). For org **sidebar** extensions, optional **`iconName`** should be a **short id** (example: `orgNav`): service-ui uses it for **sidebar click analytics**.
2. Point **`webpack.config.js` → `exposes`** to the **`.tsx`** file that renders that screen or widget.
3. In that file, use **`export default`** for the root component (the host loads extensions like a lazy-loaded module and expects a default export).

This template already wires several examples (names like `template`, `appNav` are **placeholders** — change them when you fork, but keep the same idea).

**Rough map** (details stay in `metadata.json` and the matching components under `src/components/`):

- **Instance admin** — full page under `/plugin/...` and optional **sidebar** item that links to it.
- **Organization** — a **settings tab** and/or a **sidebar** shortcut (there is no separate “org plugin URL” like the project full page; use the org tab + sidebar).
- **Project** — **settings tab**, **full plugin page** (own URL segment), and **sidebar** entry (see `projectSidebarNav.tsx` for how the sidebar opens the project page).

**When you rename routes or extension names**, search the repo for the old strings and update **`metadata.json`**, **`webpack.config.js` exposes**, and **navigation props** in the sidebar/settings components so they still match.

### Optional `metadata.json` overrides

You can add a top-level **`overrides`** object next to **`scope`** / **`extensions`**. This template includes **`overrides.disablePluginPopupContent`**: an object whose keys are **locale codes** (`en`, `ru`, `be`, `uk`, …) and values are the **body text** for the “disable plugin” confirmation when admins turn the plugin off. Remove **`overrides`** entirely if the host default wording is enough for your plugin.

### Extension props (injected by `service-ui`)

Props are built in **`createImportProps`** and spread by **`FederatedExtensionLoader`** — see [`createImportProps.js`](https://github.com/reportportal/service-ui/blob/develop/app/src/controllers/plugins/uiExtensions/createImportProps.js) and [`federatedExtensionLoader.jsx`](https://github.com/reportportal/service-ui/blob/develop/app/src/components/extensionLoader/federatedExtensionLoader/federatedExtensionLoader.jsx). Placeholder page components are typed with **`HostInjectedExtensionProps`** ([`src/types/hostExtensionProps.ts`](src/types/hostExtensionProps.ts)) so `selectors`, `components`, `constants`, etc. match the host.

### Plugin commands from the UI

Call the plugin API with **`utils.fetch`** and **`utils.URLS`** from props ([`createImportProps.js`](https://github.com/reportportal/service-ui/blob/develop/app/src/controllers/plugins/uiExtensions/createImportProps.js), [`urls.js`](https://github.com/reportportal/service-ui/blob/develop/app/src/common/urls.js)).

Example hook: [`src/hooks/useCommandExecutor.ts`](src/hooks/useCommandExecutor.ts) — **`POST`** + **`pluginsCommandsCommon`**. **`PLUGIN_NAME`** = **`pluginId`** in **`gradle.properties`**. 
Java sample command: **`TemplateCommand`**.

### Sidebar icons (host UI)

Host `SidebarButton` takes **`icon` as a string** (SVG parsed with `html-react-parser`). Here: **`.svg` files in [`src/icons/`](src/icons/)** + `import … from 'icons/…svg'` (`svg-inline-loader` → string). Types: [`declarations.d.ts`](src/declarations.d.ts).

**Shape / layout (so it matches RP sidebars)** — same idea as [`settings-icon-inline.svg`](https://github.com/reportportal/service-ui/blob/develop/app/src/common/img/sidebar/settings-icon-inline.svg) in service-ui:

| Rule | Why |
|------|-----|
| **`width="48"` `height="40"`** and matching **`viewBox="0 0 48 40"`** | `.btn-icon` in host CSS forces inner `svg` to **48×40px**; this artboard avoids tiny icons in a large slot. |
| **`fill="currentColor"`** on shapes (prefer **`<path>`**) | Default color follows sidebar text; **hover** in host applies `svg path { fill: … }` — stroke-only icons **won’t** pick that up. |
| Prefer **`<path fill="currentColor">`** over **`<circle>`** for the active state | `.active` recolors `path`, `rect`, `polygon` — **not** `circle`/`ellipse` in [`sidebarButton.scss`](https://github.com/reportportal/service-ui/blob/develop/app/src/componentLibrary/sidebar/sidebarButton/sidebarButton.scss). |

**Naming:** `metadata.scope` must equal webpack **`name`** (`plugin_name` in this repo). It is **not** the same as `extensions[].name` (tab/route key) or `package.json` name.

`redux` is listed under webpack **`externals`** so the host supplies one shared instance. You only need a direct `"redux"` dependency in `package.json` if your code imports `from 'redux'`; using `react-redux` alone is the common case.

Keep `shared` / `externals` versions aligned with the ReportPortal UI build you target (`react`, `react-dom`, `react-redux`, etc. in `webpack.config.js`).
