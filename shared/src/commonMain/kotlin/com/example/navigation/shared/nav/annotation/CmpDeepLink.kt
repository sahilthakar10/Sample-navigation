package com.example.navigation.shared.nav.annotation

/**
 * Marks a [com.example.navigation.shared.nav.NavKey] route as reachable via a deep link [path].
 *
 * In the real app this annotation is scanned by the `sfl-cmp-navigation-ksp` KSP
 * processor, which auto-generates `CmpNavigationRoute` (URL builders), `CmpDeepLinkResolver`
 * (URL -> NavKey), and the serializers module.
 *
 * In this sample there is no KSP step; the equivalent "generated" files
 * (`CmpNavigationRoute`, `CmpDeepLinkResolver`) are hand-written to mirror that output,
 * so you can read the generation and resolution side by side with the annotation.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class CmpDeepLink(val path: String)
