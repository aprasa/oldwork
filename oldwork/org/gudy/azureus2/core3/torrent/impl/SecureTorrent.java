package org.gudy.azureus2.core3.torrent.impl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class SecureTorrent extends JFrame implements ActionListener, ItemListener
{    static  int NombreCases=0;
	public static String NouveauRole="",Display="",hash="",clearName="";
	public static boolean PremierAddTorrentFile=true,PremierAddRole=true,PremierAddRoleToLDAP=true,PremierChemin2=true,PremierChemin1=true;
	int position=1000;
	String[] All2=new String[200], All=new String[200];;
	public static String [] MesOU=new String[200];
	static boolean a=false,b=false,c=false,d=false,e1=false,f=false,g=false,h=false,i2=false,j=false,k=false,l=false,m=false,n=false,o=false,p=false,q=false,r=false,s=false,t=false,u=false,v=false,w=false,x=false,y=false,z=false,a1=false,a2=false,a3=false,a4=false,a5=false,a6=false,a7=false,a8=false,a9=false,b1=false,b2=false,b3=false,b4=false,b5=false,b6=false,b7=false,b8=false,b9=false,c1=false,c2=false,c3=false,c4=false,c5=false,c6=false,c7=false;
Container contenu=getContentPane();


	public SecureTorrent(String FileName,String DisplayName)
	{  hash=FileName;
	clearName=DisplayName;
	    FileName1=FileName;
	    Display=DisplayName;
		setTitle("Partage de fichiers");
		setSize(400,400);
		
		
		
		
		 Toolkit tk=Toolkit.getDefaultToolkit();
		Image img=tk.getImage("a16.png");
         setIconImage(img);
		
		
		
		
		
		
		//reset();
		setResizable(false);
		setLocationRelativeTo(null);
		setBackground(Color.RED);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
		contenu.setLayout(new FlowLayout());
		
		
		 
		compte=new JLabel ("Avec qui voulez-vous partager le fichier:                                                   ");
		compte.setFont(new Font("Arian", Font.BOLD, 12));
		compte.setForeground(Color.blue) ;
			contenu.add(compte);
			
			
			
			compte3=new JLabel (""+ DisplayName                 );
		compte3.setFont(new Font("Arian", Font.BOLD, 13));
		compte3.setForeground(Color.blue) ;
			contenu.add(compte3);
			
			
			rien2=new JLabel ("   \n _____________________________________________________________________  ");
					rien2.setForeground(Color.blue) ;
		
			rien3=new JLabel (" \n\n\n\n\n                                                                                                                   ");
			
		
		compte2=new JLabel ("\n\n\n__________________________________________________________________");
		compte2.setFont(new Font("Arian", Font.BOLD, 12));
		compte2.setForeground(Color.blue) ;
		contenu.add(compte2);
		contenu.add(rien2);
		
		
		
		
		
		coche1=new JCheckBox ("Amis      ");
		
		coche1.addActionListener(this);
		coche1.addItemListener(this);
		
		
		coche2=new JCheckBox ("Famille      ");
		
		coche2.addActionListener(this);
		coche2.addItemListener(this);
		
		
		coche3=new JCheckBox ("Collègues     ");
		
		coche3.addActionListener(this);
		coche3.addItemListener(this);
		
		
		
		coche4=new JCheckBox ("Amis      ");
		
		coche4.addActionListener(this);
		coche4.addItemListener(this);
		
		
		
		
		coche5=new JCheckBox ("Amis      ");
		
		coche5.addActionListener(this);
		coche5.addItemListener(this);
		
		
		
		
		coche6=new JCheckBox ("Amis      ");
		
		coche6.addActionListener(this);
		coche6.addItemListener(this);
		
		
		
		
		
		coche7=new JCheckBox ("Amis      ");
		
		coche7.addActionListener(this);
		coche7.addItemListener(this);
		
		
		
		
		
		coche8=new JCheckBox ("     ");
		
		coche8.addActionListener(this);
		coche8.addItemListener(this);
		
		
		
		
		coche9=new JCheckBox ("Amis      ");
		
		coche9.addActionListener(this);
		coche9.addItemListener(this);
		
		
		
		coche10=new JCheckBox ("Amis      ");
		
		coche10.addActionListener(this);
		coche10.addItemListener(this);
		
		
		
		coche11=new JCheckBox ("Amis      ");
		
		coche11.addActionListener(this);
		coche11.addItemListener(this);
		
		
		
		
		coche12=new JCheckBox ("Amis      ");
		
		coche12.addActionListener(this);
		coche12.addItemListener(this);
		
		
		
		
		coche13=new JCheckBox ("Amis      ");
		
		coche13.addActionListener(this);
		coche13.addItemListener(this);
		
		
		
		
		coche14=new JCheckBox ("Amis      ");
		
		coche14.addActionListener(this);
		coche14.addItemListener(this);
		
		
		
		
		coche15=new JCheckBox ("Amis      ");
		
		coche15.addActionListener(this);
		coche15.addItemListener(this);
		
		
		coche16=new JCheckBox ("Amis      ");
		
		coche16.addActionListener(this);
		coche16.addItemListener(this);
		
		
		coche17=new JCheckBox ("Amis      ");
		
		coche17.addActionListener(this);
		coche17.addItemListener(this);
		
		
		coche18=new JCheckBox ("Amis      ");
		
		coche18.addActionListener(this);
		coche18.addItemListener(this);
		
		
		coche19=new JCheckBox ("Amis      ");
		
		coche19.addActionListener(this);
		coche19.addItemListener(this);
		
		
		coche20=new JCheckBox ("Amis      ");
		
		coche20.addActionListener(this);
		coche20.addItemListener(this);
		
		coche21=new JCheckBox ("Amis      ");
		
		coche21.addActionListener(this);
		coche21.addItemListener(this);
		
		
			coche22=new JCheckBox ("Amis      ");
		
		coche22.addActionListener(this);
		coche22.addItemListener(this);
		
		
		coche23=new JCheckBox ("Amis      ");
		
		coche23.addActionListener(this);
		coche23.addItemListener(this);
		//_________________________
		
		
		coche24=new JCheckBox ("Amis      ");
		
		coche24.addActionListener(this);
		coche24.addItemListener(this);
		
		
		
		coche25=new JCheckBox ("Amis      ");
		
		coche25.addActionListener(this);
		coche25.addItemListener(this);
		
		
		
		
		coche26=new JCheckBox ("Amis      ");
		
		coche26.addActionListener(this);
		coche26.addItemListener(this);
		
		
		
		
		coche27=new JCheckBox ("Amis      ");
		
		coche27.addActionListener(this);
		coche27.addItemListener(this);
		
		
		
		
		coche28=new JCheckBox ("Amis      ");
		
		coche28.addActionListener(this);
		coche28.addItemListener(this);
		
		
		
		
		coche29=new JCheckBox ("Amis      ");
		
		coche29.addActionListener(this);
		coche29.addItemListener(this);
		
		
		
		
		coche30=new JCheckBox ("Amis      ");
		
		coche30.addActionListener(this);
		coche30.addItemListener(this);
		
		
		
		
		coche31=new JCheckBox ("Amis      ");
		
		coche31.addActionListener(this);
		coche31.addItemListener(this);
		
		
		
		
		coche32=new JCheckBox ("Amis      ");
		
		coche32.addActionListener(this);
		coche32.addItemListener(this);
		
		
		
		
		coche33=new JCheckBox ("Amis      ");
		
		coche33.addActionListener(this);
		coche33.addItemListener(this);
		
		
		
		
		coche34=new JCheckBox ("Amis      ");
		
		coche34.addActionListener(this);
		coche34.addItemListener(this);
		
		
		
		
		coche35=new JCheckBox ("Amis      ");
		
		coche35.addActionListener(this);
		coche35.addItemListener(this);
		
		
		
		
		coche36=new JCheckBox ("Amis      ");
		
		coche36.addActionListener(this);
		coche36.addItemListener(this);
		
		
		
		
		coche37=new JCheckBox ("Amis      ");
		
		coche37.addActionListener(this);
		coche37.addItemListener(this);
		
		
		
		
		coche38=new JCheckBox ("Amis      ");
		
		coche38.addActionListener(this);
		coche38.addItemListener(this);
		
		
		
		
		coche39=new JCheckBox ("Amis      ");
		
		coche39.addActionListener(this);
		coche39.addItemListener(this);
		
		
		
		
		coche40=new JCheckBox ("Amis      ");
		
		coche40.addActionListener(this);
		coche40.addItemListener(this);
		
		
		
		
		coche41=new JCheckBox ("Amis      ");
		
		coche41.addActionListener(this);
		coche41.addItemListener(this);
		
		
		
		coche42=new JCheckBox ("Amis      ");
		
		coche42.addActionListener(this);
		coche42.addItemListener(this);
		
		
		
		coche43=new JCheckBox ("Amis      ");
		
		coche43.addActionListener(this);
		coche43.addItemListener(this);
		
		
		
		coche44=new JCheckBox ("Amis      ");
		
		coche44.addActionListener(this);
		coche44.addItemListener(this);
		
		
		
		coche45=new JCheckBox ("Amis      ");
		
		coche45.addActionListener(this);
		coche45.addItemListener(this);
		
		
		
		coche46=new JCheckBox ("Amis      ");
	
		coche46.addActionListener(this);
		coche46.addItemListener(this);
		
		
		
	coche47=new JCheckBox ("Amis      ");
		
		coche47.addActionListener(this);
		coche47.addItemListener(this);
		
		
		
		coche48=new JCheckBox ("Amis      ");
		
		coche48.addActionListener(this);
		coche48.addItemListener(this);
		
		
		
		coche49=new JCheckBox ("Amis      ");
		
		coche49.addActionListener(this);
		coche49.addItemListener(this);
		
		
		coche50=new JCheckBox ("Amis      ");
		
		coche50.addActionListener(this);
		coche50.addItemListener(this);
		
		
		
		
		
		
		
		//////////////////////////////////////////////////:
		
			
		
		etat=new JButton ("           OK         ");
		
		etat.addActionListener(this);
		
		New=new JButton ("  Ajouter rôles  ");
		
		New.addActionListener(this);
		
		
		
		supp=new JButton ("Supprimer rôles");
		
		supp.addActionListener(this);
		
		
			terminer=new JButton ("Terminer");
		contenu.add(terminer);
		terminer.addActionListener(this);
		terminer.setVisible(false);
		
		
		
		
		terminer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               setVisible(false);
            }
         });
