package com.italia.marxmind.bris.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.RsvpTag;
import com.italia.marxmind.bris.reader.ReadConfig;
/**
 * 
 * @author mark italia
 * @since 05/05/2017
 * @version 1.0
 *
 */
@Deprecated
public class ReservationXML {

	
	
	
public static void main(String[] args) {
	
}	
	
public static void addElement(String[] val){
	String RESERVATION_FILE_XML = ReadConfig.value(Bris.REPORT) + 
			ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + 
			Bris.SEPERATOR.getName() + 
			Bris.RESERVATION_FILE_XML.getName();
		
		
		
	}
	
	public static void updateElement(String[] val){}

	public static void deleteElement(String[] val){}
	
	/*
	 * public static List<Reservation> readReservationXML(){}
	 * 
	 * public static int getLastId(){}
	 */

}

