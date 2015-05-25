package com.sogogi.webserveroid;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Server extends Thread {
	private ServerSocket listener = null;
	private static Handler mHandler;
	private boolean running = true;
		
	public static LinkedList<Socket> clientList = new LinkedList<Socket>();
	
	public Server(String ip, int port, Handler handler) throws IOException {
		super();
		mHandler = handler;
		//InetAddress : 도메인으로 mapping 되어 있는 ip 주소를 뽑아내서 관리하기 위함.
		InetAddress ipAddress = InetAddress.getByName(ip);
		listener = new ServerSocket(port, 0, ipAddress);
	}
	
	private static void send(String s) {
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("msg", s);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}
	
	@Override
	public void run() {
		while (running) {
			try {
				send("Waiting for connections.");
				Socket client = listener.accept();
				send("New connection from : " + client.getInetAddress().toString());
				


				HttpRequestHandler request = new HttpRequestHandler(client, null);
				Thread thread = new Thread(request);

				thread.start();


				
				
				//new ServerHandler(client).start();
				//clientList.add(client);
			} catch (Exception e) {
				send(e.getMessage());
			}
		}
	}
	
	public void stopServer() {
		running = false;
		try {
			listener.close();
		} catch (Exception e) {
			send(e.getMessage());
		}
	}
	
	public synchronized static void remove(Socket s) {
		send("Closing connection : " + s.getInetAddress().toString());
		clientList.remove(s);
	}
}
