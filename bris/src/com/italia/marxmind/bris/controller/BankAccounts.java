package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
/**
 * 
 * @author mark italia
 * @since 08/09/2017
 * @version 1.0
 *
 */
public class BankAccounts {

	private int id;
	private String bankAccntNo;
	private String bankAccntName;
	private String bankAccntBranch;
	private int isActive;
	private String timestamp;
	
	public static List<BankAccounts> retrieve(String sql, String[] params){
		List<BankAccounts> cList =  Collections.synchronizedList(new ArrayList<BankAccounts>());
		
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
		
		//System.out.println("SQL " + ps.toString());
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			BankAccounts ac = new BankAccounts();
			
			try{ac.setId(rs.getInt("bankid"));}catch(NullPointerException e){}
			try{ac.setBankAccntName(rs.getString("bankname"));}catch(NullPointerException e){}
			try{ac.setBankAccntNo(rs.getString("accountno"));}catch(NullPointerException e){}
			try{ac.setBankAccntBranch(rs.getString("branch"));}catch(NullPointerException e){}
			try{ac.setIsActive(rs.getInt("isactivebank"));}catch(NullPointerException e){}
			
			cList.add(ac);
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException sl){}
		
		return cList;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBankAccntNo() {
		return bankAccntNo;
	}
	public void setBankAccntNo(String bankAccntNo) {
		this.bankAccntNo = bankAccntNo;
	}
	public String getBankAccntName() {
		return bankAccntName;
	}
	public void setBankAccntName(String bankAccntName) {
		this.bankAccntName = bankAccntName;
	}
	public String getBankAccntBranch() {
		return bankAccntBranch;
	}
	public void setBankAccntBranch(String bankAccntBranch) {
		this.bankAccntBranch = bankAccntBranch;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	
	
	
	
}

