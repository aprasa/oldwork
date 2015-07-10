package org.gudy.azureus2.core3.peer.impl.transport.server;

/*
 * serverChat.java
 *
 * Last modified on 03 march 2004, 11:00
 */

import java.io.*;
import java.net.*;
import java.lang.*;
import javax.net.*;
import javax.net.ssl.*;
import java.security.*;

/**
 * @version 1.6
 * @author  Julien GARINO
 */

public class SendPort extends Thread {
    
    public SendPort() {
    	}
    	
    	public void run()
    	{
    	
        
        /*******Declarations**********/
        // Ports and Hosts
        int port = 3001; // port to the PDA client
        int porte=port+1;
        // Sockets
        SSLServerSocket serverSocket = null; // ServerSocket listening to the Client
        SSLSocket clientSocket = null; // Socket with Client
        System.setProperty("javax.net.ssl.trustStore","serverkeys");
        // Inout/Output for PDA Client
        BufferedReader isClient = null; // BufferedReader from PDA Client
        PrintWriter osClient = null; // OutputStream to PDA Client
        
        // Strings
        String msg_String = null;
        int msg_hash = 0;
        int i = 0;
        int Clientconnected = 0; // Connection marker
        /********End declarations*********/
        
        
        ////************************************************************************************************
        //**************************************************************************************************
       
        
        
        /****************Server Socket Creation**********/
        System.out.print(
        "\nCreation of the ServerSocket\nlistening to port "+ port +"\nfor client.");
        try {
            System.out.print(".");
            
            // Security Provider
            
            /////////////////////////////////
            
            String keystore = "serverkeys";
            char keystorepass[] = "serverpwd".toCharArray();
            char keypassword[] = "serverpwd".toCharArray();
            
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keystore), keystorepass);
            KeyManagerFactory kmf =
            KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keypassword);
            SSLContext sslcontext =
            SSLContext.getInstance("SSLv3");
            sslcontext.init(kmf.getKeyManagers(), null, null);
            ServerSocketFactory ssf =
            sslcontext.getServerSocketFactory();
/*            SSLServerSocket serversocket = (SSLServerSocket)
              ssf.createServerSocket(HTTPS_PORT);
            return serversocket;*/
            
            
            /////////////////////////////////
            
            // SSL Server Socket Factory
            //SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            //serverSocket = (SSLServerSocket) factory.createServerSocket(port);
            serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
            //return serverSocket;
            // Creation of the server Socket listening on port 3001
            System.out.print("started.\n\n");
        }
        catch (IOException e) {
            System.out.print(
            "...failed.\nProblem in the creation of the SSLServerSocket : " + e);
            //System.exit(1);
        }
        catch (Exception e) {
            System.out.print(
            "...failed.\nProblem in the creation of the SSLServerSocket : " + e);
            //System.exit(1);
        }
        
        
        ///////////////////////////// again
         
        while(true)
        {
       
        
        try {
            clientSocket = (SSLSocket) serverSocket.accept(); // now we are connected
            System.out.println("Client accepted.");
            Clientconnected = 1; // we just mark here that we are connected
            String[] supported = clientSocket.getSupportedCipherSuites();
            System.out.println("cipher suites enabled : "+ supported);
            clientSocket.setEnabledCipherSuites(supported);
        }
        catch (IOException e) {
            System.out.println(
            "Unable to deal with BufferedReader and PrintWriter for the clientSocket : " +
            e);
            //System.exit(1);
        }
        /***************End of creation of Sockets**********/
        
        try {
            isClient = new BufferedReader(new InputStreamReader(clientSocket.
            getInputStream())); // InputStream from Client
            osClient = new PrintWriter(clientSocket.getOutputStream()); // prepare an OutputStream for the Client
        }
        catch (IOException e) {
            System.out.println("Cant't deal with the streams : " + e);
        }
        
       
            try {
                msg_String = isClient.readLine(); // taking the String from it
                System.out.println("String from the client : " + msg_String);
            }
            catch (IOException e) {
                System.out.println("Couldn't get I/O for the connection to: " + e);
                //System.exit(1);
            }
            
            
            
            if (Clientconnected == 1) { // if the client is connected
                try {
                    
                    msg_hash = msg_String.hashCode();
                   
                    osClient.println(porte);
                    osClient.flush(); // put it onto the network
                    System.out.print("...and sent.\n");
                } //try
                catch (Exception e) {
                    System.out.println(e);
                    //System.exit(1);
                } // catch
            } // if Clientconnected
       // 
       if (porte==3100) porte=3002;
       	SendInfo k=new SendInfo(porte);
		k.start();
       
       // 
       porte++;
      } //end while (true); 
       
       //**************************************************
       //*************************************************
        // System.exit(0);
    } // void main
} // PDAServer class
