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

public class GetInfo {
	static boolean isAuthentificated=false,auth=false;
public  static boolean	isServer=false,ok=false,PremierVerifierCN=true;
public static String UserName="";
	
	
	
	

  public GetInfo(int port,String Title,String HostIP)  {
  	////retard ////
  	int j=0;
  	
  	for(j=0;j<10400;j++)
  	{
  		System.out.print("");
  	}
  	
  	
  	
  	
  	

    /*******Declarations**********/
    // Ports and Hosts
    //int port = 3001; // port
     String myHost = HostIP;
    //String myHost = args[0]; // just change it with args[0] and will work through the network

    // Sockets
    SSLSocket clientSocket = null; // Socket with PDA Client

    // Inout/Output for PDA Client
    BufferedReader isClient = null; // BufferedReader from PDA Client
    PrintWriter osClient = null; // OutputStream to PDA Client
    BufferedReader readfromline = null;

    // Strings
    String myString = null;
    boolean a=true,premier_msg=true,t=true;
    String sString = null;
    int i = 0; // Translation counter
    int Clientconnected = 0; // Connection marker
    /********End declarations*********/

    try {
      System.out.print("\nClient.");
      //clientSocket = new Socket(myHost, port);
      SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
      clientSocket = (SSLSocket) factory.createSocket(myHost, port);
      System.out.print("..started.\n\n");
      String[] supported = clientSocket.getSupportedCipherSuites();
      System.out.println("cipher suites enabled : "+ supported);
    }
    
    catch (IOException e) {
      System.out.print("..failed.\nProblem in the creation of the Socket************  ");
      t=false;
      //System.exit(1);
      a=false;
    } //catch
if(t) {


    try {
      osClient = new PrintWriter(clientSocket.getOutputStream()); // prepare an OutputStream for the Client
      isClient = new BufferedReader(new InputStreamReader(clientSocket.
          getInputStream())); // InputStream from client
      readfromline = new BufferedReader(new InputStreamReader(System.in));
    }
    catch (IOException e) {
      System.out.println("Can't deal with the streams... : " + e);
    } //catch

}




/////////////////////////////// sending the data  ///////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
System.out.println("***** sending the data  ******");
if(t)
{
	System.out.println("***** if T ******");


    try {
    	
    	// launch the window  
    	if(premier_msg)
    {
    System.out.println("***** premier message ******");
        myString =Title;
        premier_msg=false;
        isServer=true;
        }
    	
    	osClient.println(myString); // Write it
    	System.out.println("***** wrtite it ******");
    	
    	//wiating the server to accepte the data
        osClient.flush(); // put it onto the network
        System.out.println("***** put it in the network******");
        System.out.println("\nSending...: '" + myString + "' ...done.");
    	
      while (a) {
      	

      	
    
        
        if (myString.trim().equals("/bye")) {
          osClient.println("/bye");
         // osClient.flush();
          clientSocket.close();
         // System.exit(1);
         a=false;
        }

        
        
        // receive the response from the server
        myString = isClient.readLine();
        String myPassword = isClient.readLine();
        System.out.println("From server :-[===> " + myString + "\n");
        System.out.println("From server 2 :-[===> " + myPassword + "\n");
        
       
        
       //exit if true
        boolean isPasswordTrue=false;
        
        
        if (PremierVerifierCN)
        {
        	PremierVerifierCN=false;

        
        
        VerifierCN pp=new VerifierCN();
        VerifierCN.go();

        }
        
        
       boolean isNameTrue=VerifierCN.Exist("cn="+myString);
       if (isNameTrue)
       {
    	   //trouver le chemin de cn
    	   
    	   if (org.gudy.azureus2.core3.peer.impl.transport.PEPeerTransportProtocol.PremierUserDN)
	        {
    		   org.gudy.azureus2.core3.peer.impl.transport.PEPeerTransportProtocol.PremierUserDN=false;

	        
	        
	        UserDN pp=new UserDN();
	        UserDN.go();

	        }
    	   
    	   
    	   
    	   String chemin=UserDN.getDN("cn="+myString);
    	   
    	   
    	   
    	   //retirer le password de openldap
    	   String password=UserPassword.getPassword(chemin);
    	   String received=myPassword;
    	   
    	   //comparer avec celui recu 
    		isPasswordTrue=MD5PasswordCompare.isSame(password,received);
    		
    	   if(isPasswordTrue) ok=true;
    	   
       }
       
       
       
       
       
       
       
       
       
       
       
       
       
       if(ok) 
       { UserName=myString;
       ok=false;
    	   isAuthentificated=true;
       osClient.println("/Yes");
          osClient.flush();
          clientSocket.close();
          a=false;
          //System.exit(1);
          a=false;
       }
       else 
       
       {osClient.println("/No");
          osClient.flush();}
       
       
       
  
       
       
        
        //check the received id
        
        
        
      } //while
    } //try
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    
    
    
    
    
    
    
    catch (Exception e) {
      System.out.println("Exception while sending data" + e);
      a=false;
      //System.exit(1);
    } // catch
    }
    // System.exit(0);
    
  } 
  public static boolean isAuthentificated()
  { auth=isAuthentificated;
  isAuthentificated=false;
  	return auth;
  }
  public static String UserName()
  {
	  return UserName;
  }
  
  
  
  
  // void main
} // clientChat class