package com.example.navigation.shared.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.example.navigation.shared.ksp.generator.generateDeepLinks
import com.example.navigation.shared.ksp.generator.generateResolver
import com.example.navigation.shared.ksp.generator.generateSerializers
import com.example.navigation.shared.ksp.validator.validateDuplicatePaths

/**
 * Scans all `@CmpDeepLink`-annotated NavKey routes and generates the deep link
 * plumbing. Mirrors `sfl-cmp-navigation-ksp/.../CmpNavProcessor.kt`.
 */
class CmpNavProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val deepLinkClasses = resolver
            .getSymbolsWithAnnotation(CMP_DEEPLINK_ANNOTATION)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }
            .toList()

        if (deepLinkClasses.isEmpty()) return emptyList()

        validateDuplicatePaths(logger, deepLinkClasses)

        generateSerializers(codeGenerator, deepLinkClasses)
        generateDeepLinks(codeGenerator, deepLinkClasses)
        generateResolver(codeGenerator, deepLinkClasses)

        return emptyList()
    }
}
