import fr.smolder.hytale.gradle.Patchline
import org.gradle.external.javadoc.StandardJavadocDocletOptions

plugins {
    java
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
    id("fr.smolder.hytale.dev") version "0.1.0"
}

repositories {
    mavenCentral()
}

hytale {
    val hytaleHome = project.findProperty("hytale_home") as? String
        ?: System.getenv("HYTALE_HOME")
        ?: rootProject.file(".hytale").takeIf { it.exists() }?.absolutePath
    if (hytaleHome != null) {
        hytalePath.set(hytaleHome)
    }
    patchLine.set(Patchline.RELEASE)
    gameVersion.set("latest")
    autoUpdateManifest.set(true)
    minMemory.set("2G")
    maxMemory.set("4G")
    useAotCache.set(true)
    vineflowerVersion.set("1.11.2")
    decompileFilter.set(listOf("com/hypixel/**"))
    decompilerHeapSize.set("6G")
    includeDecompiledSources.set(true)

    manifest {
        group = (project.findProperty("plugin_group") as? String) ?: "Example"
        name = (project.findProperty("plugin_name") as? String) ?: project.name
        version = project.version.toString()
        description = (project.findProperty("plugin_description") as? String) ?: "An example plugin for Hytale!"

        author {
            name = (project.findProperty("plugin_author") as? String) ?: "YourName"
        }

        website = (project.findProperty("plugin_website") as? String) ?: "https://example.org"
        serverVersion = project.property("server_version") as String
        main = (project.findProperty("plugin_main") as? String) ?: "org.example.plugin.ExamplePlugin"
        includesAssetPack = (project.findProperty("includes_pack") as? String)?.toBoolean() ?: true
        disabledByDefault = false
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
    withJavadocJar()
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:-missing", "-quiet")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("java.util.logging.manager", "com.hypixel.hytale.logger.backend.HytaleLogManager")
}

val syncLogo by tasks.registering {
    val logoFile = file("logo.png")
    val resDir = file("src/main/resources")
    onlyIf { logoFile.exists() }
    if (logoFile.exists()) {
        inputs.file(logoFile)
        outputs.files(
            resDir.resolve("icon-256.png"),
            resDir.resolve("icon.png"),
            resDir.resolve("Common/UI/Custom/${project.name}.png")
        )
    }
    doLast {
        if (logoFile.exists()) {
            copy {
                from(logoFile)
                into(resDir)
                rename { "icon-256.png" }
            }
            copy {
                from(logoFile)
                into(resDir)
                rename { "icon.png" }
            }
            copy {
                from(logoFile)
                into(resDir.resolve("Common/UI/Custom"))
                rename { "${project.name}.png" }
            }
        }
    }
}

val syncModelAssets by tasks.registering {
    val modelDir = file(".model")
    val resDir = file("src/main/resources")
    onlyIf { modelDir.exists() }
    if (modelDir.exists()) {
        inputs.dir(modelDir)
    }

    doLast {
        if (!modelDir.exists()) return@doLast

        // 1. Icons: copy *.png from .model/icons to Common/Icons/ItemsGenerated/
        val iconsDir = modelDir.resolve("icons")
        if (iconsDir.exists()) {
            copy {
                from(iconsDir) {
                    include("**/*.png")
                }
                into(resDir.resolve("Common/Icons/ItemsGenerated"))
            }
        }

        // 2. Textures: copy *.png from .model/textures (or .model root) to Common/Items/
        val texturesDir = modelDir.resolve("textures")
        if (texturesDir.exists()) {
            copy {
                from(texturesDir) {
                    include("**/*.png")
                }
                into(resDir.resolve("Common/Items"))
            }
        }
        copy {
            from(modelDir) {
                include("*.png")
            }
            into(resDir.resolve("Common/Items"))
        }

        // 3. 3D Models: copy *.blockymodel to Common/Items/
        copy {
            from(modelDir) {
                include("**/*.blockymodel")
            }
            into(resDir.resolve("Common/Items"))
            eachFile {
                path = name
            }
            includeEmptyDirs = false
        }

        // 4. Item JSONs: copy *.json to Server/Item/Items/
        copy {
            from(modelDir) {
                include("**/*.json")
            }
            into(resDir.resolve("Server/Item/Items"))
            eachFile {
                path = name
            }
            includeEmptyDirs = false
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    dependsOn("updatePluginManifest")
    dependsOn(syncLogo)
    dependsOn(syncModelAssets)
}

tasks.named<Jar>("sourcesJar") {
    dependsOn("generateManifest")
    dependsOn(syncLogo)
}

tasks.named<Jar>("jar") {
    finalizedBy("deployMod")
}

tasks.register<Copy>("deployMod") {
    dependsOn("jar")
    onlyIf { rootProject.file(".mods").exists() }
    doFirst {
        val modsDir = rootProject.file(".mods")
        if (modsDir.exists()) {
            modsDir.listFiles { file ->
                file.name.startsWith("${project.name}-") && file.name.endsWith(".jar")
            }?.forEach { it.delete() }
        }
    }
    from(tasks.named("jar"))
    into(rootProject.file(".mods"))
}
