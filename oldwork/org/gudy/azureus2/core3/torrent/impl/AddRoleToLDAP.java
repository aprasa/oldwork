package org.gudy.azureus2.core3.torrent.impl;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class AddRoleToLDAP {
	


static 	DirContext dirContext,dirContext2;
static Hashtable env = new Hashtable();
public static void go(){

//Hashtable env = new Hashtable();
env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL, "ldap://localhost:389");
env.put(Context.SECURITY_AUTHENTICATION, "simple");
env.put(Context.SECURITY_PRINCIPAL, "cn=Root,c=gb");
env.put(Context.SECURITY_CREDENTIALS, com.aelitis.azureus.ui.swt.Initializer.LdapPassword);

}

public static void AddRole(String Chemin,String Role) {


try {
	if(org.gudy.azureus2.core3.peer.impl.transport.PEPeerTransportProtocol.PremierAddRoleToLDAP)
	{
		org.gudy.azureus2.core3.peer.impl.transport.PEPeerTransportProtocol.PremierAddRoleToLDAP=false;
	
go();
	
	
dirContext = new InitialDirContext(env);

}








Attributes attributes = new BasicAttributes(true);
Attribute attribut = new BasicAttribute("description");
attribut.add(Role);
attributes.put(attribut);
dirContext.modifyAttributes(Chemin,
DirContext.REPLACE_ATTRIBUTE,attributes);






} catch (NamingException e) {
	
	
	System.err.println("Erreur lors de l'acces au serveur LDAP" + e);
e.printStackTrace();
}
//System.out.println("fin des traitements add ");
return;
}
}