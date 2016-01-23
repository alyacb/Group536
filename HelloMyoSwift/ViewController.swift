import UIKit

class ViewController: UIViewController {

  @IBOutlet weak var accelerationProgressBar: UIProgressView!
  @IBOutlet weak var helloLabel: UILabel!
  @IBOutlet weak var accelerationLabel: UILabel!
  @IBOutlet weak var armLabel: UILabel!
  @IBOutlet weak var gyroscopeLabel: UILabel!

  override func viewDidLoad() {
    super.viewDidLoad()

    NSNotificationCenter.defaultCenter().addObserver(self, selector: "didRecieveGyroScopeEvent:", name: TLMMyoDidReceiveGyroscopeEventNotification, object: nil)
  }

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
    // Uncomment to display the gyro values
        let x = gyroData.x
        let y = gyroData.y
        let z = gyroData.z
        gyroscopeLabel.text = "Gyro: (\(x), \(y), \(z))"
		print("\(date), \(x), \(y), \(z)")
  }
}

