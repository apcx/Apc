package apc.jar

import apc.java.*
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.size >= 2) {
        val project = args[1]
        when (args[0]) {
            "ignore" -> svnIgnore(project)
            "lint" -> lint(project)
            "zip" -> zipProject(project, args.size >= 3)
        }
    }
}

private val rootIgnore = matcherList(".*", "build", "gradle*", "local.properties", "*.iml")

fun svnIgnore(project: String = "") {
    val root = Paths.get(project)
    if (Files.isDirectory(root)) {
        root("svn", "ps", "svn:global-ignores", """.*
build
*.iml""", ".")
        root("svn", "ps", "svn:ignore", """gradle*
local.properties""", ".")
    }
}

fun lint(project: String = "") {
    val root = Paths.get(project)
    if (Files.isDirectory(root)) {
        root("$root/gradlew.bat", "lintDebug")
        Files.list(root).filter { Files.isDirectory(it) && rootIgnore.none(it::matchName) }.forEach {
            val xml = it + "build/reports/lint-results-debug.xml"
            if (Files.isRegularFile(xml)) {
                println("\nParsing: $xml\n")
                UnusedResources(xml)
            }
        }
    }
}

fun zipProject(project: String, copy: Boolean = false) {
    val source = Paths.get(project)
    if (Files.isDirectory(source)) {
        val target = source.resolveSibling("${source.fileName}_mini")
        copyProject(source, target)
        if (copy) println("Copied: ${target.toAbsolutePath()}")

        val zip = source.resolveSibling("${target.fileName}.zip")
        Files.deleteIfExists(zip)
        val targetFile = target.toFile()
        ZipFile(zip.toFile()).addFolder(targetFile, ZipParameters())

        if (!copy) targetFile.deleteRecursively()
    }
}

fun copyProject(source: Path, target: Path) {
    target.toFile().deleteRecursively()

    source copyTo target
    val vcs = matcherList(".git*", ".svn", "gradle.properties")
    val moduleIgnore = matcherList(".*", "build", "*.iml")
    Files.list(source).parallel().filter { vcs.any(it::matchName) || rootIgnore.none(it::matchName) }.forEach {
        val targetModule = target + it.fileName
        it copyTo targetModule
        if (Files.isDirectory(it)) {
            @Suppress("NestedLambdaShadowedImplicitParameter")
            Files.list(it).parallel().filter { moduleIgnore.none(it::matchName) }.forEach { it.copyRecursively(targetModule + it.fileName) }
        }
    }
    target.deleteEmptyDirectories()
}