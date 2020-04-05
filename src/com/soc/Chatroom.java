package com.soc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chatroom {

	static final int port = 9999;
	private int clientIDs;
	private ServerSocket ss;
	private Map<Socket, String> users;
	private Set<String> usernames;
	
	public Chatroom() {
		users = new HashMap<>();
		usernames = new HashSet<>();
		clientIDs = 0;
	}
	
	public static void main(String[] args) {
		//create chatroom object
		Chatroom chatroom = new Chatroom();
		// thread manager
		ExecutorService executorService = Executors.newFixedThreadPool(15);
		System.out.println("Server started");
		
		try {
			ServerSocket ss = new ServerSocket(port);
			chatroom.ss = ss;
		}
		catch (IOException e) {
			System.out.println("Unable to create server on port " + port);
		}
		
		System.out.println("Server is waiting for client requests...");
		
		
		//server should always be up waiting for connections
		while (true) {
			try {
				/*
				** blocking code - program flow waits here and
				** constantly listens for an incoming connection
				*/
				Socket s = chatroom.ss.accept();
				

				/* 
				** program flow continues with successful connection
				** create new thread for communication
				** calls run() method in chatroom on a separate thread
				*/
				executorService.execute(new ChatroomThread(s, chatroom));
			}
			catch (IOException e) {
				System.out.println("502 Bad Gateway");
			}
		}
	}
	
	
	public int getClientIDs() {
		return clientIDs;
	}

	public void setClientIDs(int clientIDs) {
		this.clientIDs = clientIDs;
	}

	public ServerSocket getSs() {
		return ss;
	}

	public void setSs(ServerSocket ss) {
		this.ss = ss;
	}

	public Map<Socket, String> getUsers() {
		return users;
	}

	public void setUsers(Map<Socket, String> users) {
		this.users = users;
	}

	public Set<String> getUsernames() {
		return usernames;
	}

	public void setUsernames(Set<String> usernames) {
		this.usernames = usernames;
	}

	public static int getPort() {
		return port;
	}
	
	public void removeUser(Socket s) {
		String username = users.get(s);
		usernames.remove(username);
		users.remove(s);
		sendSystemMsg(username + " has left the chat");
	}

	public void addUser(Socket s, PrintWriter out, BufferedReader br)  {
		try {
			// 
			
			//match usernames 3-12 letters, numbs and letters
			String pattern = "^[a-zA-Z0-9]{3,12}$";
			Pattern p = Pattern.compile(pattern);
			boolean isValidUserName = false;
			
			while (!isValidUserName) {
				//prompt for username
				out.println("Enter Username (min length: 3, max length: 12, " 
						+ "letters and/or numbers)");
				
				String line = br.readLine();
				System.out.println(line);
				
				//check if username meets criteria
				Matcher m = p.matcher(line);
				isValidUserName = m.matches();
				
				if (isValidUserName && !usernames.contains(line)) {
					//add client as user
					users.put(s, line);
					usernames.add(line);
					
					System.out.println(s.getInetAddress().getHostAddress() 
							+ " is now " + users.get(s));
					sendSystemMsg(users.get(s) + " has joined the chat");
				}
				else {
					isValidUserName = false;
				}
			}
		}
		catch (IOException e) {
			String ip = s.getInetAddress().getHostAddress();
			System.out.println("Connection to " + ip + " closed unexpectedly");
		} 
	}	
	
	public void sendMsg(Socket from, String msg) {
		
		users.forEach((socket, user) -> {
			//sendMsg msg to user
			PrintWriter out = null;
			try {
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				String username = users.get(from);
				out.println(username + ": " + msg);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	public void sendSystemMsg(String msg) {
		users.forEach((socket, user) -> {
			try {
				PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				out.println(msg);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	
}
