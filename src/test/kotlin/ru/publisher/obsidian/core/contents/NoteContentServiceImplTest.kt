package ru.publisher.obsidian.core.contents

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.publisher.obsidian.core.contents.frontmatter.NoteFieldType
import ru.publisher.obsidian.core.contents.frontmatter.NoteFieldValue
import ru.publisher.obsidian.core.notes.Note
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val TEST_VAULT_NAME = "frontMatterTest"

/**
 * Тесты [[NoteContentServiceImpl]]
 */
class NoteContentServiceImplTest {

    private lateinit var service: NoteContentServiceImpl

    @BeforeEach
    fun setUp() {
        val resourceUrl = this::class.java.classLoader.getResource(TEST_VAULT_NAME)
        val vaultPath = File(resourceUrl!!.toURI()).absolutePath
        service = NoteContentServiceImpl(vaultPath)
    }

    @Test
    fun `note content should be parsed correctly`() {
        val note = note("testFrontMatterFieldsMap.md")
        val content = service.getContent(note)

        assertEquals("Content of test note", content.body)
        val fields = content.frontmatter.fields.associateBy { it.name }
        assertTrue { fields.size == 7 }

        // tags
        val tags = (fields["tags"]?.value as NoteFieldValue.ListValue).value
        assertEquals(listOf("test_tag", "test_tag_2"), tags)
        assertEquals(NoteFieldType.LIST, fields["tags"]?.type)

        // string field
        assertEquals("some text", (fields["string_field"]?.value as NoteFieldValue.StringValue).value)
        assertEquals(NoteFieldType.STRING, fields["string_field"]?.type)

        // number fields
        assertEquals(1.toBigDecimal(), (fields["number_field_int"]?.value as NoteFieldValue.NumberValue).value)
        assertEquals(NoteFieldType.NUMBER, fields["number_field_int"]?.type)

        assertEquals(1.toBigDecimal(), (fields["number_field_float"]?.value as NoteFieldValue.NumberValue).value)
        assertEquals(NoteFieldType.NUMBER, fields["number_field_float"]?.type)

        // boolean
        assertEquals(true, (fields["boolean_field_true"]?.value as NoteFieldValue.BooleanValue).value)
        assertEquals(NoteFieldType.BOOLEAN, fields["boolean_field_true"]?.type)

        assertEquals(false, (fields["boolean_field_false"]?.value as NoteFieldValue.BooleanValue).value)
        assertEquals(NoteFieldType.BOOLEAN, fields["boolean_field_false"]?.type)

        // list
        val listValues = (fields["list_field"]?.value as NoteFieldValue.ListValue).value
        assertEquals(listOf("item1", "item2", "item3"), listValues)
        assertEquals(NoteFieldType.LIST, fields["list_field"]?.type)
    }

    @Test
    fun `note content should handle empty body`() {
        val note = note("testEmptyBody.md")
        val content = service.getContent(note)

        assertEquals("", content.body)

        val fields = content.frontmatter.fields.associateBy { it.name }
        assertEquals(true, (fields["boolean_field"]?.value as NoteFieldValue.BooleanValue).value)
        assertEquals(NoteFieldType.BOOLEAN, fields["boolean_field"]?.type)
    }

    @Test
    fun `note content should handle empty frontmatter`() {
        val note = note("testEmptyFrontMatter.md")
        val content = service.getContent(note)

        assertEquals(emptyList(), content.frontmatter.fields)
        assertEquals("emptyFrontMatter", content.body)
    }

    private fun note(fileName: String) = Note(
        id = "",
        fullName = fileName,
    )
}
