import Foundation
import Capacitor
import UIKit
import CoreLocation

public typealias DeviceInfo = [String:Any]

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(InfoMagnet)
public class InfoMagnet: CAPPlugin {
  let diagnostics: Diagnostics = Diagnostics()

  @objc func echo(_ call: CAPPluginCall) {
    let value = call.getString("echotext") ?? ""
    call.success([
        "echotext": value
    ])
  }

  @objc func getInfo(_ call: CAPPluginCall) {
    var isSimulator = false
    #if arch(i386) || arch(x86_64)
      isSimulator = true
    #endif

    let memUsed = diagnostics.getMemoryUsage()
    let diskFree = diagnostics.getFreeDiskSize() ?? 0
    let diskTotal = diagnostics.getTotalDiskSize() ?? 0

    call.success([
      "appId": UIDevice.current.identifierForVendor!.uuidString as? String ?? UIDevice.current.identifierForVendor?.uuidString,
      "appName": Bundle.main.infoDictionary?["CFBundleDisplayName"] as? String ?? "",
      "appVersion": Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "",
      "model": UIDevice.current.model,
      "opSystem": "ios",
      "osVersion": UIDevice.current.systemVersion,
      "phone": "",
      "name": "",
      "email": "",
      "city": "",
      "state": "",
      "ctry": "",
      "pc": "",
      "latitude": "",
      "longitude": "",
      // // Everything beow is worthless information for marketing!
      "memUsed": memUsed,
      "diskFree": diskFree,
      "diskTotal": diskTotal,
      "appBuild": Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? "",
      "appBundleId": Bundle.main.infoDictionary?["CFBundleIdentifier"] as? String ?? "",
      "platform": "ios",
      "manufacturer": "Apple",    
      // "deviceName": UIDevice.current.name,
      // "deviceId": UIDevice.current.identifierForVendor!.uuidString,
      "isVirtual": isSimulator
    ])
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////
  // UUID (Universally Unique Identifier): A sequence of 128 bits that can guarantee uniqueness 
  // across space and time, defined by RFC 4122.
  // GUID (Globally Unique Identifier): Microsoft’s implementation of the UUID specification; 
  // often used interchangeably with UUID.
  // UDID (Unique Device Identifier): A sequence of 40 hexadecimal characters that uniquely 
  // identify an iOS device (the device’s Social Security Number, if you will). This value can 
  // be retrieved through iTunes, or found using UIDevice -uniqueIdentifier. Derived from hardware 
  // details like MAC address.
  ////////////////////////////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////////////////////////////
  // iOS: "deviceId": UIDevice.current.identifierForVendor!.uuidString,
  // IMEI stands for International Mobile Equipment Identity number and it is the unique ID number assigned to nearly every modern mobile phone sold.
  // The IMEI is a 14-digit string that includes information on the origin, model, and serial number of the mobile device.
  // On GSM devices, this number is used to identify a valid phone to the network on so the IMEI can be 'blocked' on phones that are stolen or lost to stop them accessing the network. This works whether the original SIM card has been swapped or not.
  // The IMEI number is often located inside the battery compartment of a phone. Alternatively you can usually make it display on screen by entering *#06#
  // On the iPhone you can find the IMEI number by clicking on Settings then General and then selecting About
  ////////////////////////////////////////////////////////////////////////////////////////////////

  @objc func saveInfo(_ call: CAPPluginCall) {
    var isSimulator = false
    #if arch(i386) || arch(x86_64)
      isSimulator = true
    #endif

    let memUsed = diagnostics.getMemoryUsage()
    let diskFree = diagnostics.getFreeDiskSize() ?? 0
    let diskTotal = diagnostics.getTotalDiskSize() ?? 0

    call.success([
      "appId": UIDevice.current.identifierForVendor!.uuidString as? String ?? UIDevice.current.identifierForVendor?.uuidString,
      "appName": Bundle.main.infoDictionary?["CFBundleDisplayName"] as? String ?? "",
      "appVersion": Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "",
      "model": UIDevice.current.model,
      "opSystem": "ios",
      "osVersion": UIDevice.current.systemVersion,
      "phone": "",
      "name": "",
      "email": "",
      "address": "",
      "city": "",
      "state": "",
      "ctry": "",
      "pc": "",
      "latitude": getLatitude(),
      "longitude": getLongitude()
    ])
  }  

  @objc func getAdppId() -> String {
    String appId = "";
    let userDefaults = NSUserDefaults.standardUserDefaults()
    if userDefaults.objectForKey("ApplicationUniqueIdentifier") == nil {
        let UUID = NSUUID.UUID().UUIDString
        userDefaults.setObject(UUID, forKey: "ApplicationUniqueIdentifier")
        userDefaults.synchronize()
    } else {
        appId =  userDefaults.objectForKey("ApplicationUniqueIdentifier")
    }
    return appId;
  }

  // @objc func getAppId() -> String {
  //   String appId = "";
  //   let userDefaults = NSUserDefaults.standardUserDefaults()
  //   if userDefaults.objectForKey("ApplicationUniqueIdentifier") == nil {
  //       let UUID = NSUUID.UUID().UUIDString
  //       userDefaults.setObject(UUID, forKey: "ApplicationUniqueIdentifier")
  //       userDefaults.synchronize()
  //   } else {
  //       appId =  userDefaults.objectForKey("ApplicationUniqueIdentifier")
  //   }
  //   return appId;
  // }

  // @objc func application(application: UIApplication!, didFinishLaunchingWithOptions launchOptions: NSDictionary!) -> Bool {
  //   let userDefaults = NSUserDefaults.standardUserDefaults()
  //   if userDefaults.objectForKey("ApplicationUniqueIdentifier") == nil {
  //       let UUID = NSUUID.UUID().UUIDString
  //       userDefaults.setObject(UUID, forKey: "ApplicationUniqueIdentifier")
  //       userDefaults.synchronize()
  //   }
  //   return true
  // }

  @objc func getLatitude() -> String {
    String latitude = "";
    var currentLoc: CLLocation!
    if(CLLocationManager.authorizationStatus() == .authorizedWhenInUse ||
    CLLocationManager.authorizationStatus() == .authorizedAlways) {
        currentLoc = locationManager.location
        latitude = currentLoc.coordinate.latitude
    }
    return latitude
  }

  @objc func getLongitude() -> String {
    String longitude = "";
    var currentLoc: CLLocation!
    if(CLLocationManager.authorizationStatus() == .authorizedWhenInUse ||
    CLLocationManager.authorizationStatus() == .authorizedAlways) {
        currentLoc = locationManager.location
        longitude = currentLoc.coordinate.longitude
    }
    return longitude
  }

  @objc func getBatteryInfo(_ call: CAPPluginCall) {
    UIDevice.current.isBatteryMonitoringEnabled = true

    call.success([
      "batteryLevel": UIDevice.current.batteryLevel,
      "isCharging": UIDevice.current.batteryState == .charging || UIDevice.current.batteryState == .full
    ])

    UIDevice.current.isBatteryMonitoringEnabled = false
  }

  @objc func getLanguageCode(_ call: CAPPluginCall) {
    let code = String(Locale.preferredLanguages[0].prefix(2))
    call.success([
      "value": code
    ])
  }
  
  @objc func getAdvertisingIdentifier(_ call: CAPPluginCall) {
  }
  
  @objc func getMemoryUsage(_ call: CAPPluginCall) {
  }

}

