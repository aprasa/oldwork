package org.gudy.azureus2.core3.torrent.impl;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import javax.naming.directory.Attribute;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;


public class SupFichiers {
static	Hashtable env = new Hashtable();

public static void go(){

env
.put(Context.INITIAL_CONTEXT_FACTORY,
"com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL, "ldap://localhost:389");
env.put(Context.SECURITY_AUTHENTICATION, "simple");
env.put(Context.SECURITY_PRINCIPAL, "cn=Root,c=gb");
env.put(Context.SECURITY_CREDENTIALS, com.aelitis.azureus.ui.swt.Initializer.LdapPassword);

return;
}

public static void  deleteFile(String role,String FileName,String DisplayName)
{
 String Resultat="";
DirContext dirContext;






try {
dirContext = new InitialDirContext(env);
Attributes matchattribs = new BasicAttributes(true);
matchattribs.put(new BasicAttribute("cn", null));


NamingEnumeration resultat = dirContext.search("ou="+role+",o=Fichiers,c=gb", matchattribs);


while (resultat.hasMore()) {
SearchResult sr = (SearchResult)resultat.next();
System.out.println(" : " + sr.getAttributes().get("cn").get());




Resultat=(String)(sr.getAttributes().get("cn").get() );


//supprimer le fichier

dirContext.unbind("cn="+Resultat+",ou="+role+",o=Fichiers,c=gb");


}
//modifier la description du role, rendre no





try {

Attributes attributes = new BasicAttributes(true);
Attribute attribut = new BasicAttribute("description");
attribut.add("no");
attributes.put(attribut);
dirContext.modifyAttributes("ou="+role+",o=Fichiers,c=gb",
DirContext.REPLACE_ATTRIBUTE,attributes);






dirContext.close();



} catch (NamingException e) {System.err.println("Erreur lors de l'acces au serveur LDAP" + e);e.printStackTrace();
}











dirContext.close();
} catch (NamingException e) {
System.err.println("Erreur lors de l'acces au serveur LDAP" + e);
e.printStackTrace();
}


 
SecureTorrent fen1=new SecureTorrent(FileName,DisplayName);
fen1.setVisible(true);


System.out.println("fin des traitements");
return;
}
}