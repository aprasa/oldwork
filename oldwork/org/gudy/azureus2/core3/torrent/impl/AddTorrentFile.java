package org.gudy.azureus2.core3.torrent.impl;

import java.util.Hashtable; 
import javax.naming.Context; 
import javax.naming.directory.Attribute; 
import javax.naming.directory.Attributes; 
import javax.naming.directory.BasicAttribute; 
import javax.naming.directory.BasicAttributes; 
import javax.naming.directory.DirContext; 
import javax.naming.directory.InitialDirContext; 
 
 
 
public class AddTorrentFile {
 
  DirContext ldapContext;
  String baseName = ",jjjj";
  String serverIP = "X.X.X.X";
  String modelUsername = "template";
 
  public AddTorrentFile () {
    try {
      Hashtable ldapEnv = new Hashtable(11);
      
//    paramètres de connexions du serveur
 
      ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      ldapEnv.put(Context.PROVIDER_URL,  "ldap://localhost:389");
      ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
      ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=Root,c=gb");
      ldapEnv.put(Context.SECURITY_CREDENTIALS, com.aelitis.azureus.ui.swt.Initializer.LdapPassword);
      
      //if(org.gudy.azureus2.core3.peer.impl.transport.PEPeerTransportProtocol.PremierAddTorrentFile)
      if(true)
      {
    	  org.gudy.azureus2.core3.peer.impl.transport.PEPeerTransportProtocol.PremierAddTorrentFile=false;
    
      ldapContext = new InitialDirContext(ldapEnv);
      }
      }
      catch (Exception e) {
        System.out.println(" bind error: " + e);
        e.printStackTrace();
        
     }
  }
 
 
 
 
 
 
 
 
 //creation d'un nouvel utilisateur ******************************
 // 
 // la fonction prend 2 paramettres: nom et chemin
 //
 //elle met le nom dans sa destination dans ldap
 
 
 
  
  public void createNew(String FileName,String chemin,String FileTitle) {
    try {
    	
    	
      	String DN="cn="+FileName+",ou="+chemin+",o=Fichiers,c=gb";
    	String distinguishedName = DN;
    	
    	System.out.println(distinguishedName); //cn=newusers,ou=agenda,dc=alcatel,dc=fr
     
      //Ajout des attributs
    	Attributes newAttributes = new BasicAttributes(true);
    	
    	
    	Attribute crn = new BasicAttribute("objectclass" );
      crn.add("top" );
      crn.add("organizationalPerson" );
      crn.add("pmiUser" );
      crn.add("pkiUser" );
      
      newAttributes.put(crn);
    	
    	
    	
      
    	
      
      newAttributes.put(new BasicAttribute("userPassword", "secret"));

      newAttributes.put(new BasicAttribute("cn", FileName));
      newAttributes.put(new BasicAttribute("sn",FileTitle));
    
      System.out.println("New user in database LDAP -->"+" " + FileName);
      ldapContext.createSubcontext(distinguishedName, newAttributes);
      
    }
    catch (Exception e) {
      System.out.println("create error: " + e);
      e.printStackTrace();
     
    }
  }
 
 
 
 
 
 
 

  }
