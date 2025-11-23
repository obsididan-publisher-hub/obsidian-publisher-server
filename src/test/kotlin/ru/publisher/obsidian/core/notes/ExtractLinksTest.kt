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
            - [[#anchor]]
        """.trimIndent()

        val links = NoteUtils.extractLinks(text)

        val expected = setOf(
            Link("[[note2]]", "note2", null, null),
            Link("[[note2|alias]]", "note2", null, "alias"),
            Link("[[note2#title]]", "note2", "title", null),
            Link("[[note2#title|aliasOnTitle]]", "note2", "title", "aliasOnTitle"),
            Link("[[note2.md]]", "note2.md", null, null),
            Link("[[#anchor]]", null, "anchor", null),
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
            Link("[[dir1/note3|note3]]", "dir1/note3", null, "note3"),
            Link("[[dir2/note3|note3]]", "dir2/note3", null, "note3"),
            Link("[[note3]]", "note3", null, null)
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
