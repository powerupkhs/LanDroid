/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sogogi.landroid.raspberrycontrol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import android.content.SharedPreferences;
import android.util.Log;

/**
 *
 * @author Lorenzo
 */
public class Profile {
	
    String Name;
    String IpAddress;
    String Username;
    String Password;

    public Profile(String Name, String IpAddress, String Username, String Password) {
        this.Name = Name;
        this.IpAddress = IpAddress;
        this.Username = Username;
        this.Password = Password;
    }
    
    public String getName() { 
    	return Name;    	
    }
    public String getUserName() { 
    	return Username;    	
    }
    public String getPassword() { 
    	return Password;    	
    }
    public String getIpAddress() { 
    	return IpAddress;    	
    }
    
    static public void SaveProfiles(SharedPreferences prefs, List<Profile> Profiles) {
		String profiles = "";
		for (Profile p : Profiles) {
			profiles += p.Name + "|" + p.IpAddress + "|" + p.Username + "|" + p.Password + "||";
		}

		prefs.edit().putString("profiles", profiles).commit();
	}
    
    static public void LoadProfiles(SharedPreferences prefs, List<Profile> Profiles) {
        String profiles = prefs.getString("profiles", null);
        for (String profile : profiles.split("\\|\\|")) {
            String[] data = profile.split("\\|");
            String Name = data[0];
            String IpAddress = data[1];
            String Username = data[2];
            String Password = data[3];
            Profiles.add(new Profile(Name, IpAddress, Username, Password));
        }
    }

	public boolean isCollect() {

		Process process = null;
		
		int ipPos1, ipPos2, ipPos3, ipPos4;
		String ipClass1 = "192", ipClass2 = "168", ipClass3, ipClass4;
		String result = "", line = "";
			
		// Test
		if (2 > 1) {
			this.Username = "pi";
			this.Password = "raspberry";
			this.IpAddress = "211.189.127.76";
			this.Name = "Landroid Default";
			return true;
		}
		
		try {
			// Check Tethering by Properties
			process = (Runtime.getRuntime()).exec("getprop sys.usb.config");
			process.waitFor();			
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

			result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
			
			if(!result.contains("rndis")) {
				return false;
			}
			
			// Check Landroid by IP address
			process = (Runtime.getRuntime()).exec("ifconfig rndis0");
			process.waitFor();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));

			result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
			
			if(!result.contains("192.168.")) {
				return false;
			}

			// Parse Result
			ipPos1 = result.indexOf("192.168.");		// !192.168.4x.2 
			ipPos2 = ipPos1 +  + "192.168.".length();	//  192.168!4x.2
			ipPos3 = result.indexOf(".", ipPos2);		//  192.168.4x!2
			ipPos4 = result.indexOf(" ", ipPos3);		//  192.168.4x.2!
			
			ipClass3 = result.substring(ipPos2, ipPos3);
			ipClass4 = result.substring(ipPos3 + 1, ipPos4);
			
			if(ipClass4.equals("2")) {				
				result = String.format("%s.%s.%s.1", ipClass1, ipClass2, ipClass3, ipClass4);
				
				this.Username = "pi";
				this.Password = "raspberry";
				this.IpAddress = result;
				this.Name = "Landroid Default";
				
				Log.v("landroid", "Profile : " + result);
				return true;
			}
		} catch (Exception e) {		
			Log.v("landroid", "Profile : " + e.getMessage()); 
			e.printStackTrace();
		}
		return false;
	}
}
