package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.enm.CaseKind;
import com.italia.marxmind.bris.enm.CaseStatus;
import com.italia.marxmind.bris.enm.IraType;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 03/23/2018
 * @version 1.0
 *
 */
public class IRA {

	private long id;
	private String dateTrans;
	private int year;
	private int cycleDate;
	private double amount;
	private int isActive;
	
	private int type;
	private String description;
	
	private UserDtls userDtls;
	
	private String amountira;
	private String typeName;
	
	public static int startYear(){
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT irayear FROM ira WHERE iraisactive=1 limit 1");
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getInt("irayear");
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return 0;
	}
	
	public static boolean isIRAExist(int year){
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT irayear FROM ira WHERE iraisactive=1 AND irayear="+year);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return true;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
	public static double iraAmount(int year){
				
		double amount = 0;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT amount FROM ira WHERE iraisactive=1 AND irayear="+year);
		
		System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			amount += rs.getDouble("amount");
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return amount;
	}
	
	public static List<IRA> retrieve(String sqlAdd, String[] params){
		List<IRA> irs = new ArrayList<>();
		
		String tableIRA = "ir";
		String tableUser = "usr";
		String sql = "SELECT * FROM ira " + tableIRA + ", userdtls " + tableUser + " WHERE " +
				tableIRA + ".userdtlsid=" + tableUser + ".userdtlsid ";
		
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
			
			IRA ir = new IRA();
			try{ir.setId(rs.getLong("iraid"));}catch(NullPointerException e){}
			try{ir.setDateTrans(rs.getString("iradate"));}catch(NullPointerException e){}
			try{ir.setYear(rs.getInt("irayear"));}catch(NullPointerException e){}
			try{ir.setCycleDate(rs.getInt("cycledate"));}catch(NullPointerException e){}
			try{ir.setAmount(rs.getDouble("amount"));}catch(NullPointerException e){}
			try{ir.setIsActive(rs.getInt("iraisactive"));}catch(NullPointerException e){}
			try{ir.setType(rs.getInt("iratype"));}catch(NullPointerException e){}
			try{ir.setDescription(rs.getString("description"));}catch(NullPointerException e){}
			
			try{ir.setAmountira(Currency.formatAmount(rs.getDouble("amount")));}catch(NullPointerException e){}
			try{ir.setTypeName(IraType.typeName(rs.getInt("iratype")));}catch(NullPointerException e){}
			
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
			ir.setUserDtls(user);
			
			irs.add(ir);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return irs;
	}
	
	public static IRA save(IRA ir){
		if(ir!=null){
			
			long id = IRA.getInfo(ir.getId() ==0? IRA.getLatestId()+1 : ir.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ir = IRA.insertData(ir, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ir = IRA.updateData(ir);
			}else if(id==3){
				LogU.add("added new Data ");
				ir = IRA.insertData(ir, "3");
			}
			
		}
		return ir;
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
	
	public static IRA insertData(IRA ir, String type){
		String sql = "INSERT INTO ira ("
				+ "iraid,"
				+ "iradate,"
				+ "irayear,"
				+ "cycledate,"
				+ "amount,"
				+ "iraisactive,"
				+ "userdtlsid,"
				+ "iratype,"
				+ "description)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table ira");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			ir.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			ir.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, ir.getDateTrans());
		ps.setInt(cnt++, ir.getYear());
		ps.setInt(cnt++, ir.getCycleDate());
		ps.setDouble(cnt++, ir.getAmount());
		ps.setInt(cnt++, ir.getIsActive());
		ps.setLong(cnt++, ir.getUserDtls()==null? 0 : ir.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, ir.getType());
		ps.setString(cnt++, ir.getDescription());
		
		LogU.add(ir.getDateTrans());
		LogU.add(ir.getYear());
		LogU.add(ir.getCycleDate());
		LogU.add(ir.getAmount());
		LogU.add(ir.getIsActive());
		LogU.add(ir.getUserDtls()==null? 0 : ir.getUserDtls().getUserdtlsid());
		LogU.add(ir.getType());
		LogU.add(ir.getDescription());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to ira : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ir;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO ira ("
				+ "iraid,"
				+ "iradate,"
				+ "irayear,"
				+ "cycledate,"
				+ "amount,"
				+ "iraisactive,"
				+ "userdtlsid,"
				+ "iratype,"
				+ "description)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table ira");
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
		ps.setInt(cnt++, getYear());
		ps.setInt(cnt++, getCycleDate());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getType());
		ps.setString(cnt++, getDescription());
		
		LogU.add(getDateTrans());
		LogU.add(getYear());
		LogU.add(getCycleDate());
		LogU.add(getAmount());
		LogU.add(getIsActive());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getType());
		LogU.add(getDescription());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to ira : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static IRA updateData(IRA ir){
		String sql = "UPDATE ira SET "
				+ "iradate=?,"
				+ "irayear=?,"
				+ "cycledate=?,"
				+ "amount=?,"
				+ "userdtlsid=?,"
				+ "iratype=?,"
				+ "description=?" 
				+ " WHERE iraid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table ira");
		
		ps.setString(cnt++, ir.getDateTrans());
		ps.setInt(cnt++, ir.getYear());
		ps.setInt(cnt++, ir.getCycleDate());
		ps.setDouble(cnt++, ir.getAmount());
		ps.setLong(cnt++, ir.getUserDtls()==null? 0 : ir.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, ir.getType());
		ps.setString(cnt++, ir.getDescription());
		ps.setLong(cnt++, ir.getId());
		
		LogU.add(ir.getDateTrans());
		LogU.add(ir.getYear());
		LogU.add(ir.getCycleDate());
		LogU.add(ir.getAmount());
		LogU.add(ir.getUserDtls()==null? 0 : ir.getUserDtls().getUserdtlsid());
		LogU.add(ir.getType());
		LogU.add(ir.getDescription());
		LogU.add(ir.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to update : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ir;
	}
	
	public void updateData(){
		String sql = "UPDATE ira SET "
				+ "iradate=?,"
				+ "irayear=?,"
				+ "cycledate=?,"
				+ "amount=?,"
				+ "userdtlsid=?,"
				+ "iratype=?,"
				+ "description=?" 
				+ " WHERE iraid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table ira");
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getYear());
		ps.setInt(cnt++, getCycleDate());
		ps.setDouble(cnt++, getAmount());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getType());
		ps.setString(cnt++, getDescription());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getYear());
		LogU.add(getCycleDate());
		LogU.add(getAmount());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getType());
		LogU.add(getDescription());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to update : " + s.getMessage());
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
		sql="SELECT iraid FROM ira  ORDER BY iraid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("iraid");
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
		ps = conn.prepareStatement("SELECT iraid FROM ira WHERE iraid=?");
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
		String sql = "UPDATE ira set iraisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE iraid=?";
		
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getCycleDate() {
		return cycleDate;
	}

	public void setCycleDate(int cycleDate) {
		this.cycleDate = cycleDate;
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

	public String getAmountira() {
		return amountira;
	}

	public void setAmountira(String amountira) {
		this.amountira = amountira;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
