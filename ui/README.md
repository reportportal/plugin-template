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

**Plugins admin screen vs this `ui/` bundle:** the page **Administrate → Plugins → (your plugin)** (description, **Add integration**, etc.) is rendered by **ReportPortal host UI**, not by the federated components in this folder. A “stub” or extra layout **here** does not remove or replace that block. To hide integrations there, use **`isIntegrationsAllowed`** in **`gradle.properties`** (becomes plugin manifest metadata for the API), or customize **service-ui**.

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

This template already wires several examples (names like `template` are **placeholders** — change them when you fork, but keep the same idea).

**Rough map** (details stay in `metadata.json` and the matching components under `src/components/`):

- **Instance admin** — full page under `/plugin/...` and optional **sidebar** item that links to it.
- **Organization** — a **settings tab** and/or a **sidebar** shortcut (there is no separate “org plugin URL” like the project full page; use the org tab + sidebar).
- **Project** — **settings tab**.
**When you rename routes or extension names**, search the repo for the old strings and update **`metadata.json`**, **`webpack.config.js` exposes**, and **navigation props** in the sidebar/settings components so they still match.

### Localization (translations shipped with the plugin)

Plugin translations live in the plugin artifact and are merged into ReportPortal at
runtime. The host reads the manifest, fetches `locale-{lang}.json` from the same base
URL as the MF entry, and merges it over the core catalog. The workflow mirrors
`service-ui`'s exactly (extract reference catalog → merge per-language files → commit →
ship as-is, no build-time transform), using the official
[FormatJS CLI](https://formatjs.github.io/docs/tooling/cli/) (`@formatjs/cli`) for
extraction.

**Pipeline** (`npm run manage:translations`, run whenever you add/change strings):

```
defineMessages (code)
  │  formatjs extract
  ▼
src/locales/en.json            — reference catalog, git-ignored, always regenerated
  │  localization/mergeTranslations.js
  ▼
src/locales/{ru,uk,be,zh,es}.json  — flat id→string, in git; this IS the runtime shape,
                                      no further transform — missing ids added with the
                                      English text, obsolete ids removed, ids still equal
                                      to the English text reported as untranslated
  │  (translator replaces the English placeholder text by hand)
  ▼  webpack CopyPlugin (straight copy + rename, no transform)
build/public/locale-{lang}.json
```

This matches `service-ui/app/localization/` (`webpack extract` →
`react-intl-translations-manager` merge → committed `ru/uk/be/zh/es.json` → consumed
as-is by `IntlProvider`, see `messages={this.props.messages}` in
`localizationContainer.jsx`) almost exactly — `service-ui` has no separate
compile/validation step either, and the committed files there are the literal runtime
shape too. The only difference is the last hop: a plugin ships its locales as static
files fetched at runtime instead of bundling them into the host's own JS, so
`webpack.config.js` copies them into `build/public/` instead of `import`-ing them.

**Wiring in this template:**

1. **`src/metadata.json`** declares the contract:
   ```json
   "localization": { "messages": "locale-{lang}.json" }
   ```
   Without this section the plugin stays on core strings + `defaultMessage` (legacy, still works).
2. **`src/locales/en.json`** is the extract output (nested `{id: {defaultMessage}}` shape).
   It is **git-ignored** (like `service-ui`'s `localization/translated/en.json`) — fully
   derived from code, never hand-edited, and never shipped: it exists only so
   `mergeTranslations.js` knows which ids and English texts should exist.
3. **`src/locales/{ru,uk,be,zh,es}.json`** hold translations as a flat `id -> string` map,
   editable by hand or any TMS that supports flat JSON (Localizely, POEditor, BabelEdit,
   …). A key that hasn't been translated yet holds the literal English `defaultMessage`
   text (not an empty string) — see `merge:translations` below — so the file is always
   valid, readable English until a translator changes it.
4. **`webpack.config.js`** copies `src/locales/*.json` into `build/public/` as flat
   `locale-{lang}.json` (next to `metadata.json`); `en.json` is excluded — it's the
   extract reference, not a runtime file.
5. **Message ids** use a plugin namespace prefix (here `PluginTemplate.*`). The prefix only
   needs to be unique; it does **not** have to equal your `pluginId`.

**Workflow:**

1. Add strings with `defineMessages` + `defaultMessage` (English) and `formatMessage`.
2. Run `npm run manage:translations`:
   - `extract:translations` (`formatjs extract`) — regenerates `src/locales/en.json` from
     code, so ids never drift from what's in `defineMessages`;
   - `merge:translations` (`localization/mergeTranslations.js`) — adds new/missing ids to
     every `src/locales/{lang}.json` filled with the English `defaultMessage` (a key is
     "untranslated" when its stored text still equals the English one), removes ids no
     longer in code, and logs both lists. `formatjs verify --missing-keys --extra-keys`
     can detect the same drift, but only reports it (non-zero exit) instead of writing the
     fix — this script plays the same role `react-intl-translations-manager` plays in
     `service-ui` (same add/remove/report algorithm, ported to the flat format; there is
     no official FormatJS command that writes/fixes translation files, only ones that
     extract, transform, or report).
3. Translate the reported ids by hand (or via a TMS) and commit the changed
   `src/locales/*.json` files (not `en.json`).
4. `npm run build` / `npm run dev` — no translation step runs automatically (same as
   `service-ui`'s plain `webpack` build): commit up-to-date `src/locales/*.json` files
   before building, `webpack.config.js` just copies them into `build/public/locale-*.json`.

**Local check:** `window.RP.overrideExtension('template', 'http://localhost:9090')` — code and
locales are then served from the same dev origin; switch app language to see the merge.

### Optional `metadata.json` overrides

You can add a top-level **`overrides`** object next to **`scope`** / **`extensions`**. This template includes **`overrides.disablePluginPopupContent`**: an object whose keys are **locale codes** (`en`, `ru`, `be`, `uk`, …) and values are the **body text** for the “disable plugin” confirmation when admins turn the plugin off. Remove **`overrides`** entirely if the host default wording is enough for your plugin.

### Extension props (injected by `service-ui`)

Props are built in **`createImportProps`** and spread by **`FederatedExtensionLoader`** — see [`createImportProps.js`](https://github.com/reportportal/service-ui/blob/develop/app/src/controllers/plugins/uiExtensions/createImportProps.js) and [`federatedExtensionLoader.jsx`](https://github.com/reportportal/service-ui/blob/develop/app/src/components/extensionLoader/federatedExtensionLoader/federatedExtensionLoader.jsx). Placeholder page components are typed with **`ExtensionProps`** ([`src/types/extensionProps/index.ts`](src/types/extensionProps/index.ts)) so `selectors`, `components`, `constants`, etc. match the host.

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
