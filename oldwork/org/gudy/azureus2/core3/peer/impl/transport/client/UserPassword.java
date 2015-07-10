package org.gudy.azureus2.core3.peer.impl.transport.client;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class UserPassword {
	
public static String value=null;	
public static String getPassword(String DN) {
Hashtable env = new Hashtable();
env
.put(Context.INITIAL_CONTEXT_FACTORY,
"com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL, "ldap://localhost:389");
env.put(Context.SECURITY_AUTHENTICATION, "simple");
env.put(Context.SECURITY_PRINCIPAL, "cn=Root,c=gb");
env.put(Context.SECURITY_CREDENTIALS, com.aelitis.azureus.ui.swt.Initializer.LdapPassword);
DirContext dirContext;
try {
dirContext = new InitialDirContext(env);
Attributes attrs = dirContext.getAttributes(DN);
//System.out.println("Password : " + attrs.get("userPassword").get());
byte[] bb =(byte[]) (attrs.get("userPassword").get());
//System.out.println("the password is : "+bb);
int l=bb.length;
//System.out.println("\n de longueur "+l);


	//byte[] byteArray=new byte[] {109,109,87,46,46,46};
		 value=new String(bb);
	//	System.out.println(value);
		
	

		

 
//System.out.println("\n\n\n");









dirContext.close();
} catch (NamingException e) {
System.err.println("Erreur lors de l'acces au serveur LDAP" + e);
e.printStackTrace();
}
//System.out.println("fin des traitements");
return value;
}
}