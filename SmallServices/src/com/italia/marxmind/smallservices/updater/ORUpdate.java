package com.italia.marxmind.smallservices.updater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ORUpdate {
	
	public static void updateOR(){
		
		String sql = "SELECT * FROM clearance WHERE isactiveclearance=1  AND amountpaid!=0 AND clearissueddate=?";//( clearissueddate>=? AND clearissueddate<=?) ";
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
		
		 String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		 String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		 String PROVINCE = ReadConfig.value(Bris.PROVINCE);
		
		rs = ps.executeQuery();
		
		String address = BARANGAY + ", " + MUNICIPALITY + ", " + PROVINCE;
		
		while(rs.next()){
			
			String orNumber = rs.getString("ornumber");
			String dateTrans = rs.getString("clearissueddate");
			double amount = rs.getDouble("amountpaid");
			long customerId = rs.getLong("customerid");
			long userId = rs.getLong("userdtlsid");
			String purpose = purpose(rs.getInt("purposetype"));
			
			if(mapOR==null){
				insertOR(orNumber, dateTrans, amount, customerId, userId, purpose, address);
				mapOR.put(orNumber, orNumber);
			}else{
				
				if(!mapOR.containsKey(orNumber)){
					mapOR.put(orNumber, orNumber);
					insertOR(orNumber, dateTrans, amount, customerId, userId, purpose, address);
				}
			}
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		
		
	}
	
	private static String purpose(int id){
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = "SELECT purname FROM purpose WHERE isactivepurpose=1 AND purposeid=" + id;
			try{
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
		
			while(rs.next()){
				return rs.getString("purname");
			}	
			rs.close();
			ps.close();
			ConnectDB.close(conn);
			}catch(Exception e){e.getMessage();}	
			
		return "";
	}
	
	private static void insertOR(String orNumber, String dateTrans, double amount, long customerId, long userId, String purpose, String address){
		if(isNotExistingOR(orNumber)){
			
			String sql = "INSERT INTO orlisting (orid,ordate,orno,oramount,oractive,customerid,userdtlsid,purpose,orissuedaddress,orstatus) VALUES(?,?,?,?,?,?,?,?,?,?)";
			
			Connection conn = null;
			
			PreparedStatement ps = null;
			try{
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			int cnt=1;
			ps.setLong(cnt++, getNewId());
			ps.setString(cnt++, dateTrans);
			ps.setString(cnt++, orNumber);
			ps.setDouble(cnt++, amount);
			ps.setInt(cnt++, 1);
			ps.setLong(cnt++, customerId);
			ps.setLong(cnt++, userId);
			ps.setString(cnt++, purpose);
			ps.setString(cnt++, address);
			ps.setInt(cnt++, 1);
			ps.execute();
			ps.close();
			ConnectDB.close(conn);
			}catch(Exception e){e.getMessage();}
			SmallServices.createLogFile("Added OR: " + orNumber);
		}
	}
	
	private static boolean isNotExistingOR(String orNumber){
		
		String sql = "SELECT orno FROM orlisting WHERE oractive=1  AND orno=? ";
		String[] params = new String[1];
		params[0] = orNumber;
		
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
		
		String sql = "SELECT orid FROM orlisting ORDER BY orid DESC limit 1";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getLong("orid") + 1;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return 1;
	}
	
}
