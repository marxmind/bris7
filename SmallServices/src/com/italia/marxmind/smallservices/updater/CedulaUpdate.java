package com.italia.marxmind.smallservices.updater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CedulaUpdate {
	
	private static String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private static String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	public static void updateCedula(){
		String sql = "SELECT * FROM clearance WHERE isactiveclearance=1 AND cedulanumber is not null AND clearissueddate=?"; //( clearissueddate>=? AND clearissueddate<=?)";
		String[] params = new String[1];
		
		/*String dateFrom = DateUtils.getCurrentYear()+"";
		dateFrom += "-"+(DateUtils.getCurrentMonth()<=9? "0"+DateUtils.getCurrentMonth() : DateUtils.getCurrentMonth()+"");
		dateFrom += "-01";*/
		params[0] = DateUtils.getCurrentDateYYYYMMDD();
		//params[1] = DateUtils.getCurrentDateYYYYMMDD();
		
		//removing duplicate OR Number
		Map<String, String> mapOR = Collections.synchronizedMap(new HashMap<String, String>());
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			String[] cedula = rs.getString("cedulanumber").split("<:>");
			String cedNo = cedula[0];
			String dateTrans = cedula[1];
			String issuedAddress = MUNICIPALITY + ", " + PROVINCE;
			int type = 1;
			try{issuedAddress = cedula[2];}catch(IndexOutOfBoundsException e){}
			try{type = Integer.valueOf(cedula[3]);}catch(IndexOutOfBoundsException e){}
			long customerId = rs.getLong("customerid");
			long userId = rs.getLong("userdtlsid");
			
			if(mapOR==null){
				insertCedula(cedNo, dateTrans, issuedAddress, customerId, userId, type);
				mapOR.put(cedNo, cedNo);
			}else{
				
				if(!mapOR.containsKey(cedNo)){
					mapOR.put(cedNo, cedNo);
					insertCedula(cedNo, dateTrans, issuedAddress, customerId, userId, type);
				}
			}
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
	}
	
	private static void insertCedula(String cedNo, String dateTrans, String issuedAddress, long customerId, long userId, int type){
		if(isNotExistingCedula(cedNo)){
			
			String sql = "INSERT INTO cedula (cid,cedate,cedno,tin,cedtype,basictax,grosstax,totalamount,chieght,cweight,cisactive,customerid,userdtlsid,cedissuedaddress,cstatus)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			Connection conn = null;
			
			PreparedStatement ps = null;
			try{
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			int cnt=1;
			ps.setLong(cnt++, getNewId());
			ps.setString(cnt++, dateTrans);
			ps.setString(cnt++, cedNo);
			ps.setString(cnt++, null);
			ps.setInt(cnt++, type);
			ps.setDouble(cnt++, 0.0);
			ps.setDouble(cnt++, 0.0);
			ps.setDouble(cnt++, 0.0);
			ps.setString(cnt++, null);
			ps.setString(cnt++, null);
			ps.setInt(cnt++, 1);
			ps.setLong(cnt++, customerId);
			ps.setLong(cnt++, userId);
			ps.setString(cnt++, issuedAddress);
			ps.setInt(cnt++, 1);
			ps.executeUpdate();
			ps.close();
			ConnectDB.close(conn);
			}catch(Exception e){e.getMessage();}
			SmallServices.createLogFile("Added Cedula: " + cedNo);
		}else{
			updateCedulaDetails(cedNo, dateTrans, issuedAddress, customerId, userId, type);
		}
	}
	
	private static void updateCedulaDetails(String cedNo, String dateTrans, String issuedAddress, long customerId, long userId, int type){
		String sql = "UPDATE cedula SET "
				+ "cedate=?,"
				+ "cedtype=?,"
				+ "customerid=?,"
				+ "userdtlsid=?,"
				+ "cedissuedaddress=? "
				+ " WHERE cedno=?";
		
		Connection conn = null;
		
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt=1;
		
		ps.setString(cnt++, dateTrans);
		ps.setInt(cnt++, type);
		ps.setLong(cnt++, customerId);
		ps.setLong(cnt++, userId);
		ps.setString(cnt++, issuedAddress);
		ps.setString(cnt++, cedNo);
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		SmallServices.createLogFile("Update Cedula: " + cedNo);
	}
	
	private static boolean isNotExistingCedula(String cedulaNumber){
		
		String sql = "SELECT cedno FROM cedula WHERE cisactive=1  AND cedno=? ";
		String[] params = new String[1];
		params[0] = cedulaNumber;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return false;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return true;
	}
	
	private static long getNewId(){
		
		String sql = "SELECT cid FROM cedula ORDER BY cid DESC limit 1";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getLong("cid") + 1;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return 1;
	}
	
}
