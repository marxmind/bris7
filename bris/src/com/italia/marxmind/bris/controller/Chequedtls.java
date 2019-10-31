package com.italia.marxmind.bris.controller;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reports.ReadXML;
import com.italia.marxmind.bris.reports.ReportCompiler;
import com.italia.marxmind.bris.reports.ReportTag;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;
import com.italia.marxmind.bris.utils.Numbers;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
/**
 * 
 * @author mark italia
 * @since 11/12/2013
 * @version 1.0
 */

public class Chequedtls {

	private long id;
	private String dateTrans;
	private int status;
	private String checkNo;
	private double amount;
	private String issueTo;
	private int isActive;
	private BankAccounts accounts;
	private Employee signatory1;
	private Employee signatory2;
	private UserDtls userDtls;
	private MOOE mooe;
	private String dvNo;
	private String purpose;
	private String checkAmount;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	
	public Chequedtls(){}
	
	public Chequedtls(
			long id,
			String dateTrans,
			int status,
			String checkNo,
			double amount,
			String issueTo,
			int isActive,
			BankAccounts accounts,
			Employee signatory1,
			Employee signatory2,
			UserDtls userDtls
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.status = status;
		this.checkNo = checkNo;
		this.amount = amount;
		this.issueTo = issueTo;
		this.isActive = isActive;
		this.accounts = accounts;
		this.signatory1 = signatory1;
		this.signatory2 = signatory2;
		this.userDtls = userDtls;
	}
	
	public static String formatAmount(String amount){
		double money = Double.valueOf(amount);
		NumberFormat format = NumberFormat.getCurrencyInstance();
		amount = format.format(money).replace("$", "");
		amount = amount.replace("Php", "");
		return amount;
	}
	
	public static List<Chequedtls> retrieve(String sqlAdd, String[] params){
		List<Chequedtls> cList =  new ArrayList<Chequedtls>();//Collections.synchronizedList(new ArrayList<Chequedtls>());
		
		String tableChk = "chk";
		String tableAccnt = "bank";
		String tableUser = "usr";
		String tableMoe = "moe";
		
		String sql = "SELECT * FROM cheques " + tableChk + 
				", bankaccounts " + tableAccnt + 
				", userdtls " + tableUser + 
				", mooe " + tableMoe + " WHERE " + 
				tableChk + ".bankid="+ tableAccnt +".bankid AND " +
				tableChk + ".userdtlsid=" + tableUser + ".userdtlsid AND " +
				tableChk + ".moid="+ tableMoe + ".moid";
		
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
		
		//System.out.println("CHECK SQL " + ps.toString());
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			Chequedtls chk = new Chequedtls();
			try{chk.setId(rs.getLong("chkid"));}catch(NullPointerException e){}
			try{chk.setDateTrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{chk.setStatus(rs.getInt("status"));}catch(NullPointerException e){}
			try{chk.setCheckNo(rs.getString("checkno"));}catch(NullPointerException e){}
			try{chk.setAmount(Numbers.formatDouble(rs.getDouble("amount")));}catch(NullPointerException e){}
			try{chk.setIssueTo(rs.getString("issueto"));}catch(NullPointerException e){}
			try{chk.setIsActive(rs.getInt("isactivechk"));}catch(NullPointerException e){}
			try{chk.setCheckAmount(Currency.formatAmount(rs.getDouble("amount")));}catch(NullPointerException e){}
			try{chk.setDvNo(rs.getString("dvno"));}catch(NullPointerException e){}
			try{chk.setPurpose(rs.getString("purpose"));}catch(NullPointerException e){}
			
			BankAccounts ac = new BankAccounts();
			try{ac.setId(rs.getInt("bankid"));}catch(NullPointerException e){}
			try{ac.setBankAccntName(rs.getString("bankname"));}catch(NullPointerException e){}
			try{ac.setBankAccntNo(rs.getString("accountno"));}catch(NullPointerException e){}
			try{ac.setBankAccntBranch(rs.getString("branch"));}catch(NullPointerException e){}
			try{ac.setIsActive(rs.getInt("isactivebank"));}catch(NullPointerException e){}
			chk.setAccounts(ac);
			
			try{
				Employee emp1 = Employee.retrieve(rs.getLong("empid1"));
				chk.setSignatory1(emp1);
			}catch(Exception e){}
			
			try{
				Employee emp2 = Employee.retrieve(rs.getLong("empid2"));
				chk.setSignatory2(emp2);
			}catch(Exception e){}
			
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
			chk.setUserDtls(user);
			
			MOOE mo = new MOOE();
			try{mo.setId(rs.getLong("moid"));}catch(NullPointerException e){}
			try{mo.setDateTrans(rs.getString("modate"));}catch(NullPointerException e){}
			try{mo.setYear(rs.getInt("moyear"));}catch(NullPointerException e){}
			try{mo.setCode(rs.getInt("mocode"));}catch(NullPointerException e){}
			try{mo.setName(rs.getString("moname"));}catch(NullPointerException e){}
			try{mo.setAmount(rs.getDouble("amountbudget"));}catch(NullPointerException e){}
			try{mo.setIsActive(rs.getInt("moisactive"));}catch(NullPointerException e){}
			chk.setMooe(mo);
			
			cList.add(chk);
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException sl){sl.getMessage();}
		
		return cList;
	}
	
