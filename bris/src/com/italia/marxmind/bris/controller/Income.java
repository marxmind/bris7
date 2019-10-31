package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 03/23/2018
 * @version 1.0
 *
 */

public class Income {

	private long id;
	private String dateTrans;
	private String description;
	private int type;
	private double amount;
	private int isActive;
	private UserDtls userDtls;
	
	public static List<Income> retrieve(String sqlAdd, String[] params){
		List<Income> ins = new ArrayList<>();
		
		String tableIncome = "iz";
		String tableUser = "usr";
		String sql = "SELECT * FROM income " + tableIncome + ", userdtls " + tableUser + " WHERE " +
				tableIncome + ".userdtlsid=" + tableUser + ".userdtlsid ";
		
		sql = sql + sqlAdd;		
				
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
		System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Income in = new Income();
			try{in.setId(rs.getLong("inid"));}catch(NullPointerException e){}
			try{in.setDateTrans(rs.getString("indate"));}catch(NullPointerException e){}
			try{in.setDescription(rs.getString("indescription"));}catch(NullPointerException e){}
			try{in.setType(rs.getInt("intype"));}catch(NullPointerException e){}
			try{in.setAmount(rs.getDouble("amount"));}catch(NullPointerException e){}
			try{in.setIsActive(rs.getInt("inisactive"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			in.setUserDtls(user);
			
			ins.add(in);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return ins;
	}
	
	public static Income save(Income in){
		if(in!=null){
			
			long id = Income.getInfo(in.getId() ==0? Income.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = Income.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				in = Income.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				in = Income.insertData(in, "3");
			}
			
		}
		return in;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			insertData("1");
		}else if(id==2){
			LogU.add("update Data ");
			updateData();
		}else if(id==3){
			LogU.add("added new Data ");
			insertData("3");
		}
		
	}
	
	public static Income insertData(Income in, String type){
		String sql = "INSERT INTO income ("
				+ "inid,"
				+ "indate,"
				+ "indescription,"
				+ "intype,"
				+ "amount,"
				+ "inisactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table income");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			in.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			in.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setString(cnt++, in.getDescription());
		ps.setInt(cnt++, in.getType());
		ps.setDouble(cnt++, in.getAmount());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getUserDtls()==null? 0 : in.getUserDtls().getUserdtlsid());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getDescription());
		LogU.add(in.getType());
		LogU.add(in.getAmount());
		LogU.add(in.getIsActive());
		LogU.add(in.getUserDtls()==null? 0 : in.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to income : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO income ("
				+ "inid,"
				+ "indate,"
				+ "indescription,"
				+ "intype,"
				+ "amount,"
				+ "inisactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table income");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getDescription());
		ps.setInt(cnt++, getType());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getDescription());
		LogU.add(getType());
		LogU.add(getAmount());
		LogU.add(getIsActive());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to income : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Income updateData(Income in){
		String sql = "UPDATE income SET "
				+ "indate=?,"
				+ "indescription=?,"
				+ "intype=?,"
				+ "amount=?,"
				+ "userdtlsid=?)" 
				+ " WHERE inid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table income");
				
		ps.setString(cnt++, in.getDateTrans());
		ps.setString(cnt++, in.getDescription());
		ps.setInt(cnt++, in.getType());
		ps.setDouble(cnt++, in.getAmount());
		ps.setLong(cnt++, in.getUserDtls()==null? 0 : in.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getDescription());
		LogU.add(in.getType());
		LogU.add(in.getAmount());
		LogU.add(in.getUserDtls()==null? 0 : in.getUserDtls().getUserdtlsid());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to income : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public void updateData(){
		String sql = "UPDATE income SET "
				+ "indate=?,"
				+ "indescription=?,"
				+ "intype=?,"
				+ "amount=?,"
				+ "userdtlsid=?)" 
				+ " WHERE inid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table income");
				
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getDescription());
		ps.setInt(cnt++, getType());
		ps.setDouble(cnt++, getAmount());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getDescription());
		LogU.add(getType());
		LogU.add(getAmount());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to income : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT inid FROM income  ORDER BY inid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("inid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT inid FROM income WHERE inid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE income set inisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE inid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	

	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDateTrans() {
		return dateTrans;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	
}
