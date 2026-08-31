import UIKit
import Shared

/// iOS implementation of the shared Kotlin `IKNDeepLinkBridge` interface.
/// A Swift class conforming to a Kotlin interface (Kotlin/Native interop).
/// Mirrors `CMPDeepLinkBridge.swift` in the real app.
final class CMPDeepLinkBridge: IKNDeepLinkBridge {

    static let instance = CMPDeepLinkBridge()

    func navigateAppDeeplink(deepLink: String) {
        DispatchQueue.main.async {
            guard let nav = Self.topNavigationController() else { return }

            // Strip "navsample://app/" so the resolver sees "scrip/detail?...".
            let stripped = deepLink
                .replacingOccurrences(of: "navsample://app/", with: "")

            // If it's a CMP route, push the CMP host; otherwise open externally.
            if CmpDeepLinkResolver.shared.hasDeeplink(deeplink: stripped) {
                CmpDeeplinkNavigator.handleDeeplink(deeplinkUri: stripped, navigationController: nav)
            } else if let url = URL(string: deepLink) {
                UIApplication.shared.open(url)
            }
        }
    }

    private static func topNavigationController() -> UINavigationController? {
        let scenes = UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }
        let root = scenes.flatMap { $0.windows }.first { $0.isKeyWindow }?.rootViewController
        return findNavigationController(root)
    }

    private static func findNavigationController(_ vc: UIViewController?) -> UINavigationController? {
        if let nav = vc as? UINavigationController { return nav }
        for child in vc?.children ?? [] {
            if let found = findNavigationController(child) { return found }
        }
        return findNavigationController(vc?.presentedViewController)
    }
}