	public static Chequedtls retrieve(long id){
		Chequedtls chk = new Chequedtls();
		String tableChk = "chk";
		String tableAccnt = "bank";
		String tableUser = "usr";
		String tableMoe = "moe";
		
		String sql = "SELECT * FROM cheques " + tableChk + 
				", bankaccounts " + tableAccnt + 
				", userdtls " + tableUser + 
				", mooe " + tableMoe + " WHERE " + 
				tableChk + ".bankid="+ tableAccnt +".bankid AND " +
				tableChk + ".userdtlsid=" + tableUser + ".userdtlsid AND " +
				tableChk + ".moid=" + tableMoe +".moid AND "+ tableChk + ".chkid="+id;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{chk.setId(rs.getLong("chkid"));}catch(NullPointerException e){}
			try{chk.setDateTrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{chk.setStatus(rs.getInt("status"));}catch(NullPointerException e){}
			try{chk.setCheckNo(rs.getString("checkno"));}catch(NullPointerException e){}
			try{chk.setAmount(rs.getDouble("amount"));}catch(NullPointerException e){}
			try{chk.setIssueTo(rs.getString("issueto"));}catch(NullPointerException e){}
			try{chk.setIsActive(rs.getInt("isactivechk"));}catch(NullPointerException e){}
			try{chk.setCheckAmount(Currency.formatAmount(rs.getDouble("amount")));}catch(NullPointerException e){}
			try{chk.setDvNo(rs.getString("dvno"));}catch(NullPointerException e){}
			try{chk.setPurpose(rs.getString("purpose"));}catch(NullPointerException e){}
			
			BankAccounts ac = new BankAccounts();
			try{ac.setId(rs.getInt("bankid"));}catch(NullPointerException e){}
			try{ac.setBankAccntName(rs.getString("bankname"));}catch(NullPointerException e){}
			try{ac.setBankAccntNo(rs.getString("accountno"));}catch(NullPointerException e){}
			try{ac.setBankAccntBranch(rs.getString("branch"));}catch(NullPointerException e){}
			try{ac.setIsActive(rs.getInt("isactivebank"));}catch(NullPointerException e){}
			chk.setAccounts(ac);
			
			try{
				Employee emp1 = Employee.retrieve(rs.getLong("empid1"));
				chk.setSignatory1(emp1);
			}catch(Exception e){}
			
			try{
				Employee emp2 = Employee.retrieve(rs.getLong("empid2"));
				chk.setSignatory2(emp2);
			}catch(Exception e){}
			
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
			chk.setUserDtls(user);
			
			MOOE mo = new MOOE();
			try{mo.setId(rs.getLong("moid"));}catch(NullPointerException e){}
			try{mo.setDateTrans(rs.getString("modate"));}catch(NullPointerException e){}
			try{mo.setYear(rs.getInt("moyear"));}catch(NullPointerException e){}
			try{mo.setCode(rs.getInt("mocode"));}catch(NullPointerException e){}
			try{mo.setName(rs.getString("moname"));}catch(NullPointerException e){}
			try{mo.setAmount(rs.getDouble("amountbudget"));}catch(NullPointerException e){}
			try{mo.setIsActive(rs.getInt("moisactive"));}catch(NullPointerException e){}
			chk.setMooe(mo);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException sl){sl.getMessage();}
		
		return chk;
	}
	
