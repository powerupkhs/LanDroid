package com.sogogi.landroid.raspberrycontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sogogi.landroid.R;

public class RaspberryPIActivity extends Activity {

	private ListView listView;
	private Profile p;
    SharedPreferences prefs = null;
    
    ChannelExec channel;
    Session session;    
    InfoAdapter adapter;
    BufferedReader in;
    
    Integer refreshrate = 5000;
    
    List<Info> Infos = new ArrayList<Info>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        getOverflowMenu();
        String ip = getIP();
        
        if(ip == null || ip.equals("")) {
        	Toast.makeText(this, "Disconnect", Toast.LENGTH_SHORT).show();
        	ThrowException("Disconnect");
        	return;
        }
        Toast.makeText(this, "Landroid is working. \n" + getIP(), Toast.LENGTH_SHORT).show();
        p = new Profile("Landroid", ip, "pi", "raspberry");
        
        //Profiles.clear();
        //Profiles.add(new Profile("Landroid", "192.168.40.1", "pi", "raspberry"));
        //Profiles.add(new Profile("Landroid", "211.189.127.76", "pi", "raspberry"));
       
        Infos.add(new Info(R.drawable.hostname, "Hostname", "", -1));
        Infos.add(new Info(R.drawable.distribution, "Distribution", "", -1));
        Infos.add(new Info(R.drawable.kernel, "Kernel", "", -1));
        Infos.add(new Info(R.drawable.firmware, "Firmware", "", -1));
        Infos.add(new Info(R.drawable.cpuheat, "Cpu Heat", "", -1));
        Infos.add(new Info(R.drawable.uptime, "Uptime", "", -1));
        Infos.add(new Info(R.drawable.ram, "Ram Info", "", -1));
        Infos.add(new Info(R.drawable.cpu, "Cpu", "", -1));
        Infos.add(new Info(R.drawable.storage, "Storage", "", -1));
        Infos.add(new Info(R.drawable.network, "Network", "", -1));
        
        BuildList(Infos);
        
