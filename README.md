# Protected Items

Lightweight Paper plugin that allows operators to mark items as protected. Protected items cannot be dropped, lost on death, placed as blocks, used with right-click, or stored in regular containers.

## Requirements

- **Paper** 1.21+ (or compatible forks)
- Java 21

## Project Structure

```
src/main/java/ru/nyansus/mc/protected_items/
├── IndestructibleItems.java      # Plugin entry point
├── IndestructibleCommand.java    # Command dispatcher
├── IndestructibleListeners.java  # Event handlers
├── IndestructibleUtil.java       # PDC utilities
├── IndestructibleHelpTopic.java  # Custom /help topic
├── Messages.java                 # Locale-aware message loader
├── Permissions.java              # Permission constants
└── command/                      # Subcommand implementations
    ├── ICommand.java
    ├── HeldItemCommand.java
    ├── AddCommand.java
    ├── RemoveCommand.java
    ├── CheckCommand.java
    ├── ListCommand.java
    └── ListAllCommand.java
```

## Building

```bash
./gradlew build
```

The output JAR is placed in `build/libs/`.

## Installation

1. Build the plugin or download the JAR from [Releases](https://github.com/onitofu/protected-items/releases).
2. Place `protected-items-<version>.jar` into the server's `plugins/` directory.
3. Restart the server.

## Configuration

The plugin ships with two locale files (`messages_en.yml`, `messages_ru.yml`). The locale is selected per-player based on the client language setting, with English as the fallback. No additional configuration is required.

## Testing

Tests use **JUnit 4** and [MockBukkit](https://github.com/MockBukkit/MockBukkit).

```bash
./gradlew test
```

Coverage reports (JaCoCo) are generated automatically after tests:

```bash
./gradlew jacocoTestReport
# HTML report: build/reports/jacoco/test/html/index.html
```

## Code Style

The project uses [Checkstyle](https://checkstyle.org/) with a configuration based on Google Java Style (4-space indent, 120-char line length, no Javadoc enforcement). Run it with:

```bash
./gradlew checkstyleMain checkstyleTest
```

## CI

GitHub Actions workflow (`.github/workflows/build.yml`) runs on pushes and PRs to `main`/`master`:

1. Checkstyle
2. Build + tests
3. JAR artifact upload

## License

MIT
