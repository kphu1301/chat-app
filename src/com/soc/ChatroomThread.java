package com.soc;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

public class ChatroomThread implements Runnable {
	private Socket s;
	private String userIp;
	private String username;
	private Chatroom chatroom;
	
	public ChatroomThread(Socket s, Chatroom chatroom) {
		this.s = s;
		this.chatroom = chatroom;
		this.userIp = s.getInetAddress().getHostAddress();
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
			this.username = chatroom.getUsers().get(s);
			
			while(true) {
				//receive msg
				String msg = br.readLine();
				if (msg == null) {
					//socket closed unexpectedly on client side
					break;
				}
				
				//  process msg 
				if (msg.equals("/users")) {
					chatroom.getUsernames().forEach(username -> {
						out.println(username);
					});
					out.println(chatroom.getUsernames().size());
				}
				else if (!msg.startsWith("/")) {
					chatroom.sendMsg(s, msg);
				}
				
			}
		}
		catch(IOException e) {
			System.out.println("Connection to + " + userIp + " closed unexpectedly");
		}
		
		finally {
			chatroom.removeUser(s, userIp, username);
		}
	}
}
