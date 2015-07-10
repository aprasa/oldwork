package org.gudy.azureus2.core3.peer.impl.transport;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;



public class FileDN {
	static Hashtable env = new Hashtable();
	
static 	DirContext dirContext;

static DirContext dirContext1;
static DirContext dirContext2;
public static int tour=0;
public static String FirstPlace="",SecondPlace="",ThirdPlace="",fourthPlace="",fifthPlace="",sixthPlace="",seventhPlace="",eighthPlace="",ninethPlace="",tenthPlace="",eleventhPlace="",twelevthPlace="",thirteenthPlace="",fourteenthPlace="",fifteenthPlace="",sixteenthPlace="",seventeenthPlace="",eighteenthPlace="",nineteenthPlace="",twentithPlace="",twentyfirstPlace="",twentysecondPlace="",twentythirdPlace="",twentyfourthPlace="",twentyfifthPlace="",twentysixthPlace="",twentyseventhPlace="",twentyeighthPlace="",twentyninethPlace="",thirtithPlace="",thirtyfirstPlace="",thirtysecondPlace="",thirtythirdPlace="",thirtyfourthPlace="",thirtyfifthPlace="",thirtysixthPlace="",thirtyseventhPlace="",thirtyeighthPlace="",thirtyninethPlace="",fourtithPlace="",fourtyfirstPlace="",fourtysecondPlace="",fourtythirdPlace="",fourtyfourthPlace="",fourtyfifthPlace="",fourtysixthPlace="",fourtyseventhPlace="",fourtyeighthPlace="",fourtyninethPlace="",fiftithPlace="";
public static boolean First=false,Second=false,Third=false,fourth=false ,fifth=false ,sixth=false ,seventh=false ,eighth=false ,nineth=false ,tenth=false ,eleventh=false ,twelevth=false ,thirteenth=false ,fourteenth=false ,fifteenth=false ,sixteenth=false ,seventeenth=false ,eighteenth=false ,nineteenth=false ,twentith=false ,twentyfirst=false ,twentysecond=false ,twentythird=false ,twentyfourth=false ,twentyfifth=false,twentysixth=false ,twentyseventh=false, twentyeighth=false, twentynineth=false,thirtith=false,thirtyfirst=false,thirtysecond=false,thirtythird=false,thirtyfourth=false,thirtyfifth=false,thirtysixth=false,thirtyseventh=false,thirtyeighth=false,thirtynineth=false,fourtith=false,fourtyfirst=false,fourtysecond=false,fourtythird=false,fourtyfourth=false,fourtyfifth=false,fourtysixth=false,fourtyseventh=false,fourtyeighth=false,fourtynineth=false,fiftith=false;
public static boolean Premier=true,Premier1=true,isPublic=false;
	public static void UserDN()
	{
	

	
	}
	public static  void go()
	{
	
	
	
//Hashtable env = new Hashtable();
env
.put(Context.INITIAL_CONTEXT_FACTORY,
"com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL, "ldap://localhost:389");
env.put(Context.SECURITY_AUTHENTICATION, "simple");
env.put(Context.SECURITY_PRINCIPAL, "cn=Root,C=GB");
env.put(Context.SECURITY_CREDENTIALS, com.aelitis.azureus.ui.swt.Initializer.LdapPassword);




}
public static void getDN(String CN)
 {
	int NbreO=0,NbreOU=0,NbreCN=0;
	int i=0; 
	boolean A=true,Response=false;
	String kika="",kahina="",Besoin=CN;
    String Resultat="";
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





tab[0]="o=Fichiers";
tab[1]="o=Membres";
tab[2]="o=PERMISv5";
NbreO=3;
i=2;


//sauvegarder les entress "o=" dans tab




/*try {
dirContext = new InitialDirContext(env);
NamingEnumeration e = dirContext.listBindings("c=gb");


while (e.hasMore())
{
Binding b = (Binding) e.next();

kika=b.getName();

tab[i]=kika;

System.out.println("les entrees o sont "+tab[i]);
NbreO++;
i++;



}


dirContext.close();


} 

catch (NamingException e) {
System.err.println("Erreur lors de l'acces au serveur LDAP" + e);
e.printStackTrace();
}
*/





//sauvegarder les elements "ou" dans tab2
i=0;
k=0;
j=0;

while(A &(i<(NbreO)))
{


try {
	if (Premier){
	
dirContext2 = new InitialDirContext(env);
Premier=false;
}
NamingEnumeration e = dirContext2.listBindings(tab[i]+","+"c=gb");


while (e.hasMore())
{
Binding b = (Binding) e.next();

kika=b.getName();

tab2[j]=kika;


//les elements de ou sont:

//System.out.println("\n\n\n   "+tab2[j]);

//tous ces cn:
if (Premier1){

dirContext1 = new InitialDirContext(env);
Premier1=false;
}
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
String s=tab2[j];
if(s.compareTo("ou=Public")==0) isPublic=true;
tour++;
if(tour==1)
{
	FirstPlace=Resultat;First=true;
}
if(tour==2)
{
	SecondPlace=Resultat;Second=true;
}
if(tour==3)
{
	ThirdPlace=Resultat;Third=true;
}

if(tour==4)
{
	fourthPlace=Resultat;fourth=true;
}


if(tour==5)
{
	fifthPlace=Resultat;fifth=true;
}


if(tour==6)
{
	sixthPlace=Resultat;sixth=true;
}


if(tour==7)
{
	seventhPlace=Resultat;seventh=true;
}


if(tour==8)
{
	eighthPlace=Resultat;eighth=true;
}


if(tour==9)
{
	ninethPlace=Resultat;nineth=true;
}


if(tour==10)
{
	tenthPlace=Resultat;tenth=true;
}


if(tour==11)
{
	eleventhPlace=Resultat;eleventh=true;
}


if(tour==12)
{
	twelevthPlace=Resultat;twelevth=true;
}


if(tour==13)
{
	thirteenthPlace=Resultat;thirteenth=true;
}


if(tour==14)
{
	fourteenthPlace=Resultat;fourteenth=true;
}


if(tour==15)
{
	fifteenthPlace=Resultat;fifteenth=true;
}


if(tour==16)
{
	sixteenthPlace=Resultat;sixteenth=true;
}


if(tour==17)
{
	seventeenthPlace=Resultat;seventeenth=true;
}


if(tour==18)
{
	eighteenthPlace=Resultat;eighteenth=true;
}


if(tour==19)
{
	nineteenthPlace=Resultat;nineteenth=true;
}


if(tour==20)
{
	twentithPlace=Resultat;twentith=true;
}


if(tour==21)
{
	twentyfirstPlace=Resultat;twentyfirst=true;
}

if(tour==22)
{
	twentysecondPlace=Resultat;twentysecond=true;
}

if(tour==23)
{
	twentythirdPlace=Resultat;twentythird=true;
}

if(tour==24)
{
	twentyfourthPlace=Resultat;twentyfourth=true;
}

if(tour==25)
{
	twentyfifthPlace=Resultat;twentyfifth=true;
}

if(tour==26)
{
	twentysixthPlace=Resultat;twentysixth=true;
}

if(tour==27)
{
	twentyseventhPlace=Resultat;twentyseventh=true;
}

if(tour==28)
{
	twentyeighthPlace=Resultat;twentyeighth=true;
}

if(tour==29)
{
	twentyninethPlace=Resultat;twentynineth=true;
}

if(tour==30)
{
	thirtithPlace=Resultat;thirtith=true;
}

if(tour==31)
{
	thirtyfirstPlace=Resultat;thirtyfirst=true;
}

if(tour==32)
{
	thirtysecondPlace=Resultat;thirtysecond=true;
}

if(tour==33)
{
	thirtythirdPlace=Resultat;thirtythird=true;
}

if(tour==34)
{
	thirtyfourthPlace=Resultat;fourtyfourth=true;
}

if(tour==35)
{
	thirtyfifthPlace=Resultat;thirtyfifth=true;
}

if(tour==36)
{
	thirtysixthPlace=Resultat;thirtysixth=true;
}

if(tour==37)
{
	thirtyseventhPlace=Resultat;thirtyseventh=true;
}

if(tour==38)
{
	thirtyeighthPlace=Resultat;thirtyeighth=true;
}

if(tour==39)
{
	thirtyninethPlace=Resultat;thirtynineth=true;
}

if(tour==40)
{
	fourtithPlace=Resultat;fourtith=true;
}

if(tour==41)
{
	fourtyfirstPlace=Resultat;fourtyfirst=true;
}

if(tour==42)
{
	fourtysecondPlace=Resultat;fourtysecond=true;
}

if(tour==43)
{
	fourtythirdPlace=Resultat;fourtythird=true;
}

if(tour==44)
{
	fourtyfourthPlace=Resultat;fourtyfourth=true;
}

if(tour==45)
{
	fourtyfifthPlace=Resultat;fourtyfourth=true;
}

if(tour==46)
{
	fourtysixthPlace=Resultat;fourtysixth=true;
}

if(tour==47)
{
	fourtyseventhPlace=Resultat;fourtyseventh=true;
}

if(tour==48)
{
	fourtyeighthPlace=Resultat;fourtyeighth=true;
}

if(tour==49)
{
	fourtyninethPlace=Resultat;fourtynineth=true;
}

if(tour==50)
{
	fiftithPlace=Resultat;fiftith=true;
}


//A=false;
}

k++;
}






j++;
NbreOU++;
}





} 

catch (NamingException e) {
System.err.println("Erreur lors de l'acces au serveur LDAP" + e);
e.printStackTrace();
}

i++;
}//end while(A)



Object o4=Resultat;
if(o4.equals("")) 
{//	System.out.println("\n\n*****************************************************************");

//	System.out.println("\n\nle - "+Besoin+" - nexiste pas dans le serveur");

}
else 
		{
			Response=true;
		//	System.out.println("\n\n\n************************************************************************\n\n");
//System.out.println("       le chemin est:    " + Resultat);
//System.out.println("\n\n************************************************************************\n\n");
		}



//System.out.println("\n\n\n\nfin des traitements");

//return Resultat;



}//end VerifierCN(String)

