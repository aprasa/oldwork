package org.gudy.azureus2.core3.peer.impl.transport;

public class PermisDecision
{public static boolean Decision=false;
	public static boolean getDecision(String UserDN,String FileName)
	{
	
	


	
if(PEPeerTransportProtocol.PremierPermisDecision)
{
	PEPeerTransportProtocol.PremierPermisDecision=false;


FileDN dd=new FileDN();
FileDN.go();

}



 FileDN.getDN("cn="+FileName);


if(FileDN.First)
{
	System.out.println(FileDN.FirstPlace);
	
	//apeler permis
	Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.FirstPlace);
	
	
}


if(!Decision)
{


         //si permis rend une réponse négaitve, essayer avec le suivant :
         if(FileDN.Second)
         {
            	System.out.println(FileDN.SecondPlace);
            	Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.SecondPlace);
	
			}
			if(!Decision)
				{

				//si false, essayer avec le suivant
				if(FileDN.Third)
				{
				System.out.println(FileDN.SecondPlace);	
				Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.ThirdPlace);
				}
				}

}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fifth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fifthPlace);
}
}

//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.sixth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.sixthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.seventh)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.seventhPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.eighth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.eighthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.nineth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.ninethPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.tenth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.tenthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.eleventh)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.eleventhPlace);
}
}//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twelevth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twelevthPlace);
}
}//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirteenth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirteenthPlace);
}
}//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourteenth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourteenthPlace);
}
}//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fifteenth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fifteenthPlace);
}
}//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.sixteenth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.sixteenthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.seventeenth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.seventeenthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.eighteenth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.eighteenthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.nineteenth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.nineteenthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentith)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentithPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentyfirst)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentyfirstPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentysecond)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentysecondPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentythird)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentythirdPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentyfourth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentyfourthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentyfifth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentyfifthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentysixth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentysixthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentyseventh)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentyseventhPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentyeighth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentyeighthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.twentynineth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.twentyninethPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtith)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtithPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtyfirst)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtyfirstPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtysecond)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtysecondPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtythird)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtythirdPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtyfourth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtyfourthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtyfifth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtyfifthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtysixth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtysixthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtyseventh)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtyseventhPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtyeighth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtyeighthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.thirtynineth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.thirtyninethPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtith)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtithPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtyfirst)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtyfirstPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtysecond)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtysecondPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtythird)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtythirdPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtyfourth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtyfourthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtyfifth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtyfifthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtysixth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtysixthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtyseventh)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtyseventhPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtyeighth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtyeighthPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fourtynineth)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fourtyninethPlace);
}
}
//************************************************
if(!Decision)
{

//si false, essayer avec le suivant
if(FileDN.fiftith)
{

Decision=issrg.aef.SampleAEF1.Permis(UserDN,FileDN.fiftithPlace);
}
}





return Decision;
}




}