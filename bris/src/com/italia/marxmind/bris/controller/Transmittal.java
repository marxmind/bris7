package com.italia.marxmind.bris.controller;
/**
 * 
 * @author Mark Italia
 * @since 08/02/2018
 * @version 1.0
 *
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.utils.LogU;

public class Transmittal {
	
	private long id;
	private String dateTrans;
	private int isActive;
	private Employee captain;
	private Employee treasurer;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	private String checkNos;
	private String rcdsContent;
	private String rptsContent;
	
	private double totalAmount;
	
	public static List<Transmittal> retrieve(String sqlAdd, String[] params){
		List<Transmittal> trans = new ArrayList<Transmittal>();
		
		String tableTrans = "tran";
		String tableUser = "usr";
		String sql = "SELECT * FROM transmittal " + tableTrans + ", userdtls " + tableUser  + " WHERE " + 
				tableTrans + ".userdtlsid=" + tableUser + ".userdtlsid AND " + tableTrans + ".transisactive=1 ";
		
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
		
		System.out.println("SQL: " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Transmittal tran = new Transmittal();
			tran.setId(rs.getLong("transid"));
			tran.setDateTrans(rs.getString("transdate"));
			tran.setIsActive(rs.getInt("transisactive"));
			tran.setCheckNos(rs.getString("checknos"));
			tran.setRcdsContent(rs.getString("rcdscontent"));
			tran.setRptsContent(rs.getString("rptscontent"));
			tran.setTotalAmount(rs.getDouble("totalamount"));
			
			Employee cap = new Employee();
			cap = Employee.retrieve(rs.getLong("captain"));
			tran.setCaptain(cap);
			
			Employee treas = new Employee();
			treas = Employee.retrieve(rs.getLong("treas"));
			tran.setTreasurer(treas);
			
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
			tran.setUserDtls(user);
			
			trans.add(tran);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static Transmittal retrieve(long id){
		
		Transmittal tran = new Transmittal();
		String tableTrans = "tran";
		String tableUser = "usr";
		String sql = "SELECT * FROM transmittal " + tableTrans + ", userdtls " + tableUser  + " WHERE " + 
				tableTrans + ".userdtlsid=" + tableUser + ".userdtlsid AND " + tableTrans + ".transid=1 AND " + tableTrans + ".transid=" + id;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			tran.setId(rs.getLong("transid"));
			tran.setDateTrans(rs.getString("transdate"));
			tran.setIsActive(rs.getInt("transisactive"));
			tran.setCheckNos(rs.getString("checknos"));
			tran.setRcdsContent(rs.getString("rcdscontent"));
			tran.setRptsContent(rs.getString("rptscontent"));
			tran.setTotalAmount(rs.getDouble("totalamount"));
			
			Employee cap = new Employee();
			cap = Employee.retrieve(rs.getLong("captain"));
			tran.setCaptain(cap);
			
			Employee treas = new Employee();
			treas = Employee.retrieve(rs.getLong("treas"));
			tran.setTreasurer(treas);
			
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
			tran.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return tran;
	}
	
	public static Transmittal save(Transmittal tr){
		if(tr!=null){
			
			long id = Transmittal.getInfo(tr.getId() ==0? Transmittal.getLatestId()+1 : tr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				tr = Transmittal.insertData(tr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				tr = Transmittal.updateData(tr);
			}else if(id==3){
				LogU.add("added new Data ");
				tr = Transmittal.insertData(tr, "3");
			}
			
		}
		return tr;
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
	
	public static Transmittal insertData(Transmittal bnk, String type){
		String sql = "INSERT INTO transmittal ("
				+ "transid,"
				+ "transdate,"
				+ "transisactive,"
				+ "captain,"
				+ "treas,"
				+ "userdtlsid,"
				+ "checknos,"
				+ "rcdscontent,"
				+ "rptscontent,"
				+ "totalamount) " 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transmittal");
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
		ps.setInt(cnt++, bnk.getIsActive());
		ps.setLong(cnt++, bnk.getCaptain()==null? 0 : bnk.getCaptain().getId());
		ps.setLong(cnt++, bnk.getTreasurer()==null? 0 : bnk.getTreasurer().getId());
		ps.setLong(cnt++, bnk.getUserDtls()==null? 0 : bnk.getUserDtls().getUserdtlsid());
		ps.setString(cnt++, bnk.getCheckNos());
		ps.setString(cnt++, bnk.getRcdsContent());
		ps.setString(cnt++, bnk.getRptsContent());
		ps.setDouble(cnt++, bnk.getTotalAmount());
		
		LogU.add(bnk.getDateTrans());
		LogU.add(bnk.getIsActive());
		LogU.add(bnk.getCaptain()==null? 0 : bnk.getCaptain().getId());
		LogU.add(bnk.getTreasurer()==null? 0 : bnk.getTreasurer().getId());
		LogU.add(bnk.getUserDtls()==null? 0 : bnk.getUserDtls().getUserdtlsid());
		LogU.add(bnk.getCheckNos());
		LogU.add(bnk.getRcdsContent());
		LogU.add(bnk.getRptsContent());
		LogU.add(bnk.getTotalAmount());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transmittal : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return bnk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO transmittal ("
				+ "transid,"
				+ "transdate,"
				+ "transisactive,"
				+ "captain,"
				+ "treas,"
				+ "userdtlsid,"
				+ "checknos,"
				+ "rcdscontent,"
				+ "rptscontent,"
				+ "totalamount) " 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transmittal");
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
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getCaptain()==null? 0 : getCaptain().getId());
		ps.setLong(cnt++, getTreasurer()==null? 0 : getTreasurer().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setString(cnt++, getCheckNos());
		ps.setString(cnt++, getRcdsContent());
		ps.setString(cnt++, getRptsContent());
		ps.setDouble(cnt++, getTotalAmount());
		
		LogU.add(getDateTrans());
		LogU.add(getIsActive());
		LogU.add(getCaptain()==null? 0 : getCaptain().getId());
		LogU.add(getTreasurer()==null? 0 : getTreasurer().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getCheckNos());
		LogU.add(getRcdsContent());
		LogU.add(getRptsContent());
		LogU.add(getTotalAmount());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transmittal : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Transmittal updateData(Transmittal bnk){
		String sql = "UPDATE transmittal SET "
				+ "transdate=?,"
				+ "captain=?,"
				+ "treas=?,"
				+ "userdtlsid=?,"
				+ "checknos=?,"
				+ "rcdscontent=?,"
				+ "rptscontent=?,"
				+ "totalamount=? " 
				+ " WHERE transid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table transmittal");
		
		ps.setString(cnt++, bnk.getDateTrans());
		ps.setLong(cnt++, bnk.getCaptain()==null? 0 : bnk.getCaptain().getId());
		ps.setLong(cnt++, bnk.getTreasurer()==null? 0 : bnk.getTreasurer().getId());
		ps.setLong(cnt++, bnk.getUserDtls()==null? 0 : bnk.getUserDtls().getUserdtlsid());
		ps.setString(cnt++, bnk.getCheckNos());
		ps.setString(cnt++, bnk.getRcdsContent());
		ps.setString(cnt++, bnk.getRptsContent());
		ps.setDouble(cnt++, bnk.getTotalAmount());
		ps.setLong(cnt++, bnk.getId());
		
		LogU.add(bnk.getDateTrans());
		LogU.add(bnk.getCaptain()==null? 0 : bnk.getCaptain().getId());
		LogU.add(bnk.getTreasurer()==null? 0 : bnk.getTreasurer().getId());
		LogU.add(bnk.getUserDtls()==null? 0 : bnk.getUserDtls().getUserdtlsid());
		LogU.add(bnk.getCheckNos());
		LogU.add(bnk.getRcdsContent());
		LogU.add(bnk.getRptsContent());
		LogU.add(bnk.getTotalAmount());
		LogU.add(bnk.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to transmittal : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return bnk;
	}
	
	public void updateData(){
		String sql = "UPDATE transmittal SET "
				+ "transdate=?,"
				+ "captain=?,"
				+ "treas=?,"
				+ "userdtlsid=?,"
				+ "checknos=?,"
				+ "rcdscontent=?,"
				+ "rptscontent=?,"
				+ "totalamount=? " 
				+ " WHERE transid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table transmittal");
		
		ps.setString(cnt++, getDateTrans());
		ps.setLong(cnt++, getCaptain()==null? 0 : getCaptain().getId());
		ps.setLong(cnt++, getTreasurer()==null? 0 : getTreasurer().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setString(cnt++, getCheckNos());
		ps.setString(cnt++, getRcdsContent());
		ps.setString(cnt++, getRptsContent());
		ps.setDouble(cnt++, getTotalAmount());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getCaptain()==null? 0 : getCaptain().getId());
		LogU.add(getTreasurer()==null? 0 : getTreasurer().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getCheckNos());
		LogU.add(getRcdsContent());
		LogU.add(getRptsContent());
		LogU.add(getTotalAmount());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to transmittal : " + s.getMessage());
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
		sql="SELECT transid FROM transmittal  ORDER BY transid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("transid");
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
		ps = conn.prepareStatement("SELECT transid FROM transmittal WHERE transid=?");
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
		String sql = "UPDATE transmittal set transisactive=0 WHERE transid=?";
		
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
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Employee getCaptain() {
		return captain;
	}
	public void setCaptain(Employee captain) {
		this.captain = captain;
	}
	public Employee getTreasurer() {
		return treasurer;
	}
	public void setTreasurer(Employee treasurer) {
		this.treasurer = treasurer;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getCheckNos() {
		return checkNos;
	}

	public String getRcdsContent() {
		return rcdsContent;
	}

	public String getRptsContent() {
		return rptsContent;
	}

	public void setCheckNos(String checkNos) {
		this.checkNos = checkNos;
	}

	public void setRcdsContent(String rcdsContent) {
		this.rcdsContent = rcdsContent;
	}

	public void setRptsContent(String rptsContent) {
		this.rptsContent = rptsContent;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

}
