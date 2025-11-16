package ru.publisher.obsidian.core.notes

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Строит граф заметок по переданному хранилищу obsidian. Граф заметок представляет из себя карту из идентификатора
 * заметки [Note.id] и самой заметки [Note]
 *
 * @author fenya
 * @since 2025.09.21
 */
class GraphBuilder(private val vaultPath: String, private val ignoredDirectories: List<String>) {

    private val logger: Logger = LoggerFactory.getLogger(GraphBuilder::class.java)

    /**
     * Строит граф заметок на основе переданного хранилища
     */
    fun build(): Map<String, Note> {
        val graph = HashMap<String, Note>()
        val root = File(vaultPath)
        require(root.exists()) { "vault directory $vaultPath is not exist" }
        logger.info("Start processing vault $vaultPath")
        root.walkTopDown()
            .onEnter { !it.isHidden && !(ignoredDirectories.contains(it.name)) }
            .filter { it.isFile && it.extension == NOTE_EXTENSION } // Оставляем только md-файлы
            .forEach { file ->
                try {
                    val fullName: String = file.relativeTo(root).path
                    logger.info("processing $fullName")
                    val noteId = NoteUtils.calculateResourceId(fullName.substringBeforeLast('.'))

                    val extractedLinks: Set<Link> = NoteUtils.extractLinks(file.readText())
                        .associateBy { it.path }
                        .values
                        .toSet()

                    val outgoingLinks: Set<String> = extractedLinks
                        .filter { it.extension == NOTE_EXTENSION }
                        .map { link -> NoteUtils.calculateResourceId(link.path) }
                        .toSet()

                    val note = Note(
                        id = noteId,
                        fullName = fullName,
                        outgoingNotes = outgoingLinks,
                        incomingNotes = mutableSetOf()
                    )
                    graph[noteId] = note
                } catch (e: Exception) {
                    logger.error("Failed to read note file: ${file.absolutePath}", e)
                }
            }

        // Заполняем входящие ссылки
        graph.values.forEach { note ->
            note.outgoingNotes.forEach { targetId ->
                graph[targetId]?.incomingNotes?.add(note.id)
            }
        }
        logger.info("Vault processing ended. ${graph.size} notes totally processed")
        return graph
    }
}