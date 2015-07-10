package org.gudy.azureus2.core3.torrent.impl;



import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;

public class AddTorrentWindow extends JFrame implements ActionListener {
   
   public static String ClearFileName="";
   
   private JLabel Message = new JLabel( "Avec qui voulez-vous partager \nce(s) fichier(s) ?");
   private  CaseACocher  Famille = new CaseACocher("Famille");
   private CaseACocher Amis = new CaseACocher("Amis");
    private CaseACocher Collegues = new CaseACocher("Collegues");
	private JButton  ok=new JButton ("OK");
	private JButton  role=new JButton ("Nouveau rôle");
	boolean replay=true;

	
	
	 boolean x = false,y=false,z=false,t=false;
	 private JButton terminer=new JButton ("Terminer");
	 String FileName1="";
	 public String NouveauRole="";
	 
	 
			
	
	
	
   public JPanel panneau = new JPanel();
   
   
   public AddTorrentWindow(String FileName,String Titre) {
      super("Note de sécurité");     
       FileName1=FileName;
       ClearFileName=Titre;
       
      Message.setFont(new Font("Arial", Font.BOLD, 12));
      Message.setForeground(Color.blue) ;
     // Message.setHorizontalAlignment(JLabel.CENTER);
      Message.setBorder(BorderFactory.createLoweredBevelBorder());
      Message.setText(" Avec qui partager le fichier: "+Titre);
     
      add(Message);    
        
      panneau.add(Famille);
      panneau.add(Amis);
      panneau.add(Collegues);
      panneau.setBackground(Color.ORANGE);
      panneau.add(ok);
       panneau.add(role);
      panneau.add(terminer);
      ok.addActionListener(this);
       role.addActionListener(this);
      
      terminer.addActionListener(this);
	  terminer.setVisible(false);
      
      
      
      add(panneau, BorderLayout.SOUTH);
     
      setSize(400, 100);
      setLocationRelativeTo(null);
     
      setResizable(false);
      setVisible(true);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      
      	terminer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               setVisible(false);
            }
         });
         
         
         
         
         
      
      
      
      
      
   }   
   
   
   
   
   
   
   
   
   
   //////////////////////////////////////////////////  les actions  //////////////////////////////////////:

   public void actionPerformed(ActionEvent e) {
   	Object source=e.getSource();
      if(source==ok) {
      					
      					if(x) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,"Famille",ClearFileName) ;	}
   							 
   						if(y) { 	AddTorrentFile  adc = new AddTorrentFile ();
   							 adc.createNew(FileName1,"Amis",ClearFileName) ;	}
   							 
   						 if(z) { 	AddTorrentFile  adc = new AddTorrentFile ();
   						 adc.createNew(FileName1,"Collegues",ClearFileName) ;	}
   						 
   						 
   						 if(!x&!y&!z) {Message.setText("\nVous devez cocher au moins une case!");Message.setForeground(Color.red) ; }
   						 else{
   						 			terminer.setVisible(true);
									ok.setVisible(false);
									Amis.setVisible(false);
									Famille.setVisible(false);
									Collegues.setVisible(false);
									Message.setText("\nInformations sauvegardées. Terminer!");
									Message.setForeground(Color.blue) ;
									}
      					
      					
     						 }
     						 
     						 
     	if(source==role)
     	{
     		//ajouter un role et le mettre dans openldap
     	
     	//	roler=new NewRole();
     	new  AjouterRole();
     		
     		
     	
     		
     	}
     						 
     						 
     						 
     				 		
     						 
							
							
							
      
      
      
   }
   
   
   ////////////////////////////////// fin des actions  //////////////////////////////////////////////
   
   
   
   
   
   
   
   
   
   
   
   
   
   private class CaseACocher extends JCheckBox implements ItemListener {
      public CaseACocher(String libellé) {
         super(libellé, false);
         setMnemonic(libellé.charAt(0));
         addItemListener(this);
         setOpaque(false);
      }
      
      
      /////////////////////////////////////////////////////////////////////////////////////////////////////
      
      

      public void itemStateChanged(ItemEvent e) {
        
         
         
         
         x = Famille.isSelected(); 
         y = Amis.isSelected() ;
         z = Collegues.isSelected() ;
         
         
         
         
         
         
        
         
         System.out.println("x= "+x+"    y="+y+"     z="+z);
         
         
      }
      
      
     ///////////////////////////////////////////////////////////////////////////////////////////// 
      
      
      
      
      
   }
   
   
   
   //////////////////////////////////////////// ajouter role /////////////////////
   
   
   
   
   public class AjouterRole extends JFrame implements ActionListener {
   
   
   
   private JLabel Message = new JLabel( "Ajout d'un rôle\n" );
   
	private JButton  ok=new JButton ("OK");
	private JButton  annuler=new JButton ("Annuler");
	 private Saisie nom = new Saisie("");
	 String text="";

	
	
	
	 
	 
			
	
	
	
   private JPanel panneau = new JPanel();
   
   public AjouterRole() {
      super("Add roles");     
       
      Message.setFont(new Font("Arial", Font.BOLD, 12));
      Message.setForeground(Color.blue) ;
      Message.setHorizontalAlignment(JLabel.CENTER);
      Message.setBorder(BorderFactory.createLoweredBevelBorder());
     
      add(Message);    
        
      
      
      panneau.setBackground(Color.GREEN);
      panneau.add(nom);
      panneau.add(ok);
      panneau.add(annuler);
      
      ok.addActionListener(this);
       annuler.addActionListener(this);
      
      
      
      
      
      add(panneau, BorderLayout.SOUTH);
     
      setSize(350, 120);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      setResizable(false);
      setVisible(true);
      
      //****************** ok add roles  *******************
      
      ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	
            	String text=nom.getText();
      					NouveauRole=text;
      					
      					//mettre le role dans le serveur ldap   : une entrée "ou=role"	
      						AddRole  adc = new AddRole ();
   						 adc.createNew(text) ;
   						
            			panneau.setSize(700, 100);	
            
                             
               setVisible(false);
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

   
   
   
   
   
   
   
   
   
   
   
   
   
    
   
}
