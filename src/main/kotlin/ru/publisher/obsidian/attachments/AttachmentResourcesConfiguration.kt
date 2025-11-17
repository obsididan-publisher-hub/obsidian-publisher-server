package ru.publisher.obsidian.attachments

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.publisher.obsidian.core.notes.NoteUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

const val ATTACHMENTS = "attachments"

@Configuration
class AttachmentResourcesConfiguration(
    @param:Value("\${obsidian.vault.path}") private val vaultPath: String,
    @param:Value("\${notes.attachments.path}") private val attachmentPath: String
) {

    private val logger = LoggerFactory.getLogger(AttachmentResourcesConfiguration::class.java)

    @Bean(ATTACHMENTS)
    fun attachmentResources(): Map<String, Attachment> {
        val root = File(vaultPath)
        require(root.exists()) { "Vault directory $vaultPath does not exist" }

        val targetDir = File(attachmentPath)
        if (!targetDir.exists()) targetDir.mkdirs()

        logger.info("Start processing attachments in vault: $vaultPath")
        logger.info("Copying attachments to: ${targetDir.absolutePath}")

        val attachments = HashMap<String, Attachment>()

        root.walkTopDown()
            .onEnter { !it.isHidden }
            .filter { it.isFile }
            .forEach {
                val ext = AttachmentExtension.fromExtension(it.extension) ?: return@forEach
                try {
                    logger.info("Copying attachment: ${it.path}")
                    val fullName = it.relativeTo(root).path
                    val attachmentId = NoteUtils.calculateResourceId(fullName)
                    val newFileName = "$attachmentId.${ext.extentionStr}"
                    val targetPath = targetDir.toPath().resolve(newFileName)
                    Files.copy(
                        it.toPath(),
                        targetPath,
                        StandardCopyOption.REPLACE_EXISTING
                    )
                    attachments[attachmentId] = Attachment(
                        attachmentId = attachmentId,
                        fullName = fullName,
                        path = targetPath,
                        extension = ext
                    )
                } catch (e: Exception) {
                    logger.error("Failed to process attachment file: ${it.absolutePath}", e)
                }
            }
        return attachments.toMap()
    }
}
