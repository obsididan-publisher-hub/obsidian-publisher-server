package ru.publisher.obsidian.core.notes

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class GraphBuilderTest {

    private lateinit var vaultPath: String

    @BeforeEach
    fun setup() {
        val resourceUrl = this::class.java.classLoader.getResource("graphBuilderTest")
        requireNotNull(resourceUrl) { "Test vault 'graphBuilderTest' not found in resources" }
        vaultPath = File(resourceUrl.toURI()).absolutePath
    }

    @Test
    fun `build should create graph without errors`() {
        val builder = GraphBuilder(vaultPath)
        val graph = builder.build()

        // Базовые проверки
        assertNotNull(graph)
        assertTrue(graph.isNotEmpty(), "Graph must not be empty")

        // Пример: проверить, что у всех заметок id не пустой
        graph.values.forEach { note ->
            assertTrue(note.id.isNotBlank(), "Note id must not be blank")
            assertTrue(note.fullName.endsWith(".md"), "Note filename must have .md extension")
        }
    }

    @Test
    fun `graph should contain valid incoming and outgoing links`() {
        val builder = GraphBuilder(vaultPath)
        val graph = builder.build()

        // Примерная проверка согласованности ссылок
        graph.values.forEach { note ->
            note.outgoingNotes.forEach { targetId ->
                assertTrue(
                    graph.containsKey(targetId),
                    "Outgoing note $targetId from ${note.id} must exist in graph"
                )
                assertTrue(
                    graph[targetId]!!.incomingNotes.contains(note.id),
                    "Target note $targetId must contain incoming link from ${note.id}"
                )
            }
        }
    }
}
