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
	var theMax: Float = 0.0
	var theMin: Float = 0.0
	var message = ""

	var socket = WebSocket(url: NSURL(string: "ws://f732a04e.ngrok.io")!)

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

		let date = NSDate().timeIntervalSince1970

		let gyroData = GLKitPolyfill.getGyro(gyroEvent)


		currentZ = gyroData.z
		lastZ = lastZ == 0 ? currentZ : lastZ

		theMax = max(currentZ, theMax)
		theMin = min(currentZ, theMin)

		if lastZ < 0 && currentZ > 0 {
//			print("ping")

			let dataString = "{ \"timestamp\": \(date), \"peak\": \(theMax), \"trough\": \(theMin) }"
//			print("\(theMax), \(theMin)")

			socket.writeString(dataString)

			theMax = 0
			theMin = 0
		}

		gyroscopeLabel.text = "Gyro: \(currentZ))"
		lastZ = currentZ

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
		if message != text {
			print("Updated gait: \(text)")
		}
		message = text

	}

	func websocketDidReceiveData(ws: WebSocket, data: NSData) {
		print("Received data: \(data.length)")
	}

}