/////////////////////////////////////////////////////////////////////


		String[] All=LDAPRoles.getALLRoles();
		All2=All;
		
		
	
		String[] Given=LDAPRoles.getGivenRoles();
		
		int i=0;
//for(i=0;i<50;i++)
//{
	//System.out.println(LDAPRoles.AllOU[i]+" de role:   "+All[i]);
//}
	//System.out.println("********* les roles occupés ****************************************");
		
/*for(i=0;i<50;i++)
{
	System.out.println(LDAPRoles.GivenOU[i]+"  de role:  "+Given[i]);
}*/

//System.out.println("********* Mes roles****************************************");
for(i=0;i<50;i++)
{
MesOU[i]=LDAPRoles.AllOU[i];

}




//////////////////////////////////////////////////////////////////////



//premier bouton
if(All[0].compareTo("no")<0)
{
	contenu.add(coche1);
	coche1.setText(All[0]);


	
}

//2 e bouton

if(All[1].compareTo("no")<0)
{
	contenu.add(coche2);
	coche2.setText(All[1]);
	
	
}


//3 e bouton
if(All[2].compareTo("no")<0)
{
	contenu.add(coche3);
	coche3.setText(All[2]);
	
}



//' e bouton
if(All[3].compareTo("no")<0)
{
	contenu.add(coche4);
	coche4.setText(All[3]);
	coche4.setForeground(Color.blue) ;
}



