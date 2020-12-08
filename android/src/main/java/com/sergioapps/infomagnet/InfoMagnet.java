package com.sergioapps.infomagnet;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.widget.Toast;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.plugin.LocalNotifications;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@NativePlugin
public class InfoMagnet extends Plugin {

	// Identify PREFS_FILE with an identifier specific to app, i.e., "appdata_dt"
    protected static final String PREFS_FILE = "appdata_dt.xml";
    
    protected static final String PREFS_APP_ID = "appid";        
    protected volatile static String appid;
    protected volatile static UUID uuid_appid;
    
    protected static final String PREFS_IPHONE_ID = "iphoneid";   
    protected volatile static String iphoneid; 
    
    protected static final String PREFS_ANDROID_ID = "androidid";   
    protected volatile static String androidid; 
    protected volatile static UUID uuid_androidid;       
      
    // IMEI( International Mobile Equipment Identity ) Most commonly referred to as "Device ID"
    // The unique number to identify GSM, WCDMA mobile phones as well as some satellite phones
    // String   imeistring = null;                                                          
    // String   imsistring = null; 
    // TelephonyManager  telephonyManager;                                             
    // telephonyManager = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );               
    // getDeviceId() function Returns the unique device ID.
    // for example,the IMEI for GSM and the MEID or ESN for CDMA phones.                                                        
    // imeistring = telephonyManager.getDeviceId(); 
    protected static final String PREFS_DEVICE_ID = "deviceid";
    protected volatile static String deviceid; 
    protected volatile static UUID uuid_deviceid;       
   
    // MEID(Mobile Equipment IDentifier)
    // The globally unique number identifying a physical piece of CDMA mobile station equipment, the MEID was created to replace ESNs(Electronic Serial Number)
    
    // ESN(Electronic Serial Number)
    // The unique number to identify CDMA mobile phones
        
    // IMSI(International Mobile Subscriber Identity)
    // The unique identification associated with all GSM and UMTS network mobile phone users    
    // getSubscriberId() function Returns the unique subscriber ID, for example, the IMSI for a GSM phone.
    // imsistring = telephonyManager.getSubscriberId();    
    protected static final String PREFS_SUBSCRIBER_ID ="subscriberid";   
    protected volatile static String subscriberid; 
    
    protected static final String PREFS_SIMCARD_SN ="simcardsn";   
    protected volatile static String simcardsn;   
    
    protected static final String PREFS_IP_ADDRESS ="ipaddress";   
    protected volatile static String ipaddress;  
    
    protected static final String PREFS_IP_ADDRESS2 ="ipaddress2";   
    protected volatile static String ipaddress2;      
    
    protected static final String PREFS_MAC_ADDRESSES ="macaddress";   
    protected volatile static String macaddress;    
             
    protected static final String PREFS_DEVICE_NAME ="devicename";   
    protected volatile static String devicename;    
    
    protected static final String PREFS_APP_VERSION ="appversion";    
    protected volatile static String appversion;		
        
    protected static final String PREFS_SDK ="sdk";   
    protected volatile static String sdk;   //Android|....

    protected static final String PREFS_ADDRESS ="address";
    protected volatile static String address;

    protected static final String PREFS_CITY ="city";   
    protected volatile static String city;
    
    protected static final String PREFS_STATE ="state";  
    protected volatile static String state;
    
    protected static final String PREFS_CTRY ="ctry";  
    protected volatile static String ctry;
    
    protected static final String PREFS_PC ="pc";  
    protected volatile static String pc;
    
    protected static final String PREFS_NAME ="name";  
    protected volatile static String name;
    
    protected static final String PREFS_PH ="ph";  
    protected volatile static String ph;
    
    protected static final String PREFS_EMAIL ="email";  
    protected volatile static String email;		
    
    protected static final String PREFS_LAT ="lat";  
    protected volatile static String lat;
    
    protected static final String PREFS_LNG ="lng";  
    protected volatile static String lng;

    protected volatile static String fr = "0";
    
