plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

configurations {
    create("common")
    create("shadowCommon")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentNeoForge").extendsFrom(configurations["common"])
}

loom {
    neoForge{

    }
    enableTransitiveAccessWideners.set(true)
    //silentMojangMappingsLicense()
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
//    mixin {
//        defaultRefmapName.set("mixins.${project.name}.refmap.json")
//    }
}

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://neoforged.forgecdn.net/releases")
}

dependencies {
    neoForge("net.neoforged:neoforge:${project.properties["neoforge_version"]}")
    modApi("dev.architectury:architectury-neoforge:${project.properties["architectury_version"]}")

    "common"(project(":common", "namedElements")) { isTransitive = false }
    "shadowCommon"(project(":common", "transformProductionNeoForge")) { isTransitive = false }

    modApi("maven.modrinth:ordsPcFz:${project.properties["kff_version"]}") //kotlinforforge

    include(modApi("me.shedaniel.cloth:cloth-config-neoforge:${project.properties["cloth_config_version"]}")!!)
    modImplementation("com.cobblemon:neoforge:${project.properties["cobblemon_version"]}")
}

tasks {
    base.archivesName.set(base.archivesName.get() + "-neoforge")
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("generations/gg/generations/core/generationscore/neoforge/datagen/**")
        exclude("architectury-common.accessWidener")
        exclude("architectury.common.json")

        configurations = listOf(project.configurations.getByName("shadowCommon"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
    }

    jar.get().archiveClassifier.set("dev")

    sourcesJar {
        val commonSources = project(":common").tasks.sourcesJar
        duplicatesStrategy=DuplicatesStrategy.EXCLUDE
        dependsOn(commonSources)
        from(commonSources.get().archiveFile.map { zipTree(it) })
    }
}

components {
    java.run {
        if (this is AdhocComponentWithVariants)
            withVariantsFromConfiguration(project.configurations.shadowRuntimeElements.get()) { skip() }
    }
}
