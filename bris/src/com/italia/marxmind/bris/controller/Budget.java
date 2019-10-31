package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 08/12/2017
 * @version 1.0
 *
 */

public class Budget {

	private long id;
	private int monthyear;
	private int cycleDate;
	private int activated;
	private double amount;
	private int isActive;
	
	private BankAccounts accounts;
	private UserDtls userDtls;
	
	public static List<Budget> retrieve(String sqlAdd, String[] params){
		List<Budget> budgets = Collections.synchronizedList(new ArrayList<Budget>());
		
		String tableBud = "bud";
		String tableAccnt = "accnt";
		String tableUser = "usr";
		
		String sql = "SELECT * FROM budgettrans " + tableBud + ", bankaccounts " + tableAccnt +", userdtls " + tableUser + 
				" WHERE " + tableBud + ".bankid="+tableAccnt+".bankid AND " + tableBud + ".userdtlsid="+tableUser+".userdtlsid ";
		
		sql = sql + sqlAdd;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL: " + ps.toString());
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Budget bud = new Budget();
			bud.setId(rs.getLong("budid"));
			bud.setMonthyear(rs.getInt("iramonthyear"));
			bud.setCycleDate(rs.getInt("cycledate"));
			bud.setActivated(rs.getInt("isactivated"));
			bud.setAmount(rs.getDouble("amount"));
			bud.setIsActive(rs.getInt("isactivebud"));
			
			BankAccounts ac = new BankAccounts();
			try{ac.setId(rs.getInt("bankid"));}catch(NullPointerException e){}
			try{ac.setBankAccntName(rs.getString("bankname"));}catch(NullPointerException e){}
			try{ac.setBankAccntNo(rs.getString("accountno"));}catch(NullPointerException e){}
			try{ac.setBankAccntBranch(rs.getString("branch"));}catch(NullPointerException e){}
			try{ac.setIsActive(rs.getInt("isactivebank"));}catch(NullPointerException e){}
			bud.setAccounts(ac);
			
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
			bud.setUserDtls(user);
			
			budgets.add(bud);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return budgets;
	}
	
	public static Budget save(Budget emp){
		if(emp!=null){
			
			long id = Budget.getInfo(emp.getId() ==0? Budget.getLatestId()+1 : emp.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				emp = Budget.insertData(emp, "1");
			}else if(id==2){
				LogU.add("update Data ");
				emp = Budget.updateData(emp);
			}else if(id==3){
				LogU.add("added new Data ");
				emp = Budget.insertData(emp, "3");
			}
			
		}
		return emp;
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
	
	public static Budget insertData(Budget emp, String type){
		String sql = "INSERT INTO budgettrans ("
				+ "budid,"
				+ "iramonthyear,"
				+ "cycledate,"
				+ "isactivated,"
				+ "amount,"
				+ "isactivebud,"
				+ "bankid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table budgettrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			emp.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			emp.setId(id);
			LogU.add("id: " + id);
		}
		ps.setInt(cnt++, emp.getMonthyear());
		ps.setInt(cnt++, emp.getCycleDate());
		ps.setInt(cnt++, emp.getActivated());
		ps.setDouble(cnt++, emp.getAmount());
		ps.setInt(cnt++, emp.getIsActive());
		ps.setInt(cnt++, emp.getAccounts().getId());
		ps.setLong(cnt++, emp.getUserDtls()==null? 0 : emp.getUserDtls().getUserdtlsid());
		
		LogU.add(emp.getMonthyear());
		LogU.add(emp.getCycleDate());
		LogU.add(emp.getActivated());
		LogU.add(emp.getAmount());
		LogU.add(emp.getIsActive());
		LogU.add(emp.getAccounts().getId());
		LogU.add(emp.getUserDtls()==null? 0 : emp.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to budgettrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return emp;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO budgettrans ("
				+ "budid,"
				+ "iramonthyear,"
				+ "cycledate,"
				+ "isactivated,"
				+ "amount,"
				+ "isactivebud,"
				+ "bankid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table budgettrans");
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
		ps.setInt(cnt++, getMonthyear());
		ps.setInt(cnt++, getCycleDate());
		ps.setInt(cnt++, getActivated());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getAccounts().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add(getMonthyear());
		LogU.add(getCycleDate());
		LogU.add(getActivated());
		LogU.add(getAmount());
		LogU.add(getIsActive());
		LogU.add(getAccounts().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to budgettrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static Budget updateData(Budget emp){
		String sql = "UPDATE budgettrans SET "
				+ "iramonthyear=?,"
				+ "cycledate=?,"
				+ "isactivated=?,"
				+ "amount=?,"
				+ "bankid=?,"
				+ "userdtlsid=?" 
				+ " WHERE budid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table budgettrans");
		
		ps.setInt(cnt++, emp.getMonthyear());
		ps.setInt(cnt++, emp.getCycleDate());
		ps.setInt(cnt++, emp.getActivated());
		ps.setDouble(cnt++, emp.getAmount());
		ps.setInt(cnt++, emp.getAccounts().getId());
		ps.setLong(cnt++, emp.getUserDtls()==null? 0 : emp.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, emp.getId());
		
		LogU.add(emp.getMonthyear());
		LogU.add(emp.getCycleDate());
		LogU.add(emp.getActivated());
		LogU.add(emp.getAmount());
		LogU.add(emp.getAccounts().getId());
		LogU.add(emp.getUserDtls()==null? 0 : emp.getUserDtls().getUserdtlsid());
		LogU.add(emp.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to budgettrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return emp;
	}
	
	public void updateData(){
		String sql = "UPDATE budgettrans SET "
				+ "iramonthyear=?,"
				+ "cycledate=?,"
				+ "isactivated=?,"
				+ "amount=?,"
				+ "bankid=?,"
				+ "userdtlsid=?" 
				+ " WHERE budid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table budgettrans");
		
		ps.setInt(cnt++, getMonthyear());
		ps.setInt(cnt++, getCycleDate());
		ps.setInt(cnt++, getActivated());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getAccounts().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getMonthyear());
		LogU.add(getCycleDate());
		LogU.add(getActivated());
		LogU.add(getAmount());
		LogU.add(getAccounts().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to budgettrans : " + s.getMessage());
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
		sql="SELECT budid FROM budgettrans  ORDER BY budid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("budid");
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
		ps = conn.prepareStatement("SELECT budid FROM budgettrans WHERE budid=?");
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
		String sql = "UPDATE budgettrans set isactivebud=0 WHERE budid=?";
		
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
	public int getMonthyear() {
		return monthyear;
	}
	public void setMonthyear(int monthyear) {
		this.monthyear = monthyear;
	}
	public int getCycleDate() {
		return cycleDate;
	}
	public void setCycleDate(int cycleDate) {
		this.cycleDate = cycleDate;
	}
	public int getActivated() {
		return activated;
	}
	public void setActivated(int activated) {
		this.activated = activated;
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
	public BankAccounts getAccounts() {
		return accounts;
	}
	public void setAccounts(BankAccounts accounts) {
		this.accounts = accounts;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	
}
