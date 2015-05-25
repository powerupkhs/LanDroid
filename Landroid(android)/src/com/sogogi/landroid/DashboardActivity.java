package com.sogogi.landroid;

import java.io.File;
import java.io.IOException;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sogogi.landroid.raspberrycontrol.RaspberryPI;
import com.sogogi.landroid.raspberrycontrol.RaspberryPIActivity;
 
public class DashboardActivity extends Activity implements OnItemClickListener {
 
    static final String EXTRA_MAP = "map";
    public static Context mContext;
    public static RaspberryPI rasp;
    public static LauncherIcon desIcon = new LauncherIcon(R.drawable.desactivate, "Desactivate", "ic_desactivate.png");
    public static LauncherIcon actIcon = new LauncherIcon(R.drawable.activate, "Activate", "ic_activate.png");
    public static ImageAdapter ia;
    public static GridView gridview;
    public static WifiConfiguration netConfig = new WifiConfiguration();
    static final LauncherIcon[] ICONS = {
    		actIcon,
            desIcon,
            new LauncherIcon(R.drawable.framework, "Framework 설정", "framework.png"),
            new LauncherIcon(R.drawable.usbtethering, "USB Tethering", "usbtethering.png"),
            new LauncherIcon(R.drawable.portforwarding, "Port Forwading", "portforwarding.png"),
            new LauncherIcon(R.drawable.hotspot, "Hotspot", "hotspot.png"),
            new LauncherIcon(R.drawable.ipconfig, "IP 설정", "ipconfig.png"),
            new LauncherIcon(R.drawable.rasppi2, "Raspberry PI", "rasppi2.png")
    };
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        ia = new ImageAdapter(this);
        gridview = (GridView) findViewById(R.id.dashboard_grid);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(this);
        mContext = this;
        
