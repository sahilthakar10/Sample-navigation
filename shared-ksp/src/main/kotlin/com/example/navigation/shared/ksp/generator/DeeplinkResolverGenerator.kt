package com.example.navigation.shared.ksp.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.example.navigation.shared.ksp.GENERATED_PACKAGE
import com.example.navigation.shared.ksp.createKotlinFile
import com.example.navigation.shared.ksp.isNullable
import com.example.navigation.shared.ksp.kotlinTypeString
import com.example.navigation.shared.ksp.needsJsonEncoding
import com.example.navigation.shared.ksp.paramName
import com.example.navigation.shared.ksp.queryParamName

/**
 * READ side generator -> `CmpDeepLinkResolver` (deep link string -> typed NavKey).
 * Uses ktor `Url` exactly like `sfl-cmp-navigation-ksp/.../generator/DeeplinkResolverGenerator.kt`.
 */
fun generateResolver(codeGenerator: CodeGenerator, classes: List<KSClassDeclaration>) {
    val imports = mutableSetOf<String>()
    val mappings = classes.mapNotNull { cls ->
        val parentDecl = cls.parentDeclaration ?: return@mapNotNull null
        val parentName = parentDecl.simpleName.asString()
        val screenName = cls.simpleName.asString()

        parentDecl.qualifiedName?.asString()?.let { imports += it }

        val params = cls.primaryConstructor?.parameters.orEmpty()

        val ctor = if (params.isEmpty()) {
            "$parentName.$screenName"
        } else {
            val args = params
                .filter { it.queryParamName.isNotBlank() }
                .joinToString(", ") { p ->
                    val queryKey = p.queryParamName
                    val paramName = p.paramName
                    val typeStr = p.kotlinTypeString
                    val baseType = typeStr.removeSuffix("?")
                    val isNullable = p.isNullable

                    val conversion = when {
                        p.needsJsonEncoding -> {
                            if (isNullable) {
                                "uri.parameters[\"$queryKey\"]?.takeIf { it.isNotEmpty() }?.let { Json.decodeFromString<$baseType>(it) }"
                            } else {
                                "uri.parameters[\"$queryKey\"]?.takeIf { it.isNotEmpty() }?.let { Json.decodeFromString<$baseType>(it) }!!"
                            }
                        }
                        baseType in listOf("kotlin.String", "String") -> {
                            if (isNullable) {
                                "uri.parameters[\"$queryKey\"]?.takeIf { it.isNotEmpty() }"
                            } else {
                                "uri.parameters[\"$queryKey\"]?.takeIf { it.isNotEmpty() } ?: \"\""
                            }
                        }
                        baseType in listOf("kotlin.Boolean", "Boolean") -> {
                            if (isNullable) "uri.parameters[\"$queryKey\"]?.toBoolean()"
                            else "uri.parameters[\"$queryKey\"]?.toBoolean() ?: false"
                        }
                        baseType in listOf("kotlin.Int", "Int") -> {
                            if (isNullable) "uri.parameters[\"$queryKey\"]?.toIntOrNull()"
                            else "uri.parameters[\"$queryKey\"]?.toIntOrNull() ?: 0"
                        }
                        baseType in listOf("kotlin.Long", "Long") -> {
                            if (isNullable) "uri.parameters[\"$queryKey\"]?.toLongOrNull()"
                            else "uri.parameters[\"$queryKey\"]?.toLongOrNull() ?: 0L"
                        }
                        baseType in listOf("kotlin.Float", "Float") -> {
                            if (isNullable) "uri.parameters[\"$queryKey\"]?.toFloatOrNull()"
                            else "uri.parameters[\"$queryKey\"]?.toFloatOrNull() ?: 0f"
                        }
                        baseType in listOf("kotlin.Double", "Double") -> {
                            if (isNullable) "uri.parameters[\"$queryKey\"]?.toDoubleOrNull()"
                            else "uri.parameters[\"$queryKey\"]?.toDoubleOrNull() ?: 0.0"
                        }
                        else -> {
                            if (isNullable) "uri.parameters[\"$queryKey\"]"
                            else "uri.parameters[\"$queryKey\"] ?: \"\""
                        }
                    }

                    "$paramName = $conversion"
                }
            "$parentName.$screenName($args)"
        }

        "    CmpNavigationRoute.$parentName.${screenName.uppercase()}_PATH to { uri -> $ctor }"
    }

    val hasSerializableParams = classes.any { cls ->
        cls.primaryConstructor?.parameters.orEmpty().any { param -> param.needsJsonEncoding }
    }

    codeGenerator.createKotlinFile("CmpDeepLinkResolver") { w ->
        w.appendLine("package $GENERATED_PACKAGE\n")
        w.appendLine("import com.example.navigation.shared.nav.NavKey")
        w.appendLine("import io.ktor.http.Url")
        if (hasSerializableParams) {
            w.appendLine("import kotlinx.serialization.json.Json")
        }
        imports.forEach { w.appendLine("import $it") }
        w.appendLine()
        w.appendLine("object CmpDeepLinkResolver {")
        w.appendLine("  private val mapping: Map<String, (Url) -> NavKey> = mapOf(")
        mappings.forEach { w.appendLine("$it,") }
        w.appendLine("  )\n")
        w.appendLine("  fun resolve(deeplink: String): NavKey? {")
        w.appendLine("    val uri = Url(deeplink)")
        w.appendLine("    return mapping[uri.encodedPath.removePrefix(\"/\")]?.invoke(uri)")
        w.appendLine("  }\n")
        w.appendLine("  fun hasDeeplink(deeplink: String): Boolean {")
        w.appendLine("    return mapping.containsKey(deeplink.removePrefix(\"/\"))")
        w.appendLine("  }")
        w.appendLine("}")
    }
}
