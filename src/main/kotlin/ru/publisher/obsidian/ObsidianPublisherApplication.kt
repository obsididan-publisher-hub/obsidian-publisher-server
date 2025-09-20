package ru.publisher.obsidian

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ObsidianPublisherApplication

fun main(args: Array<String>) {
	runApplication<ObsidianPublisherApplication>(*args)
}
