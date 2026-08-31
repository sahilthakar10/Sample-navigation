package com.example.navigation.shared.ksp.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.example.navigation.shared.ksp.GENERATED_PACKAGE
import com.example.navigation.shared.ksp.createKotlinFile

/**
 * Generates `CmpNavSerializers.kt` -> `cmpSerializersModule` (polymorphic NavKey).
 * Mirrors `sfl-cmp-navigation-ksp/.../generator/SerializerGenerator.kt`.
 */
fun generateSerializers(codeGenerator: CodeGenerator, classes: List<KSClassDeclaration>) {
    val imports = mutableSetOf<String>()
    val subclasses = classes.map { cls ->
        val parent = cls.parentDeclaration?.simpleName?.asString()!!
        val screen = cls.simpleName.asString()
        imports += cls.parentDeclaration!!.qualifiedName!!.asString()
        "    subclass($parent.$screen::class, $parent.$screen.serializer())"
    }

    codeGenerator.createKotlinFile("CmpNavSerializers") { w ->
        w.appendLine("package $GENERATED_PACKAGE\n")
        w.appendLine("import com.example.navigation.shared.nav.NavKey")
        w.appendLine("import kotlinx.serialization.modules.*")
        imports.forEach { w.appendLine("import $it") }
        w.appendLine()
        w.appendLine("val cmpSerializersModule = SerializersModule {")
        w.appendLine("  polymorphic(NavKey::class) {")
        subclasses.forEach { w.appendLine(it) }
        w.appendLine("  }")
        w.appendLine("}")
    }
}
