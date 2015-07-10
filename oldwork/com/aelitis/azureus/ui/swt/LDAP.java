package com.aelitis.azureus.ui.swt;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
public class LDAP {
	public static boolean decision=false,res=false;
public static void connect(String password) {
	Hashtable env = new Hashtable();
env
.put(Context.INITIAL_CONTEXT_FACTORY,
"com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL, "ldap://localhost:389");
env.put(Context.SECURITY_AUTHENTICATION, "simple");
env.put(Context.SECURITY_PRINCIPAL, "cn=Root,c=gb");
env.put(Context.SECURITY_CREDENTIALS, password);
DirContext dirContext;
try {
dirContext = new InitialDirContext(env);
dirContext.close();
System.out.println("access succeeded...");
decision=true;
} catch (NamingException e) {
System.err.println("Erreur lors de l'acces au serveur LDAP" + e);

e.printStackTrace();
}
return;
}
public static boolean getDecision()
{ res=decision;
decision=false;
	return res;
}

}
