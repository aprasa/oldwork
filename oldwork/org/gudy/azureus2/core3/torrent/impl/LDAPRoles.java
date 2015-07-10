package org.gudy.azureus2.core3.torrent.impl;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;


public class LDAPRoles {

	
	
public static int taille=0;	
static	String[] Tous=new String[200];
static	String [] Given=new String[200];
static String[] AllOU=new String[200];
static String[] GivenOU=new String[200];
	
	
	
public static String[] getALLRoles()
{
String result="";
		int i=0;
	int j=1;
	for(i=0;i<200;i++)
{
	Tous[i]="";Given[i]="";GivenOU[i]="";AllOU[i]="";
}
	

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
Attributes matchattribs = new BasicAttributes(true);
matchattribs.put(new BasicAttribute("description", null));
NamingEnumeration resultat = dirContext.search("o=Fichiers,c=gb", matchattribs);


i=0;

while (resultat.hasMore()) {
	
	
SearchResult sr = (SearchResult)resultat.next();
//System.out.println("Les roles existants : " + sr.getAttributes().get("ou").get());




result=(String)(sr.getAttributes().get("ou").get());
String Des=(String)(sr.getAttributes().get("description").get());
Tous[i]=Des;
AllOU[i]=result;


if(Des.compareTo("no")<0)
{
	Given[i]=Des;
	GivenOU[i]=result;
	taille++;
}

i++;
j++;



}

i=0;
for(i=0;i<j;i++)
{
//	System.out.println(Tous[i]);
}






dirContext.close();
} catch (NamingException e) {
System.err.println("Erreur lors de l'acces au serveur LDAP" + e);
e.printStackTrace();
}
//System.out.println("fin des traitements");

return Tous;
}



public static String[] getGivenRoles()
{
	return Given;
}






}