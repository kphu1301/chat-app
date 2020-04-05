package com.soc;

import java.io.BufferedReader;
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
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream());
    }
    
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        SocClient client = new SocClient("localhost", 9999);
        
        executorService.execute(client);
        
        //display server prompt
        System.out.println(client.br.readLine());
        System.out.println(client.br.readLine());

        Scanner sc = new Scanner(System.in);
        String user = sc.nextLine();
        client.out.println(user);
        client.out.flush();
        
        String line = "";
        
        while (!line.equals("/quit")) {
            line = sc.nextLine();
            if (!line.equals("")) {
                client.out.println(line);
                client.out.flush();
            }
        }
        client.s.close();
            
    }

    @Override
    public void run() {
        try {
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}