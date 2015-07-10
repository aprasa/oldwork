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
import javax.swing.*;
/**
 * @version 1.6
 * @author  Julien GARINO
 */

public class SendInfo extends Thread  {
    int port=0;
    boolean First=true;
    public SendInfo(int NewPorte)  {
    	port=NewPorte;
    	
    	
    	}
    	
    	
    	
    	public	void run () 
{
        
        /*******Declarations**********/
        // Ports and Hosts
        //int port = 3001; // port to the PDA client
        
        // Sockets
        SSLServerSocket serverSocket = null; // ServerSocket listening to the Client
        SSLSocket clientSocket = null; // Socket with Client
        
        // Inout/Output for PDA Client
        BufferedReader isClient = null; // BufferedReader from PDA Client
        PrintWriter osClient = null; // OutputStream to PDA Client
        
        // Strings
        String msg_String = null,id="",Nom,Password="",Titre="";
        int msg_hash = 0, tentatives=4;
        int i = 0;
        boolean b=true,T=true,again=false;
        int Clientconnected = 0; // Connection marker
        /********End declarations*********/
        
        
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
        
        
        
        
        
        
        
        
        ////////////////////////// receive the data ////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        
        while (T) {
            try {
            	
            	if(First)
            	{
            	
            	
                msg_String = isClient.readLine(); // taking the String from it
                System.out.println("String from the client : " + msg_String);
                Titre=msg_String;
                First=false;
                
                }
            	else
            	{
                msg_String = isClient.readLine(); // taking the String from it
                System.out.println("String from the client : " + msg_String);
            	}
            }
            catch (IOException e) {
                System.out.println("Couldn't get I/O for the connection to: " + e);
                //System.exit(1);
            }
            
            if (msg_String.trim().equals("/Yes")) {
            	
            	
			//JOptionPane.showMessageDialog(null, "Nom et mot de passe corrects!"); 
					
                Clientconnected = 0; // the server is now disconnected
               //clientSocket.close();
                //serverSocket.close();
                JOptionPane.showMessageDialog(null,"Téléchargement de: "+ Titre+ "\n\nNom et mot de passe corrects!"); 
                b=false;
                T=false;
                again=true;
                //System.exit(0);
            }
            
            int tenta=tentatives-1;
            if (msg_String.trim().equals("/No")&tenta!=0)
            			{
					JOptionPane.showMessageDialog(null, 
        		  new String[] {"Téléchargement de: "+ Titre+"\n\nNom ou mot de passe incorrect", "Réesseyez.."},
          "Erreur!",
          JOptionPane.ERROR_MESSAGE);
				}
            
            
            if (msg_String.trim().equals("/No")&tenta==0)
            {
            	// clientSocket.close();
                //serverSocket.close();
                JOptionPane.showMessageDialog(null, 
        		  new String[] {"Téléchargement de: "+ Titre+"\n\nDésolé! ", "Vous ne pouvez pas télécharger ce fichier!"},
          "Erreur!",
          JOptionPane.ERROR_MESSAGE);
                  //System.exit(0);
                  T=false;
                  b=false;
                  again=true;
            }
            
            //******************************************************
            if (Clientconnected == 1) { // if the client is connected
                try {
                    System.out.print("\nComputing...");
                    msg_hash = msg_String.hashCode();
                    System.out.print("done");
                    
                    
                    
                    
                    
     //8888888888888888888888888888888888 THE WINDOW  888888888888888888888888888
     
     if(b){
     
     JTextField utilisateur = new JTextField();
    JPasswordField motDePasse = new JPasswordField();
    
    
    
    int choix = JOptionPane.showConfirmDialog(null, 
      new Object[] {"Téléchargement de: "+ Titre+"\n\nVotre nom :", utilisateur, "Mot de passe :", motDePasse},
      "Authentification",
      JOptionPane.OK_CANCEL_OPTION); 
      
      
      
     
    
    if (choix == JOptionPane.OK_OPTION) 
    
    {
    	id=utilisateur.getText();
    	Password=motDePasse.getText();
    }
    if (choix == JOptionPane.CANCEL_OPTION) {
    
    
    clientSocket.close();
                serverSocket.close();
                T=false;
                // System.exit(0);
                }
     
     
     
     
     
     }
     
     
     
     
    if(!b)
     {
     	clientSocket.close();
                serverSocket.close();
                //System.exit(0);
     }
     
     
     
     		
   
				tentatives--;
				//quitter si le nombres de tentatives est dépassé 
			/*	if(tenta==0) {
					
          clientSocket.close();
                serverSocket.close();
                JOptionPane.showMessageDialog(null, 
        		  new String[] {"Sorry! ", "You are not allowed to downmoad"},
          "Erreur!",
          JOptionPane.ERROR_MESSAGE);
                  System.exit(0);
          
					 } */ 
   
   
   
   
   
   
  if (again)
  {
  		clientSocket.close();
                serverSocket.close();
  }
                   
                   
                   
                   
                   
    //88888888888888888888888888888888888 END WINDOW 88888888888888888888888888888
                   
                   
                    
                    //send id
                    osClient.println(id);
                    osClient.flush(); // put it onto the network
                    
                    //send password
                    osClient.println(Password);
                    osClient.flush();
                    
                    System.out.print("...and sent.\n");
                } //try
                catch (Exception e) {
                    System.out.println(e);
                    //System.exit(1);
                } // catch
            } // if Clientconnected
            
            //*****
            //*****
        } // while
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////
        
        
        
        
        
      System.out.println("*********** fin du socket serveur**********");
      
     
	
		
        
        // System.exit(0);
    } // void main
} // PDAServer class
