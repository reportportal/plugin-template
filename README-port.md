# Ported to the `develop` package layout

This branch builds the template against **service-api's `develop` branch**, not against the
published `com.epam.reportportal:plugin-api` artifact. A plugin built the normal way loads into
a develop build, starts, and then dies the moment PF4J instantiates its extension:

```
java.lang.NoClassDefFoundError: com/epam/ta/reportportal/exception/ReportPortalException
    at ReportPortalExtensionFactory.createPlugin
```

Two things moved and the published artifact has neither:

- every domain class went from `com.epam.ta.reportportal.*` to `com.epam.reportportal.base.*`;
- `ReportPortalExtensionPoint` gained methods and now imports from `com.epam.reportportal.base`,
  while `plugin-api@develop` still declares the older, smaller interface (untouched since
  2025-12).

## What changed here

| | |
|---|---|
| Imports | `com.epam.ta.reportportal.dao.*` → `com.epam.reportportal.base.infrastructure.persistence.dao.*`, `com.epam.ta.reportportal.entity.integration.*` → `…persistence.entity.integration.*` |
| Removed | `TemplatePluginExtensionWithListener` and the whole `event/` package — they need `com.epam.reportportal.extension.event.PluginEvent`, and the plugin-lifecycle hook is gone from this build's extension API. An example built on an API that no longer exists is worse than no example. |
| Compile classpath | the running service-api, unpacked from its image (see below), all `compileOnly` — every one of those classes is already on the host and PF4J hands the plugin the host's copy |
| Toolchain | service-api compiles to class file 69 (Java 25) and javac will not read a newer class file than it targets, so the compiler forks to a 25 toolchain while Gradle itself stays on 21 |
| Kept, and load-bearing | `annotationProcessor 'org.pf4j:pf4j:3.15.0'` — it writes `META-INF/extensions.idx`, which is how PF4J finds the extension at all. Without it the plugin loads, starts, and exposes nothing. |
| Dropped | the node/UI build — this is a backend-only port |

## Building

Unpack the classes the plugin compiles against, straight out of the image that is running:

```sh
cid=$(docker create reportportal/service-api:mp-local)
docker cp "$cid":/usr/app/service-api-exec.jar /tmp/rp-api-classes/app.jar
docker rm -f "$cid"
cd /tmp/rp-api-classes && unzip -q app.jar 'BOOT-INF/classes/*' 'BOOT-INF/lib/*' \
  && (cd BOOT-INF/classes && jar cf ../../service-api-classes.jar .)
```

Then, with `JAVA_HOME` on a JDK Gradle supports and a JDK 25 available to the toolchain:

```sh
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew jar \
  -Porg.gradle.java.installations.paths=$(/usr/libexec/java_home -v 25)
```

`-PrpApiDir=…` points the build at those classes if they are not in `/tmp/rp-api-classes`.

## Status

Built, published to a local marketplace registry, and installed through the ReportPortal
plugins page end to end: PF4J resolved and started it, `integration_type` carries the row with
`allowedCommands: ["TemplateCommand"]`, and the jar sits in the instance's plugins directory.

This is a port for a build that has not shipped. When `plugin-api` catches up with the rename,
the right move is to delete this branch and go back to the artifact.
