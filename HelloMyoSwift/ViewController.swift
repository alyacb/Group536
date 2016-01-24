import UIKit
import Starscream

class ViewController: UIViewController, WebSocketDelegate {

	@IBOutlet weak var accelerationProgressBar: UIProgressView!
	@IBOutlet weak var helloLabel: UILabel!
	@IBOutlet weak var accelerationLabel: UILabel!
	@IBOutlet weak var armLabel: UILabel!
	@IBOutlet weak var gyroscopeLabel: UILabel!

	var currentZ: Float = 0.0
	var lastZ: Float = 0.0

	var socket = WebSocket(url: NSURL(string: "ws://7106d781.ngrok.io:8080")!)

	override func viewDidLoad() {
		super.viewDidLoad()
		socket.delegate = self
		socket.connect()

		NSNotificationCenter.defaultCenter().addObserver(self, selector: "didRecieveGyroScopeEvent:", name: TLMMyoDidReceiveGyroscopeEventNotification, object: nil)
	}

	// MARK: - Myo stuff

	@IBAction func didTapSettings(sender: AnyObject) {
		// Settings view must be in a navigation controller when presented
		let controller = TLMSettingsViewController.settingsInNavigationController()
		presentViewController(controller, animated: true, completion: nil)
	}

	func didRecieveGyroScopeEvent(notification: NSNotification) {
		let eventData = notification.userInfo as! Dictionary<NSString, TLMGyroscopeEvent>
		let gyroEvent = eventData[kTLMKeyGyroscopeEvent]!

		let gyroData = GLKitPolyfill.getGyro(gyroEvent)
		lastZ = currentZ
		currentZ = gyroData.z
		if currentZ > 0 && lastZ < 0 {
			print("ping")
			socket.writePing(NSData())
		}

		gyroscopeLabel.text = "Gyro: \(currentZ))"

	}


	// MARK: - websocket stuff

	func websocketDidConnect(ws: WebSocket) {
		print("websocket is connected")
	}

	func websocketDidDisconnect(ws: WebSocket, error: NSError?) {
		if let e = error {
			print("websocket is disconnected: \(e.localizedDescription)")
		} else {
			print("websocket disconnected")
		}
	}

	func websocketDidReceiveMessage(ws: WebSocket, text: String) {
		print("Received text: \(text)")
	}

	func websocketDidReceiveData(ws: WebSocket, data: NSData) {
		print("Received data: \(data.length)")
	}

}

