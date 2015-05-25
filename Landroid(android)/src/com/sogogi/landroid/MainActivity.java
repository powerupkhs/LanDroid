package com.sogogi.landroid;

import java.io.File;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity
{
	public Context context = this;
	private Button button1;
	private Button button2;
	private Button button3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button1.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {

				ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				if ( !wifi.isConnectedOrConnecting() )
				{
					Log.i("msg","검사");
					Toast.makeText(context, "Newtwork UnConnected", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Log.i("msg","검사");
					Toast.makeText(context, "Newtwork Connected", Toast.LENGTH_SHORT).show();
				}
			}
		});
		button2.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				String path = "/sdcard/NetworkApply";        
		        File file = new File(path);
		        file.mkdirs();
		        Toast.makeText(MainActivity.this, "Virtual Network Activate", Toast.LENGTH_SHORT).show();
			}
		});
		button3.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
//				SharedPreferences pref = getSharedPreferences("ModePref", MODE_MULTI_PROCESS);
//				SharedPreferences.Editor editor = pref.edit();
//				editor.putInt("mode", 0);
//				editor.commit();
				String path = "/sdcard/NetworkApply";        
		        File file = new File(path);
		        file.delete();
		        Toast.makeText(MainActivity.this, "Virtual Network Desactivate", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
//	
//	public int getMode()
//	{
//		SharedPreferences pref = getSharedPreferences("ModePref", MODE_MULTI_PROCESS);
//		return pref.getInt("mode", 0);
//	}

}

