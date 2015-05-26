package com.sogogi.landroid.model;

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
	private String name;
	private String ipAddress;
	private String userName;
	private String password;

	public Profile(String Name, String IpAddress, String Username, String Password) {
		this.name = Name;
		this.ipAddress = IpAddress;
		this.userName = Username;
		this.password = Password;
	}

	static public void SaveProfiles(SharedPreferences prefs, List<Profile> Profiles) {
		String profiles = "";
		for (Profile p : Profiles) {
			profiles += p.name + "|" + p.ipAddress + "|" + p.userName + "|" + p.password + "||";
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

		try {
			// Check Tethering by Properties
			process = (Runtime.getRuntime()).exec("getprop sys.usb.config");
			process.waitFor();
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

			result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}

			if (!result.contains("rndis")) {
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

			if (!result.contains("192.168.")) {
				return false;
			}

			// Parse Result
			ipPos1 = result.indexOf("192.168."); // !192.168.4x.2 
			ipPos2 = ipPos1 + +"192.168.".length(); //  192.168!4x.2
			ipPos3 = result.indexOf(".", ipPos2); //  192.168.4x!2
			ipPos4 = result.indexOf(" ", ipPos3); //  192.168.4x.2!

			ipClass3 = result.substring(ipPos2, ipPos3);
			ipClass4 = result.substring(ipPos3 + 1, ipPos4);

			if (ipClass4.equals("2")) {
				result = String.format("%s.%s.%s.1", ipClass1, ipClass2, ipClass3, ipClass4);

				this.userName = "pi";
				this.password = "raspberry";
				this.ipAddress = result;
				this.name = "Landroid Default";

				Log.v("landroid", "Profile : " + result);
				return true;
			}
		} catch (Exception e) {
			Log.v("landroid", "Profile : " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
