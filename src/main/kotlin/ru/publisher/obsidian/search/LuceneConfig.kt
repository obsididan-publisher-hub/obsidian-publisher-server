package ru.publisher.obsidian.search

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.store.ByteBuffersDirectory
import org.apache.lucene.store.Directory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LuceneConfig {

    @Bean
    fun luceneAnalyzer(): Analyzer = StandardAnalyzer()

    @Bean
    fun luceneDirectory(): Directory = ByteBuffersDirectory()
}