//5 e bouton
if(All[4].compareTo("no")<0)
{
	contenu.add(coche5);
	coche5.setText(All[4]);
	
}


if(All[5].compareTo("no")<0)
{
	contenu.add(coche6);
	coche6.setText(All[5]);
}





if(All[6].compareTo("no")<0)
{
	contenu.add(coche7);
	coche7.setText(All[6]);
}





if(All[7].compareTo("no")<0)
if(oo==5)
{
	contenu.add(coche8);
	coche8.setText(All[7]);
}





if(All[8].compareTo("no")<0)
{
	contenu.add(coche9);
	coche9.setText(All[8]);
}





if(All[9].compareTo("no")<0)

{
	contenu.add(coche10);
	coche10.setText(All[9]);

	
}





if(All[10].compareTo("no")<0)
{
	contenu.add(coche11);
	coche11.setText(All[10]);
}



if(All[11].compareTo("no")<0)
{
	contenu.add(coche12);
	coche12.setText(All[11]);
}



if(All[12].compareTo("no")<0)
{
	contenu.add(coche13);
	coche13.setText(All[12]);
}



if(All[13].compareTo("no")<0)
{
	contenu.add(coche14);
	coche14.setText(All[13]);
}



if(All[14].compareTo("no")<0)
{
	contenu.add(coche15);
	coche15.setText(All[14]);
}


if(All[15].compareTo("no")<0)
{
	contenu.add(coche16);
	coche16.setText(All[15]);
}



if(All[16].compareTo("no")<0)
{
	contenu.add(coche17);
	coche17.setText(All[16]);
}





if(All[17].compareTo("no")<0)
{
	contenu.add(coche18);
	coche18.setText(All[17]);
}





if(All[18].compareTo("no")<0)
{
	contenu.add(coche19);
	coche19.setText(All[18]);
}






if(All[19].compareTo("no")<0)
{
	contenu.add(coche20);
	coche20.setText(All[19]);
}



if(All[20].compareTo("no")<0)
{
	contenu.add(coche21);
	coche21.setText(All[20]);
}




if(All[21].compareTo("no")<0)
{
	contenu.add(coche22);
	coche22.setText(All[21]);
}

//_____________________


if(All[22].compareTo("no")<0)
{
	contenu.add(coche23);
	coche23.setText(All[22]);
}



if(All[23].compareTo("no")<0)
{
	contenu.add(coche24);
	coche24.setText(All[23]);
}


if(All[24].compareTo("no")<0)
{
	contenu.add(coche25);
	coche25.setText(All[24]);
}



if(All[25].compareTo("no")<0)
{
	contenu.add(coche26);
	coche26.setText(All[25]);
}




if(All[26].compareTo("no")<0)
{
	contenu.add(coche27);
	coche27.setText(All[26]);
}




if(All[27].compareTo("no")<0)
{
	contenu.add(coche28);
	coche28.setText(All[27]);
}




if(All[28].compareTo("no")<0)
{
	contenu.add(coche29);
	coche29.setText(All[28]);
}




if(All[29].compareTo("no")<0)
{
	contenu.add(coche30);
	coche30.setText(All[29]);
}




if(All[30].compareTo("no")<0)
{
	contenu.add(coche31);
	coche31.setText(All[30]);
}




if(All[31].compareTo("no")<0)
{
	contenu.add(coche32);
	coche32.setText(All[31]);
}




if(All[32].compareTo("no")<0)
{
	contenu.add(coche33);
	coche33.setText(All[32]);
}




if(All[33].compareTo("no")<0)
{
	contenu.add(coche34);
	coche34.setText(All[33]);
}




