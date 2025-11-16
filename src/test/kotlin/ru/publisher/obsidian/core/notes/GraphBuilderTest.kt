package ru.publisher.obsidian.core.notes

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private const val TEST_VAULT1_NAME = "graphBuilderTest"
private const val TEST_VAULT2_NAME = "graphBuilderTestDirs"

/**
 * Тесты для [GraphBuilder]
 */
class GraphBuilderTest {

    @Test
    fun `graph should match expected structure`() {
        val expectedGraph = setOf(
            Note(
                id = "a875c0ed68518715ceaa62bd54b79e84",
                fullName = "orphanNote.md",
                outgoingNotes = mutableSetOf(),
                incomingNotes = mutableSetOf()
            ),
            Note(
                id = "9ef6e5e18112cf3736e048daa947fcdc",
                fullName = "note2.md",
                outgoingNotes = mutableSetOf(),
                incomingNotes = mutableSetOf("d6089d6c1295ad5fb7d7ae771c0ad821")
            ),
            Note(
                id = "d6089d6c1295ad5fb7d7ae771c0ad821",
                fullName = "note1.md",
                outgoingNotes = mutableSetOf("9ef6e5e18112cf3736e048daa947fcdc", "2be3b632f1f670bb4034c18e43f7ffd9"),
                incomingNotes = mutableSetOf()
            ),
            Note(
                id = "71155da47b935d4ca1897a6d1cbfec2c",
                fullName = "selfLinkingNote.md",
                outgoingNotes = mutableSetOf("71155da47b935d4ca1897a6d1cbfec2c"),
                incomingNotes = mutableSetOf("71155da47b935d4ca1897a6d1cbfec2c")
            ),
            Note(
                id = "a9a9d3981a995ad5d90d10f2508124cd",
                fullName = "cycleNote1.md",
                outgoingNotes = mutableSetOf("f6e9e5fcb3c374aadbcb19caf16472f5"),
                incomingNotes = mutableSetOf("30cbb9a2918f3c6d892fac398a1451ec")
            ),
            Note(
                id = "f6e9e5fcb3c374aadbcb19caf16472f5",
                fullName = "cycleNote2.md",
                outgoingNotes = mutableSetOf("30cbb9a2918f3c6d892fac398a1451ec"),
                incomingNotes = mutableSetOf("a9a9d3981a995ad5d90d10f2508124cd")
            ),
            Note(
                id = "30cbb9a2918f3c6d892fac398a1451ec",
                fullName = "cycleNote3.md",
                outgoingNotes = mutableSetOf("a9a9d3981a995ad5d90d10f2508124cd"),
                incomingNotes = mutableSetOf("f6e9e5fcb3c374aadbcb19caf16472f5")
            )
        ).associateBy { it.id }

        val resourceUrl = this::class.java.classLoader.getResource(TEST_VAULT1_NAME)
        val builder = GraphBuilder(File(resourceUrl!!.toURI()).absolutePath)
        val graph = builder.build()

        assertEquals(expectedGraph.keys, graph.keys)
        expectedGraph.forEach { (id, expectedNote) ->
            val actualNote = graph[id]!!
            assertEquals(expectedNote, actualNote)
        }
    }

    @Test
    fun `graph should match expected structure with same name notes in different dirs`() {

        val expectedGraph = setOf(
            Note(
                id = "098f6bcd4621d373cade4e832627b4f6",
                fullName = "test.md",
                outgoingNotes = mutableSetOf(
                    "aad653ca3ee669635f2938b73098b6d7",
                    "03309b1db5686101da70b264eaa5bd19",
                    "b0c638ea27434e2af5bf86fc6bd5fcf6"
                ),
                incomingNotes = mutableSetOf()
            ),
            Note(
                id = "aad653ca3ee669635f2938b73098b6d7",
                fullName = "note.md",
                outgoingNotes = mutableSetOf(),
                incomingNotes = mutableSetOf("098f6bcd4621d373cade4e832627b4f6")
            ),
            Note(
                id = "03309b1db5686101da70b264eaa5bd19",
                fullName = "dir1/note.md",
                outgoingNotes = mutableSetOf(),
                incomingNotes = mutableSetOf("098f6bcd4621d373cade4e832627b4f6")
            ),
            Note(
                id = "b0c638ea27434e2af5bf86fc6bd5fcf6",
                fullName = "dir2/note.md",
                outgoingNotes = mutableSetOf(),
                incomingNotes = mutableSetOf("098f6bcd4621d373cade4e832627b4f6")
            )
        ).associateBy { it.id }

        val resourceUrl = this::class.java.classLoader.getResource(TEST_VAULT2_NAME)
        val builder = GraphBuilder(File(resourceUrl!!.toURI()).absolutePath)
        val graph = builder.build()

        assertEquals(expectedGraph.keys, graph.keys)
        expectedGraph.forEach { (id, expectedNote) ->
            val actualNote = graph[id]!!
            assertEquals(expectedNote, actualNote)
        }
    }
}
