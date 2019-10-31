package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 03/24/2018
 * @version 1.0
 *
 */
public class MOOE {

	private long id;
	private String dateTrans;
	private int code;
	private int year;
	private String name;
	private double amount;
	private int isActive;
	private UserDtls userDtls;
	
	private String amountMo;
	private String usedBudget;
	private String remaining;
	private List<Chequedtls> cheques;
	private String percentage;
	private String percentPerMooe;
	private String percentBudgetUsed;
	
	public static int startYear(){
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT moyear FROM mooe WHERE moisactive=1 limit 1");
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getInt("moyear");
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return 0;
	}
	
	public static double mooeAmount(int year){
		
		double amount = 0;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT amountbudget FROM mooe WHERE moisactive=1 AND moyear="+year);
		
		//System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			amount += rs.getDouble("amountbudget");
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return amount;
	}
	
	public static double moeBudget(long moeid, int year){
		
		double amount = 0;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT amountbudget FROM mooe WHERE moisactive=1 AND moid="+ moeid +" AND moyear="+ year);
		
		//System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getDouble("amountbudget");
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return amount;
	}
	
	public static MOOE retrieveMOOE(String sql, String[] params){
		MOOE mo = new MOOE();
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
			try{mo.setId(rs.getLong("moid"));}catch(NullPointerException e){}
			try{mo.setDateTrans(rs.getString("modate"));}catch(NullPointerException e){}
			try{mo.setYear(rs.getInt("moyear"));}catch(NullPointerException e){}
			try{mo.setCode(rs.getInt("mocode"));}catch(NullPointerException e){}
			try{mo.setName(rs.getString("moname"));}catch(NullPointerException e){}
			try{mo.setAmount(rs.getDouble("amountbudget"));}catch(NullPointerException e){}
			try{mo.setIsActive(rs.getInt("moisactive"));}catch(NullPointerException e){}
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mo;
	}
	
	public static List<MOOE> retrieve(String sqlAdd, String[] params){
		List<MOOE> mos = new ArrayList<>();
		
		String tableMO = "mo";
		String tableUser = "usr";
		String sql = "SELECT * FROM mooe " + tableMO + ", userdtls " + tableUser + " WHERE " +
				tableMO + ".userdtlsid=" + tableUser + ".userdtlsid ";
		
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
		
		//System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			MOOE mo = new MOOE();
			try{mo.setId(rs.getLong("moid"));}catch(NullPointerException e){}
			try{mo.setDateTrans(rs.getString("modate"));}catch(NullPointerException e){}
			try{mo.setYear(rs.getInt("moyear"));}catch(NullPointerException e){}
			try{mo.setCode(rs.getInt("mocode"));}catch(NullPointerException e){}
			try{mo.setName(rs.getString("moname"));}catch(NullPointerException e){}
			try{mo.setAmount(rs.getDouble("amountbudget"));}catch(NullPointerException e){}
			try{mo.setIsActive(rs.getInt("moisactive"));}catch(NullPointerException e){}
			
			try{mo.setAmountMo(Currency.formatAmount(rs.getDouble("amountbudget")));}catch(NullPointerException e){}
			
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
			mo.setUserDtls(user);
			
			mos.add(mo);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mos;
	}
	
