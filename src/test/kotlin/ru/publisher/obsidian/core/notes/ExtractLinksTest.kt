package ru.publisher.obsidian.core.notes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Тесты на метод [NoteUtils.extractLinks]
 */
class ExtractLinksTest {

    @Test
    fun `should extract simple links with section and alias`() {
        // [note2](note2) обычные markdown-ссылки игнорируем
        val text = """
            - [[note2]]
            - [[note2|alias]]
            - [[note2#title]]
            - [[note2#title|aliasOnTitle]]
            - [[note2.md]]
        """.trimIndent()

        val links = NoteUtils.extractLinks(text)

        val expected = setOf(
            NoteLink("[[note2]]", "note2", null, null),
            NoteLink("[[note2|alias]]", "note2", null, "alias"),
            NoteLink("[[note2#title]]", "note2", "title", null),
            NoteLink("[[note2#title|aliasOnTitle]]", "note2", "title", "aliasOnTitle"),
            NoteLink("[[note2.md]]", "note2.md", null, null)
        )

        assertEquals(expected, links)
    }

    @Test
    fun `should distinguish links with same note name in different dirs`() {
        val text = """
            - [[dir1/note3|note3]]
            - [[dir2/note3|note3]]
            - [[note3]]
        """.trimIndent()

        val links = NoteUtils.extractLinks(text)

        val expected = setOf(
            NoteLink("[[dir1/note3|note3]]", "dir1/note3", null, "note3"),
            NoteLink("[[dir2/note3|note3]]", "dir2/note3", null, "note3"),
            NoteLink("[[note3]]", "note3", null, null)
        )

        assertEquals(expected, links)
    }

    @Test
    fun `should ignore escaped links`() {
        // [[note4\]] воспринимается как ссылка и самим obsidian (1.9.12)
        val text = """
            - \[[note4]]
            - [\[note4]]
            - [[note4]\]]
        """.trimIndent()

        val links = NoteUtils.extractLinks(text)
        print(links)
        assertTrue(links.isEmpty(), "Escaped links must not be parsed")
    }
}
