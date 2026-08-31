import UIKit
import Shared

/// NATIVE (UIKit) home screen. Buttons deep link into the shared CMP (Nav3) graph.
/// Mirrors the native side of the real app that pushes `CmpMainViewController`.
final class NativeHomeViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Native Home"
        view.backgroundColor = .systemBackground

        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 16
        stack.alignment = .fill
        stack.translatesAutoresizingMaskIntoConstraints = false

        stack.addArrangedSubview(makeLabel(
            "NATIVE • UIKit (iosApp)\nButtons cross into the CMP (Nav3) graph via a deep link."
        ))
        stack.addArrangedSubview(makeButton("Open IPO Home (Native → CMP)") { [weak self] in
            self?.openCmp(CmpNavigationRoute.IpoRoute.shared.home(source: "native_home"))
        })
        stack.addArrangedSubview(makeButton("Open OI Analysis (Native → CMP)") { [weak self] in
            self?.openCmp(CmpNavigationRoute.OiAnalysisRoute.shared.home(instrumentId: nil, source: "native_home"))
        })
        stack.addArrangedSubview(makeButton("Open Profile (Native → CMP)") { [weak self] in
            self?.openCmp(CmpNavigationRoute.ProfileRoute.shared.home())
        })
        stack.addArrangedSubview(makeButton("Open Orders (Native → CMP, Metro DI)") { [weak self] in
            self?.openCmp(CmpNavigationRoute.OrdersRoute.shared.home())
        })

        view.addSubview(stack)
        NSLayoutConstraint.activate([
            stack.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            stack.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 24),
            stack.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -24)
        ])
    }

    private func openCmp(_ deeplink: String) {
        guard let nav = navigationController else { return }
        CmpDeeplinkNavigator.handleDeeplink(deeplinkUri: deeplink, navigationController: nav)
    }

    private func makeLabel(_ text: String) -> UILabel {
        let l = UILabel()
        l.text = text
        l.numberOfLines = 0
        l.font = .systemFont(ofSize: 15)
        l.textColor = .secondaryLabel
        return l
    }

    private func makeButton(_ title: String, action: @escaping () -> Void) -> UIButton {
        var config = UIButton.Configuration.filled()
        config.title = title
        let b = UIButton(configuration: config, primaryAction: UIAction { _ in action() })
        return b
    }
}

/// NATIVE (UIKit) scrip detail screen — where CMP screens navigate back to (CMP → Native).
final class NativeScripDetailViewController: UIViewController {
    private let instrumentId: String
    private let source: String

    init(instrumentId: String, source: String) {
        self.instrumentId = instrumentId
        self.source = source
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Scrip Detail: \(instrumentId)"
        view.backgroundColor = .systemBackground

        let label = UILabel()
        label.text = "NATIVE • UIKit (iosApp)\nReached from a CMP screen via HostNavigation.DeeplinkRoute.\nsource = \(source)"
        label.numberOfLines = 0
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(label)
        NSLayoutConstraint.activate([
            label.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            label.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            label.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 24),
            label.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -24)
        ])
    }
}
