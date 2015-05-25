package com.sogogi.webserveroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.R.bool;
import android.util.Log;

class ServerHandler extends Thread {
	private BufferedReader in;
	private PrintWriter out;
	private Socket toClient;

	public ServerHandler(Socket s) {
		toClient = s;
	}

	@Override
	public void run() {
		String document = " ";

		try {
			in = new BufferedReader(new InputStreamReader(
					toClient.getInputStream()));

			// Receive data
			while (true) {
				String s = in.readLine().trim();

				if (s.equals("")) {
					break;
				}

				if (s.substring(0, 3).equals("GET")) {
					int leerstelle = s.indexOf(" HTTP/");
					document = s.substring(5, leerstelle);
					document = document.replaceAll("[/]+", "/");
				}
			}
		} catch (Exception e) {
			Server.remove(toClient);
			try {
				toClient.close();
			} catch (Exception e2) {
			}
		}

		// Standard-Doc
		if (document.equals(""))
			document = "index.html";

		// Don't allow directory traversal
		if (document.indexOf("..") != -1)
			document = "403.html";

		////////////////////////////////////////////////////////////////////////////////////////////
		// File Download
		if (!document.contains(".html")) {
			// this is File Request Message
			String file_name = document;
			String file_extend = file_name.substring(file_name.lastIndexOf(".")+1);
			
//			document = "/sdcard/com.sogogi.webserveroid/" + document;
			document = "/sdcard/com.sogogi.webserveroid/" + document;
			
			document = document.replaceAll("[/]+", "/");
			if (document.charAt(document.length() - 1) == '/')
				document = "/sdcard/com.sogogi.webserveroid/404.html";

			/*
			Response 주요 구성
			=> 상태 코드(요청이 성공했는지 아닌지 등)
			=> content type (HTML, text, 그림 등)
			=> content (HTML 코드, 이미지 등)
			=> %code% : 100 정보전송 / 200 성공 / 300 리다이렉션 / 400 클라이언트 측 에러 / 500 서버 측 에러 / 
			*/
			String headerBase = "HTTP/1.1 %code%\n" + "Server: Bolutions/1\n"
					+ "Content-Length: %length%\n" + "Connection: Keep-Alive\n" +"Accept-Ranges:bytes\n" +"Keep-Alive:timeout=5, max=100\n"
					+ "Content-Type: %type%; charset=utf-8\n\n";

			String header = headerBase;
			// 403 : 접근한 디렉터리에 가용한 기본 페이지가 없음. 
			header = header.replace("%code%", "403 Forbidden");

			try {
				File f = new File(document);
				if (!f.exists()) {
					header = headerBase;
					// 404 : 서버가 요청한 파일이나 스크립트를 찾지 못함.
					header = header.replace("%code%", "404 File not found");
					document = "404.html";
				}
			} catch (Exception e) {
			}

			if (!document.equals("/sdcard/com.sogogi.webserveroid/403.html")) {
				// 200 : 오류 없이 전송 성공.
				header = headerBase.replaceAll("%code%", "200 OK");
			}
			
            if (file_extend.equals("mp3")) {
                header = headerBase.replaceAll("%type%", "audio/*");
            } else if (file_extend.equals("mp4")) {  
            	header = headerBase.replaceAll("%type%", "video/*");
            } else if (file_extend.equals("jpg") || file_extend.equals("jpeg")  
                    || file_extend.equals("JPG") || file_extend.equals("gif")  
                    || file_extend.equals("png") || file_extend.equals("bmp")) {  
                header = headerBase.replaceAll("%type%", "image/*");
            } else if (file_extend.equals("txt") || file_extend.equals("php")) {
                header = headerBase.replaceAll("%type%", "text/plain");
            } else if (file_extend.equals("doc") || file_extend.equals("docx")) {  
                header = headerBase.replaceAll("%type%", "application/msword");
            } else if (file_extend.equals("xls") || file_extend.equals("xlsx")) {  
                header = headerBase.replaceAll("%type%", "application/vnd.ms-excel");
            } else if (file_extend.equals("ppt") || file_extend.equals("pptx")) {
                header = headerBase.replaceAll("%type%", "application/vnd.ms-powerpoint");
            } else if (file_extend.equals("pdf")) {
                header = headerBase.replaceAll("%type%", "application/pdf");
            } else if(file_extend.equals("zip")){
            	header = headerBase.replaceAll("%type%", "application/zip");
            }else if(file_extend.equals("wmv")){
            	header = headerBase.replaceAll("%type%", "video/x-ms-wmv");
            }else {
            	header = headerBase.replaceAll("%type%", "application/unknown");
            }
			
			Log.d("test", file_extend);
			Log.d("test", header);

			try {


//				BufferedOutputStream toServer = null;
//				BufferedInputStream bis = null;
//				FileInputStream fis = null;
//				DataOutputStream dos = null;
//
//				try{
//
//					
//
//					toServer = new BufferedOutputStream( toClient.getOutputStream() );
//					dos = new DataOutputStream( toClient.getOutputStream() );
//					
//					//dos.writeUTF( document );
//					
//					fis = new FileInputStream( document );
//					bis = new BufferedInputStream( fis );
//					
//					header = header.replace("%length%", "" + fis.available());
//					toServer.write(header.getBytes());
//					
//
//					
//					
//
//					int ch = 0;
//					//? file내용 유무 check하면서 읽어 들임과 동시에 server단으로 전송
//
//					while((ch = bis.read()  ) != -1) {
//						toServer.write(ch);        
//					}    
//
//					toServer.flush();     
//					toServer.close();
//					fis.close();
//					toClient.close();      
//
//
//				}catch(Exception fnfe){
//					fnfe.printStackTrace();
//				}
				



				
				
				
				File file = new File(document);
				if (file.exists()) {
					InputStream fileInputStream = new FileInputStream(file);
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(toClient.getOutputStream());
					
					header = header.replace("%length%", "" + fileInputStream.available());
					//bufferedOutputStream.write(header.getBytes());
					
					byte[] fileData = new byte[1024];
					int nRead = 0;
					while((nRead = fileInputStream.read(fileData, 0, fileData.length)) != -1){
						bufferedOutputStream.write(fileData, 0, nRead);
					}
					fileInputStream.close();
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
				} else {
					// Send HTML-File (Asc, not as a stream)
					header = headerBase;
					header = header.replace("%code%", "404 File not found");
					header = header.replace("%length%", ""+ "404 - File not Found".length());

					out = new PrintWriter(toClient.getOutputStream(), true);
					out.print(header);
					out.print("404 - File not Found");
					out.flush();
				}

				Server.remove(toClient);
				toClient.close();
			} catch (Exception e) {
			}
		} else {
			// Search for files in docroot
//			document = "/sdcard/com.sogogi.webserveroid/" + document;
			document = "/sdcard/com.sogogi.webserveroid/" + document;
			
			Log.d("test", document);
			document = document.replaceAll("[/]+", "/");
			Log.d("test", document);
			if (document.charAt(document.length() - 1) == '/')
				document = "/sdcard/com.sogogi.webserveroid/404.html";

			String headerBase = "HTTP/1.1 %code%\n" + "Server: Bolutions/1\n"
					+ "Content-Length: %length%\n" + "Connection: close\n"
					+ "Content-Type: text/html; charset=utf-8\n\n";

			String header = headerBase;
			header = header.replace("%code%", "403 Forbidden");

			try {
				File f = new File(document);
				if (!f.exists()) {
					header = headerBase;
					header = header.replace("%code%", "404 File not found");
					document = "404.html";
				}
			} catch (Exception e) {
			}

			if (!document.equals("/sdcard/com.sogogi.webserveroid/403.html")) {
				header = headerBase.replaceAll("%code%", "200 OK");
			}

			try {
				File f = new File(document);
				if (f.exists()) {
					BufferedInputStream in = new BufferedInputStream(
							new FileInputStream(document));
					BufferedOutputStream out = new BufferedOutputStream(
							toClient.getOutputStream());
					ByteArrayOutputStream tempOut = new ByteArrayOutputStream();

					byte[] buf = new byte[4096];
					int count = 0;
					while ((count = in.read(buf)) != -1) {
						tempOut.write(buf, 0, count);
					}

					tempOut.flush();
					header = header.replace("%length%", "" + tempOut.size());

					out.write(header.getBytes());
					out.write(tempOut.toByteArray());
					out.flush();
				} else {
					// Send HTML-File (Asc, not as a stream)
					header = headerBase;
					header = header.replace("%code%", "404 File not found");
					header = header.replace("%length%", ""
							+ "404 - File not Found".length());

					out = new PrintWriter(toClient.getOutputStream(), true);
					out.print(header);
					out.print("404 - File not Found");
					out.flush();
				}

				Server.remove(toClient);
				toClient.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static void httpResponseCODE(String headerBase){
		
	}
}
