package com.soc;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocClient implements Runnable {
    private Socket s;
    private PrintWriter out;
    private BufferedReader br;
    
    public SocClient(String serverIp, int serverPort) throws UnknownHostException, IOException  {
        s = new Socket(serverIp, serverPort);
        out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }
    
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        SocClient client = new SocClient("localhost", 9999);
        
        executorService.execute(client);

        Scanner sc = new Scanner(System.in);
        String line = "";
       
        while (!line.equals("/quit")) {
            line = sc.nextLine();
 
            if (!line.equals("")) {
                client.out.println(line);
            }
        }   
    }

    @Override
    public void run() {
        try {
        	//display server prompt
            String fromServer = "";
            while ((fromServer = br.readLine()) != null) {
            	System.out.println(fromServer);
            }
        } catch (IOException e) {
            System.out.println("Connection to server closed unexpectedly");
        }
    }

}