	public static Chequedtls retrieve(String checkNo){
		Chequedtls chk = new Chequedtls();
		String sql = "SELECT * FROM cheques WHERE checkno='"+checkNo+"'";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{chk.setId(rs.getLong("chkid"));}catch(NullPointerException e){}
			try{chk.setDateTrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{chk.setStatus(rs.getInt("status"));}catch(NullPointerException e){}
			try{chk.setCheckNo(rs.getString("checkno"));}catch(NullPointerException e){}
			try{chk.setAmount(rs.getDouble("amount"));}catch(NullPointerException e){}
			try{chk.setIssueTo(rs.getString("issueto"));}catch(NullPointerException e){}
			try{chk.setIsActive(rs.getInt("isactivechk"));}catch(NullPointerException e){}
			try{chk.setCheckAmount(Currency.formatAmount(rs.getDouble("amount")));}catch(NullPointerException e){}
			try{chk.setDvNo(rs.getString("dvno"));}catch(NullPointerException e){}
			try{chk.setPurpose(rs.getString("purpose"));}catch(NullPointerException e){}
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException sl){sl.getMessage();}
		
		return chk;
	}
	
	
	/**
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static Double sum(String sql, String[] params,String fieldName){
		Connection conn = null;
		ResultSet rs = null;
		double total = 0d;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		//System.out.println("Budget SQL: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			total = rs.getDouble(fieldName);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException e){}
		
		return total;
	}
	
	private static String timeStamp(String timestamp){
		try{
			timestamp = timestamp.split("\\.")[0];
			//System.out.println("new timeStamp " + timestamp);
			String time = "", date="";
			date = timestamp.split(" ")[0];
			time = timestamp.split(" ")[1];
			//System.out.println("new time " + time);
			return date + " " + DateUtils.timeTo12Format(time,true);
		}catch(Exception e){}
		
		return "error";
	}
	
	
	
	
	public static List<String> retrievePayOrderOf(String sql, String[] params){
		List<Chequedtls> cList =  new ArrayList<Chequedtls>();//Collections.synchronizedList(new ArrayList<Chequedtls>());
		List<String> results = new ArrayList<String>();
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
			results.add(rs.getString("issueto"));
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException sl){}
		
		return results;
	}
	
	public static long getLastCheckNo(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		long result = 0;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT checkno FROM cheques WHERE isactivechk=1 ORDER BY chkid DESC LIMIT 1");
		System.out.println("SQL getLastCheckNo: " + ps.toString());
		rs = ps.executeQuery();
		
		if(rs.next()){
			result= Long.valueOf(rs.getString("checkno"));
		}
		
		rs.close();
		ps.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		ConnectDB.close(conn);
		return result;
	}
	
	public static String getLastDVNo(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		String result = "";
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT dvno FROM cheques  WHERE isactivechk=1 ORDER BY chkid DESC LIMIT 1");
		System.out.println("SQL getLastDVNo: " + ps.toString());
		rs = ps.executeQuery();
		
		if(rs.next()){
			result= rs.getString("dvno");
		}
		
		if(result!=null && !result.isEmpty() && !"NULL".equalsIgnoreCase(result)) {
			String year= DateUtils.getCurrentYear()+"";
			       year= year.substring(2, 4);
			int month = DateUtils.getCurrentMonth();
			int seqDV = Integer.valueOf(result.split("-")[2]);
			
			seqDV +=1;//increment DV No
			String lenDV = seqDV+"";
			int lengthDV = lenDV.length();//get the length
			String newDVNo="";
			if(lengthDV==1) {
				newDVNo ="00"+seqDV;
			}else if(lengthDV==2) {
				newDVNo ="0"+seqDV;
			}else if(lengthDV==3) {
				newDVNo =seqDV+"";
			}
			
			String oldYear = result.split("-")[0];
			String thisYear = DateUtils.getCurrentYear()+"";
			thisYear = thisYear.substring(2, 4);
			
			if(!oldYear.equalsIgnoreCase(thisYear)) {
				//reset dv number to one
				year = thisYear;
				newDVNo ="001";
			}
			
			if(month<=9) {
				result = year+"-0"+month+"-"+newDVNo;
			}else {
				result = year+"-"+month+"-"+newDVNo;
			}
		}else {
			String year= DateUtils.getCurrentYear()+"";
		       year= year.substring(2, 4);
			int month = DateUtils.getCurrentMonth();
			
			if(month<=9) {
				result = year+"-0"+month+"-001";
			}else {
				result = year+"-"+month+"-001";
			}
			
		}
		
		rs.close();
		ps.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		ConnectDB.close(conn);
		return result;
	}
	
	public static long getLastAccountCheckNo(int accountId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		long result = 0;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT chk.checkno FROM cheques chk, bankaccounts acc WHERE  chk.bankid=acc.bankid AND acc.bankid="+ accountId +" ORDER BY chk.chkid DESC LIMIT 1");
		System.out.println("SQL getLastAccountCheckNo: " + ps.toString());
		rs = ps.executeQuery();
		
		if(rs.next()){
			result= Long.valueOf(rs.getString("checkno"));
		}
		
		rs.close();
		ps.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		ConnectDB.close(conn);
		return result;
	}
	
	public static Chequedtls save(Chequedtls chk){
		if(chk!=null){
			long id = getInfo(chk.getId()==0? getLatestId()+1 : chk.getId());
			if(id==1){
				chk = Chequedtls.insertData(chk, "1");
			}else if(id==2){
				chk = Chequedtls.updateData(chk);
			}else if(id==3){
				chk = Chequedtls.insertData(chk, "3");
			}
			
		}
		return chk;
	}
	
	public void save(){
			long id = getInfo(getId()==0? getLatestId()+1 : getId());
			if(id==1){
				insertData("1");
			}else if(id==2){
				updateData();
			}else if(id==3){
				insertData("3");
			}
	}
	
	public static Chequedtls insertData(Chequedtls chk, String type){
		String sql = "INSERT INTO cheques ("
				+ "chkid,"
				+ "status,"
				+ "datetrans,"
				+ "checkno,"
				+ "amount,"
				+ "issueto,"
				+ "isactivechk,"
				+ "empid1,"
				+ "empid2,"
				+ "bankid,"
				+ "userdtlsid,"
				+ "moid,"
				+ "dvno,"
				+ "purpose) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			chk.setId(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			chk.setId(id);
		}
		
		ps.setInt(cnt++, chk.getStatus());
		ps.setString(cnt++, chk.getDateTrans());
		ps.setString(cnt++, chk.getCheckNo());
		ps.setDouble(cnt++, chk.getAmount());
		ps.setString(cnt++, chk.getIssueTo());
		ps.setInt(cnt++, chk.getIsActive());
		ps.setLong(cnt++, chk.getSignatory1()==null? 0 : chk.getSignatory1().getId());
		ps.setLong(cnt++, chk.getSignatory2()==null? 0 : chk.getSignatory2().getId());
		ps.setInt(cnt++, chk.getAccounts().getId());
		ps.setLong(cnt++, chk.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, chk.getMooe().getId());
		ps.setString(cnt++, chk.getDvNo());
		ps.setString(cnt++, chk.getPurpose());
		
		ps.execute();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
		return chk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO cheques ("
				+ "chkid,"
				+ "status,"
				+ "datetrans,"
				+ "checkno,"
				+ "amount,"
				+ "issueto,"
				+ "isactivechk,"
				+ "empid1,"
				+ "empid2,"
				+ "bankid,"
				+ "userdtlsid,"
				+ "moid,"
				+ "dvno,"
				+ "purpose) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
		}
		
		ps.setInt(cnt++, getStatus());
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getCheckNo());
		ps.setDouble(cnt++, getAmount());
		ps.setString(cnt++, getIssueTo());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getSignatory1()==null? 0 : getSignatory1().getId());
		ps.setLong(cnt++, getSignatory2()==null? 0 : getSignatory2().getId());
		ps.setInt(cnt++, getAccounts().getId());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getMooe().getId());
		ps.setString(cnt++, getDvNo());
		ps.setString(cnt++, getPurpose());
		
		ps.execute();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
	}
	
	public static Chequedtls updateData(Chequedtls chk){
		String sql = "UPDATE cheques SET "
				+ "status=?,"
				+ "datetrans=?,"
				+ "checkno=?,"
				+ "amount=?,"
				+ "issueto=?,"
				+ "empid1=?,"
				+ "empid2=?,"
				+ "bankid=?,"
				+ "userdtlsid=?,"
				+ "moid=?,"
				+ "dvno=?,"
				+ "purpose=? " 
				+ " WHERE chkid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt =1;
		
		ps.setInt(cnt++, chk.getStatus());
		ps.setString(cnt++, chk.getDateTrans());
		ps.setString(cnt++, chk.getCheckNo());
		ps.setDouble(cnt++, chk.getAmount());
		ps.setString(cnt++, chk.getIssueTo());
		ps.setLong(cnt++, chk.getSignatory1()==null? 0 : chk.getSignatory1().getId());
		ps.setLong(cnt++, chk.getSignatory2()==null? 0 : chk.getSignatory2().getId());
		ps.setInt(cnt++, chk.getAccounts().getId());
		ps.setLong(cnt++, chk.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, chk.getMooe().getId());
		ps.setString(cnt++, chk.getDvNo());
		ps.setString(cnt++, chk.getPurpose());
		ps.setLong(cnt++, chk.getId());
		
		ps.execute();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
		return chk;
	}
	
	public void updateData(){
		String sql = "UPDATE cheques SET "
				+ "status=?,"
				+ "datetrans=?,"
				+ "checkno=?,"
				+ "amount=?,"
				+ "issueto=?,"
				+ "empid1=?,"
				+ "empid2=?,"
				+ "bankid=?,"
				+ "userdtlsid=?,"
				+ "moid=?,"
				+ "dvno=?,"
				+ "purpose=? " 
				+ " WHERE chkid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt =1;
		
		ps.setInt(cnt++, getStatus());
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getCheckNo());
		ps.setDouble(cnt++, getAmount());
		ps.setString(cnt++, getIssueTo());
		ps.setLong(cnt++, getSignatory1()==null? 0 : getSignatory1().getId());
		ps.setLong(cnt++, getSignatory2()==null? 0 : getSignatory2().getId());
		ps.setInt(cnt++, getAccounts().getId());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getMooe().getId());
		ps.setString(cnt++, getDvNo());
		ps.setString(cnt++, getPurpose());
		ps.setLong(cnt++, getId());
		
		ps.execute();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT chkid FROM cheques  ORDER BY chkid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("chkid");
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
		ps = conn.prepareStatement("SELECT chkid FROM cheques WHERE chkid=?");
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
		}catch(SQLException s){
			s.getMessage();
		}
		
	}
	
	public void delete(){
		String sql = "update cheques set isactivechk=0, status=0 WHERE chkid=?";
		String params[] = new String[1];
		params[0] = getId()+"";
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
		}catch(SQLException s){
			s.getMessage();
		}
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getCheckNo() {
		return checkNo;
	}
	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}
	
	
	

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	public static void compileReport(Chequedtls reportFields){
		try{
		String REPORT_PATH = Bris.PRIMARY_DRIVE.getName() +  Bris.SEPERATOR.getName() + Bris.APP_FOLDER.getName() 
				 + Bris.SEPERATOR.getName() + Bris.REPORT.getName() + Bris.SEPERATOR.getName();
		String REPORT_NAME = ReadXML.value(ReportTag.CHECK);
		String JRXMLFILE= ReadXML.value(ReportTag.CHECK);
		
		System.out.println("CheckReport path: " + REPORT_PATH);
		HashMap paramMap = new HashMap();
		Chequedtls rpt = reportFields;
		ReportCompiler compiler = new ReportCompiler();
		System.out.println("REPORT_NAME: " +REPORT_NAME + " REPORT_PATH: " + REPORT_PATH);
		String jasperreportLocation = compiler.compileReport(JRXMLFILE, REPORT_NAME, REPORT_PATH);
		System.out.println("Check report path: " + jasperreportLocation);
		HashMap params = new HashMap();
		
		params.put("PARAM_ACCOUNT_NUMBER", rpt.getAccounts().getBankAccntNo()+"");
		params.put("PARAM_CHECK_NUMBER", rpt.getCheckNo());
		params.put("PARAM_DATE_DISBURSEMENT", DateUtils.convertDateToMonthDayYear(rpt.getDateTrans()));
		params.put("PARAM_BANK_NAME", rpt.getAccounts().getBankAccntBranch().toUpperCase());
		params.put("PARAM_ACCOUNT_NAME", rpt.getAccounts().getBankAccntName().toUpperCase());
		params.put("PARAM_AMOUNT", Currency.formatAmount(rpt.getAmount()));
		params.put("PARAM_PAYTOORDEROF", rpt.getIssueTo().toUpperCase());
		params.put("PARAM_AMOUNT_INWORDS", NumberToWords.changeToWords(rpt.getAmount()).toUpperCase());
		
		Employee sig1 = rpt.getSignatory1();
		Employee sig2 = rpt.getSignatory2();
		
		params.put("PARAM_SIGNATORY1", sig1.getFirstName().toUpperCase() + " " + sig1.getMiddleName().substring(0,1).toUpperCase()+". " + sig1.getLastName().toUpperCase());
		params.put("PARAM_SIGNATORY2", sig2.getFirstName().toUpperCase() + " " + sig2.getMiddleName().substring(0,1).toUpperCase()+". " + sig2.getLastName().toUpperCase());
		
		
		
		JasperPrint print = compiler.report(jasperreportLocation, params);
		File pdf = null;
		
		pdf = new File(REPORT_PATH+REPORT_NAME+".pdf");
		pdf.createNewFile();
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		System.out.println("pdf successfully created...");
		System.out.println("Creating a backup copy....");
		pdf = new File(REPORT_PATH+REPORT_NAME+"_copy"+".pdf");
		pdf.createNewFile();
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		
		
		/*String jrxmlFile = compiler.compileReport(JRXMLFILE, REPORT_NAME, REPORT_PATH);
		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, params);
		JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");*/
		