    // protected volatile static GPSTracker gps = null;

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("echotext");
        JSObject ret = new JSObject();
        ret.put("echotext", value);
        call.success(ret);
        // Toast.makeText(getContext(), "WOW!", Toast.LENGTH_LONG).show();
    }

    @PluginMethod()
    public void getInfo(PluginCall call) {
      JSObject r = new JSObject();
      r = getInfo2JSObject();
      call.success(r);
    } 
    
    private JSObject getInfo2JSObject() {
        final SharedPreferences prefs = this.bridge.getContext().getSharedPreferences(PREFS_FILE, 0);
        getLocation();
        JSObject r = new JSObject();
        r.put("appId", getUuid());   // ANDROID_ID is Worthless!
        r.put("appName", getAppName());
        r.put("appVersion", getAppVersion());
        r.put("model", android.os.Build.MODEL);
        r.put("opSystem", "android");
        r.put("osVersion", android.os.Build.VERSION.RELEASE);
        r.put("phone", getPhone());
        r.put("name", getName());
        r.put("email", getEmail());
        r.put("address", prefs.getString(PREFS_ADDRESS, null));
        r.put("city", prefs.getString(PREFS_CITY, null));
        r.put("state", prefs.getString(PREFS_STATE, null));
        r.put("ctry", prefs.getString(PREFS_CTRY, null));
        r.put("pc", prefs.getString(PREFS_PC, null));
        r.put("latitude", prefs.getString(PREFS_LAT, null));
        r.put("longitude", prefs.getString(PREFS_LNG, null));
        // Everything beow is worthless information for marketing!
        r.put("memUsed", getMemUsed());
        r.put("diskFree", getDiskFree());
        r.put("diskTotal", getDiskTotal());
        r.put("appBuild", getAppBuild());
        r.put("appBundleId", getAppBundleId());
        r.put("platform", getPlatform());
        r.put("manufacturer", android.os.Build.MANUFACTURER);
        r.put("isVirtual", isVirtual());
        // r.put("iphoneid", iphoneid);  
        // r.put("androidid", androidid);   
        // r.put("deviceid", deviceid);  
        // r.put("subscriberid", subscriberid);      	
        // r.put("simcardsn", simcardsn);  
        // r.put("ipaddress", ipaddress);     
        // r.put("ipaddress2", ipaddress2);    
        // r.put("macaddress", macaddress);    
        // r.put("devicename", devicename);  	
        // r.put("sdk", sdk);
        // call.success(r);
        return r;
    }

    @PluginMethod()
    public void saveInfo(PluginCall call) {
      JSObject r = new JSObject();
      r = saveInfo2JSObject();
      call.success(r);
    } 
    
    private JSObject saveInfo2JSObject() {
        final SharedPreferences prefs = this.bridge.getContext().getSharedPreferences(PREFS_FILE, 0);
        getLocation();
        JSObject r = new JSObject();
        r.put("appId", getUuid());   // ANDROID_ID is Worthless!
        r.put("appName", getAppName());
        r.put("appVersion", getAppVersion());
        r.put("model", android.os.Build.MODEL);
        r.put("opSystem", "android");
        r.put("osVersion", android.os.Build.VERSION.RELEASE);
        r.put("phone", getPhone());
        r.put("name", getName());
        r.put("email", getEmail());
        r.put("address", prefs.getString(PREFS_ADDRESS, null));
        r.put("city", prefs.getString(PREFS_CITY, null));
        r.put("state", prefs.getString(PREFS_STATE, null));
        r.put("ctry", prefs.getString(PREFS_CTRY, null));
        r.put("pc", prefs.getString(PREFS_PC, null));
        r.put("latitude", prefs.getString(PREFS_LAT, null));
        r.put("longitude", prefs.getString(PREFS_LNG, null));
        return r;
    }

    @PluginMethod()
    public void getBatteryInfo(PluginCall call) {
        JSObject r = new JSObject();

        r.put("batteryLevel", getBatteryLevel());
        r.put("isCharging", isCharging());

        call.success(r);
    }

    @PluginMethod()
    public void getLanguageCode(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", Locale.getDefault().getLanguage());
        call.success(ret);
    }

    private long getMemUsed() {
        final Runtime runtime = Runtime.getRuntime();
        final long usedMem = (runtime.totalMemory() - runtime.freeMemory());
        return usedMem;
    }

    private long getDiskFree() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
    }

    private long getDiskTotal() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
    }

    private String getAppVersion() {
        try {
            PackageInfo pinfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            return pinfo.versionName;
        } catch (Exception ex) {
            return "";
        }
    }

    private String getAppBuild() {
        try {
            PackageInfo pinfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            return Integer.toString(pinfo.versionCode);
        } catch (Exception ex) {
            return "";
        }
    }

    private String getAppBundleId() {
        try {
            PackageInfo pinfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            return pinfo.packageName;
        } catch (Exception ex) {
            return "";
        }
    }

    private String getAppName() {
        try {
            ApplicationInfo applicationInfo = getContext().getApplicationInfo();
            int stringId = applicationInfo.labelRes;
            return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getContext().getString(stringId);
        } catch (Exception ex) {
            return "";
        }
    }

    private String getPlatform() {
        return "android";
    }

    private String getUuid() {
        // ANDROID_ID is WORTHLESS! It is almost always an empty string so we need to improvise!
        // return Settings.Secure.getString(this.bridge.getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        ////////////////////////////////////////////////////////////////////////////////////////////////
		// Create An "APP ID" as a UUID to be a unique indentifier for the app & set "fr or "first run."
        ////////////////////////////////////////////////////////////////////////////////////////////////
        final SharedPreferences prefs = this.bridge.getContext().getSharedPreferences(PREFS_FILE, 0);
        if (uuid_appid == null) {

            final String id = prefs.getString(PREFS_APP_ID, null);
            if (id != null) {
                // Use ids previously computed and stored in the prefs file
                uuid_appid = UUID.fromString(id);                            
                fr = "0";
            } else {
                // This is the first installation!
                // We will Use Android ID to create the UUID appid as a String unless it's broken, 
                // in which case fallback on deviceId, unless not available, so as a last resort we will fallback 
                // to random number which we store in prefs file as a UUID using "nameUUIDFromBytes." 
                fr = "1";
                try {
                    // String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                    String androidId = Settings.Secure.getString(this.bridge.getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                    @SuppressLint("MissingPermission") String deviceId = ((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    if (!"9774d56d682e549c".equals(androidId)) {
                        uuid_appid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                    } else {
                        uuid_appid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                // Write the value out to the prefs file
                prefs.edit().putString(PREFS_APP_ID, uuid_appid.toString()).commit();
            }
        }
        return String.valueOf(uuid_appid);
    }

    private float getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, ifilter);

        int level = -1;
        int scale = -1;

        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }

        return level / (float) scale;
    }

    private boolean isCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        }
        return false;
    }

    private boolean isVirtual() {
        return android.os.Build.FINGERPRINT.contains("generic") || android.os.Build.PRODUCT.contains("sdk");
    }

    @SuppressLint("MissingPermission")
    private String getPhone() {
        final SharedPreferences prefs = this.bridge.getContext().getSharedPreferences(PREFS_FILE, 0);
        // final String _ph = prefs.getString(PREFS_PH, null);
        String ph = "000-000-0000";
        try {
            TelephonyManager tMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            ph = tMgr.getLine1Number();
            String name = getContactDisplayNameByNumber(getContext(), ph);
            //if(name.length() > 0) {
            //   prefs.edit().putString(PREFS_NAME, name).commit();
            //}
            return ph;
        } catch (Exception e) {
            e.printStackTrace();
            return "000-000-0000";
        } 
        // prefs.edit().putString(PREFS_PH, ph).commit();
    }

    public String getContactDisplayNameByNumber(Context context, String number) {
        final SharedPreferences prefs = this.bridge.getContext().getSharedPreferences(PREFS_FILE, 0);
	    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
	    String name = "";

	    ContentResolver contentResolver = context.getContentResolver();
	    Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
	            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

	    try {
	        if (contactLookup != null && contactLookup.getCount() > 0) {
	            contactLookup.moveToNext();
	            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
	            //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                if((name != null) && (name.length() > 0) && (name != "UNKNOWN")) {
                    prefs.edit().putString(PREFS_NAME, name).commit();
                } else {
                    name = "";
                    prefs.edit().putString(PREFS_NAME, null).commit();
                }
	        }
	    } catch (Exception ex) {
            name = "";
            prefs.edit().putString(PREFS_NAME, null).commit();
        }finally {
	        if (contactLookup != null) {
	            contactLookup.close();
	        }
	    }
	    return name;
    }
    
    private String getName() {
        final SharedPreferences prefs = this.bridge.getContext().getSharedPreferences(PREFS_FILE, 0);
        final String name = prefs.getString(PREFS_NAME, null);
        if (name != null && name.length() > 0) {
            return name;
        } else {
            return "";
        }
    }

    private String getEmail() {
        final SharedPreferences prefs = this.bridge.getContext().getSharedPreferences(PREFS_FILE, 0);
        final String _email = prefs.getString(PREFS_EMAIL, null);
        String email = "";
        if (_email != null && _email != "undefined" && _email != "UNDEFINED") {
            email = _email;
        } else {
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            try {
                @SuppressLint("MissingPermission")
                Account[] accounts = AccountManager.get(getContext()).getAccounts();
                for (Account account : accounts) {
                    if (emailPattern.matcher(account.name).matches()) {
                        email = account.name;
                        prefs.edit().putString(PREFS_EMAIL, email).commit();
                    }
                }
            }
            catch (Exception ex) {
                prefs.edit().putString(PREFS_EMAIL, null).commit();
                email = "";
            }
        }
        return email;
    }

    // r.put("location", getLocation());
    // LocalNotifications

    @SuppressLint("MissingPermission")
    public void getLocation() {
        // postalCode is the most important!
        final SharedPreferences prefs = this.bridge.getContext().getSharedPreferences(PREFS_FILE, 0);
        final String name = prefs.getString(PREFS_PC, null);
        GPSTracker gps = null;
        Location location = null;
        if (pc == null) {
            final String _lat = prefs.getString(PREFS_LAT, null);
            final String _lng = prefs.getString(PREFS_LNG, null);
            final String _address = prefs.getString(PREFS_ADDRESS, null);
            final String _city = prefs.getString(PREFS_CITY, null);
            final String _state = prefs.getString(PREFS_STATE, null);
            final String _ctry = prefs.getString(PREFS_CTRY, null);
            final String _pc = prefs.getString(PREFS_PC, null);        
            if (_pc != null) {
                lat = _lat;
                lng = _lng;
                address = _address;
                city = _city;
                state = _state;
                ctry = _ctry;                   	
                pc = _pc;
            } else {
                /////////////////////////////////////////////////////////////////////////////////////   
                try {
                    gps = new GPSTracker(getContext());
                    if(gps.canGetLocation()){                            
                        lat = Double.toString(gps.getLatitude());
                        lng = Double.toString(gps.getLongitude());
                        address = gps.getAddress();
                        city = gps.getCityStr();
                        state = gps.getStateStr();
                        ctry = gps.getCtryStr();
                        pc = gps.getPCStr();
                        //Toast.makeText(context, "Your Location is - \nLat: " + lat + "\nLong: " + lng, Toast.LENGTH_LONG).show();
                    }else{
                        // can't get location GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings?
                        //gps.showSettingsAlert();
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    gps.stopUsingGPS();                    		
                }
                if (pc != null) {
                    // If we have a value for pc (Postal Code) then we set the firstRun flag to true or "1"
                    // Setting this first run flag will upload the data to our server!
                    // In order to deliver targeted advertising we must get only the pc (Postal Code)
                    fr = "1";
                    prefs.edit().putString(PREFS_LAT, lat).commit();
                    prefs.edit().putString(PREFS_LNG, lng).commit();
                    prefs.edit().putString(PREFS_ADDRESS, address).commit();
                    prefs.edit().putString(PREFS_CITY, city).commit();
                    prefs.edit().putString(PREFS_STATE, state).commit();
                    prefs.edit().putString(PREFS_CTRY, ctry).commit();                       
                    prefs.edit().putString(PREFS_PC, pc).commit();
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // iOS: "deviceId": UIDevice.current.identifierForVendor!.uuidString,
    // IMEI stands for International Mobile Equipment Identity number and it is the unique ID number assigned to nearly every modern mobile phone sold.
    // The IMEI is a 14-digit string that includes information on the origin, model, and serial number of the mobile device.
    // On GSM devices, this number is used to identify a valid phone to the network on so the IMEI can be 'blocked' on phones that are stolen or lost to stop them accessing the network. This works whether the original SIM card has been swapped or not.
    // The IMEI number is often located inside the battery compartment of a phone. Alternatively you can usually make it display on screen by entering *#06#
    // On the iPhone you can find the IMEI number by clicking on Settings then General and then selecting About
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("MissingPermission")
    public void getDeviceId() {
        final SharedPreferences prefs = this.bridge.getContext().getSharedPreferences(PREFS_FILE, 0);
        if (deviceid == null) {
            final String _deviceid = prefs.getString(PREFS_DEVICE_ID, null);
            if ( (_deviceid != null)  && (!"77777777777777".equals(_deviceid)) ){
                deviceid = _deviceid;
            } else {
                String deviceId = ((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                if (!"77777777777777".equals(deviceId)) {
                    deviceid = deviceId.toString();
                    prefs.edit().putString(PREFS_DEVICE_ID, deviceid).commit();
                } else {
                    deviceid = "77777777777777".toString();
                    prefs.edit().putString(PREFS_DEVICE_ID, deviceid).commit();
                }
            }
        }                                  
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////    

    // private String GetNames() {
    //     GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
    //     if (acct != null) {
    //     String personName = acct.getDisplayName();
    //     String personGivenName = acct.getGivenName();
    //     String personFamilyName = acct.getFamilyName();
    //     String personEmail = acct.getEmail();
    //     String personId = acct.getId();
    //     Uri personPhoto = acct.getPhotoUrl();
    //     }
    // }
    
    // public String getUsername() {
    //     AccountManager manager = AccountManager.get(this); 
    //     Account[] accounts = manager.getAccountsByType("com.google"); 
    //     List<String> possibleEmails = new LinkedList<String>();
    
    //     for (Account account : accounts) {
    //       // TODO: Check possibleEmail against an email regex or treat
    //       // account.name as an email address only for certain account.type values.
    //       possibleEmails.add(account.name);
    //     }
    
    //     if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
    //         String email = possibleEmails.get(0);
    //         String[] parts = email.split("@");
    
    //         if (parts.length > 1)
    //             return parts[0];
    //     }
    //     return null;
    // }


  
  }
  