        ConnectSSH();
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
    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {         
            case R.id.customcommand:
                final AlertDialog.Builder builder = new AlertDialog.Builder(RaspberryPIActivity.this);
                final View dialog_layout = getLayoutInflater().inflate(R.layout.sendcustomcommand_dialog_layout, null);
                builder.setTitle("Send custom command");

                final EditText et = (EditText) dialog_layout.findViewById(R.id.customcommand);

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String output = ExecuteCommand(et.getText().toString());

                        AlertDialog outDialog = new AlertDialog.Builder(RaspberryPIActivity.this)
                                .setMessage(output)
                                .setTitle("Output")
                                .setCancelable(true)
                                .setPositiveButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        })
                                .show();
                        TextView textView = (TextView) outDialog.findViewById(android.R.id.message);
                        textView.setTypeface(android.graphics.Typeface.MONOSPACE);
                    }
                });

                final AlertDialog Dialog = builder.create();

                Dialog.setView(dialog_layout);
                Dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }

    public void ShowChangeRefreshRateDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(RaspberryPIActivity.this);
                final View dialog_layout = getLayoutInflater().inflate(R.layout.refreshrate_dialog_layout, null);
                final NumberPicker np = (NumberPicker) dialog_layout.findViewById(R.id.numberPicker1);
                np.setMaxValue(30);
                np.setMinValue(1);
                np.setWrapSelectorWheel(false);

                np.setValue(Integer.parseInt(prefs.getString("refreshrate", null)) / 1000);

                builder.setTitle("Change refresh rate");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prefs.edit().putString("refreshrate", Integer.toString(np.getValue() * 1000)).commit();
                        refreshrate = np.getValue() * 1000;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog Dialog = builder.create();
                Dialog.setView(dialog_layout);
                Dialog.show();
            }
        });
    }

    public void CreateNewProfile() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(RaspberryPIActivity.this);
                final View dialog_layout = getLayoutInflater().inflate(R.layout.profile_dialog_layout, null);

                builder.setTitle("Create new profile");

                final EditText ProfileName = (EditText) dialog_layout.findViewById(R.id.profilename);
                final EditText IpAddress = (EditText) dialog_layout.findViewById(R.id.ipaddress);
                final EditText username = (EditText) dialog_layout.findViewById(R.id.sshusername);
                final EditText password = (EditText) dialog_layout.findViewById(R.id.sshpassword);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        p = new Profile(ProfileName.getText().toString(), IpAddress.getText().toString(), username.getText().toString(), password.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

                AlertDialog Dialog = builder.create();
                Dialog.setView(dialog_layout);
                Dialog.show();
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public void BuildList(List<Info> data) {
        if (adapter == null) {
            adapter = new InfoAdapter(this,
                    R.layout.listview_item_row, data);

            listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
        } else {
            adapter.Update(data);
        }
    }

    int current = 0;
    
    String internalip = "";
    String externalip = "";

    public void StartUpdateLoop() {
    	
        new Thread(new Runnable() {
            @Override
            public void run() {
                DecimalFormat df;
                try {
                    while (isOnline() && session.isConnected()) {
                    	
                    	while (true) {

                            try {
                                if (Infos.get(0).Description.equals("")) {
                                    String hostname = ExecuteCommand("hostname -f");
                                    Infos.get(0).Description = hostname;
                                }
                                if (Infos.get(1).Description.equals("")) {
                                    String distribution = ExecuteCommand("cat /etc/*-release | grep PRETTY_NAME=");
                                    distribution = distribution.replace("PRETTY_NAME=\"", "");
                                    distribution = distribution.replace("\"", "");
                                    Infos.get(1).Description = distribution;
                                }
                                if (Infos.get(2).Description.equals("")) {
                                    String kernel = ExecuteCommand("uname -mrs");
                                    Infos.get(2).Description = kernel;
                                }
                                if (Infos.get(3).Description.equals("")) {
                                    String firmware = ExecuteCommand("uname -v");
                                    Infos.get(3).Description = firmware;
                                }
                                df = new DecimalFormat("0.0");
                                String cputemp_str = ExecuteCommand("cat /sys/class/thermal/thermal_zone0/temp");

                                if (!cputemp_str.isEmpty()) {
                                    String cputemp = df.format(Float
                                            .parseFloat(cputemp_str) / 1000) + "'C";
                                    Infos.get(4).Description = cputemp;
                                } else {
                                    Infos.get(4).Description = "* not available *";
                                }

                                Double d = Double.parseDouble(ExecuteCommand("cat /proc/uptime").split(" ")[0]);
                                Integer uptimeseconds = d.intValue();
                                String uptime = convertMS(uptimeseconds * 1000);
                                Infos.get(5).Description = uptime;

                                String info = ExecuteCommand("cat /proc/meminfo");
                                info = info.replaceAll(" ", "");
                                info = info.replaceAll("kB", "");
                                String[] lines = info.split(System.getProperty("line.separator"));
                                df = new DecimalFormat("0");
                                Integer MemTot = Integer.parseInt(df.format(Integer.parseInt(lines[0].substring(lines[0].indexOf(":") + 1)) / 1024.0f));
                                Integer MemFree = Integer.parseInt(df.format(Integer.parseInt(lines[1].substring(lines[1].indexOf(":") + 1)) / 1024.0f));
                                Integer Buffers = Integer.parseInt(df.format(Integer.parseInt(lines[2].substring(lines[2].indexOf(":") + 1)) / 1024.0f));
                                Integer Cached = Integer.parseInt(df.format(Integer.parseInt(lines[3].substring(lines[3].indexOf(":") + 1)) / 1024.0f));
                                Integer Used = MemTot - MemFree;
                                Integer fMemFree = MemFree + Buffers + Cached;
                                Integer MemUsed = Used - Buffers - Cached;
                                Integer Percentage = Integer.parseInt(df.format((float) ((float) MemUsed / (float) MemTot) * 100.0f));

                                Infos.get(6).Description = "Used: " + MemUsed + "Mb\nFree: " + fMemFree + "Mb\nTot: " + MemTot + "Mb";
                                Infos.get(6).ProgressBarProgress = Percentage;

                                df = new DecimalFormat("0.0");
                                String[] loadavg = ExecuteCommand(
                                        "cat /proc/loadavg").split(" ");
                                String cpuCurFreq_cmd = ExecuteCommand("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");

                                String cpuCurFreq = "*N/A*";
                                if (!cpuCurFreq_cmd.isEmpty()) {
                                    cpuCurFreq = df.format(Float
                                            .parseFloat(cpuCurFreq_cmd) / 1000) + "Mhz";
                                }
                                String cpuMinFreq_cmd = ExecuteCommand("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");

                                String cpuMinFreq = "*N/A*";
                                if (!cpuMinFreq_cmd.isEmpty()) {
                                    cpuMinFreq = df.format(Float
                                            .parseFloat(cpuMinFreq_cmd) / 1000) + "Mhz";
                                }
                                String cpuMaxFreq_cmd = ExecuteCommand("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");

                                String cpuMaxFreq = "*N/A*";
                                if (!cpuMaxFreq_cmd.isEmpty()) {
                                    cpuMaxFreq = df.format(Float
                                            .parseFloat(cpuMaxFreq_cmd) / 1000) + "Mhz";
                                }

                                Infos.get(7).Description = "Loads\n" + loadavg[0] + " [1 min] · " + loadavg[1] + " [5 min] · " + loadavg[2] + " [15 min]\nRunning at " + cpuCurFreq + "\n(min: " + cpuMinFreq + " · max: " + cpuMaxFreq + ")";

                                String Drives = ExecuteCommand("df -T | grep -vE \"tmpfs|rootfs|Filesystem|File system\"");
                                lines = Drives.split(System.getProperty("line.separator"));

                                Infos.get(8).Description = "";

                                Integer totalSize = 0;
                                Integer usedSize = 0;
                                Integer partSize = 0;
                                Integer partUsed = 0;
                                for (int i = 0; i < lines.length; i++) {
                                    String line = lines[i];
                                    line = line.replaceAll("\\s+", "|");
                                    String[] DriveInfos = line.split("\\|");
                                    String name = DriveInfos[6];
                                    partSize = Integer.parseInt(DriveInfos[2]);
                                    String total = kConv(partSize);
                                    String free = kConv(Integer.parseInt(DriveInfos[4]));
                                    partUsed = Integer.parseInt(DriveInfos[3]);
                                    String used = kConv(partUsed);
                                    String format = DriveInfos[1];
                                    totalSize += partSize;
                                    usedSize += partUsed;

                                    Integer percentage = partUsed * 100 / partSize;
                                  
                                    Infos.get(8).Description += name + "\n" + "Free: " + free + " · used: " + used + "\nTotal: " + total + " · format: " + format + ((i == (lines.length - 1)) ? "" : "\n\n");
                                }

                                Integer percentage = usedSize * 100 / totalSize;
                                Infos.get(8).ProgressBarProgress = percentage;

                                Integer connections = Integer.parseInt(ExecuteCommand("netstat -nta --inet | wc -l"));
                                String data = ExecuteCommand("/sbin/ifconfig eth0 | grep RX\\ bytes");
                                data = data.replace("RX bytes:", "");
                                data = data.replace("TX bytes:", "");
                                data = data.trim();
                                String[] data1 = data.split(" ");
                                Float rxRaw = Long.parseLong(data1[0]) / 1024.0f / 1024.0f;
                                Float txRaw = Long.parseLong(data1[4]) / 1024.0f / 1024.0f;
                                DecimalFormat f = new DecimalFormat("0.00");
                                Float rx = Float.parseFloat(f.format(rxRaw).replace(",", "."));
                                Float tx = Float.parseFloat(f.format(txRaw).replace(",", "."));
                                Float total = Float.parseFloat(f.format(rx + tx).replace(",", "."));
                                if (internalip.equals("")) {
                                    internalip = ExecuteCommand("ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}'");
                                }
                                if (externalip.equals("")) {
                                    externalip = ExecuteCommand("wget http://whatismyip.akamai.com -O - -q ; echo");
                                }
                                Infos.get(9).Description = "Internal IP: " + internalip + "\nExternal IP: " + externalip + "\n";
                                Infos.get(9).Description += "Received: " + rx + "Mb · sent: " + tx + "Mb\nTotal: " + total + "Mb \n";
                                Infos.get(9).Description += "Connections: " + connections;

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        BuildList(Infos);
                                    }
                                });

                                Thread.sleep(refreshrate);

                            } catch (Exception e) {
                                ThrowException(e.getMessage());
                            }
                        }
                    }

                    DisconnectSSH();
                    ThrowException("Can't communicate with the Raspberry Pi through SSH");
                } catch (Exception e) {
                    ThrowException(e.getMessage());
                }
            }
        }).start();
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
                    
                    StartUpdateLoop();
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
    
    public String ExecuteCommand(String command) {
        try {
        	if (!session.isConnected()) {				
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
					return "";
				}
			}
        	
            if (session.isConnected()) {
                channel = (ChannelExec) session.openChannel("exec");
                in = new BufferedReader(new InputStreamReader(channel.getInputStream()));

                command = "sudo " + command;
                

                channel.setCommand(command);
                channel.connect();

                StringBuilder builder = new StringBuilder();

                String line = null;
                while ((line = in.readLine()) != null) {
                    builder.append(line).append(System.getProperty("line.separator"));
                }

                String output = builder.toString();
                if (output.lastIndexOf("\n") > 0) {
                    return output.substring(0, output.lastIndexOf("\n"));
                } else {
                    return output;
                }
            }
        } catch (Exception e) {
            ThrowException(e.getMessage());
        }

        return "Disconnect";
    }

    public void ThrowException(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(RaspberryPIActivity.this);
                builder.setTitle("Error");
                builder.setMessage(msg);
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
/*                builder.setPositiveButton("Re-try", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       if(ExecuteCommand("date").equals("")) {
                    	   Toast.makeText(getApplicationContext(), "Disconnect", Toast.LENGTH_SHORT).show();
                    	   ThrowException("Disconnect");
                       }
                    }
                });
*/                builder.show();
            }
        });
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

    public void shutdown(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to shutdown your Raspberry Pi?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ExecuteCommand("shutdown -h now");
                        DisconnectSSH();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    public void reboot(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to reboot your Raspberry Pi?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ExecuteCommand("shutdown -r now");
                        //DisconnectSSH();
                    	StringBuffer str = new StringBuffer();
            			Runtime rt = Runtime.getRuntime();
            			Process p = null;
            			try {
            				p = rt.exec("netcfg");
            				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            	 
            				String cl = null;
            				while ((cl = in.readLine()) != null) {
            					if (cl.contains("rndis0")) {
            						str.append(cl.indexOf("192.168."));
            					}
            					//System.out.print(cl);
//            					str.append(cl + "\n");
            					//System.out.print("\n");
            				}
            				Toast.makeText(getApplication().getBaseContext(), str , Toast.LENGTH_LONG).show();
            				//System.out.print("\n");
            				in.close();
            			} catch (IOException e) {
            				e.printStackTrace();
            			
                    	}
                    }
                })
                .setNegativeButton("No", null)
                .show();

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

    @Override
    protected void onPause() {
        
        super.onPause();
    }

    @Override
    protected void onResume() {
        
        super.onResume();
    }
}