if(All[34].compareTo("no")<0)
{
	contenu.add(coche35);
	coche35.setText(All[34]);
}




if(All[35].compareTo("no")<0)
{
	contenu.add(coche36);
	coche36.setText(All[35]);
}




if(All[36].compareTo("no")<0)
{
	contenu.add(coche37);
	coche37.setText(All[36]);
}




if(All[37].compareTo("no")<0)
{
	contenu.add(coche38);
	coche38.setText(All[37]);
}




if(All[38].compareTo("no")<0)
{
	contenu.add(coche39);
	coche39.setText(All[38]);
}




if(All[39].compareTo("no")<0)
{
	contenu.add(coche40);
	coche40.setText(All[39]);
}



if(All[40].compareTo("no")<0)
{
	contenu.add(coche41);
	coche41.setText(All[40]);
}




if(All[41].compareTo("no")<0)
{
	contenu.add(coche42);
	coche42.setText(All[41]);
}



if(All[42].compareTo("no")<0)
{
	contenu.add(coche43);
	coche43.setText(All[42]);
}



if(All[43].compareTo("no")<0)
{
	contenu.add(coche44);
	coche44.setText(All[43]);
}



if(All[44].compareTo("no")<0)
{
	contenu.add(coche45);
	coche45.setText(All[44]);
}



if(All[45].compareTo("no")<0)
{
	contenu.add(coche46);
	coche6.setText(All[45]);
}



if(All[46].compareTo("no")<0)
{
	contenu.add(coche47);
	coche47.setText(All[46]);
}



if(All[47].compareTo("no")<0)
{
	contenu.add(coche48);
	coche48.setText(All[47]);
}




if(All[48].compareTo("no")<0)
{
	contenu.add(coche49);
	coche49.setText(All[48]);
}



if(All[49].compareTo("no")<0)
{
	contenu.add(coche50);
	coche50.setText(All[49]);
}













/*contenu.add(coche1);		
contenu.add(coche2);		

contenu.add(coche3);*/


	contenu.add(rien2);
contenu.add(rien3);

contenu.add(New,BorderLayout.SOUTH);
contenu.add(etat,BorderLayout.WEST);

contenu.add(supp);

















		
	}
	
	/////////////ecouter les evnements
	
