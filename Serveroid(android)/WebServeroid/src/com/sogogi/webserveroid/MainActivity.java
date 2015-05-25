package com.sogogi.webserveroid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;

import com.sogogi.webserveroid.R;
import com.sogogi.webserveroid.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private DrawerLayout drawerLayout;
	private View drawerView;

	static private ToggleButton server_btn;
	static private Button iportSet_btn;
	static private Server server;
	public EditText ipEdit;
	public EditText portEdit;
	public AlertDialog ad;
	
	public String ipAd = "0";
	public String portNum = "8080";
	
	private static ScrollView mScroll;
	private static TextView log_txt;
	// 상단의 인디케이터바 (베터리표시) 영역
	static private NotificationManager mNotificationManager;
	private Button log_clearBtn;
	static private boolean serverOn = false;
	static private boolean isChecked = false;
	public static Process p;
	public static boolean firstStart = true;

	final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			log(b.getString("msg"));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startActivity(new Intent(this, SplashActivity.class));

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		server_btn = (ToggleButton) findViewById(R.id.serverBtn);
		mScroll = (ScrollView) findViewById(R.id.scrollView);
		log_txt = (TextView) findViewById(R.id.log_text);
		log_clearBtn = (Button) findViewById(R.id.log_clearBtn);
		iportSet_btn = (Button) findViewById(R.id.setBtn);
		
		//
		Context ctx = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
		
		View iportDialogView = inflater.inflate(R.layout.alertdialog_iport, null);
		
		AlertDialog.Builder aDlgBuilder = new AlertDialog.Builder(this);
		aDlgBuilder.setTitle("Ip/Port Setting");
		aDlgBuilder.setView(iportDialogView);
		aDlgBuilder.setIcon(R.drawable.serverset_icon3);
		
		ipEdit = (EditText)iportDialogView.findViewById(R.id.ipEditText);
		portEdit = (EditText)iportDialogView.findViewById(R.id.portEditText);
		
		aDlgBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				ipAd = ipEdit.getText().toString();
				portNum = portEdit.getText().toString();
				
			}
		});
		
		aDlgBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				//finish();
			}
		});
		
		ad = aDlgBuilder.create();
		//

		if (isChecked) {
			server_btn.setChecked(true);
			serverOn = true;

			server_btn.setBackgroundDrawable(getResources()
					.getDrawable(R.drawable.server_on));
		}
		else {
			server_btn.setChecked(false);
			serverOn = false;

			server_btn.setBackgroundDrawable(getResources()
					.getDrawable(R.drawable.server_off));
		}

		String packageSrc = "/sdcard/com.sogogi.webserveroid/";
		String packagefile = "/sdcard/com.sogogi.webserveroid/files";

		// /sdcard/com.sogogi.webserveroid/에 폴더 생성됐는지 여부.
		boolean exists = (new File(packageSrc)).exists();

		try {
			// 생성안됐으면 폴더 만들고 html 파일 만들어 준다.
			if (!exists) {
				(new File(packageSrc)).mkdir();
				(new File(packagefile)).mkdir();
				BufferedWriter bout = new BufferedWriter(new FileWriter(
						packageSrc + "index.html"));
				bout.write("<html><head><title>Serveroid</title>");
				bout.write("</head>");
				bout.write("<body>");
				bout.write("<h1>Serveroid created by Sogogi</h1>Die HTML-Dateien liegen in /sdcard/com.sogogi.webserveroid/</body></html>");
				bout.flush();
				bout.close();
				bout = new BufferedWriter(new FileWriter(packageSrc
						+ "403.html"));
				bout.write("<html><head><title>Error 403</title>");
				bout.write("</head>");
				bout.write("<body>403 - Forbidden</body></html>");
				bout.flush();
				bout.close();
				bout = new BufferedWriter(new FileWriter(packageSrc
						+ "404.html"));
				bout.write("<html><head><title>Error 404</title>");
				bout.write("</head>");
				bout.write("<body>404 - File not found</body></html>");
				bout.flush();
				bout.close();
			}
		} catch (Exception e) {
			Log.v("ERROR", e.getMessage());
		}
		
