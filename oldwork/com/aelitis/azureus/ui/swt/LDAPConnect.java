package com.aelitis.azureus.ui.swt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 class LDAPConnect extends JFrame implements ActionListener 

 {
	
	public  LDAPConnect()
	{
		setTitle("Accès à l'annuaire");
		setSize(280,150);
		Toolkit tk=Toolkit.getDefaultToolkit();
		Image img=tk.getImage("a16.png");
         setIconImage(img);
		titre=new JLabel("Mot de passe de l'annuaire:");
		saisir=new JPasswordField(20);
		
		ok=new JButton("ok");
		annuler=new JButton("Annuler");
		Container contenu=getContentPane();
		contenu.setLayout(new FlowLayout()  );
		contenu.add(titre);
		contenu.add(saisir);
		contenu.add(ok);
		contenu.add(annuler);
		
		
		saisir.addActionListener(this);
		ok.addActionListener(this);
		annuler.addActionListener(this);	
	}
	
	
	
	public void actionPerformed(ActionEvent ev)
	{
		if(ev.getSource()==ok )
		{
			 password=saisir.getText();
			if(password.length()==0)
			{
				titre.setForeground(Color.red) ;
			}
				else
		{
			LDAP.connect(password);
			boolean response=LDAP.getDecision();
			
			
			if(!response)
			{
				titre.setText("mot de passe incorrect!");
				titre.setForeground(Color.red) ;
				saisir.setText("");
				
			}
			else{
				titre.setText("mot de passe correct!");
				titre.setForeground(Color.blue) ;				
				Initializer.repeat=true;
				com.aelitis.azureus.ui.swt.Initializer.LdapPassword=password;
			}
			
		}
			
		}
		
		if(ev.getSource()==annuler ) System.exit(0);
		
		
		
		
		
	
		
	}
	
	
	
	
	private JLabel titre;
	private JPasswordField saisir;
	private JButton ok,annuler;
	private String password="";
	
	

}















