package ru.publisher.obsidian.core.contents

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.publisher.obsidian.core.contents.frontmatter.Frontmatter
import ru.publisher.obsidian.core.contents.frontmatter.NoteField
import ru.publisher.obsidian.core.contents.frontmatter.NoteFieldType
import ru.publisher.obsidian.core.contents.frontmatter.NoteFieldValue
import ru.publisher.obsidian.core.notes.Note
import java.io.File

@Service
class NoteContentServiceImpl(
    @Value("\${obsidian.vault.path}")
    private val vaultPath: String
) : NoteContentService {

    private val yamlMapper: ObjectMapper = ObjectMapper(YAMLFactory())

    private val frontMatterRegex = Regex("^---\\s*\\n(.*?)\\n---\\s*\\n(.*)$", RegexOption.DOT_MATCHES_ALL)

    override fun getContent(note: Note): NoteContent {
        val file = File("$vaultPath/${note.fullName}")
        val text = file.readText()
        val matchResult = frontMatterRegex.find(text)
        val frontMatterText = matchResult?.groupValues?.getOrElse(1) { "" } ?: ""
        val frontMatter = extractFrontMatter(frontMatterText)
        val bodyText = matchResult?.groupValues?.getOrElse(2) { "" } ?: ""
        return NoteContent(frontMatter, bodyText)
    }

    /**
     * @return конвертированный в [[Frontmatter]] frontmatter obsidian
     */
    private fun extractFrontMatter(frontMatterText: String): Frontmatter {
        if (frontMatterText.isEmpty()) return Frontmatter(emptyList())

        val rootNode = yamlMapper.readTree(frontMatterText)

        val fields = rootNode.fieldNames().asSequence().map { key ->
            val valueNode = rootNode[key]
            val (type, noteFieldValue) = when {
                valueNode.isTextual -> NoteFieldType.STRING to NoteFieldValue.StringValue(valueNode.asText())
                valueNode.isNumber -> NoteFieldType.NUMBER to NoteFieldValue.NumberValue(
                    valueNode.asText().toBigDecimal()
                )

                valueNode.isBoolean -> NoteFieldType.BOOLEAN to NoteFieldValue.BooleanValue(valueNode.asBoolean())
                valueNode.isArray -> {
                    val list = valueNode.mapNotNull { it.asText() }
                    NoteFieldType.LIST to NoteFieldValue.ListValue(list)
                }

                else -> NoteFieldType.STRING to NoteFieldValue.StringValue(valueNode.toString())
            }
            NoteField(key, type, noteFieldValue)
        }.toList()

        return Frontmatter(fields)
    }
}
