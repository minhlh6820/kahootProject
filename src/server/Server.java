package server;

import java.io.*; 
import java.util.*;

import db.ConnectionUtils;

import java.net.*; 
  
// Server class 
public class Server  
{ 
	
    // Vector to store active clients 
    static Vector<ClientHandler> ar = new Vector<>(); 
      
    // counter for clients 
    static int i = 0; 
  
    public static void main(String[] args) throws IOException  
    { 
        // server is listening on port 1234 
        ServerSocket ss = new ServerSocket(1234); 
        //ss.bind(new InetSocketAddress(InetAddress.getByName("192.168.43.232"),1234));
        Socket s; 
        ConnectionUtils myConnection = new ConnectionUtils();
        // running infinite loop for getting 
        // client request 
        
        while (true)  
        { 
            // Accept the incoming request 
            s = ss.accept(); 
  
            System.out.println("New client request received : " + s); 
              
            // obtain input and output streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
              
            System.out.println("Creating a new handler for this client..."); 
  
            // Create a new handler object for handling this request. x`
            ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos, myConnection); 
            System.out.println("Adding this client to active client list"); 
            
            // add this client to active clients list 
            ar.add(mtch); 
            // Create a new Thread with this object. 
            Thread t = new Thread(mtch); 

            
            // start the thread. 
            t.start(); 
            
  
            // increment i for new client. 
            // i is used for naming only, and can be replaced 
            // by any naming scheme 
            i++; 
  
        } 
        

    } 
} 