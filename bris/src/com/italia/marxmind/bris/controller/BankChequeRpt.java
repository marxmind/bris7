package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @since 12/14/2017
 * @version 1.0
 *
 */
public class BankChequeRpt {

	private long id;
	private String dateTrans;
	private String dateApplying;
	private String ctaxNo;
	private String ctaxIssuedAt;
	private String ctaxIssuedDate;
	private int isActive;
	private String recepient;
	private String pbcNo;
	private String bankName;
	private String bankLocation;
	private String bankProvince;
	private Timestamp timestamp;
	private UserDtls userDtls;
	private Employee employee;
	
	private String totalChequesAmount;
	private List<BankChequeTrans> banksCheques;
	
	public static String getLastPBCNo(){
		
		String sql = "SELECT pbcno FROM bankchequerpt WHERE bankrptisactive=1 ORDER BY pbcno DESC limit 1";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			return rs.getString("pbcno");
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return "";
	}
	
	public static List<BankChequeRpt> retrieve(String sqlAdd, String[] params){
		List<BankChequeRpt> bnks = new ArrayList<BankChequeRpt>();
		
		String tableBank = "bnk";
		String tableUser = "usr";
		String tableEmp = "emp";
		String sql = "SELECT * FROM bankchequerpt " + tableBank + ", employee " + tableEmp +", userdtls " + tableUser  + " WHERE " + 
		tableBank + ".userdtlsid=" + tableUser + ".userdtlsid AND " +
		tableBank + ".empid=" + tableEmp + ".empid";
		
		sql += sqlAdd;
		
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
			
			BankChequeRpt bnk = new BankChequeRpt();
			try{bnk.setId(rs.getLong("bankchkid"));}catch(NullPointerException e){}
			try{bnk.setDateTrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{bnk.setDateApplying(rs.getString("dateapplying"));}catch(NullPointerException e){}
			try{bnk.setCtaxNo(rs.getString("ctaxno"));}catch(NullPointerException e){}
			try{bnk.setCtaxIssuedAt(rs.getString("ctaxissuedat"));}catch(NullPointerException e){}
			try{bnk.setCtaxIssuedDate(rs.getString("ctaxissueddate"));}catch(NullPointerException e){}
			try{bnk.setIsActive(rs.getInt("bankrptisactive"));}catch(NullPointerException e){}
			try{bnk.setRecepient(rs.getString("recepient"));}catch(NullPointerException e){}
			try{bnk.setPbcNo(rs.getString("pbcno"));}catch(NullPointerException e){}
			try{bnk.setBankName(rs.getString("bankname"));}catch(NullPointerException e){}
			try{bnk.setBankLocation(rs.getString("bankloc"));}catch(NullPointerException e){}
			try{bnk.setBankProvince(rs.getString("bankprov"));}catch(NullPointerException e){}
			
			Employee emp = new Employee();
			try{emp.setId(rs.getLong("empid"));}catch(NullPointerException e){}
			try{emp.setDateRegistered(rs.getString("datereg"));}catch(NullPointerException e){}
			try{emp.setDateResigned(rs.getString("dateend"));}catch(NullPointerException e){}
			try{emp.setFirstName(rs.getString("firstname"));}catch(NullPointerException e){}
			try{emp.setMiddleName(rs.getString("middlename"));}catch(NullPointerException e){}
			try{emp.setLastName(rs.getString("lastname"));}catch(NullPointerException e){}
			try{emp.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{emp.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{emp.setIsOfficial(rs.getInt("isofficial"));}catch(NullPointerException e){}
			try{emp.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			try{emp.setContactNo(rs.getString("contactno"));}catch(NullPointerException e){}
			try{emp.setPurok(rs.getString("purok"));}catch(NullPointerException e){}
			try{emp.setIsActiveEmployee(rs.getInt("isactiveemp"));}catch(NullPointerException e){}
			bnk.setEmployee(emp);
			
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
			bnk.setUserDtls(user);
			
			bnks.add(bnk);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return bnks;
	}
	
	public static BankChequeRpt retrieve(long id){
		BankChequeRpt bnk = new BankChequeRpt();
		String tableBank = "bnk";
		String tableUser = "usr";
		String tableEmp = "emp";
		
		String sql = "SELECT * FROM bankchequerpt " + tableBank + ", employee " + tableEmp +", userdtls " + tableUser  + " WHERE " + 
		tableBank + ".userdtlsid=" + tableUser + ".userdtlsid AND " + 
		tableBank + ".empid=" + tableEmp + ".empid AND " + tableBank +".bankchkid=" + id + " AND " + tableBank + ".bankrptisactive=1";		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{bnk.setId(rs.getLong("bankchkid"));}catch(NullPointerException e){}
			try{bnk.setDateTrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{bnk.setDateApplying(rs.getString("dateapplying"));}catch(NullPointerException e){}
			try{bnk.setCtaxNo(rs.getString("ctaxno"));}catch(NullPointerException e){}
			try{bnk.setCtaxIssuedAt(rs.getString("ctaxissuedat"));}catch(NullPointerException e){}
			try{bnk.setCtaxIssuedDate(rs.getString("ctaxissueddate"));}catch(NullPointerException e){}
			try{bnk.setIsActive(rs.getInt("bankrptisactive"));}catch(NullPointerException e){}
			try{bnk.setRecepient(rs.getString("recepient"));}catch(NullPointerException e){}
			try{bnk.setPbcNo(rs.getString("pbcno"));}catch(NullPointerException e){}
			try{bnk.setBankName(rs.getString("bankname"));}catch(NullPointerException e){}
			try{bnk.setBankLocation(rs.getString("bankloc"));}catch(NullPointerException e){}
			try{bnk.setBankProvince(rs.getString("bankprov"));}catch(NullPointerException e){}
			
			Employee emp = new Employee();
			try{emp.setId(rs.getLong("empid"));}catch(NullPointerException e){}
			try{emp.setDateRegistered(rs.getString("datereg"));}catch(NullPointerException e){}
			try{emp.setDateResigned(rs.getString("dateend"));}catch(NullPointerException e){}
			try{emp.setFirstName(rs.getString("firstname"));}catch(NullPointerException e){}
			try{emp.setMiddleName(rs.getString("middlename"));}catch(NullPointerException e){}
			try{emp.setLastName(rs.getString("lastname"));}catch(NullPointerException e){}
			try{emp.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{emp.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{emp.setIsOfficial(rs.getInt("isofficial"));}catch(NullPointerException e){}
			try{emp.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			try{emp.setContactNo(rs.getString("contactno"));}catch(NullPointerException e){}
			try{emp.setPurok(rs.getString("purok"));}catch(NullPointerException e){}
			try{emp.setIsActiveEmployee(rs.getInt("isactiveemp"));}catch(NullPointerException e){}
			bnk.setEmployee(emp);
			
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
			bnk.setUserDtls(user);
						
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return bnk;
	}
	
	public static BankChequeRpt save(BankChequeRpt ed){
		if(ed!=null){
			
			long id = BankChequeRpt.getInfo(ed.getId() ==0? BankChequeRpt.getLatestId()+1 : ed.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ed = BankChequeRpt.insertData(ed, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ed = BankChequeRpt.updateData(ed);
			}else if(id==3){
				LogU.add("added new Data ");
				ed = BankChequeRpt.insertData(ed, "3");
			}
			
		}
		return ed;
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
	
	public static BankChequeRpt insertData(BankChequeRpt bnk, String type){
		String sql = "INSERT INTO bankchequerpt ("
				+ "bankchkid,"
				+ "datetrans,"
				+ "dateapplying,"
				+ "ctaxno,"
				+ "ctaxissuedat,"
				+ "ctaxissueddate,"
				+ "bankrptisactive,"
				+ "empid,"
				+ "recepient,"
				+ "pbcno,"
				+ "userdtlsid,"
				+ "bankname,"
				+ "bankloc,"
				+ "bankprov) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table bankchequerpt");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			bnk.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			bnk.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, bnk.getDateTrans());
		ps.setString(cnt++, bnk.getDateApplying());
		ps.setString(cnt++, bnk.getCtaxNo());
		ps.setString(cnt++, bnk.getCtaxIssuedAt());
		ps.setString(cnt++, bnk.getCtaxIssuedDate());
		ps.setInt(cnt++, bnk.getIsActive());
		ps.setLong(cnt++, bnk.getEmployee()==null? 0 : bnk.getEmployee().getId());
		ps.setString(cnt++, bnk.getRecepient());
		ps.setString(cnt++, bnk.getPbcNo());
		ps.setLong(cnt++, bnk.getUserDtls()==null? 0 : bnk.getUserDtls().getUserdtlsid());
		ps.setString(cnt++, bnk.getBankName());
		ps.setString(cnt++, bnk.getBankLocation());
		ps.setString(cnt++, bnk.getBankProvince());
		
		LogU.add(bnk.getDateTrans());
		LogU.add(bnk.getDateApplying());
		LogU.add(bnk.getCtaxNo());
		LogU.add(bnk.getCtaxIssuedAt());
		LogU.add(bnk.getCtaxIssuedDate());
		LogU.add(bnk.getIsActive());
		LogU.add(bnk.getEmployee()==null? 0 : bnk.getEmployee().getId());
		LogU.add(bnk.getRecepient());
		LogU.add(bnk.getPbcNo());
		LogU.add(bnk.getUserDtls()==null? 0 : bnk.getUserDtls().getUserdtlsid());
		LogU.add(bnk.getBankName());
		LogU.add(bnk.getBankLocation());
		LogU.add(bnk.getBankProvince());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to bankchequerpt : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return bnk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO bankchequerpt ("
				+ "bankchkid,"
				+ "datetrans,"
				+ "dateapplying,"
				+ "ctaxno,"
				+ "ctaxissuedat,"
				+ "ctaxissueddate,"
				+ "bankrptisactive,"
				+ "empid,"
				+ "recepient,"
				+ "pbcno,"
				+ "userdtlsid,"
				+ "bankname,"
				+ "bankloc,"
				+ "bankprov) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			long id =1;
			int cnt = 1;
			LogU.add("===========================START=========================");
			LogU.add("inserting data into table bankchequerpt");
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
			ps.setString(cnt++, getDateApplying());
			ps.setString(cnt++, getCtaxNo());
			ps.setString(cnt++, getCtaxIssuedAt());
			ps.setString(cnt++, getCtaxIssuedDate());
			ps.setInt(cnt++, getIsActive());
			ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
			ps.setString(cnt++, getRecepient());
			ps.setString(cnt++, getPbcNo());
			ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
			ps.setString(cnt++, getBankName());
			ps.setString(cnt++, getBankLocation());
			ps.setString(cnt++, getBankProvince());
			
			LogU.add(getDateTrans());
			LogU.add(getDateApplying());
			LogU.add(getCtaxNo());
			LogU.add(getCtaxIssuedAt());
			LogU.add(getCtaxIssuedDate());
			LogU.add(getIsActive());
			LogU.add(getEmployee()==null? 0 : getEmployee().getId());
			LogU.add(getRecepient());
			LogU.add(getPbcNo());
			LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
			LogU.add(getBankName());
			LogU.add(getBankLocation());
			LogU.add(getBankProvince());
			
			LogU.add("executing for saving...");
			ps.execute();
			LogU.add("closing...");
			ps.close();
			ConnectDB.close(conn);
			LogU.add("data has been successfully saved...");
			}catch(SQLException s){
				LogU.add("error inserting data to bankchequerpt : " + s.getMessage());
			}
			LogU.add("===========================END=========================");
	}
	
	public static BankChequeRpt updateData(BankChequeRpt bnk){
		String sql = "UPDATE bankchequerpt SET "
				+ "datetrans=?,"
				+ "dateapplying=?,"
				+ "ctaxno=?,"
				+ "ctaxissuedat=?,"
				+ "ctaxissueddate=?,"
				+ "empid=?,"
				+ "recepient=?,"
				+ "pbcno=?,"
				+ "userdtlsid=?,"
				+ "bankname=?,"
				+ "bankloc=?,"
				+ "bankprov=?" 
				+ " WHERE bankchkid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bankchequerpt");
		
		
		ps.setString(cnt++, bnk.getDateTrans());
		ps.setString(cnt++, bnk.getDateApplying());
		ps.setString(cnt++, bnk.getCtaxNo());
		ps.setString(cnt++, bnk.getCtaxIssuedAt());
		ps.setString(cnt++, bnk.getCtaxIssuedDate());
		ps.setLong(cnt++, bnk.getEmployee()==null? 0 : bnk.getEmployee().getId());
		ps.setString(cnt++, bnk.getRecepient());
		ps.setString(cnt++, bnk.getPbcNo());
		ps.setLong(cnt++, bnk.getUserDtls()==null? 0 : bnk.getUserDtls().getUserdtlsid());
		ps.setString(cnt++, bnk.getBankName());
		ps.setString(cnt++, bnk.getBankLocation());
		ps.setString(cnt++, bnk.getBankProvince());
		ps.setLong(cnt++, bnk.getId());
		
		LogU.add(bnk.getDateTrans());
		LogU.add(bnk.getDateApplying());
		LogU.add(bnk.getCtaxNo());
		LogU.add(bnk.getCtaxIssuedAt());
		LogU.add(bnk.getCtaxIssuedDate());
		LogU.add(bnk.getEmployee()==null? 0 : bnk.getEmployee().getId());
		LogU.add(bnk.getRecepient());
		LogU.add(bnk.getPbcNo());
		LogU.add(bnk.getUserDtls()==null? 0 : bnk.getUserDtls().getUserdtlsid());
		LogU.add(bnk.getBankName());
		LogU.add(bnk.getBankLocation());
		LogU.add(bnk.getBankProvince());
		LogU.add(bnk.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bankchequerpt : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return bnk;
	}
	
	public void updateData(){
		String sql = "UPDATE bankchequerpt SET "
				+ "datetrans=?,"
				+ "dateapplying=?,"
				+ "ctaxno=?,"
				+ "ctaxissuedat=?,"
				+ "ctaxissueddate=?,"
				+ "empid=?,"
				+ "recepient=?,"
				+ "pbcno=?,"
				+ "userdtlsid=?,"
				+ "bankname=?,"
				+ "bankloc=?,"
				+ "bankprov=?" 
				+ " WHERE bankchkid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bankchequerpt");
		
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getDateApplying());
		ps.setString(cnt++, getCtaxNo());
		ps.setString(cnt++, getCtaxIssuedAt());
		ps.setString(cnt++, getCtaxIssuedDate());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
		ps.setString(cnt++, getRecepient());
		ps.setString(cnt++, getPbcNo());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setString(cnt++, getBankName());
		ps.setString(cnt++, getBankLocation());
		ps.setString(cnt++, getBankProvince());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getDateApplying());
		LogU.add(getCtaxNo());
		LogU.add(getCtaxIssuedAt());
		LogU.add(getCtaxIssuedDate());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		LogU.add(getRecepient());
		LogU.add(getPbcNo());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getBankName());
		LogU.add(getBankLocation());
		LogU.add(getBankProvince());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bankchequerpt : " + s.getMessage());
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
		sql="SELECT bankchkid FROM bankchequerpt  ORDER BY bankchkid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("bankchkid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static long getInfo(long id){
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
		ps = conn.prepareStatement("SELECT bankchkid FROM bankchequerpt WHERE bankchkid=?");
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
		String sql = "UPDATE bankchequerpt set bankrptisactive=0 WHERE bankchkid=?";
		
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
	public String getDateApplying() {
		return dateApplying;
	}
	public void setDateApplying(String dateApplying) {
		this.dateApplying = dateApplying;
	}
	public String getCtaxNo() {
		return ctaxNo;
	}
	public void setCtaxNo(String ctaxNo) {
		this.ctaxNo = ctaxNo;
	}
	public String getCtaxIssuedAt() {
		return ctaxIssuedAt;
	}
	public void setCtaxIssuedAt(String ctaxIssuedAt) {
		this.ctaxIssuedAt = ctaxIssuedAt;
	}
	public String getCtaxIssuedDate() {
		return ctaxIssuedDate;
	}
	public void setCtaxIssuedDate(String ctaxIssuedDate) {
		this.ctaxIssuedDate = ctaxIssuedDate;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getRecepient() {
		return recepient;
	}

	public void setRecepient(String recepient) {
		this.recepient = recepient;
	}

	public String getPbcNo() {
		return pbcNo;
	}

	public void setPbcNo(String pbcNo) {
		this.pbcNo = pbcNo;
	}

	public String getTotalChequesAmount() {
		return totalChequesAmount;
	}

	public void setTotalChequesAmount(String totalChequesAmount) {
		this.totalChequesAmount = totalChequesAmount;
	}

	public List<BankChequeTrans> getBanksCheques() {
		return banksCheques;
	}

	public void setBanksCheques(List<BankChequeTrans> banksCheques) {
		this.banksCheques = banksCheques;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankLocation() {
		return bankLocation;
	}

	public void setBankLocation(String bankLocation) {
		this.bankLocation = bankLocation;
	}

	public String getBankProvince() {
		return bankProvince;
	}

	public void setBankProvince(String bankProvince) {
		this.bankProvince = bankProvince;
	}

	
}
