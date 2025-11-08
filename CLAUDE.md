# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a collection of plugins for logbook-kai (航海日誌), a Japanese KanColle game companion application. The project uses a multi-module Gradle build structure with Java 21 and JavaFX.

## Build Commands

**Prerequisites**: Place `logbook-kai.jar` in the `lib/` folder before building.

```bash
# Build all plugins (creates fat JARs with dependencies)
./gradlew shadowJar

# Build specific plugin
./gradlew :slack:shadowJar
./gradlew :discord:shadowJar
./gradlew :rankingchart:shadowJar

# Run tests
./gradlew test

# Run tests for specific plugin
./gradlew :rankingchart:test

# Clean build artifacts
./gradlew clean
```

## Project Structure

The repository contains 4 Gradle subprojects:

- **commons**: Shared utilities used by all plugins
  - `ConfigLoader`: Handles config loading with proper classloader context
  - `StageUtil`: JavaFX stage/window utilities

- **slack**: Slack notification plugin for expedition/dock completion
  - Uses Slack SDK for Java to send webhook notifications
  - Uses RxJava for periodic polling

- **discord**: Discord notification plugin (same functionality as Slack)
  - Uses plain HTTP webhooks (no SDK dependency)
  - Shares notification logic pattern with Slack plugin

- **rankingchart**: Ranking chart visualization plugin
  - Tracks player ranking over time with SQLite database
  - Implements obfuscated ranking score decoding
  - Provides chart visualization and CSV/TSV export

## Plugin Architecture

All plugins follow logbook-kai's plugin system:

1. **Lifecycle Hook**: Plugins implement `logbook.plugin.lifecycle.StartUp` for initialization
   - Example: `NotificationController` starts RxJava polling on startup

2. **API Listeners**: Plugins implement `logbook.api.APIListenerSpi` to intercept game API calls
   - Annotated with `@API({"/kcsapi/api_..."})` to specify which endpoints to listen to
   - Example: `RankingListener` intercepts ranking API calls

3. **Menu Extensions**: Plugins implement `logbook.plugin.gui.MainExtMenu` to add menu items
   - Creates JavaFX MenuItem that opens plugin configuration windows
   - FXML files define UI layout, Controller classes handle logic

4. **Configuration**: Plugin configs extend serializable beans and are loaded via:
   - `ConfigLoader.load(ConfigClass.class, DefaultSupplier)` from commons module
   - This handles classloader context switching to avoid ClassNotFoundException

## Key Implementation Patterns

### Notification Plugins (Slack/Discord)

- Both use identical logic for polling game state via RxJava `Flowable.interval()`
- Track timestamps to avoid duplicate notifications
- Integrate with main app's `AppConfig.get().getRemind()` for reminder intervals
- Notification controllers listen to:
  - Mission (expedition) completion via `DeckPortCollection`
  - Dock repair completion via `NdockCollection`

### Ranking Chart Plugin

- **Data Flow**: API intercept → decode obfuscated score → store in SQLite → render chart
- **Auto-detection**: When 1-100 rank data is collected, automatically detects rate factor
- **Manual Override**: Users can manually set rate factor via config UI
- The ranking score obfuscation algorithm is decoded in `Calculator.calcRate()`
- Database schema is initialized in `DatabaseInitializer` (via ServiceLoader)

### JavaFX Controllers

- Controllers are instantiated by FXML loader, not manually
- Use `@FXML` annotations for UI element injection
- Config windows use bidirectional bindings with RxJavaFX
- Commons provides `StageUtil.show()` to properly open dialogs with correct classloader

## Testing

- Uses JUnit 5 (Jupiter) for test framework
- Mockito for mocking dependencies
- PowerMock for static method mocking (rankingchart tests)
- Test resources available in `src/test/resources/`

## Dependencies

Key shared dependencies across plugins:
- **Lombok**: Annotation processing for boilerplate reduction
- **RxJava 2.x**: Reactive programming (polling, event handling)
- **RxJavaFX**: JavaFX bindings for reactive streams
- **Log4j2**: Logging (provided by logbook-kai)
- **JavaFX**: UI framework (provided by logbook-kai)

Plugin-specific:
- Slack: `slack-api-client` SDK
- RankingChart: `sqlite-jdbc`, `javax.json`

## Distribution

Plugins are distributed as fat JARs (via shadowJar):
- `slack-x.y.z-all.jar` → renamed to `slack.jar`
- `discord-x.y.z-all.jar` → renamed to `discord.jar`
- `rankingchart-x.y.z-all.jar` → renamed to `rankingchart.jar`

Users place these in logbook-kai's `plugins/` folder and restart the application.