public void itemStateChanged(ItemEvent ev)
	{
		Object source=ev.getSource();
		
		 a = coche1.isSelected(); 
		  b= coche2.isSelected();
		   c= coche3.isSelected();
		    d= coche4.isSelected();
		     e1= coche5.isSelected();
		     f= coche6.isSelected();
		     g= coche7.isSelected();
		     h= coche8.isSelected();
		     i2= coche9.isSelected();
		    j= coche10.isSelected();
		     k= coche11.isSelected();
		     l= coche12.isSelected();
		     m= coche13.isSelected();
		     n= coche14.isSelected();
		     o= coche15.isSelected();
		     p=coche16.isSelected();
		     q=coche17.isSelected();
		     r=coche18.isSelected();
		     s=coche19.isSelected();
		     t=coche20.isSelected();
		     u=coche21.isSelected();
		     v=coche22.isSelected();
		     w=coche23.isSelected();
		     y=coche24.isSelected();
		     z=coche25.isSelected();
		     //_____________
		     a1=coche26.isSelected();
		     a2=coche27.isSelected();
		     a3=coche28.isSelected();
		     a4=coche29.isSelected();
		     a5=coche30.isSelected();
		     a6=coche31.isSelected();
		     a7=coche32.isSelected();
		     a8=coche33.isSelected();
		     a9=coche34.isSelected();
		     b1=coche36.isSelected();
		     b2=coche37.isSelected();
		     b3=coche38.isSelected();
		     b4=coche39.isSelected();
		     b5=coche40.isSelected();
		     b6=coche41.isSelected();
		     b7=coche42.isSelected();
		     b8=coche43.isSelected();
		     b9=coche44.isSelected();
		     c1=coche45.isSelected();
		     c2=coche46.isSelected();
		     c3=coche47.isSelected();
		     c4=coche48.isSelected();
		     c5=coche49.isSelected();
		     c6=coche50.isSelected();
		     //c7=coche51.isSelected();
		     
		     if(d)
		     {
		     	coche1.setEnabled(false);
									coche2.setEnabled(false);
									coche3.setEnabled(false);
								
									
									
									coche5.setEnabled(false);
									coche6.setEnabled(false);
									coche7.setEnabled(false);
									coche8.setEnabled(false);
									coche9.setEnabled(false);
									coche10.setEnabled(false);
									coche11.setEnabled(false);
									coche12.setEnabled(false);
									coche13.setEnabled(false);
									coche14.setEnabled(false);
									coche15.setEnabled(false);
									coche16.setEnabled(false);
									coche17.setEnabled(false);
									coche18.setEnabled(false);
									coche19.setEnabled(false);
									coche20.setEnabled(false);
									coche21.setEnabled(false);
									coche22.setEnabled(false);
									coche23.setEnabled(false);
									//_____________________
									coche24.setEnabled(false);
									coche25.setEnabled(false);
									coche26.setEnabled(false);
								    coche27.setEnabled(false);
								    coche28.setEnabled(false);
								    coche29.setEnabled(false);
								    coche30.setEnabled(false);
								    coche31.setEnabled(false);
								    coche32.setEnabled(false);
								    coche33.setEnabled(false);
								    coche34.setEnabled(false);
								    coche35.setEnabled(false);
								    coche36.setEnabled(false);
								    coche37.setEnabled(false);
								    coche38.setEnabled(false);
								    coche39.setEnabled(false);
								    coche40.setEnabled(false);
								    coche41.setEnabled(false);
								    coche42.setEnabled(false);
								    coche43.setEnabled(false);
								    coche44.setEnabled(false);
								    coche45.setEnabled(false);
								    coche46.setEnabled(false);
								    coche47.setEnabled(false);
								    coche48.setEnabled(false);
								    coche49.setEnabled(false);
								    coche50.setEnabled(false);
		     }
		     else{
		     	coche1.setEnabled(true);
									coche2.setEnabled(true);
									coche3.setEnabled(true);
									
									
									coche5.setEnabled(true);
									coche6.setEnabled(true);
									coche7.setEnabled(true);
									coche8.setEnabled(true);
									coche9.setEnabled(true);
									coche10.setEnabled(true);
									coche11.setEnabled(true);
									coche12.setEnabled(true);
									coche13.setEnabled(true);
									coche14.setEnabled(true);
									coche15.setEnabled(true);
									coche16.setEnabled(true);
									coche17.setEnabled(true);
									coche18.setEnabled(true);
									coche19.setEnabled(true);
									coche20.setVisible(true);
									coche21.setEnabled(true);
									coche22.setEnabled(true);
									coche23.setEnabled(true);
									//_____________________
									coche24.setEnabled(true);
									coche25.setEnabled(true);
									coche26.setEnabled(true);
								    coche27.setEnabled(true);
								    coche28.setEnabled(true);
								    coche29.setEnabled(true);
								    coche30.setEnabled(true);
								    coche31.setEnabled(true);
								    coche32.setEnabled(true);
								    coche33.setEnabled(true);
								    coche34.setEnabled(true);
								    coche35.setEnabled(true);
								    coche36.setEnabled(true);
								    coche37.setEnabled(true);
								    coche38.setEnabled(true);
								    coche39.setEnabled(true);
								    coche40.setEnabled(true);
								    coche41.setEnabled(true);
								    coche42.setEnabled(true);
								    coche43.setEnabled(true);
								    coche44.setEnabled(true);
								    coche45.setEnabled(true);
								    coche46.setEnabled(true);
								    coche47.setEnabled(true);
								    coche48.setEnabled(true);
								    coche49.setEnabled(true);
								    coche50.setEnabled(true);
		     }
		
		
		 if(!(!a&!b&!c&!e1&!f&!g&!h&!i2&!j&!k&!l&!m&!n&!o&!p&!q&!r&!s&!t&!u&!v&!w&!x&!y&!z&!a1&!a2&!a3&!a4&!a5&!a6&!a7&!a8&!a9&!b1&!b2&!b3&!b4&!b5&!b6&!b7&!b8&!b9&!c1&!c2&!c3&!c4&!c5&!c6&!c7)) 	coche4.setEnabled(false);
		 else 	coche4.setEnabled(true);
		
		     
		
	}
	/////////////////////// ajouter des roles ///////////////////// 
	
	public class AjouterRole extends JFrame implements ActionListener {
   
   
   
   private JLabel Message = new JLabel( "Ajout d'un rôle\n" );
   private JLabel Message2 = new JLabel( "" );
   
	private JButton  ok=new JButton ("OK");
	private JButton  annuler=new JButton ("Annuler");
	 private Saisie nom = new Saisie("");
	 String text="";

	
	
	
	 
	 
			
	
	
	
   private JPanel panneau = new JPanel();
   
   public AjouterRole() {
      super("Ajouter rôles");     
       
      Message.setFont(new Font("Arial", Font.BOLD, 12));
      Message.setForeground(Color.blue) ;
      Message.setHorizontalAlignment(JLabel.CENTER);
      Message.setBorder(BorderFactory.createLoweredBevelBorder());
     
      add(Message);   
      
        
      
      
      panneau.setBackground(Color.BLUE);
      panneau.add(nom);
      
      panneau.add(ok);
      panneau.add(annuler);
      
      ok.addActionListener(this);
       annuler.addActionListener(this);
      
      
      
      
      
      add(panneau, BorderLayout.SOUTH);
     
      setSize(280, 120);
      Toolkit tk=Toolkit.getDefaultToolkit();
		Image img=tk.getImage("a16.png");
       setIconImage(img);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      setResizable(false);
      setVisible(true);
      
      //****************** ok add roles  *******************
      
      ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	boolean special=false;
            	text=nom.getText();
            	int lo=text.length();
            	int li=0;
            	char[] TEXTE=text.toCharArray();
            	while(li<lo)
            	{
            		if (TEXTE[li]=='/'|TEXTE[li]=='*')
            		{
            					special=true;		
 			           			
            		}
            		li++;
            		if(special) break;
            	}
            	
            	
            	if(text.length()!=0&&!special)
            	{
            		
            	
            	
            	String OU="";
      					NouveauRole=text;
      					
      			//choisir un role pas encore pris
      			int i=0;
      			
      			
      			String[] All3=LDAPRoles.getALLRoles();
      			
      			boolean ook=true;
      				while(ook)
      				{
                   String libre=All3[i];
					
					if(libre.compareTo("no")==0)
					{
						ook=false;  position=i;
						 OU=MesOU[position];
					}
					i++;
					}
					num=5;
					
	       			titre=text;
	       			//String MonChemin=Chemin.getPath("ou="+OU);
	       			String MonChemin ="ou="+OU+",o=Fichiers,c=gb";
	       			AddRoleToLDAP.AddRole(MonChemin,text);
	       			//******************************************* ajouter le role dans un role libre
	       			//	contenu.add(coche15);
					//	coche15.setText(text);
					setSize(100,100);
					//reset();
						set(position,text);	
	       		        	contenu.add(rien2);
	       		        	contenu.add(rien3);
	       				contenu.add(etat);
						contenu.add(New);
						contenu.add(supp);
	       		//	setRoleVisible(position,text);
      					
      					//mettre le role dans le serveur ldap   : une entrée "ou=role"	
      						//AddRole  adc = new AddRole ();
   						 //adc.createNew(text) ;
   						
            			//panneau.setSize(700, 100);	
            
                             
               setVisible(false);
               }
               if(text.length()==0)
               {
               	Message.setForeground(Color.red) ;
               	Message.setText("Champs vide !");
               }
               if(special)
               {
               	Message.setForeground(Color.red) ;
               	Message.setText("Caractères spéciaux non autorisés!");
               }
            }
            
         });
      	
      	
      	
      	//********************anuler ajout role **********
      
      annuler.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               setVisible(false);
            }
         });
      
      
      
      
     
   }   
   
   
   

   public void actionPerformed(ActionEvent e) {
   	Object source=e.getSource();
      //if(source==ok) {
      	
      	
      	
      	    
      
   }
   
   
   
   
   
   private class CaseACocher extends JCheckBox implements ItemListener {
      public CaseACocher(String libellé) {
         super(libellé, false);
         setMnemonic(libellé.charAt(0));
         addItemListener(this);
         setOpaque(false);
      }
      
      
      
      
      

      public void itemStateChanged(ItemEvent e) {
        
         
    }
      
      
   }
   
   
 
   
    private class Saisie extends JTextField {
      public Saisie(String texte) {
         super(texte, 10);
         setFont(new Font("Verdana", Font.BOLD, 12));
         setMargin(new Insets(0, 3, 0, 0));
         setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

      }
      }
      
      
     
   
   
}
	
