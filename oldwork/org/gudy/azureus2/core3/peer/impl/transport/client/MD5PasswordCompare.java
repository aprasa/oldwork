package org.gudy.azureus2.core3.peer.impl.transport.client;

public class MD5PasswordCompare
{
	public static boolean isSame(String Origine,String Received)
	{
		boolean response=false;
	
		String pass=MD5Password.getEncodedPassword(Received);
		
		
	if(Origine.compareTo(pass)==0)  response=true;
	else response=false;
		
		
		
		return response;
	}
}