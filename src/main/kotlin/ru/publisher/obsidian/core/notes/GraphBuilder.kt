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
class GraphBuilder(private val vaultPath: String) {

    private val logger: Logger = LoggerFactory.getLogger(GraphBuilder::class.java)

    /**
     * Строит граф заметок на основе переданного хранилища
     */
    fun build(): Map<String, Note> {
        val graph = HashMap<String, Note>()
        val root = File(vaultPath)
        require(root.exists()) { "vault directory $vaultPath is not exist" }

        root.walkTopDown()
            .onEnter { !it.isHidden } // Не заходим в скрытые директории
            .filter { it.isFile && it.extension == NOTE_EXTENSION } // Оставляем только md-файлы
            .forEach { file ->
                try {
                    val noteId = NoteUtils.calculateNoteId(file.relativeTo(root).path)
                    val fullName = file.relativeTo(root).path

                    val extractedLinks: Set<NoteLink> = NoteUtils.extractLinks(file.readText())
                        .associateBy { it.path }
                        .values
                        .toSet()

                    val outgoingLinks: MutableSet<String> = extractedLinks
                        .map { link -> NoteUtils.calculateNoteId(link.path) } // считаем id
                        .toMutableSet()

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

        return graph
    }
}