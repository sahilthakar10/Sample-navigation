package com.example.navigation.shared.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Nullability

internal const val ANNOTATION_NAME = "CmpDeepLink"
internal const val GENERATED_PACKAGE = "com.example.navigation.shared.nav.generated"
internal const val CMP_DEEPLINK_ANNOTATION = "com.example.navigation.shared.nav.annotation.$ANNOTATION_NAME"
private const val SERIAL_NAME_FQCN = "kotlinx.serialization.SerialName"

internal fun CodeGenerator.createKotlinFile(fileName: String, block: (Appendable) -> Unit) {
    createNewFile(Dependencies.ALL_FILES, GENERATED_PACKAGE, fileName)
        .bufferedWriter()
        .use(block)
}

internal fun KSClassDeclaration.getDeepLinkAnnotation(): KSAnnotation? =
    annotations.firstOrNull { it.shortName.asString() == ANNOTATION_NAME }

internal fun KSAnnotation.getPath(): String =
    arguments.first { it.name?.asString() == "path" }.value as String

internal val KSValueParameter.queryParamName: String
    get() = annotations
        .firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == SERIAL_NAME_FQCN }
        ?.arguments
        ?.firstOrNull { it.name?.asString() == "value" }
        ?.value as? String
        ?: name?.asString().orEmpty()

internal val KSValueParameter.isNullable: Boolean
    get() = type.resolve().nullability == Nullability.NULLABLE

internal val KSValueParameter.kotlinTypeString: String
    get() = type.resolve().render()

private fun KSType.render(): String {
    val base = declaration.qualifiedName?.asString()
        ?: declaration.simpleName.asString()
    val typeArgs = arguments.mapNotNull { it.type?.resolve()?.render() }
    val rendered = if (typeArgs.isEmpty()) base else "$base<${typeArgs.joinToString(", ")}>"
    return if (nullability == Nullability.NULLABLE) "$rendered?" else rendered
}

internal val KSValueParameter.paramName: String
    get() = name?.asString().orEmpty()

/**
 * Whether the parameter must be `Json.encodeToString(...)`-encoded before being placed
 * into the deep link URL (and `Json.decodeFromString(...)`-decoded when read back).
 * Only primitives and String live in a URL as-is; everything else needs JSON.
 */
internal val KSValueParameter.needsJsonEncoding: Boolean
    get() = !isPrimitiveType

internal val KSValueParameter.isPrimitiveType: Boolean
    get() {
        val typeStr = kotlinTypeString.removeSuffix("?")
        return when (typeStr) {
            "kotlin.String", "String",
            "kotlin.Boolean", "Boolean",
            "kotlin.Int", "Int",
            "kotlin.Long", "Long",
            "kotlin.Float", "Float",
            "kotlin.Double", "Double",
            "kotlin.Byte", "Byte",
            "kotlin.Short", "Short",
            "kotlin.Char", "Char" -> true
            else -> false
        }
    }
