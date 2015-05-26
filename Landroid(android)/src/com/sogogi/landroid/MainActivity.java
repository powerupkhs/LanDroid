package com.sogogi.landroid;

import java.io.File;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.sogogi.landroid.adapter.ImageAdapter;
import com.sogogi.landroid.model.LauncherIcon;
import com.sogogi.landroid.raspberrycontrol.RaspberryPI;
import com.sogogi.landroid.raspberrycontrol.RaspberryPIActivity;

public class MainActivity extends Activity implements OnItemClickListener {
	private static final int ACTIVATE_MENU = 0;
	private static final int DESACTIVATE_MENU = 1;
	private static final int FRAMEWORK_SETTING_MENU = 2;
	private static final int USB_TETHERING_MENU = 3;
	private static final int PORT_FORWARDING_MENU = 4;
	private static final int HOTSPOT_MENU = 5;
	private static final int IP_SETTING_MENU = 6;
	private static final int RASPBERRY_PI_MENU = 7;

	private static final int ACTIVATE_ICON = 0;
	private static final int DESACTIVATE_ICON = 1;

	private static final String MOD_FOLDER_PATH = Environment.getExternalStorageDirectory().getPath() + "/NetworkApply";

	private Context mContext;
	private RaspberryPI raspberryPI;

	public static final LauncherIcon[] ICONS = { new LauncherIcon(R.drawable.desactivate, "Desactivate", "ic_desactivate.png"), new LauncherIcon(R.drawable.activate, "Activate", "ic_activate.png"), new LauncherIcon(R.drawable.framework, "Framework 설정", "framework.png"), new LauncherIcon(R.drawable.usbtethering, "USB Tethering", "usbtethering.png"), new LauncherIcon(R.drawable.portforwarding, "Port Forwading", "portforwarding.png"), new LauncherIcon(R.drawable.hotspot, "Hotspot", "hotspot.png"), new LauncherIcon(R.drawable.ipconfig, "IP 설정", "ipconfig.png"), new LauncherIcon(R.drawable.rasppi2, "Raspberry PI", "rasppi2.png") };
	private ImageAdapter imageAdapter;
	private GridView gridView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);

		imageAdapter = new ImageAdapter(this);
		gridView = (GridView) findViewById(R.id.dashboard_grid);
		gridView.setAdapter(new ImageAdapter(this));
		gridView.setOnItemClickListener(this);
		mContext = this;
		raspberryPI = new RaspberryPI();

		// Hack to disable GridView scrolling
		gridView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return event.getAction() == MotionEvent.ACTION_MOVE;
			}
		});

		initNetworkStateIcon();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		File file;
		Intent intent;
		View layout;

		switch (position) {
		case ACTIVATE_MENU:
			file = new File(MOD_FOLDER_PATH);
			file.mkdirs();
			initNetworkStateIcon();
			break;

		case DESACTIVATE_MENU:
			file = new File(MOD_FOLDER_PATH);
			file.delete();
			initNetworkStateIcon();
			break;

		case FRAMEWORK_SETTING_MENU:
			AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
			alert_confirm.setMessage("FrameWork을 설정하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent("TEST");
					startActivity(intent);
				}
			}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});

			AlertDialog alert = alert_confirm.create();
			alert.show();
			break;

		case USB_TETHERING_MENU:
			intent = new Intent(Intent.ACTION_MAIN);
			intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.TetherSettings"));
			startActivity(intent);
			break;

		case PORT_FORWARDING_MENU:
			if (raspberryPI.ExecuteCommand("date").equals("Disconnect")) {
				Toast.makeText(this, "Landroid is not working. \n", Toast.LENGTH_SHORT).show();
				return;
			}

			Toast.makeText(this, "Landroid is working. \n" + raspberryPI.getIP(), Toast.LENGTH_SHORT).show();

			layout = getLayout(R.layout.port_setting_dialog, R.id.layout_root2);
			final EditText dip = (EditText) layout.findViewById(R.id.dip_edit);
			final EditText dport = (EditText) layout.findViewById(R.id.dport_edit);
			final EditText sip = (EditText) layout.findViewById(R.id.sip_edit);
			final EditText sport = (EditText) layout.findViewById(R.id.sport_edit);

			AlertDialog.Builder builder2;
			builder2 = new AlertDialog.Builder(mContext);
			builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.i("msg", "ok버튼");
					if (TextUtils.isEmpty(dip.getText())) {
						Toast.makeText(mContext, "Destination IP is empty", Toast.LENGTH_LONG).show();
					} else if (TextUtils.isEmpty(dport.getText())) {
						Toast.makeText(mContext, "Destination Port is empty", Toast.LENGTH_LONG).show();
					} else if (TextUtils.isEmpty(sip.getText())) {
						Toast.makeText(mContext, "Source IP is empty", Toast.LENGTH_LONG).show();
					} else if (TextUtils.isEmpty(sport.getText())) {
						Toast.makeText(mContext, "Source Port is empty", Toast.LENGTH_LONG).show();
					} else {
						String dipTemp = dip.getText().toString();
						String dportTemp = dport.getText().toString();
						String sipTemp = sip.getText().toString();
						String sportTemp = sport.getText().toString();
						raspberryPI.AddForward(dipTemp, dportTemp, sipTemp, sportTemp, null);
						Toast.makeText(getApplicationContext(), "\nExcuted Command \n", Toast.LENGTH_SHORT).show();
					}
				}
			});
			builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			});
			builder2.setView(layout);
			builder2.show();
			break;

		case HOTSPOT_MENU:
			final WifiConfiguration netConfig = new WifiConfiguration();

			if (netConfig.SSID == null) {
				Log.i("msg", "현재 SSID : " + netConfig.SSID);
				AlertDialog.Builder alert_confirm2 = new AlertDialog.Builder(MainActivity.this);
				alert_confirm2.setMessage("Hotspot 켜시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
						wifiManager.setWifiEnabled(false);

						try {
							Process p;
							p = Runtime.getRuntime().exec(new String[] { "su", "-c", "iptables -t nat -I POSTROUTING -s 192.168.43.0/24 -o rndis0 -j MASQUERADE" });
							p.waitFor();
							p = Runtime.getRuntime().exec(new String[] { "su", "-c", "iptables -A FORWARD -p all -i wlan0 -j ACCEPT" });
							p.waitFor();

							WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
							netConfig.SSID = "SogogiNetwork";
							Method method = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
							method.invoke(wifi, netConfig, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
				AlertDialog alert2 = alert_confirm2.create();
				alert2.show();
			} else {
				Log.i("msg", netConfig.SSID.toString() + "   핫스팟 켜있음");

				WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(false);
				netConfig.SSID = null;
			}
			break;

		case IP_SETTING_MENU:
			if (raspberryPI.ExecuteCommand("date").equals("Disconnect")) {
				Toast.makeText(this, "Landroid is not working. \n", Toast.LENGTH_SHORT).show();
				return;
			}

			Toast.makeText(this, "Landroid is working. \n" + raspberryPI.getIP(), Toast.LENGTH_SHORT).show();

			layout = getLayout(R.layout.ip_setting_dialog, R.id.layout_root);
			final EditText ip = (EditText) layout.findViewById(R.id.ipEdit);
			final EditText netmask = (EditText) layout.findViewById(R.id.netmaskEdit);
			final EditText gateway = (EditText) layout.findViewById(R.id.gatewatEdit);

			RadioGroup radioGroup = (RadioGroup) layout.findViewById(R.id.radioSelect);
			final RadioButton radioManual = (RadioButton) layout.findViewById(R.id.radioManual);
			radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
					case R.id.radioAuto: // Auto 
						Log.v("landroid", "Check Auto");
						ip.setEnabled(false);
						netmask.setEnabled(false);
						gateway.setEnabled(false);
						break;
					case R.id.radioManual: // Menual
						Log.v("landroid", "Check Manual");
						ip.setEnabled(true);
						netmask.setEnabled(true);
						gateway.setEnabled(true);
						break;
					}
				}
			});

			AlertDialog.Builder builder;
			builder = new AlertDialog.Builder(mContext);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i("msg", "ok버튼");
					if (radioManual.isChecked() && TextUtils.isEmpty(ip.getText())) {
						Toast.makeText(mContext, "IP is empty", Toast.LENGTH_LONG).show();
					} else if (radioManual.isChecked() && TextUtils.isEmpty(netmask.getText())) {
						Toast.makeText(mContext, "Netmask is empty", Toast.LENGTH_LONG).show();
					} else if (radioManual.isChecked() && TextUtils.isEmpty(gateway.getText())) {
						Toast.makeText(mContext, "Gateway is empty", Toast.LENGTH_LONG).show();
					} else {
						String ipTemp = ip.getText().toString();
						String netMaskTemp = netmask.getText().toString();
						String gatewayTemp = gateway.getText().toString();
						raspberryPI.ChangeConfig(ipTemp, netMaskTemp, gatewayTemp);
						Toast.makeText(getApplicationContext(), "\nExcuted Command \n", Toast.LENGTH_SHORT).show();
					}
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			builder.setView(layout);
			builder.show();
			break;

		case RASPBERRY_PI_MENU:
			intent = new Intent(MainActivity.this, RaspberryPIActivity.class);
			startActivity(intent);
			break;
		}
	}

	public void initNetworkStateIcon() {
		int activateImgId;
		int desactivateImgId;

		if (isNetworkConnected()) {
			activateImgId = R.drawable.activate_red;
			desactivateImgId = R.drawable.desactivate;
			Toast.makeText(mContext, "Virtual Network Activate", Toast.LENGTH_SHORT).show();
		} else {
			activateImgId = R.drawable.activate;
			desactivateImgId = R.drawable.desactivate_red;
			Toast.makeText(mContext, "Virtual Network Desactivate", Toast.LENGTH_SHORT).show();
		}

		ICONS[ACTIVATE_ICON] = new LauncherIcon(activateImgId, "Activate", "activate_red.png");
		ICONS[DESACTIVATE_ICON] = new LauncherIcon(desactivateImgId, "Desactivate", "ic_desactivate.png");
		imageAdapter.notifyDataSetChanged();
		gridView.invalidateViews();
	}

	public boolean isNetworkConnected() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!wifi.isConnectedOrConnecting() && !mobile.isConnectedOrConnecting()) {
			return false;
		}

		return true;
	}

	public View getLayout(int layoutId, int viewGroupId) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(layoutId, (ViewGroup) findViewById(viewGroupId));
	}
}