<img width="82" align="left" src="https://raw.githubusercontent.com/kryptonbutterfly/Localizer4J/master/resources/icon_82x82.png"/>

# Localizer4J [![Maven Package](https://github.com/kryptonbutterfly/Localizer4J/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/kryptonbutterfly/Localizer4J/actions/workflows/maven-publish.yml)
A Language file Editor with special sauce for java projects.

This program enables you to edit language files.
Supported file types are `.properties`, `.json` and any key-value based files using delimiters, with UTF-8 encoding.
When editing a language file the application will indicate translations missing from the current language file.

When creating translations for a java project the sources can be linked.
After linking the editor will search for all String literals in the program and display the occurences of translations within the source code.

## Getting the latest release

```xml
<repository>
  <id>github</id>
  <url>https://maven.pkg.github.com/kryptonbutterfly/maven-repo</url>
</repository>
```

```xml
<dependency>
  <groupId>de.tinycodecrank</groupId>
  <artifactId>localizer4j</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Downloads

java version | app version | jar | deb
:----------: | :---------: | :-: | :-:
18+          | 3.0.0       | [localizer4j-3.0.0.jar](https://github.com/kryptonbutterfly/Localizer4J/releases/download/v3.0.0/localizer4j-3.0.0.jar) | [Localizer4J-3.0.0.deb](https://github.com/kryptonbutterfly/Localizer4J/releases/download/v3.0.0/Localizer4J-3.0.0.deb)
18+          | 2.4.0       | [Localizer4J.jar](https://github.com/kryptonbutterfly/Localizer4J/releases/download/v2.4.0/Localizer4J.jar) | [Localizer4J-2.4.0.deb](https://github.com/kryptonbutterfly/Localizer4J/releases/download/v2.4.0/Localizer4J-2.4.0.deb)
