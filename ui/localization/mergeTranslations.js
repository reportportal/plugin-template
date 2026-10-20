/*
 * Copyright 2026 EPAM Systems
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

/**
 * Plays the role of `react-intl-translations-manager` (used in service-ui):
 * adds keys missing from the reference catalog, removes obsolete ones, and
 * reports what still needs translation. A missing key is filled in with the
 * English `defaultMessage`; "untranslated" means the stored value still
 * equals it. Run after `extract:translations` — see `npm run manage:translations`.
 */
const fs = require('fs');
const path = require('path');

// Keep in sync with service-ui/app/localization/config.js `languages`.
const languages = ['uk', 'ru', 'be', 'zh', 'es'];

const localesDir = path.resolve(__dirname, '..', 'src/locales');
const referencePath = path.join(localesDir, 'en.json');

const readJson = (filePath) => JSON.parse(fs.readFileSync(filePath, 'utf8'));
const writeJson = (filePath, data) =>
  fs.writeFileSync(filePath, `${JSON.stringify(data, null, 2)}\n`, 'utf8');

const mergeLanguage = (lang, defaultMessages) => {
  const filePath = path.join(localesDir, `${lang}.json`);
  const existing = fs.existsSync(filePath) ? readJson(filePath) : {};
  const merged = {};
  const added = [];
  const untranslated = [];

  Object.entries(defaultMessages).forEach(([id, defaultMessage]) => {
    const oldMessage = existing[id];
    // Truthy check (not `!== undefined`): an empty string is treated the same
    // as a missing key, matching react-intl-translations-manager exactly.
    if (!oldMessage) {
      merged[id] = defaultMessage;
      added.push(id);
      return;
    }
    merged[id] = oldMessage;
    if (oldMessage === defaultMessage) {
      untranslated.push(id);
    }
  });

  const referenceKeys = new Set(Object.keys(defaultMessages));
  const deleted = Object.keys(existing).filter((id) => !referenceKeys.has(id));

  writeJson(filePath, merged);

  return { added, deleted, untranslated };
};

if (!fs.existsSync(referencePath)) {
  console.error(`Reference catalog not found: ${referencePath}`);
  console.error('Run npm run extract:translations first.');
  process.exit(1);
}

// `en.json` is the extracted (nested) shape `{ id: { defaultMessage } }`.
const referenceCatalog = readJson(referencePath);
const defaultMessages = {};
for (const id of Object.keys(referenceCatalog)) {
  defaultMessages[id] = referenceCatalog[id].defaultMessage;
}

if (Object.keys(defaultMessages).length === 0) {
  console.warn('Reference catalog is empty — nothing to merge.');
  process.exit(0);
}

languages.forEach((lang) => {
  const { added, deleted, untranslated } = mergeLanguage(lang, defaultMessages);
  const total = Object.keys(defaultMessages).length;

  if (!added.length && !deleted.length && !untranslated.length) {
    console.log(`[${lang}] up to date (${total} keys)`);
    return;
  }

  console.log(`[${lang}] merged (${total} keys)`);
  if (added.length) console.log(`  added (${added.length}): ${added.join(', ')}`);
  if (deleted.length) console.log(`  removed (${deleted.length}): ${deleted.join(', ')}`);
  if (untranslated.length) {
    console.log(`  untranslated (${untranslated.length}): ${untranslated.join(', ')}`);
  }
});