	public static MOOE save(MOOE mo){
		if(mo!=null){
			
			long id = MOOE.getInfo(mo.getId() ==0? MOOE.getLatestId()+1 : mo.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				mo = MOOE.insertData(mo, "1");
			}else if(id==2){
				LogU.add("update Data ");
				mo = MOOE.updateData(mo);
			}else if(id==3){
				LogU.add("added new Data ");
				mo = MOOE.insertData(mo, "3");
			}
			
		}
		return mo;
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
	
	public static MOOE insertData(MOOE mo, String type){
		String sql = "INSERT INTO mooe ("
				+ "moid,"
				+ "modate,"
				+ "moyear,"
				+ "mocode,"
				+ "moname,"
				+ "amountbudget,"
				+ "moisactive,"
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
		LogU.add("inserting data into table mooe");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			mo.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			mo.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, mo.getDateTrans());
		ps.setInt(cnt++, mo.getYear());
		ps.setInt(cnt++, mo.getCode());
		ps.setString(cnt++, mo.getName());
		ps.setDouble(cnt++, mo.getAmount());
		ps.setInt(cnt++, mo.getIsActive());
		ps.setLong(cnt++, mo.getUserDtls()==null? 0 : mo.getUserDtls().getUserdtlsid());
		
		LogU.add(mo.getDateTrans());
		LogU.add(mo.getYear());
		LogU.add(mo.getCode());
		LogU.add(mo.getName());
		LogU.add(mo.getAmount());
		LogU.add(mo.getIsActive());
		LogU.add(mo.getUserDtls()==null? 0 : mo.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to mooe : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mo;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO mooe ("
				+ "moid,"
				+ "modate,"
				+ "moyear,"
				+ "mocode,"
				+ "moname,"
				+ "amountbudget,"
				+ "moisactive,"
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
		LogU.add("inserting data into table mooe");
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
		ps.setInt(cnt++, getCode());
		ps.setString(cnt++, getName());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getYear());
		LogU.add(getCode());
		LogU.add(getName());
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
			LogU.add("error inserting data to mooe : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static MOOE updateData(MOOE mo){
		String sql = "UPDATE mooe SET "
				+ "modate=?,"
				+ "moyear=?,"
				+ "mocode=?,"
				+ "moname=?,"
				+ "amountbudget=?,"
				+ "userdtlsid=?" 
				+ " WHERE moid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table mooe");
		
		ps.setString(cnt++, mo.getDateTrans());
		ps.setInt(cnt++, mo.getYear());
		ps.setInt(cnt++, mo.getCode());
		ps.setString(cnt++, mo.getName());
		ps.setDouble(cnt++, mo.getAmount());
		ps.setLong(cnt++, mo.getUserDtls()==null? 0 : mo.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, mo.getId());
		
		LogU.add(mo.getDateTrans());
		LogU.add(mo.getYear());
		LogU.add(mo.getCode());
		LogU.add(mo.getName());
		LogU.add(mo.getAmount());
		LogU.add(mo.getUserDtls()==null? 0 : mo.getUserDtls().getUserdtlsid());
		LogU.add(mo.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to mooe : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mo;
	}
	
	public void updateData(){
		String sql = "UPDATE mooe SET "
				+ "modate=?,"
				+ "moyear=?,"
				+ "mocode=?,"
				+ "moname=?,"
				+ "amountbudget=?,"
				+ "userdtlsid=?" 
				+ " WHERE moid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table mooe");
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getYear());
		ps.setInt(cnt++, getCode());
		ps.setString(cnt++, getName());
		ps.setDouble(cnt++, getAmount());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getYear());
		LogU.add(getCode());
		LogU.add(getName());
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
			LogU.add("error updating data to mooe : " + s.getMessage());
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
		sql="SELECT moid FROM mooe  ORDER BY moid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("moid");
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
		ps = conn.prepareStatement("SELECT moid FROM mooe WHERE moid=?");
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
		String sql = "UPDATE mooe set moisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE moid=?";
		
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
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public String getAmountMo() {
		return amountMo;
	}

	public void setAmountMo(String amountMo) {
		this.amountMo = amountMo;
	}

	public List<Chequedtls> getCheques() {
		return cheques;
	}

	public void setCheques(List<Chequedtls> cheques) {
		this.cheques = cheques;
	}

	public String getUsedBudget() {
		return usedBudget;
	}

	public void setUsedBudget(String usedBudget) {
		this.usedBudget = usedBudget;
	}

	public String getRemaining() {
		return remaining;
	}

	public void setRemaining(String remaining) {
		this.remaining = remaining;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getPercentPerMooe() {
		return percentPerMooe;
	}

	public void setPercentPerMooe(String percentPerMooe) {
		this.percentPerMooe = percentPerMooe;
	}

	public String getPercentBudgetUsed() {
		return percentBudgetUsed;
	}

	public void setPercentBudgetUsed(String percentBudgetUsed) {
		this.percentBudgetUsed = percentBudgetUsed;
	}
	
}
