@file:Suppress("unused")

package apc.java

import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.*
import java.nio.file.FileVisitResult.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.util.*

fun matcherList(vararg matcher: String) = matcher.map { FileSystems.getDefault().getPathMatcher("glob:$it") }

val Path.lastModifiedTime
    get() = Files.getAttribute(this, "lastModifiedTime") as FileTime

val Path.properties
    get() = Properties().apply {
        try {
            toFile().inputStream().use(::load)
        } catch (e: FileNotFoundException) {
        }
    }

operator fun Path.plus(other: Path) = resolve(other)!!
operator fun Path.plus(other: String) = resolve(other)!!
operator fun Path.minus(parent: Path) = parent.relativize(this)!!
fun Path.matchName(matcher: PathMatcher) = matcher.matches(fileName)

infix fun Path.copyTo(target: Path) {
    Files.copy(this, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
}

fun Path.copyRecursively(target: Path, includeHidden: Boolean = false) {
    Files.walkFileTree(this, object : SimpleFileVisitor<Path>() {
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes) = dir.copy()
        override fun visitFile(file: Path, attrs: BasicFileAttributes) = file.copy()
        private fun Path.copy() = if (!fileName.startsWith(".") || includeHidden) {
            copyTo(target + (this - this@copyRecursively))
            CONTINUE
        } else {
            SKIP_SUBTREE
        }
    })
}

fun Path.deleteEmptyDirectories() {
    Files.walkFileTree(this, object : SimpleFileVisitor<Path>() {
        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
            if (dir != this@deleteEmptyDirectories) {
                try {
                    Files.delete(dir)
                } catch (e: Exception) {
                }
            }
            return CONTINUE
        }
    })
}

operator fun Path.invoke(vararg command: String) {
    val process = ProcessBuilder(*command).directory(toFile()).redirectErrorStream(true).start()
    process.inputStream.bufferedReader().use { it.forEachLine(::println) }
    process.waitFor()
}