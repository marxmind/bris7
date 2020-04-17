package com.italia.marxmind.bris.security;

import java.util.Base64;

import com.italia.marxmind.bris.enm.Bris;

/**
 * 
 * @author mark italia
 * @since 9/27/2016
 * this class is use for encoding and decoding of character
 */
public class SecureChar {

	
	
	public static String encode(String val){
		
		try{
		// encode with padding
		String encoded = Base64.getEncoder().encodeToString(val.getBytes(Bris.SECURITY_ENCRYPTION_FORMAT.getName()));
		// encode without padding
		//String encoded = Base64.getEncoder().withoutPadding().encodeToString(val.getBytes(Ipos.SECURITY_ENCRYPTION_FORMAT.getName()));
		
		return encoded;
		}catch(Exception e){}
		return null;
	}
	public static String decode(String val){
		try{
			byte [] barr = Base64.getDecoder().decode(val);
			return new String(barr,Bris.SECURITY_ENCRYPTION_FORMAT.getName());
			}catch(Exception e){}
			return null;
	}
	
	/**
	 * 
	 * @param do not delete
	 */
	public static void main(String[] args) {
		//String en = encodePassword("10181986");
		//System.out.println(en);
		//System.out.println(decodePassword("QmFzMTJ4QXMzNGRmUjU2KCoxaw=="));//*/
		
		//themes encode
		//System.out.println(SecureChar.encode("marknovabris-lightmarxmind"));
	}
	
	public static String encodePassword(String pass){
		String[] xx = {"a8^","Bas","12%$","dfR","(*1k","34%","aoz","xAs","wed"};
		try{
		int len = pass.length();
		String a1=pass.substring(0,2);
		String a2=pass.substring(2,4);
		String a3=pass.substring(4,len);
		pass = SecureChar.encode(xx[(int)(Math.random()*xx.length)]+a1+ xx[(int)(Math.random()*xx.length)]+a2+xx[(int)(Math.random()*xx.length)]+a3+xx[(int)(Math.random()*xx.length)]);
		}catch(Exception e){}
		return pass;
	}
	public static String decodePassword(String pass){
		try{
		pass = decode(pass);
		pass = pass.replace("a8^", "");
		pass = pass.replace("Bas", "");
		pass = pass.replace("12%$", "");
		pass = pass.replace("dfR", "");
		pass = pass.replace("(*1k", "");
		pass = pass.replace("34%", "");
		pass = pass.replace("aoz", "");
		pass = pass.replace("xAs", "");
		pass = pass.replace("wed", "");
		}catch(Exception e){}
		return pass;
	}
	
	public static String decodeLicense(String data){
		//data = SecureChar.decode(data);
		try{
		data = data.replace("B4X", "");
		data = data.replace("3EQ", "");
		data = data.replace("CD9", "");
		data = data.replace("L6P", "");
		data = data.replace("M88", "");
		}catch(Exception e){}
		return data;
	}
	
}
