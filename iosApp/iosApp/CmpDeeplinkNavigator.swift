import UIKit
import Shared

/// iOS navigator that resolves a deep link into a CMP route and pushes the shared
/// `CmpMainViewController`, wiring `onHostNavigation` to native UIKit operations.
/// Mirrors `CmpDeeplinkNavigator.swift` in the real app.
enum CmpDeeplinkNavigator {

    /// Decision point: is this a CMP deep link? If yes, push the CMP host.
    static func handleDeeplink(deeplinkUri: String, navigationController: UINavigationController) {
        // Same generated resolver the real app uses (`CmpDeepLinkResolver.shared.resolve`).
        guard let startNavKey = CmpDeepLinkResolver.shared.resolve(deeplink: deeplinkUri) else {
            // Not a CMP route -> a native handler would take over in the real app.
            return
        }
        pushCmp(startNavKey: startNavKey, navigationController: navigationController)
    }

    private static func pushCmp(startNavKey: NavKey, navigationController: UINavigationController) {
        let cmpVC = CmpMainViewControllerKt.CmpMainViewController(
            startNavKey: startNavKey,
            onHostNavigation: { hostNavigation in
                handleHostNavigation(hostNavigation, navigationController: navigationController)
            }
        )
        cmpVC.title = "CMP (Nav3)"
        navigationController.pushViewController(cmpVC, animated: true)
    }

    /// The Swift closure that fills the shared `onHostNavigation` hole.
    /// CMP -> CMP navigation never reaches here — only CMP -> host does.
    private static func handleHostNavigation(
        _ hostNavigation: HostNavigation,
        navigationController: UINavigationController
    ) {
        switch hostNavigation {
        case let deeplinkRoute as HostNavigationDeeplinkRoute:
            // CMP -> Native: route the native deep link to a native screen.
            handleHostDeeplinkRoute(route: deeplinkRoute.route, navigationController: navigationController)

        case is HostNavigationOnExit:
            // CMP back stack empty -> pop the whole CMP view controller.
            navigationController.popViewController(animated: true)

        case let externalUri as HostNavigationExternalUri:
            if let url = URL(string: externalUri.uri) {
                UIApplication.shared.open(url)
            }

        default:
            break
        }
    }

    /// Minimal native router for the sample. The real app dispatches via `DeeplinkModule`.
    private static func handleHostDeeplinkRoute(route: String, navigationController: UINavigationController) {
        // route looks like: "scrip/detail?instrumentId=TATA-TECH&source=ipo"
        let path = route.components(separatedBy: "?").first ?? route
        let query = parseQuery(route)
        if path == "scrip/detail" {
            let vc = NativeScripDetailViewController(
                instrumentId: query["instrumentId"] ?? "",
                source: query["source"] ?? ""
            )
            navigationController.pushViewController(vc, animated: true)
        }
    }

    private static func parseQuery(_ route: String) -> [String: String] {
        guard let q = route.components(separatedBy: "?").dropFirst().first else { return [:] }
        var result: [String: String] = [:]
        for pair in q.components(separatedBy: "&") {
            let kv = pair.components(separatedBy: "=")
            if kv.count == 2 { result[kv[0]] = kv[1] }
        }
        return result
    }
}