//		if(firstStart)
//		{
//			copyAssets();
//			folderSetting();
//			firstStart = false;
//		}		

		server_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (server_btn.isChecked()) {
					if(ipAd.isEmpty() || portNum.isEmpty()){
						Toast.makeText(MainActivity.this, "Setting Ip/Port", Toast.LENGTH_SHORT).show();
					}else{
						startServer(ipAd, portNum); // (포트 넣을곳 )
					}
					if (serverOn) {
						server_btn.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.server_on));
						Toast.makeText(MainActivity.this, "WebServer ON!",
								Toast.LENGTH_LONG).show();
					}
				} else {
					if (serverOn) {
						serverOn = false;
						isChecked = false;
						
						if (!serverOn) {
							server_btn.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.server_off));
							Toast.makeText(MainActivity.this, "WebServer OFF!",
									Toast.LENGTH_LONG).show();
						}
						
						stopServer();
					}
				}
			}
		});
		
		iportSet_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ad.show();
				
			}
		});

		final Button log_btn = (Button) findViewById(R.id.logBtn);

		log_clearBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				log_txt.setText("");
			}
		});

		// drawerLayout
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerView = (View) findViewById(R.id.drawer);

		log_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(drawerView);
			}
		});

	}
	
	private void stopServer() {
		if (server != null) {
			server.stopServer();
			server.interrupt();
			log("Server was killed.");
			mNotificationManager.cancelAll();
		} else {
			log("Cannot kill server!? Please restart your phone.");
		}
	}

	public static void log(String s) {
		log_txt.append(s + "\n");
		mScroll.fullScroll(ScrollView.FOCUS_DOWN);
	}

	public static String intToIp(int i) {
		// 0xFF 10진수로 255
		return ((i) & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
				+ ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	private void startServer(String ipAdd, String port2) {
		int port = Integer.parseInt(port2);
		try {
			WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			
			ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if ( !wifi.isConnectedOrConnecting() )
			{
				Log.i("msg","검사");
				Toast.makeText(MainActivity.this, "Newtwork UnConnected", Toast.LENGTH_SHORT).show();
				return;
			}

//			String ipAddress = intToIp(wifiInfo.getIpAddress());
			HttpRequestHandler.ipAddress = ipAdd;

//			if (wifiInfo.getSupplicantState() != SupplicantState.COMPLETED) {
//				serverOn = false;
//				// 다이얼로그 창으로 error 알려줌.
//				new AlertDialog.Builder(this)
//						.setTitle("WIFI not Connected")
//						.setMessage(
//								"Please connect to a WIFI-network for starting the webserver.")
//						.setPositiveButton("OK", null).show();
//				throw new Exception("Please connect to a WIFI-network.");
//			}

			log("Starting server " + ipAdd + " : " + port + ".");
			// server = new Server(ipAddress, port, mHandler);
			server = new Server(ipAdd, port, mHandler);             // 테스트 (ip 넣을곳)
			server.start();

			Intent i = new Intent(this, MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
					0);

			Notification notif = new Notification(R.drawable.serveroid_icon, "Webserver is running", System.currentTimeMillis());
			notif.setLatestEventInfo(this, "Webserver", "Webserver is running", contentIntent);
			notif.flags = Notification.FLAG_NO_CLEAR;
			mNotificationManager.notify(1234, notif);
			
			serverOn = true;
			isChecked = true;

		} catch (Exception e) {
			log(e.getMessage());
			server_btn.setChecked(false);
			Toast.makeText(MainActivity.this, "Server Start Fail", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	// 안씁니당
	public void folderSetting()
	{
		try
		{
			p = Runtime.getRuntime().exec(new String[] { "su", "-c","/system/bin/mkdir /data/data/com.sogogi.webserveroid/files" });
			p.waitFor();
			p = Runtime.getRuntime().exec(new String[] { "su", "-c","/data/data/com.sogogi.webserveroid/busybox cp -a /sdcard/com.sogogi.webserveroid /data/data/com.sogogi.webserveroid/files/com.sogogi.webserveroid" });
			p.waitFor();
			p = Runtime.getRuntime().exec(new String[] { "su", "-c","/system/bin/chmod -R 777 /data/data/com.sogogi.webserveroid/files" });
			p.waitFor();
			
			Log.d("test", "folderSetting finish");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	public void copyAssets()
	{
		AssetManager assetManager = getApplicationContext().getAssets();
		InputStream in = null;
		OutputStream out = null;
		
		try
		{
			in = assetManager.open("busybox");
			out = new FileOutputStream("/data/data/com.sogogi.webserveroid/busybox");
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			
			//p = Runtime.getRuntime().exec(new String[] { "su", "-c","/system/bin/mount -o remount,rw /system" });
			//p.waitFor();
			p = Runtime.getRuntime().exec(new String[] { "su", "-c","/system/bin/chmod 777 /data/data/com.sogogi.webserveroid/busybox" });
			p.waitFor();
		}
		catch (Exception e)
		{
			Log.e("tag", "Failed to copy asset file : ", e);
		}
	}

	public void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}