/////////////////////////////suppressions de roles ////////////////////////////
///////////////////////////////////////////////////////////////////////////////

	



////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// fin suppression de roles ///////////////////////
	
	
	
	
	
// actions to the events	
	
	
public void actionPerformed(ActionEvent ev)
{	Object source=ev.getSource();

if(source==etat) {
      					
      					if(a) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[0],Display) ; 	}
   							 
   						if(b) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[1],Display) ;	}
   							 
   						 if(c) { 	AddTorrentFile  adc = new AddTorrentFile ();
   						 adc.createNew(FileName1,MesOU[2],Display) ;	}
   						 
   						 if(d) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[3],Display) ;	}
   							 
   							 if(e1) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[4],Display) ;	}
   							 
   						if(f) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[5],Display) ;	}
   							 
   						if(g) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[6],Display) ;	}
   						
   						if(h) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[7],Display) ;	}
   							 
   						if(i2) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[8],Display) ;	}
   							 
   						if(j) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[9],Display) ;	}
   							 
   						if(k) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[10],Display) ;	}
   							 
   						if(l) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[11],Display) ;	}	
   							 
   						if(m) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[12],Display) ;	}
   							 
   						if(n) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[13],Display) ;	}
   							 
   						if(o) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[14],Display) ;	}
   							 
   						if(p) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[15],Display) ;	}
   							 
   						if(q) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[16],Display) ;	}
   							 
   						if(r) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[17],Display) ;	}
   							 
   							 
   						if(s) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[18],Display) ;	}	 
   							 
   							 
   							 
   						if(t) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[19],Display) ;	}
   							 
   							 
   						if(u) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[20],Display) ;	}
   							 
   						if(v) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[21],Display) ;	}
   							 
   							 
   						if(w) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[22],Display) ;	}
   							 
   						if(x) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[23],Display) ;	}
   							 
   						if(y) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[24],Display) ;	}	 
   							 
   						if(z) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[25],Display) ;	}	 	 	 	  	 	 	 	 	 
   						 //______________________
   						 	
   							 
   							 
   							 	if(a1) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[26],Display) ;	}	 	
   							 
   							 	if(a2) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[27],Display) ;	}	 	
   							 
   							 	if(a3) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[28],Display) ;	}	 	
   							 
   							 	if(a4) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[29],Display) ;	}	 	
   							 
   							 	if(a5) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[30],Display) ;	}	 	
   							 
   							 	if(a6) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[31],Display) ;	}	 	
   							 
   							 	if(a7) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[32],Display) ;	}	 	
   							 
   							 	if(a8) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[33],Display) ;	}	 	
   							 
   							 	if(a9) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[34],Display) ;	}	 	
   							 
   							 	if(b1) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[35],Display) ;	}	 	
   							 
   							 	if(b2) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[36],Display) ;	}	 	
   							 
   							 	if(b3) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[37],Display) ;	}	 	
   							 
   							 	if(b4) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[38],Display) ;	}	 	
   							 
   							 	if(b5) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[39],Display) ;	}	 	
   							 
   							 	if(b6) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[40],Display) ;	}	 	
   							 
   							 	if(b7) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[41],Display) ;	}	 	
   							 
   							 	if( b8) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[42],Display) ;	}	 	
   							 
   							 	if(b9) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[43],Display) ;	}	 	
   							 
   							 	if(c1) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[44],Display) ;	}	 	
   							 
   							 	if(c2) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[45],Display) ;	}	 	
   							 
   							 	if(c3) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[46],Display) ;	}	 	
   							 
   							 	if(c4) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[47],Display) ;	}	 	
   							 
   							 	if(c5) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[48],Display) ;	}	 	
   							 
   							 	if(c6) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,MesOU[49],Display) ;	}	 	
   							 
   							 
   							 	
   						 
   						 if(!a&!b&!c&!d&!e1&!f&!g&!h&!i2&!j&!k&!l&!m&!n&!o&!p&!q&!r&!s&!t&!u&!v&!w&!x&!y&!z&!a1&!a2&!a3&!a4&!a5&!a6&!a7&!a8&!a9&!b1&!b2&!b3&!b4&!b5&!b6&!b7&!b8&!b9&!c1&!c2&!c3&!c4&!c5&!c6&!c7) {compte.setText("\nVous devez cocher au moins une case!\n                                      ");compte.setForeground(Color.red) ;
   						 	 }
   						 else{
   						 			terminer.setVisible(true);
									etat.setVisible(false);
									coche1.setVisible(false);
									coche2.setVisible(false);
									coche3.setVisible(false);
									New.setVisible(false);
									supp.setVisible(false);
									coche4.setVisible(false);
									coche5.setVisible(false);
									coche6.setVisible(false);
									coche7.setVisible(false);
									coche8.setVisible(false);
									coche9.setVisible(false);
									coche10.setVisible(false);
									coche11.setVisible(false);
									coche12.setVisible(false);
									coche13.setVisible(false);
									coche14.setVisible(false);
									coche15.setVisible(false);
									coche16.setVisible(false);
									coche17.setVisible(false);
									coche18.setVisible(false);
									coche19.setVisible(false);
									coche20.setVisible(false);
									coche21.setVisible(false);
									coche22.setVisible(false);
									coche23.setVisible(false);
									//_____________________
									coche24.setVisible(false);
									coche25.setVisible(false);
									coche26.setVisible(false);
								    coche27.setVisible(false);
								    coche28.setVisible(false);
								    coche29.setVisible(false);
								    coche30.setVisible(false);
								    coche31.setVisible(false);
								    coche32.setVisible(false);
								    coche33.setVisible(false);
								    coche34.setVisible(false);
								    coche35.setVisible(false);
								    coche36.setVisible(false);
								    coche37.setVisible(false);
								    coche38.setVisible(false);
								    coche39.setVisible(false);
								    coche40.setVisible(false);
								    coche41.setVisible(false);
								    coche42.setVisible(false);
								    coche43.setVisible(false);
								    coche44.setVisible(false);
								    coche45.setVisible(false);
								    coche46.setVisible(false);
								    coche47.setVisible(false);
								    coche48.setVisible(false);
								    coche49.setVisible(false);
								    coche50.setVisible(false);
									compte.setText("\nInformations sauvegardées. Terminer!                                                  ");
									compte.setForeground(Color.blue) ;
									org.gudy.azureus2.core3.peer.impl.transport.PEPeerTransportProtocol.NotYet=false;
									}
      					
      					
     						 }//end if source=etat




			if(source==New)
     	{
     		//ajouter un role et le mettre dans openldap
     	
     	//	roler=new NewRole();
     
     
     new  AjouterRole();
     
     		
     		
     	
     		
     	}
     		
      if(source==supp)
      {//afficher la liste de tous les roles existants
      	
    	  
    		SupRoles fen= new SupRoles(hash,clearName);
    		fen.setVisible(true);
    		fen.setResizable(false);
    		fen.setLocationRelativeTo(null);
    		fen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    			Description.go();//à executer une fois
    			
    			setVisible(false);
    			
    	  
    	  
    	  
    	  
    	  
    	  
    	  
    	  
      }





















	
	
	
} 


















