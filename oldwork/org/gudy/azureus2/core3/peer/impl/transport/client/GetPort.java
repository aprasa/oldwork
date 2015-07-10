package org.gudy.azureus2.core3.peer.impl.transport.client;


/*
 * clientChat.java
 *
 * Created on 17 february 2004, 19:33
 */

// java -Djavax.net.ssl.trustStore=myfile MyJavaProgram


import java.io.*; // for input output
import java.lang.*; // for Threads
import java.net.*; // sockets
import javax.net.ssl.*;

/**
 * @version 1.8
 * @author  Julien GARINO
 */

public class GetPort {
	public static boolean IsConnected=false;
 
  public GetPort(String IP,String Title) {

    /*******Declarations**********/
    // Ports and Hosts
    int port = 3001; // port
    boolean NoProblem=true;
    //System.setProperty("javax.net.ssl.trustStore","cacerts");
    System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.6.0\\jre\\lib\\security\\cacertsmoi");
    System.setProperty("javax.net.ssl.trustStorePassword","serverpwd");
    String myHost =IP;
    //String myHost = args[0]; // just change it with args[0] and will work through the network

    // Sockets
    SSLSocket clientSocket = null; // Socket with PDA Client

    // Inout/Output for PDA Client
    BufferedReader isClient = null; // BufferedReader from PDA Client
    PrintWriter osClient = null; // OutputStream to PDA Client
    BufferedReader readfromline = null;
   
    // Strings
    String myString = null;
    int HisPort=0;
    String sString = null;
    int i = 0; // Translation counter
    int Clientconnected = 0; // Connection marker
    /********End declarations*********/

    try {
        System.out.print("\nClient.");
        //clientSocket = new Socket(myHost, port);
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
         System.out.print("factory\n");
        clientSocket = (SSLSocket) factory.createSocket(myHost, port);
         System.out.println("\nPort\n");
         //System.setProperty("javax.net.ssl.trustStore","cacerts");
        System.out.print("..started.\n\n");
        String[] supported = clientSocket.getSupportedCipherSuites();
        System.out.println("cipher suites enabled : "+ supported);
         IsConnected=false;
         NoProblem=true;
      }
    catch (IOException e) {
      System.out.print("..failed.\nProblem in the creation of the Socket :"+e+"\n\n\n");
                       IsConnected=true;
                       NoProblem=false;
      //System.exit(1);
    } //catch



////
if(NoProblem)
{

    try {
      osClient = new PrintWriter(clientSocket.getOutputStream()); // prepare an OutputStream for the Client
      isClient = new BufferedReader(new InputStreamReader(clientSocket.
          getInputStream())); // InputStream from client
      readfromline = new BufferedReader(new InputStreamReader(System.in));
    }
    catch (IOException e) {
      System.out.println("Can't deal with the streams... : " + e);
    } //catch




    try {
     
        myString = "hello";
        if (myString.trim().equals("/bye")) {
          osClient.println("/bye");
          osClient.flush();
          clientSocket.close();
          System.exit(1);
        }

        osClient.println(myString); // Write it
        osClient.flush(); // put it onto the network
        System.out.println("\nSending...: '" + myString + "' ...done.");
        myString = isClient.readLine();
        System.out.println("From server :-[===> " + myString + "\n");
     
    } //try
    catch (Exception e) {
      System.out.println("Exception while sending data" + e);
      System.exit(1);
    } // catch
    System.out.println("Closing connection to client");
    //convertire string en Int
    double x=Double.parseDouble(myString);
    int y= (int)x;
    
    // lancer le socket pour récupérer les infos
    GetInfo m=new GetInfo(y,Title,IP);
    	//clientSocket.close();
    	}//end if no problem
    	
    // System.exit(0);
  } // void main
  public static boolean Connected()
  {
  	return IsConnected;
  }
} // clientChat class