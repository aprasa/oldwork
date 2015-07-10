package org.gudy.azureus2.core3.peer.impl.transport.torrent;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;



public class VerifierFile {
	
	
public static boolean VerifierFile(String CN) {
	int NbreO=0,NbreOU=0,NbreCN=0;
	int i=0; 
	boolean A=true,Response=false;
	String kika="",kahina="",Resultat="",Besoin=CN;
	String[] tab=new String[400];
	String[] tab2=new String[400];
	String[] tab3=new String[400];
	
	
	for (i=0;i<400;i++)
	{
		tab[i]="";
		tab2[i]="";
		tab3[i]="";
		

}
i=0;
int j=0,k=0;
	
	
	
	
	
Hashtable env = new Hashtable();
env
.put(Context.INITIAL_CONTEXT_FACTORY,
"com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL, "ldap://localhost:389");
env.put(Context.SECURITY_AUTHENTICATION, "simple");
env.put(Context.SECURITY_PRINCIPAL, "cn=Root,C=GB");
env.put(Context.SECURITY_CREDENTIALS, com.aelitis.azureus.ui.swt.Initializer.LdapPassword);



DirContext dirContext;

DirContext dirContext1;






//sauvegarder les entress "o=" dans tab




try {
dirContext = new InitialDirContext(env);
NamingEnumeration e = dirContext.listBindings("c=gb");


while (e.hasMore())
{
Binding b = (Binding) e.next();

kika=b.getName();

tab[i]=kika;

//System.out.println("les entrees o sont "+tab[i]);
NbreO++;
i++;



}




dirContext.close();
} 

catch (NamingException e) {
System.err.println("Erreur lors de l'acces au serveur LDAP" + e);
e.printStackTrace();
}






//sauvegarder les elements "ou" dans tab2
i=0;
k=0;
j=0;

while(A &(i<(NbreO)))
{


try {
dirContext = new InitialDirContext(env);
NamingEnumeration e = dirContext.listBindings(tab[i]+","+"c=gb");


while (e.hasMore())
{
Binding b = (Binding) e.next();

kika=b.getName();

tab2[j]=kika;


//les elements de ou sont:

//System.out.println("\n\n\n   "+tab2[j]);

//tous ces cn:

dirContext1 = new InitialDirContext(env);

NamingEnumeration f = dirContext1.listBindings(tab2[j]+","+tab[i]+","+"c=gb");
k=0;
while (f.hasMore())
{
Binding c = (Binding) f.next();

kahina=c.getName();

tab3[k]=kahina;

//System.out.println("           "+tab3[k]);
Object o2=Besoin;
if(o2.equals(tab3[k]) ) 
{
Resultat=tab3[k]+","+tab2[j]+","+tab[i]+","+"c=gb";

A=false;
}

k++;
}


dirContext1.close();



j++;
NbreOU++;
}


dirContext.close();



} 

catch (NamingException e) {
System.err.println("Erreur lors de l'acces au serveur LDAP" + e);
e.printStackTrace();
}

i++;
}//end while(A)



Object o4=Resultat;
if(o4.equals("")) 
{	System.out.println("\n\n*****************************************************************");

	System.out.println("\n\nle - "+Besoin+" - nexiste pas dans le serveur");

}
else 
		{
			Response=true;
			System.out.println("\n\n\n************************************************************************\n\n");
System.out.println("       le chemin est:    " + Resultat);
System.out.println("\n\n************************************************************************\n\n");
		}



//System.out.println("\n\n\n\nfin des traitements");

return Response;}
}