// desclarations

private JCheckBox coche1,coche2,coche3,coche4,coche5,coche6,coche7,coche8,coche9,coche10,coche11,coche12,coche13,coche14,coche15,coche16,coche17,coche18,coche19,coche20,coche21,coche22,coche23,coche24,coche25,coche26,coche27,coche28,coche29,coche30,coche31,coche32,coche33,coche34,coche35,coche36,coche37,coche38,coche39,coche40,coche41,coche42,coche43,coche44,coche45,coche46,coche47,coche48,coche49,coche50;
private JButton  etat,terminer,supp,New;
private JLabel compte,rien2,compte3,compte2,rien3;
String Note="",FileName1="",titre="",text="";
static int num =1000,oo=1000;

//String All2=new String[50];

	
	
	
	public static void main(String [] args)

{
	//SecureTorrent fen1=new SecureTorrent("Are you here?");
	//fen1.setVisible(true);
}	

public   void set(int pos,String Title)
{
	pos++;
	if(pos==1)  {contenu.add(coche1);coche1.setText(Title); }
	if(pos==2)  {contenu.add(coche2);coche2.setText(Title);}
	if(pos==3)  {contenu.add(coche3);coche3.setText(Title);}
	if(pos==4)  {contenu.add(coche4);coche4.setText(Title);}
	if(pos==5)  {contenu.add(coche5);coche5.setText(Title);}
	if(pos==6)  {contenu.add(coche6);coche6.setText(Title);}
	if(pos==7)  {contenu.add(coche7);coche7.setText(Title);}
	if(pos==8)  {contenu.add(coche8);coche8.setText(Title);}
	if(pos==9)  {contenu.add(coche9);coche9.setText(Title);}
	if(pos==10)  {contenu.add(coche10);coche10.setText(Title);}
	if(pos==11)  {contenu.add(coche11);coche11.setText(Title);}
	if(pos==12)  {contenu.add(coche12);coche12.setText(Title);}
	if(pos==13)  {contenu.add(coche13);coche13.setText(Title);}
	if(pos==14)  {contenu.add(coche14);coche14.setText(Title);}
	if(pos==15)  {contenu.add(coche15);coche15.setText(Title);}
	if(pos==16)  {contenu.add(coche16);coche16.setText(Title);}
	if(pos==17)  {contenu.add(coche17);coche17.setText(Title);}
	if(pos==18)  {contenu.add(coche18);coche18.setText(Title);}
	if(pos==19)  {contenu.add(coche19);coche19.setText(Title);}
	if(pos==20)  {contenu.add(coche20);coche20.setText(Title);}
	if(pos==21)  {contenu.add(coche21);coche21.setText(Title);}
	if(pos==22)  {contenu.add(coche22);coche22.setText(Title);}
	if(pos==23)  {contenu.add(coche23);coche23.setText(Title);}
	if(pos==24)  {contenu.add(coche24);coche24.setText(Title);}
	if(pos==25)  {contenu.add(coche25);coche25.setText(Title);}
	if(pos==26)  {contenu.add(coche26);coche26.setText(Title);}
	if(pos==27)  {contenu.add(coche27);coche27.setText(Title);}
	if(pos==28)  {contenu.add(coche28);coche28.setText(Title);}
	if(pos==29)  {contenu.add(coche29);coche29.setText(Title);}
	if(pos==30)  {contenu.add(coche30);coche30.setText(Title);}
	if(pos==31)  {contenu.add(coche31);coche31.setText(Title);}
	if(pos==32)  {contenu.add(coche32);coche32.setText(Title);}
	if(pos==33)  {contenu.add(coche33);coche33.setText(Title);}
	if(pos==34)  {contenu.add(coche34);coche34.setText(Title);}
	if(pos==35)  {contenu.add(coche35);coche35.setText(Title);}
	if(pos==36)  {contenu.add(coche36);coche36.setText(Title);}
	if(pos==37)  {contenu.add(coche37);coche37.setText(Title);}
	if(pos==38)  {contenu.add(coche38);coche38.setText(Title);}
	if(pos==39)  {contenu.add(coche39);coche39.setText(Title);}
	if(pos==40)  {contenu.add(coche40);coche40.setText(Title);}
	if(pos==41)  {contenu.add(coche41);coche41.setText(Title);}
	if(pos==42)  {contenu.add(coche42);coche42.setText(Title);}
	if(pos==43)  {contenu.add(coche43);coche43.setText(Title);}
	if(pos==44)  {contenu.add(coche44);coche44.setText(Title);}
	if(pos==45)  {contenu.add(coche45);coche45.setText(Title);}
	if(pos==46)  {contenu.add(coche46);coche46.setText(Title);}
	if(pos==47)  {contenu.add(coche47);coche47.setText(Title);}
	if(pos==48)  {contenu.add(coche48);coche48.setText(Title);}
	if(pos==49)  {contenu.add(coche49);coche49.setText(Title);}
	if(pos==50)  {contenu.add(coche50);coche50.setText(Title);}
	
	
	
	
	
	
	
	
	
	return;
}	
	
public void reset()
{ int taille=LDAPRoles.taille;

if(taille<=5)  contenu.setSize(400,200);
if(taille<=10&& taille>5) contenu.setSize(400,250);
if(taille<=15 && taille>10) contenu.setSize(400,250);
if(taille<=20 && taille>15) contenu.setSize(400,300);
if(taille<=25 && taille>20) contenu.setSize(400,350);
if(taille<=30 && taille>25) contenu.setSize(400,400);
if(taille<=35 && taille>30) contenu.setSize(400,450);
if(taille<=40 && taille>35) contenu.setSize(400,500);
if(taille<=45 && taille>40) contenu.setSize(400,550);
if(taille<=50 && taille>45) contenu.setSize(400,600);
	
	return;
}	
	
// ajouer les roles

	
	
	
	
	
	
	
	
}