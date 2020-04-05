package com.soc;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
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
		System.out.println("Client from " + s.getInetAddress().getHostAddress() + " has connected!");
		
		try {
			//get input/output streams
			PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			//greet client
			out.println("Welcome to the 626g Chatroom!");
			
			// try to add client to chatroom
			chatroom.addUser(s, out, br);
			
			while(true) {
				//receive msg
				String msg = br.readLine();
				if (msg == null) {
					//socket closed unexpectedly on client side
					break;
				}
				if (msg != null) {
					if (msg.equals("/quit")) {
						break;
					}
					if(!msg.startsWith("/")) {
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
