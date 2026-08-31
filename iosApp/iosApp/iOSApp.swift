import SwiftUI
import Shared

/// App entry point. Registers the Swift [CMPDeepLinkBridge] into the shared
/// Kotlin `ServiceLocator` at startup — mirrors the Koin binding on iOS
/// (`provideKoinDependencies(deepLinkBridge: { CMPDeepLinkBridge.instance })`).
@main
struct iOSApp: App {
    init() {
        ServiceLocator.shared.registerDeepLinkBridge(bridge: CMPDeepLinkBridge.instance)
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .ignoresSafeArea()
        }
    }
}

/// Bridges a UIKit `UINavigationController` (the native stack) into SwiftUI.
/// The real app is fully UIKit; here we host it so the sample stays a single target.
struct RootView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UINavigationController {
        UINavigationController(rootViewController: NativeHomeViewController())
    }
    func updateUIViewController(_ uiViewController: UINavigationController, context: Context) {}
}
