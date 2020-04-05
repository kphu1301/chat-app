package com.soc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatroomThread implements Runnable {
	private Socket s;
	private Chatroom chatroom;
	
	public ChatroomThread(Socket s, Chatroom chatroom) {
		this.s = s;
		this.chatroom = chatroom;
	}
	
	@Override
	public void run() {
		//new thread/connection made. listen for msgs
		try {
			while(true) {
				//get input/output streams
				PrintWriter out = new PrintWriter(s.getOutputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				
				//receive msg
				String msg = br.readLine();
				if (msg != null) {
					if (!msg.startsWith("/")){
						chatroom.sendMsg(s, msg);
					}
					
				}
			}
		}
		catch(IOException e) {
			System.out.println("Connection to + " + s.getInetAddress().getHostAddress() + " closed unexpectedly");
			
		}
		
		finally {
			chatroom.removeUser(s);
		}
	}
}
