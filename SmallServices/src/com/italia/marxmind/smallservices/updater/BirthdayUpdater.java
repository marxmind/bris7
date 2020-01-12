package com.italia.marxmind.smallservices.updater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BirthdayUpdater {
	
	
	public static void updateBirthday(){
		
		String sql = "SELECT customerid,borndate,cusage,fullname FROM customer WHERE cusisactive=1"; 
		String[] params = new String[0];
		
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
		int count = 1;
		while(rs.next()){
			//System.out.println("Name: " + rs.getString("fullname") + " Current Age: "+ rs.getInt("cusage"));
			updateAge(rs.getLong("customerid"), rs.getString("borndate"));
			count++;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		SmallServices.createLogFile("Searched data (" + count + ")");
		}catch(Exception e){e.getMessage();}
	}
	
	private static void updateAge(long customerid, String birthdate){
		
		int age=0;
		age = DateUtils.calculateAge(birthdate);
		//System.out.println("Updated Age: "+ age);
		String sql = "UPDATE customer SET cusage=? WHERE customerid=?"; 
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setInt(1, age);
		ps.setLong(2, customerid);
		
		ps.executeUpdate();
		
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
	}
	
}