public static void SetZero()
{try{

	//dirContext.close();
	dirContext1.close();
	dirContext2.close();
	}
	catch (NamingException e) {
System.err.println("Erreur lors de l'acces au serveur LDAP" + e);
e.printStackTrace();
}
	
}
public static boolean isPublic()
{
	return isPublic;
}
public static void reset()
{
	FirstPlace="";SecondPlace="";ThirdPlace="";fourthPlace="";fifthPlace="";sixthPlace="";seventhPlace="";eighthPlace="";ninethPlace="";tenthPlace="";eleventhPlace="";twelevthPlace="";thirteenthPlace="";fourteenthPlace="";fifteenthPlace="";sixteenthPlace="";seventeenthPlace="";eighteenthPlace="";nineteenthPlace="";twentithPlace="";twentyfirstPlace="";twentysecondPlace="";twentythirdPlace="";twentyfourthPlace="";twentyfifthPlace="";twentysixthPlace="";twentyseventhPlace="";twentyeighthPlace="";twentyninethPlace="";thirtithPlace="";thirtyfirstPlace="";thirtysecondPlace="";thirtythirdPlace="";thirtyfourthPlace="";thirtyfifthPlace="";thirtysixthPlace="";thirtyseventhPlace="";thirtyeighthPlace="";thirtyninethPlace="";fourtithPlace="";fourtyfirstPlace="";fourtysecondPlace="";fourtythirdPlace="";fourtyfourthPlace="";fourtyfifthPlace="";fourtysixthPlace="";fourtyseventhPlace="";fourtyeighthPlace="";fourtyninethPlace="";fiftithPlace="";	
	First=false;Second=false;Third=false;fourth=false ;fifth=false ;sixth=false ;seventh=false ;eighth=false ;nineth=false ;tenth=false ;eleventh=false ;twelevth=false ;thirteenth=false ;fourteenth=false ;fifteenth=false ;sixteenth=false ;seventeenth=false ;eighteenth=false ;nineteenth=false ;twentith=false ;twentyfirst=false ;twentysecond=false ;twentythird=false ;twentyfourth=false ;twentyfifth=false;twentysixth=false ;twentyseventh=false; twentyeighth=false; twentynineth=false;thirtith=false;thirtyfirst=false;thirtysecond=false;thirtythird=false;thirtyfourth=false;thirtyfifth=false;thirtysixth=false;thirtyseventh=false;thirtyeighth=false;thirtynineth=false;fourtith=false;fourtyfirst=false;fourtysecond=false;fourtythird=false;fourtyfourth=false;fourtyfifth=false;fourtysixth=false;fourtyseventh=false;fourtyeighth=false;fourtynineth=false;fiftith=false;
	isPublic=false;
	return;
}





}