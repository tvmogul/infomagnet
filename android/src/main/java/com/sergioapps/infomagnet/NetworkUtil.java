package com.sergioapps.infomagnet;

//public class NetworkStuff 

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * NetworkUtils.
 * 
 * @author ccollins
 *
 */
public final class NetworkUtil {

   private NetworkUtil() {
   }

   public static String getLocalIpAddress() {
	  try {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
          NetworkInterface intf = en.nextElement();
          for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
            InetAddress inetAddress = enumIpAddr.nextElement();
            if (!inetAddress.isLoopbackAddress()) {
              return inetAddress.getHostAddress().toString();
            }
          }
        }
      } catch (SocketException ex) {
        ex.printStackTrace();
      }
      return null;
   }

   public static boolean connectionPresent(final ConnectivityManager cMgr) {
      if (cMgr != null) {
         @SuppressLint("MissingPermission")
         NetworkInfo netInfo = cMgr.getActiveNetworkInfo();
         if ((netInfo != null) && (netInfo.getState() != null)) {
            return netInfo.getState().equals(State.CONNECTED);
         } else {
            return false;
         }
      }
      return false;
   }
   
  
   static final int IP_ADDRESS_LENGTH = 32;

   public static String ipToDottedDecimal(int ipAddress)
   {
     int a = (ipAddress >> 0) & 0xFF, b = (ipAddress >> 8) & 0xFF, c = (ipAddress >> 16) & 0xFF, d = (ipAddress >> 24) & 0xFF;
     return Integer.toString(a) + "." + Integer.toString(b) + "." + Integer.toString(c) + "." + Integer.toString(d);
   }   
   
   public static byte byteOfInt(int value, int which) {
	int shift = which * 8;
    return (byte)(value >> shift); 
  }
   
  public static InetAddress intToInet(int value) {
    byte[] bytes = new byte[4];
    for(int i = 0; i<4; i++) {
      bytes[i] = byteOfInt(value, i);
    }
    try {
      return InetAddress.getByAddress(bytes);
    } catch (UnknownHostException e) {
      // This only happens if the byte array has a bad length
      return null;
    }
  }   
   
}

   
