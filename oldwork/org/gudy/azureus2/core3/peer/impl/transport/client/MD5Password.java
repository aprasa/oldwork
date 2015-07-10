package org.gudy.azureus2.core3.peer.impl.transport.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/*
 * Cr�� le 12 d�c. 2003
 *
 * Fonctions utilitaires pour le cryptage MD5
 * Encode une chaine et renvoi son r�sultat crypt� en
 * h�xad�cimal avec l'algorithme MD5 ou test
 * une chaine et une valeur crypt�. Main d'exemple fourni.
 *//**
 * @author Gaelle
 *
 * Version : 1.0
 * Last update : 12/12/2003
 */public class MD5Password
  {
  	/*
  	 * Encode la chaine pass� en param�tre avec l'algorithme MD5
  	 * 
  	 * @param key : la chaine � encoder
  	 * 
  	 * @return la valeur (string) hexad�cimale sur 32 bits
  	 */	public static String getEncodedPassword(String key) {
	  byte[] uniqueKey = key.getBytes();
	  byte[] hash = null;
	  try {
		hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
	  } catch (NoSuchAlgorithmException e) {
		throw new Error("no MD5 support in this VM");
	  }
	  StringBuffer hashString = new StringBuffer();
	  for ( int i = 0; i < hash.length; ++i ) {
		String hex = Integer.toHexString(hash[i]);
		if ( hex.length() == 1 ) {
		  hashString.append('0');
		  hashString.append(hex.charAt(hex.length()-1));
		} else {
		  hashString.append(hex.substring(hex.length()-2));
		}
	  }
	  String eee= hashString.toString();
	  String passwd2 = new String(Base64.encode(hash));
		//System.out.println(passwd2);
	  
	  
	  
	  return "{MD5}"+passwd2;
	  
	  
	}
	
	
	
  /*
   * Test une chaine et une valeur encod� (chaine hexad�cimale)
   * 
   * @param clearTextTestPassword : la chaine non cod� � tester
   * @param encodedActualPassword : la valeur hexa MD5 de r�f�rence
   * 
   * @return true si v�rifi� false sinon
   */  public static boolean testPassword(String clearTextTestPassword,
				   String encodedActualPassword)
				   throws NoSuchAlgorithmException
	{
	String encodedTestPassword = MD5Password.getEncodedPassword(
					  clearTextTestPassword);
	return (encodedTestPassword.equals(encodedActualPassword));
	}
	/*
	 * Un exemple b�te d'utilisation
	 */	public static void main(String[] args) {
	//	System.out.println(getEncodedPassword("mestafa"));
		try {
			if (testPassword("mot de passe", "729f2d8b3d3d9bc07ba349faab7fdf44"))
				System.out.println("Les passwords sont v�rifi�s");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}