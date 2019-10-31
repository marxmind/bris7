package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;

/**
 * 
 * @author mark italia
 * @since 09/15/2017
 * @version 1.0
 *
 */
public class IncidentReportTypes {

	private int id;
	private String name;
	private int isActive;
	
	public static List<IncidentReportTypes> retrieve(String sql, String[] params){
		List<IncidentReportTypes> types = Collections.synchronizedList(new ArrayList<IncidentReportTypes>());
		
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
			IncidentReportTypes type = new IncidentReportTypes();
			type.setId(rs.getInt("typeid"));
			type.setName(rs.getString("typename"));
			type.setIsActive(rs.getInt("isactivetype"));
			
			types.add(type);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		return types;
	}

	public static IncidentReportTypes retrieve(int id){
		IncidentReportTypes type = new IncidentReportTypes();
		
		String sql = "SELECT * FROM incidenttype WHERE typeid=? AND isactivetype=1";
		String[] params = new String[1];
		params[0] = id+"";
		
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
		System.out.println("INCIDENT TYPE " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			type.setId(rs.getInt("typeid"));
			type.setName(rs.getString("typename"));
			type.setIsActive(rs.getInt("isactivetype"));
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		return type;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
}
