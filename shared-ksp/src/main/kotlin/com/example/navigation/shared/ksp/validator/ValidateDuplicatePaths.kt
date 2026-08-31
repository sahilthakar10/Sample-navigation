package com.example.navigation.shared.ksp.validator

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.example.navigation.shared.ksp.getDeepLinkAnnotation
import com.example.navigation.shared.ksp.getPath

/**
 * Compile-time guard against two routes declaring the same deep link path.
 * Mirrors `sfl-cmp-navigation-ksp/.../validator/ValidateDuplicatePaths.kt`.
 */
fun validateDuplicatePaths(logger: KSPLogger, classes: List<KSClassDeclaration>) {
    val seen = mutableMapOf<String, String>()
    classes.forEach { cls ->
        val path = cls.getDeepLinkAnnotation()?.getPath() ?: return@forEach
        val name = cls.qualifiedName?.asString() ?: cls.simpleName.asString()
        val existing = seen[path]
        if (existing != null) {
            logger.error("Duplicate @CmpDeepLink path \"$path\" on $name (already used by $existing)", cls)
        } else {
            seen[path] = name
        }
    }
}
