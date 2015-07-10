package org.gudy.azureus2.core3.torrent.impl;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 class SupRoles extends JFrame implements ActionListener 


 {
 	public static String role="",position="",MyHash="",MyName="";
 	 public static boolean stop=false;
	
	public  SupRoles(String FileName,String DisplayName)
	{
		MyHash=FileName;
		MyName=DisplayName;
		setTitle("Supprimer un rôle");
		setSize(280,150);
		Toolkit tk=Toolkit.getDefaultToolkit();
		Image img=tk.getImage("a16.png");
         setIconImage(img);
		titre=new JLabel("Entrez le rôle à supprimer:");
		saisir=new JTextField(20);
		
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
		role=saisir.getText();
		if (role.length()==0)
		{
			titre.setText("Entrez le rôle à supprimer");
			titre.setForeground(Color.red) ;
		}
		
		
		position=Description.getDescription(role);
		if(position.equals("Public"))
		{
			titre.setText("Vous ne pouvez pas supprimer le rôle PUBLIC");
			titre.setForeground(Color.red) ;
			saisir.setText("");
			
		}
			
		
		else
		{
			
		
				position=Description.getDescription(role);
			
		System.out.println(position);
	
		//retirer tous les fichiers se ce rôle:
		SupFichiers.go();
		if(!position.equals("") )
		{
		
			SupFichiers.deleteFile(position,MyHash,MyName);
			
			}
				
		stop=true;
		
		setVisible(false);
			
		}
		
		
			
			
		}
		
		if(ev.getSource()==annuler ) {
			SecureTorrent fen1=new SecureTorrent(MyHash,MyName);
			fen1.setVisible(true);
			setVisible(false);
	}
		
		
		
		
		
	
		
	}
	
	
	
	
	private JLabel titre;
	private JTextField saisir;
	private JButton ok,annuler;
	private String password="";
	
	

}















