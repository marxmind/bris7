package com.italia.marxmind.smallservices.updater;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

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
	
	
	public static void main(String[] args) {
		String[] au = {"mark","italia","rivera","bris","marxmind"};  
		String val = SecureChar.encode("marxmindvadbrisermarxmind");
		//String val = SecureChar.encode(au[(int)(Math.random()*au.length)]+"vad"+ au[(int)(Math.random()*au.length)]+"er"+au[(int)(Math.random()*au.length)]);
		System.out.println("Encode: " + val);
		System.out.println("Decode: " + SecureChar.decode(val));
		val = SecureChar.decode(val);
		val = val.replace("mark", "");
		val = val.replace("rivera", "");
		val = val.replace("italia", "");
		val = val.replace("bris", "");
		val = val.replace("marxmind", "");
		System.out.println(val);
	}
	
}

