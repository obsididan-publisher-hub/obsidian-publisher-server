package ru.publisher.obsidian.core.notes

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Собирает все заметки, присутствующие в хранилище
 *
 * @author fenya
 * @since 2025.09.21
 */
class NotesCollector(private val vaultPath: String, private val ignoredDirectories: List<String>) {

    private val logger: Logger = LoggerFactory.getLogger(NotesCollector::class.java)

    /**
     * Обрабатывает и сохраняет все заметки, присутствующие в хранилище
     */
    fun collect(): Map<String, Note> {
        val notes = HashMap<String, Note>()
        val root = File(vaultPath)
        require(root.exists()) { "vault directory $vaultPath is not exist" }
        logger.info("Start processing vault $vaultPath")
        root.walkTopDown()
            .onEnter { !it.isHidden && !(ignoredDirectories.contains(it.name)) }
            .filter { it.isFile && it.extension == NOTE_EXTENSION } // Оставляем только md-файлы
            .forEach {
                try {
                    val fullName: String = it.relativeTo(root).path
                    logger.info("processing $fullName")
                    val noteId = NoteUtils.calculateResourceId(fullName)
                    notes[noteId] = Note(
                        id = noteId,
                        fullName = fullName,
                    )
                } catch (e: Exception) {
                    logger.error("Failed to read note file: ${it.absolutePath}", e)
                }
            }
        logger.info("Vault processing ended. ${notes.size} notes totally processed.")
        return notes
    }
}