        rasp = new RaspberryPI();
        
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if ( !wifi.isConnectedOrConnecting() )
		{
			ICONS[0] = new LauncherIcon(R.drawable.activate, "Activate", "activate.png");
			ICONS[1] =  new LauncherIcon(R.drawable.desactivate_red, "Desactivate", "desactivate_red.png");
//			Log.i("msg","검사");
//			Toast.makeText(mContext, "Newtwork UnConnected", Toast.LENGTH_SHORT).show();
		}
		else
		{
			ICONS[0] = new LauncherIcon(R.drawable.activate_red, "Activate", "activate_red.png");
			ICONS[1] =  new LauncherIcon(R.drawable.desactivate, "Desactivate", "ic_desactivate.png");
//			Log.i("msg","검사");
//			Toast.makeText(mContext, "Newtwork Connected", Toast.LENGTH_SHORT).show();
		}
        // Hack to disable GridView scrolling
        gridview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }
 
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    	if(position == 0){

			String path = "/sdcard/NetworkApply";        
	        File file = new File(path);
	        file.mkdirs();
	        
			ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if ( !wifi.isConnectedOrConnecting() )
			{
				Toast.makeText(mContext, "Virtual Network Desactivate(Need Framework Setting)", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(DashboardActivity.this, "Virtual Network Activate", Toast.LENGTH_SHORT).show();
			       
		        ICONS[0] = new LauncherIcon(R.drawable.activate_red, "Activate", "activate_red.png");
				ICONS[1] =  new LauncherIcon(R.drawable.desactivate, "Desactivate", "ic_desactivate.png");
				ia.notifyDataSetChanged();
				gridview.invalidateViews();
			}
 
    	}
    	if(position == 1){

			String path = "/sdcard/NetworkApply";        
	        File file = new File(path);
	        file.delete();
	        
	        
	        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if ( !wifi.isConnectedOrConnecting() )
			{
				Toast.makeText(mContext, "Virtual Network Desactivate", Toast.LENGTH_SHORT).show();
		        ICONS[0] = new LauncherIcon(R.drawable.activate, "Activate", "activate_red.png");
				ICONS[1] =  new LauncherIcon(R.drawable.desactivate_red, "Desactivate", "ic_desactivate.png");
				ia.notifyDataSetChanged();
				gridview.invalidateViews();
			}
			else
			{
				Toast.makeText(DashboardActivity.this, "Virtual Network Activate", Toast.LENGTH_SHORT).show();

			}
			
    	}
    	if(position == 2){
    		AlertDialog.Builder alert_confirm = new AlertDialog.Builder(DashboardActivity.this);
	    	alert_confirm.setMessage("FrameWork을 설정하시겠습니까?").setCancelable(false).setPositiveButton("확인",
	    	new DialogInterface.OnClickListener() {
	    	    @Override
	    	    public void onClick(DialogInterface dialog, int which) {
	    	    	Intent intent = new Intent("TEST");
	    	    	startActivity(intent);
	    	    	
	    	        // 'YES'
	    	    }
	    	}).setNegativeButton("취소",
	    	new DialogInterface.OnClickListener() {
	    	    @Override
	    	    public void onClick(DialogInterface dialog, int which) {
	    	        // 'No'
	    	    return;
	    	    }
	    	});
	    	AlertDialog alert = alert_confirm.create();
	    	alert.show();
		}
    	
    	if(position == 3){
    		Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setComponent(new ComponentName("com.android.settings","com.android.settings.TetherSettings"));
			startActivity(intent);
    	}
    	if(position == 4){
    		String result = rasp.ExecuteCommand("date"); 
    		if (result.equals("Disconnect"))
    		{   
        		Toast.makeText(this, "Landroid is not working. \n", Toast.LENGTH_SHORT).show();        		
    			return;
    		}
    		Toast.makeText(this, "Landroid is working. \n" + rasp.getIP(), Toast.LENGTH_SHORT).show();
    		
    		AlertDialog.Builder builder2;
    		AlertDialog alertDialog2;

    		builder2 = new AlertDialog.Builder(mContext);
    		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    		View layout = inflater.inflate(R.layout.port_setting_dialog,(ViewGroup)findViewById(R.id.layout_root2));
    		final EditText dip = (EditText)layout.findViewById(R.id.dip_edit);
    		final EditText dport = (EditText)layout.findViewById(R.id.dport_edit);
    		final EditText sip = (EditText)layout.findViewById(R.id.sip_edit);
    		final EditText sport = (EditText)layout.findViewById(R.id.sport_edit);

    		builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.i("jisu","ok버튼");
    				if(TextUtils.isEmpty(dip.getText())){
    					Toast.makeText(mContext, "Destination IP is empty", Toast.LENGTH_LONG).show();
    				}
    				else if(TextUtils.isEmpty(dport.getText())){
    					Toast.makeText(mContext, "Destination Port is empty", Toast.LENGTH_LONG).show();
    				}
    				else if(TextUtils.isEmpty(sip.getText())){
    					Toast.makeText(mContext, "Source IP is empty", Toast.LENGTH_LONG).show();
    				}
    				else if(TextUtils.isEmpty(sport.getText())){
    					Toast.makeText(mContext, "Source Port is empty", Toast.LENGTH_LONG).show();
    				}
    				else{
    					String dipTemp = dip.getText().toString();
    					String dportTemp = dport.getText().toString();
    					String sipTemp = sip.getText().toString();
    					String sportTemp = sport.getText().toString();
    					rasp.AddForward(dipTemp, dportTemp, sipTemp, sportTemp, null);
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
    		alertDialog2 = builder2.create();
    		builder2.show();
    	}
    	if(position == 5){
//    		if(netConfig.SSID.equals("SogogiNetwork")){
//    			Log.i("jisu","핫스팟 켜있음");
//    			WifiManager wifiManager = (WifiManager)DashboardActivity.mContext.getSystemService(Context.WIFI_SERVICE);
//        		//int state = wifiManager.getWifiState();
//        		//Log.i("jisu","현재 상태 : " + state);
//        		wifiManager.setWifiEnabled(false);
//        		
//    			return;
//    		}
    		if(netConfig.SSID==null){
    		Log.i("jisu","현재 SSID : " + netConfig.SSID);
    		AlertDialog.Builder alert_confirm2 = new AlertDialog.Builder(DashboardActivity.this);
	    	alert_confirm2.setMessage("Hotspot 켜시겠습니까?").setCancelable(false).setPositiveButton("확인",
	    	new DialogInterface.OnClickListener() {
	    	    @Override
	    	    public void onClick(DialogInterface dialog, int which) {

	        		WifiManager wifiManager = (WifiManager)DashboardActivity.mContext.getSystemService(Context.WIFI_SERVICE);
	        		//int state = wifiManager.getWifiState();
	        		//Log.i("jisu","현재 상태 : " + state);
	        		wifiManager.setWifiEnabled(false);
	        		
	        				
	        		try{
	        			Process p;
	        			p = Runtime.getRuntime().exec(new String[] { "su", "-c","iptables -t nat -I POSTROUTING -s 192.168.43.0/24 -o rndis0 -j MASQUERADE" });
	        			p.waitFor();
	        			
	        			p = Runtime.getRuntime().exec(new String[] { "su", "-c", "iptables -A FORWARD -p all -i wlan0 -j ACCEPT"});
	        			p.waitFor();
	        		}
	        		catch(InterruptedException e){
	        			
	        		} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		try {
	        			WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

	        			
	        			netConfig.SSID = "SogogiNetwork";

	        			Method method = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
	        			method.invoke(wifi, netConfig, true);
	        		}
	        		catch(Exception e){
	        			
	        		}
	    	        // 'YES'
	    	    }
	    	}).setNegativeButton("취소",
	    	new DialogInterface.OnClickListener() {
	    	    @Override
	    	    public void onClick(DialogInterface dialog, int which) {
	    	        // 'No'
	    	    return;
	    	    }
	    	});
	    	AlertDialog alert2 = alert_confirm2.create();
	    	alert2.show();
    		}
    		else{
    			Log.i("jisu",netConfig.SSID.toString());
    			Log.i("jisu","핫스팟 켜있음");
    		
    			WifiManager wifiManager = (WifiManager)DashboardActivity.mContext.getSystemService(Context.WIFI_SERVICE);
        		//int state = wifiManager.getWifiState();
        		//Log.i("jisu","현재 상태 : " + state);
        		wifiManager.setWifiEnabled(false);
        		netConfig.SSID = null;
    		}
    	}
    	if(position == 6){
    		    		
    		String result = rasp.ExecuteCommand("date"); 
    		if (result.equals("Disconnect"))
    		{   
        		Toast.makeText(this, "Landroid is not working. \n", Toast.LENGTH_SHORT).show();        		
    			return;
    		}
    		Toast.makeText(this, "Landroid is working. \n" + rasp.getIP(), Toast.LENGTH_SHORT).show();
    		
    		AlertDialog.Builder builder;
    		AlertDialog alertDialog;

    		builder = new AlertDialog.Builder(mContext);
    		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    		View layout = inflater.inflate(R.layout.ip_setting_dialog,(ViewGroup)findViewById(R.id.layout_root));
    		final EditText ip = (EditText)layout.findViewById(R.id.ipEdit);
    		final EditText netmask = (EditText)layout.findViewById(R.id.netmaskEdit);
    		final EditText gateway = (EditText)layout.findViewById(R.id.gatewatEdit);

    		RadioGroup radioGroup =(RadioGroup) layout.findViewById(R.id.radioSelect);    		
//    		RadioButton radioAuto = (RadioButton) layout.findViewById(R.id.radioAuto);
    		final RadioButton radioManual = (RadioButton) layout.findViewById(R.id.radioManual);
    		
    		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch(checkedId) {
					case R.id.radioAuto :	// Auto 
						Log.v("landroid", "Check Auto");
						ip.setEnabled(false);
						netmask.setEnabled(false);
						gateway.setEnabled(false);
						break;
					case R.id.radioManual : 	// Menual
						Log.v("landroid", "Check Manual");
						ip.setEnabled(true);
						netmask.setEnabled(true);
						gateway.setEnabled(true);
						break;					
					}
				}
			});
    		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.i("jisu","ok버튼");
    				if(radioManual.isChecked() && TextUtils.isEmpty(ip.getText())){
    					Toast.makeText(mContext, "IP is empty", Toast.LENGTH_LONG).show();
    				}
    				else if(radioManual.isChecked() && TextUtils.isEmpty(netmask.getText())){
    					Toast.makeText(mContext, "Netmask is empty", Toast.LENGTH_LONG).show();
    				}
    				else if(radioManual.isChecked() && TextUtils.isEmpty(gateway.getText())){
    					Toast.makeText(mContext, "Gateway is empty", Toast.LENGTH_LONG).show();
    				}
    				else{
    					String ipTemp = ip.getText().toString();
    					String netMaskTemp = netmask.getText().toString();
    					String gatewayTemp = gateway.getText().toString();    					
    					rasp.ChangeConfig(ipTemp,netMaskTemp, gatewayTemp);
    					Toast.makeText(getApplicationContext(), "\nExcuted Command \n", Toast.LENGTH_SHORT).show();   
    				}
				}
			});
    		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			});
    		builder.setView(layout);
    		alertDialog = builder.create();
    		builder.show();
    		
    	}
		if (position == 7) {						
			
			Intent intent = new Intent(DashboardActivity.this, RaspberryPIActivity.class);	
			startActivity(intent);
			
		//	String result = rasp.ExecuteCommand("ifconfig");
		//	Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
			
//			AlertDialog.Builder builder2;
//    		AlertDialog alertDialog2;
//
//    		builder2 = new AlertDialog.Builder(mContext);
//    		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//    		View layout = inflater.inflate(R.layout.commend_dialog,(ViewGroup)findViewById(R.id.commend_layout_root));
//    		final EditText commendEdit = (EditText)findViewById(R.id.inputEdit);
//    		final TextView commendResult = (TextView)findViewById(R.id.commendResult);
//    		builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
////					if(TextUtils.isEmpty(commendEdit.getText())){
////    					Toast.makeText(mContext, "명령을 입력해주세요.", Toast.LENGTH_LONG).show();
////    				}
////    				else{
//    					String ipTemp = commendEdit.getText().toString();
//    					
//    					//커맨드 입력
//    					String result = rasp.ExecuteCommand(ipTemp);
//    					commendResult.setText(result);
//    					
////    				}
//				}
//			});
//    		builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//    				
//				}
//			});
//    		builder2.setView(layout);
//    		alertDialog2 = builder2.create();
//    		builder2.show();
////			ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
////			NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
////			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
////
////			if ( !wifi.isConnectedOrConnecting() )
////			{
////				Log.i("msg","검사");
////				Toast.makeText(mContext, "Newtwork UnConnected", Toast.LENGTH_SHORT).show();
////			}
////			else
////			{
////				Log.i("msg","검사");
////				Toast.makeText(mContext, "Newtwork Connected", Toast.LENGTH_SHORT).show();
////			}
		}

	}

    static class LauncherIcon {
        final String text;
        final int imgId;
        final String map;
 
        public LauncherIcon(int imgId, String text, String map) {
            super();
            this.imgId = imgId;
            this.text = text;
            this.map = map;
        }
 
    }
 
    static class ImageAdapter extends BaseAdapter {
        private Context mContext;
 
        public ImageAdapter(Context c) {
            mContext = c;
        }
 
        @Override
        public int getCount() {
            return ICONS.length;
        }
 
        @Override
        public LauncherIcon getItem(int position) {
            return null;
        }
 
        @Override
        public long getItemId(int position) {
            return 0;
        }
 
        static class ViewHolder {
            public ImageView icon;
            public TextView text;
        }
 
        // Create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	Log.i("jisu","getView 불림");
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
 
                v = vi.inflate(R.layout.dashboard_icon, null);
                holder = new ViewHolder();
                holder.text = (TextView) v.findViewById(R.id.dashboard_icon_text);
                holder.icon = (ImageView) v.findViewById(R.id.dashboard_icon_img);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
 
            holder.icon.setImageResource(ICONS[position].imgId);
            holder.text.setText(ICONS[position].text);
            return v;
        }
    }
}