		System.out.println("Done creating a backup copy....");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		
	}
	
	private static void close(Closeable resource) {
	    if (resource != null) {
	        try {
	            resource.close();
	        } catch (IOException e) {
	            // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
	            // know that this will generally only be thrown when the client aborted the download.
	            e.printStackTrace();
	        }
	    }
}


	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDateTrans() {
		return dateTrans;
	}

	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}

	public String getIssueTo() {
		return issueTo;
	}

	public void setIssueTo(String issueTo) {
		this.issueTo = issueTo;
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

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setSignatory1(Employee signatory1) {
		this.signatory1 = signatory1;
	}

	public void setSignatory2(Employee signatory2) {
		this.signatory2 = signatory2;
	}

	public double getAmount() {
		return amount;
	}

	public Employee getSignatory1() {
		return signatory1;
	}

	public Employee getSignatory2() {
		return signatory2;
	}

	public String getCheckAmount() {
		return checkAmount;
	}

	public void setCheckAmount(String checkAmount) {
		this.checkAmount = checkAmount;
	}

	public MOOE getMooe() {
		return mooe;
	}

	public void setMooe(MOOE mooe) {
		this.mooe = mooe;
	}

	public String getDvNo() {
		return dvNo;
	}

	public void setDvNo(String dvNo) {
		this.dvNo = dvNo;
	}

	public static void main(String[] args) {
		
		/*Chequedtls chk = new Chequedtls();
		//chk.setCheque_id(4);
		chk.setAccntNumber("12345678");
		chk.setCheckNo("255476586");
		chk.setAccntName("TRUST FUND");
		chk.setBankName("BP Marbel");
		chk.setDate_disbursement(DateUtils.getCurrentDateMMMMDDYYYY());
		chk.setAmount("100.00");
		chk.setPayToTheOrderOf("Sodo");
		chk.setAmountInWOrds("One Hundred Pesos Only");
		chk.setProcessBy("Mark");
		chk.setSignatory1(1);
		chk.setSignatory2(2);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		chk.setDate_edited(dateFormat.format(date));
		chk.save();
		
		String sql = "SELECT * FROM tbl_chequedtls WHERE cheque_id=?";
		String[] params = new String[1];
		params[0] = "1";
		for(Chequedtls c : Chequedtls.retrieve(sql, params)){
			System.out.println(c.getBankName());
		}
		*/
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	
	
}
