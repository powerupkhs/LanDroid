package com.sogogi.landroid.raspberrycontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sogogi.landroid.R;

public class RaspberryPI {
	ChannelExec channel;
	Session session;    
	InfoAdapter adapter;
	BufferedReader in;
	Profile p;
	public RaspberryPI () {
		ConnectSSH();
	}


	public String ChangeConfig(String ip, String nm, String gw) {
		String result = "";
		if (ip == null || ip.equals("") || gw == null || gw.equals("")) {
			result = ExecuteCommand("ifconfig eth0 down && sudo ifconfig eth0 up && sudo route del default dev eth0").toString();
		}
		else {
			result = ExecuteCommand("ifconfig eth0 " + ip + " netmask " + (nm==null||nm.equals("")?"255.255.255.0":nm) + " up && sudo route add default gw " + gw + " dev eth0").toString();		
		}	
		return result;
	}

	public String AddForward(String dip, String dport, String sip, String sport, String type) {
		// sudo iptables -t nat -A PREROUTING -p tcp -d 211.189.127.76 --dport 80 -j DNAT --to 192.168.40.2:8080
		String command = String.format("iptables -t nat -A PREROUTING -p tcp -d %s --dport %s -j DNAT --to %s:%s", dip, dport, sip, sport);
		String result = ExecuteCommand(command);
		return result;
	}

	public String ExecuteCommand(final String command) {
		if (session == null || !session.isConnected()) {				
			ConnectSSH();
			int i ;
			for (i = 0; i < 5; i++) {
				try {
					if (session != null && session.isConnected())
						break;
					Thread.sleep(1);
				}
				catch (Exception e) {
					;
				}
			}
			if (i == 5) {
				ThrowException("Disconnect");
				return "Disconnect";
			}
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {			

					if (session.isConnected()) {
						channel = (ChannelExec) session.openChannel("exec");
						in = new BufferedReader(new InputStreamReader(channel.getInputStream()));

						channel.setCommand("sudo " + command);
						
						channel.connect();

						StringBuilder builder = new StringBuilder();

						String line = null;
						while ((line = in.readLine()) != null) {
							builder.append(line).append(System.getProperty("line.separator"));
						}						
					}
				} catch (Exception e) {
					ThrowException(e.getMessage());
				}


			}
		}).start();

		return "";
	}

	public void ThrowException(final String msg) {
		;
	}
	public String convertMS(int ms) {
		int seconds = (int) ((ms / 1000) % 60);
		int minutes = (int) (((ms / 1000) / 60) % 60);
		int hours = (int) ((((ms / 1000) / 60) / 60) % 24);

		String sec, min, hrs;
		if (seconds < 10) {
			sec = "0" + seconds;
		} else {
			sec = "" + seconds;
		}
		if (minutes < 10) {
			min = "0" + minutes;
		} else {
			min = "" + minutes;
		}
		if (hours < 10) {
			hrs = "0" + hours;
		} else {
			hrs = "" + hours;
		}

		if (hours == 0) {
			return min + ":" + sec;
		} else {
			return hrs + ":" + min + ":" + sec;
		}
	}

	public static String kConv(Integer kSize) {
		char[] unit = {'K', 'M', 'G', 'T'};
		Integer i = 0;
		Float fSize = (float) (kSize * 1.0);
		while (i < 3 && fSize > 1024) {
			i++;
			fSize = fSize / 1024;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(fSize) + unit[i];
	}

	public String getIP() {

		Process process = null;

		int ipPos1, ipPos2, ipPos3, ipPos4;
		String ipClass1 = "192", ipClass2 = "168", ipClass3, ipClass4;
		String result = "", line = "";

		// Test
		if (1 > 1) {
			p = new Profile("Landroid", "211.189.127.76", "pi", "raspberry");
			return "211.189.127.76";
			//			this.Username = "pi";
			//			this.Password = "raspberry";
			//			this.IpAddress = "211.189.127.76";
			//			this.Name = "Landroid Default";
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
				return "";
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
				return "";
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

				p = new Profile("Landroid", result, "pi", "raspberry");
				//				this.Username = "pi";
				//				this.Password = "raspberry";
				//				this.IpAddress = result;
				//				this.Name = "Landroid Default";

				Log.v("landroid", "Profile : " + result);
				return result;
			}
		} catch (Exception e) {		
			Log.v("landroid", "Profile : " + e.getMessage()); 
			e.printStackTrace();
		}
		return "";
	}
	public void ConnectSSH() {

		getIP();

		if(p == null)
			return;
		else if(session != null && session.isConnected())
			return;

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSch jsch = new JSch();

					session = jsch.getSession(p.Username, p.IpAddress, 22);
					session.setPassword(p.Password);
					Properties config = new Properties();
					config.put("StrictHostKeyChecking", "no");
					session.setConfig(config);
					session.connect();

					Log.v("landroid", "Notify");

				} catch (final Exception e) {
					ThrowException(e.getMessage());
				}
			}
		}).start();
	}

	public void DisconnectSSH() {
		channel.disconnect();
		session.disconnect();
	}

}