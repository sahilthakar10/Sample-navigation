package com.example.navigation.shared.ksp.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.example.navigation.shared.ksp.GENERATED_PACKAGE
import com.example.navigation.shared.ksp.createKotlinFile
import com.example.navigation.shared.ksp.getDeepLinkAnnotation
import com.example.navigation.shared.ksp.getPath
import com.example.navigation.shared.ksp.kotlinTypeString
import com.example.navigation.shared.ksp.needsJsonEncoding
import com.example.navigation.shared.ksp.paramName
import com.example.navigation.shared.ksp.queryParamName

/**
 * WRITE side generator -> `CmpNavigationRoute` (path/URI consts + URL builders + deeplinkUris).
 * Mirrors `sfl-cmp-navigation-ksp/.../generator/RouteGenerator.kt`.
 */
fun generateDeepLinks(codeGenerator: CodeGenerator, classes: List<KSClassDeclaration>) {
    val hasSerializableParams = classes.any { cls ->
        cls.primaryConstructor?.parameters.orEmpty().any { param -> param.needsJsonEncoding }
    }

    codeGenerator.createKotlinFile("CmpNavigationRoute") { w ->
        w.appendLine("package $GENERATED_PACKAGE\n")
        if (hasSerializableParams) {
            w.appendLine("import kotlinx.serialization.json.Json")
            w.appendLine("import kotlinx.serialization.encodeToString")
        }
        w.appendLine()
        w.appendLine("object CmpNavigationRoute {")

        val uriRefs = mutableListOf<String>()

        classes
            .groupBy { it.parentDeclaration!!.simpleName.asString() }
            .forEach { (featureName, screens) ->
                w.appendLine("  object $featureName {")
                screens.forEach { cls ->
                    val annotation = cls.getDeepLinkAnnotation() ?: return@forEach
                    val path = annotation.getPath()
                    val screenName = cls.simpleName.asString()
                    val pathConstName = "${screenName.uppercase()}_PATH"
                    val uriConstName = "${screenName.uppercase()}_URI"

                    uriRefs.add("$featureName.$uriConstName")

                    w.appendLine("    internal const val $pathConstName = \"$path\"")

                    val params = cls.primaryConstructor?.parameters.orEmpty()
                    val queryParams = params.filter { it.queryParamName.isNotBlank() }

                    val uriTemplate = buildUriTemplate(path, queryParams)
                    w.appendLine("    const val $uriConstName = \"$uriTemplate\"")

                    generateUrlBuilderFunction(w, screenName, path, pathConstName, params, queryParams)
                }
                w.appendLine("  }")
            }

        w.appendLine()
        w.appendLine("  val deeplinkUris = listOf(")
        w.appendLine(uriRefs.joinToString(",\n") { "    $it" })
        w.appendLine("  )")

        w.appendLine("}")
    }
}

private fun buildUriTemplate(path: String, queryParams: List<KSValueParameter>): String {
    return if (queryParams.isEmpty()) {
        path
    } else {
        val queryString = queryParams.joinToString("&") { "${it.queryParamName}={${it.queryParamName}}" }
        "$path?$queryString"
    }
}

private fun generateUrlBuilderFunction(
    w: Appendable,
    screenName: String,
    path: String,
    constName: String,
    allParams: List<KSValueParameter>,
    queryParams: List<KSValueParameter>
) {
    val functionName = screenName.replaceFirstChar { it.lowercase() }

    if (allParams.isEmpty()) {
        w.appendLine("    fun $functionName(): String {")
        w.appendLine("      return $constName")
        w.appendLine("    }")
        return
    }

    val signature = allParams.joinToString(", ") { "${it.paramName}: ${it.kotlinTypeString}" }
    w.appendLine("    fun $functionName($signature): String {")

    val (nullableParams, nonNullableParams) = queryParams.partition { it.kotlinTypeString.endsWith("?") }

    if (nullableParams.isEmpty()) {
        val queryString = nonNullableParams.joinToString("&") { p ->
            val valueTemplate = if (p.needsJsonEncoding) {
                "\${Json.encodeToString(${p.paramName})}"
            } else {
                "\$${p.paramName}"
            }
            "${p.queryParamName}=$valueTemplate"
        }
        w.appendLine("      return \"$path?$queryString\"")
    } else {
        w.appendLine("      val params = mutableListOf<String>()")
        nonNullableParams.forEach { p ->
            val valueTemplate = if (p.needsJsonEncoding) {
                "\${Json.encodeToString(${p.paramName})}"
            } else {
                "\$${p.paramName}"
            }
            w.appendLine("      params += \"${p.queryParamName}=$valueTemplate\"")
        }
        nullableParams.forEach { p ->
            if (p.needsJsonEncoding) {
                w.appendLine("      ${p.paramName}?.let { params += \"${p.queryParamName}=\${Json.encodeToString(it)}\" }")
            } else {
                w.appendLine("      ${p.paramName}?.let { params += \"${p.queryParamName}=\$it\" }")
            }
        }
        w.appendLine("      return if (params.isEmpty()) $constName else \"\$$constName?\" + params.joinToString(\"&\")")
    }
    w.appendLine("    }")
}
