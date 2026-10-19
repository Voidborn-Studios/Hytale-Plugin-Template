# Hytale Plugin Template

An example project and template for making mods and plugins for Hytale using Gradle and the Smolder plugin.

> **⚠️ Note: Early Access**  
> Hytale and its modding ecosystem are in active development. Features may evolve as new game builds and tooling versions are released.

---

## 🚀 Getting Started / Requirements

1. **Java 25** (JDK 25) installed and configured in your environment.
2. **Hytale** installed via the official launcher.
3. **IntelliJ IDEA** or your preferred IDE.

---

## ⚙️ Configuring Your Project

To customize this template for your own plugin:

### 1. Project Name
Open [`settings.gradle.kts`](file:///f:/Github/Style-Core/settings.gradle.kts) and set `rootProject.name` to your plugin name (e.g. `MyPlugin`). This controls the generated jar name.

### 2. Plugin Properties
Open [`gradle.properties`](file:///f:/Github/Style-Core/gradle.properties) and update the metadata:
- `maven_group`: your package namespace (e.g. `com.example.myplugin`)
- `plugin_group`: plugin group identifier for Hytale manifest
- `plugin_name`: display name of your plugin
- `plugin_author`: author name
- `plugin_website`: project URL
- `plugin_main`: fully qualified main class path (e.g. `com.example.myplugin.MyPlugin`)
- `version`: version of your plugin

### 3. Java Package & Code
Rename `src/main/java/org/example/plugin` to match your package and update your plugin entrypoint extending `JavaPlugin`.

---

## 🛠️ Features & Tooling Included

- **Gradle Kotlin DSL**: Modern `build.gradle.kts` and `settings.gradle.kts`.
- **Smolder Integration**: Uses `fr.smolder.hytale.dev` (v0.1.0) for game SDK classpath and automatic manifest generation.
- **Dynamic Manifest**: No manual `manifest.json` maintenance needed; Smolder generates and updates it automatically from Gradle configuration.
- **Asset Pipeline (`.model`)**:
  Drop your raw assets into `.model/` and Gradle will automatically sync them into `src/main/resources/`:
  - `*.blockymodel` -> `Common/Items/`
  - `*.json` -> `Server/Item/Items/`
  - `textures/*.png` -> `Common/Items/`
  - `icons/*.png` -> `Common/Icons/ItemsGenerated/`
- **Logo Sync (`syncLogo`)**: Place a `logo.png` in the project root to automatically generate `icon.png`, `icon-256.png`, and custom UI icons on build.
- **Auto-Deploy to `.mods`**: Running `./gradlew jar` automatically copies the built artifact to `.mods` (if present or linked to Hytale).
- **Unit Testing**: Pre-configured with **JUnit 5** and `HytaleLogManager`.

---

## 💻 Gradle Commands

- **Build jar and deploy**:
  ```bash
  ./gradlew jar
  ```
- **Run tests**:
  ```bash
  ./gradlew test
  ```
- **Sync staging models/assets**:
  ```bash
  ./gradlew syncModelAssets
  ```
- **Sync logo icon**:
  ```bash
  ./gradlew syncLogo
  ```

---

## 📂 Project Structure

```text
├── .hytale/                  # Directory junction to your Hytale game folder (optional)
├── .mods/                    # Directory junction to Hytale UserData/Mods (optional)
├── .model/                   # Asset staging folder for 3D models, textures and icons
├── gradle/                   # Gradle wrapper
├── src/
│   ├── main/
│   │   ├── java/             # Plugin source code
│   │   └── resources/        # Plugin resources and assets
│   └── test/
│       └── java/             # JUnit 5 unit tests
├── build.gradle.kts          # Build configuration & tasks
├── gradle.properties         # Template properties & metadata
└── settings.gradle.kts       # Repositories & project root name
```
