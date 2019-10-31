package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.enm.KPForms;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 03/06/2018
 * @version 1.0
 *
 */

public class CaseFilling {

	private long id;
	private String fillingDate;
	private String settlementDate;
	private String settlementTime;
	private String orNumber;
	private double fees;
	private double msgFee;
	private int count;
	private int isActive;
	private int formType;
	private Cases cases;
	private UserDtls userDtls;
	private Employee chaiman;
	private Employee secretary;
	
	private String style;
	private String formName;
	
	public static boolean checkConflictTime(String date, String time){
		
		String sql = "SELECT settlementdate,settlementtime FROM  casefilling WHERE filisactive=1 AND settlementdate=? AND settlementtime=? AND formtype=" + KPForms.KP_FORM9.getId();		
		String[] params = new String[2];
		params[0] = date;
		params[1] = time;
		
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
			return true;
		}
				
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
	public static boolean isConflictDateAndTime(String date, String time){
		
		String sql = "SELECT settlementdate,settlementtime FROM  casefilling WHERE filisactive=1 AND settlementdate=? AND settlementtime=? AND formtype=" + KPForms.KP_FORM9.getId();	
		String[] params = new String[2];
		params[0] = date;
		params[1] = time;
		
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
		int cnt = 0;
		while(rs.next()){
			cnt++;
		}
		
		if(cnt>1){
			return true;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
	public static List<CaseFilling> retrieve(String sqlAdd, String[] params){
		List<CaseFilling> fils = new ArrayList<>();
		
		String tableFil = "fil";
		String tableCase = "ciz";
		String tableUser = "usr";
		String sql = "SELECT * FROM casefilling " + tableFil + ", cases " + tableCase + ", userdtls " + tableUser + " WHERE " +
				tableFil + ".caseid=" + tableCase + ".caseid AND " +
				tableCase + ".userdtlsid=" + tableUser + ".userdtlsid ";
		
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
			
			CaseFilling fil = new CaseFilling();
			try{fil.setId(rs.getLong("filid"));}catch(NullPointerException e){}
			try{fil.setFillingDate(rs.getString("fildate"));}catch(NullPointerException e){}
			try{fil.setSettlementDate(rs.getString("settlementdate"));}catch(NullPointerException e){}
			try{fil.setSettlementTime(rs.getString("settlementtime"));}catch(NullPointerException e){}
			try{fil.setOrNumber(rs.getString("filorno"));}catch(NullPointerException e){}
			try{fil.setFees(rs.getDouble("filfee"));}catch(NullPointerException e){}
			try{fil.setMsgFee(rs.getDouble("filmessengerfee"));}catch(NullPointerException e){}
			try{fil.setCount(rs.getInt("filcount"));}catch(NullPointerException e){}
			try{fil.setIsActive(rs.getInt("filisactive"));}catch(NullPointerException e){}
			try{fil.setFormType(rs.getInt("formtype"));}catch(NullPointerException e){}
			try{fil.setFormName(KPForms.typeName(fil.getFormType()));}catch(NullPointerException e){}
			
			Employee lchairman = new Employee();
			try{lchairman.setId(rs.getLong("luponchairman"));}catch(NullPointerException e){}
			fil.setChaiman(lchairman);
			
			Employee lsec = new Employee();
			try{lsec.setId(rs.getLong("luponsec"));}catch(NullPointerException e){}
			fil.setSecretary(lsec);
			
			Cases caz = new Cases();
			try{caz.setId(rs.getLong("caseid"));}catch(NullPointerException e){}
			try{caz.setDate(rs.getString("casedate"));}catch(NullPointerException e){}
			try{caz.setCaseNo(rs.getString("caseno"));}catch(NullPointerException e){}
			try{caz.setNarratives(rs.getString("filcomplaints"));}catch(NullPointerException e){}
			try{caz.setStatus(rs.getInt("casestatus"));}catch(NullPointerException e){}
			try{caz.setType(rs.getInt("casetype"));}catch(NullPointerException e){}
			try{caz.setKind(rs.getInt("casekind"));}catch(NullPointerException e){}
			try{caz.setIsActive(rs.getInt("caseisactive"));}catch(NullPointerException e){}
			try{caz.setComplainants(rs.getString("complainants"));}catch(NullPointerException e){}
			try{caz.setComAddress(rs.getString("comaddress"));}catch(NullPointerException e){}
			try{caz.setRespondents(rs.getString("respondents"));}catch(NullPointerException e){}
			try{caz.setResAddress(rs.getString("resaddress"));}catch(NullPointerException e){}
			try{caz.setOtherCaseName(rs.getString("othername"));}catch(NullPointerException e){}
			try{caz.setWitnesses(rs.getString("witnesses"));}catch(NullPointerException e){}
			try{caz.setWitnessAddress(rs.getString("witnessaddress"));}catch(NullPointerException e){}
			try{caz.setSolutions(rs.getString("filsolution"));}catch(NullPointerException e){}
			fil.setCases(caz);
			
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
			fil.setUserDtls(user);
			
			fils.add(fil);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return fils;
	}
	
	public static CaseFilling save(CaseFilling cz){
		if(cz!=null){
			
			long id = CaseFilling.getInfo(cz.getId() ==0? CaseFilling.getLatestId()+1 : cz.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				cz = CaseFilling.insertData(cz, "1");
			}else if(id==2){
				LogU.add("update Data ");
				cz = CaseFilling.updateData(cz);
			}else if(id==3){
				LogU.add("added new Data ");
				cz = CaseFilling.insertData(cz, "3");
			}
			
		}
		return cz;
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
	
	public static CaseFilling insertData(CaseFilling cz, String type){
		String sql = "INSERT INTO casefilling ("
				+ "filid,"
				+ "fildate,"
				+ "settlementdate,"
				+ "settlementtime,"
				+ "filorno,"
				+ "filfee,"
				+ "filmessengerfee,"
				+ "caseid,"
				+ "filcount,"
				+ "filisactive,"
				+ "userdtlsid,"
				+ "formtype,"
				+ "luponchairman,"
				+ "luponsec)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table casefilling");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			cz.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			cz.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, cz.getFillingDate());
		ps.setString(cnt++, cz.getSettlementDate());
		ps.setString(cnt++, cz.getSettlementTime());
		ps.setString(cnt++, cz.getOrNumber());
		ps.setDouble(cnt++, cz.getFees());
		ps.setDouble(cnt++, cz.getMsgFee());
		ps.setLong(cnt++, cz.getCases()==null? 0 : cz.getCases().getId());
		ps.setInt(cnt++, cz.getCount());
		ps.setInt(cnt++, cz.getIsActive());
		ps.setLong(cnt++, cz.getUserDtls()==null? 0 : cz.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, cz.getFormType());
		ps.setLong(cnt++, cz.getChaiman().getId());
		ps.setLong(cnt++, cz.getSecretary().getId());
		
		LogU.add(cz.getFillingDate());
		LogU.add(cz.getSettlementDate());
		LogU.add(cz.getSettlementTime());
		LogU.add(cz.getOrNumber());
		LogU.add(cz.getFees());
		LogU.add(cz.getMsgFee());
		LogU.add(cz.getCases()==null? 0 : cz.getCases().getId());
		LogU.add(cz.getCount());
		LogU.add(cz.getIsActive());
		LogU.add(cz.getUserDtls()==null? 0 : cz.getUserDtls().getUserdtlsid());
		LogU.add(cz.getFormType());
		LogU.add(cz.getChaiman().getId());
		LogU.add(cz.getSecretary().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to casefilling : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cz;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO casefilling ("
				+ "filid,"
				+ "fildate,"
				+ "settlementdate,"
				+ "settlementtime,"
				+ "filorno,"
				+ "filfee,"
				+ "filmessengerfee,"
				+ "caseid,"
				+ "filcount,"
				+ "filisactive,"
				+ "userdtlsid,"
				+ "formtype,"
				+ "luponchairman,"
				+ "luponsec)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table casefilling");
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
		
		ps.setString(cnt++, getFillingDate());
		ps.setString(cnt++, getSettlementDate());
		ps.setString(cnt++, getSettlementTime());
		ps.setString(cnt++, getOrNumber());
		ps.setDouble(cnt++, getFees());
		ps.setDouble(cnt++, getMsgFee());
		ps.setLong(cnt++, getCases()==null? 0 : getCases().getId());
		ps.setInt(cnt++, getCount());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getFormType());
		ps.setLong(cnt++, getChaiman().getId());
		ps.setLong(cnt++, getSecretary().getId());
		
		LogU.add(getFillingDate());
		LogU.add(getSettlementDate());
		LogU.add(getSettlementTime());
		LogU.add(getOrNumber());
		LogU.add(getFees());
		LogU.add(getMsgFee());
		LogU.add(getCases()==null? 0 : getCases().getId());
		LogU.add(getCount());
		LogU.add(getIsActive());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getFormType());
		LogU.add(getChaiman().getId());
		LogU.add(getSecretary().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to casefilling : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static CaseFilling updateData(CaseFilling cz){
		String sql = "UPDATE casefilling SET "
				+ "fildate=?,"
				+ "settlementdate=?,"
				+ "settlementtime=?,"
				+ "filorno=?,"
				+ "filfee=?,"
				+ "filmessengerfee=?,"
				+ "caseid=?,"
				+ "filcount=?,"
				+ "userdtlsid=?,"
				+ "formtype=?,"
				+ "luponchairman=?,"
				+ "luponsec=?" 
				+ " WHERE filid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table casefilling");
		
		ps.setString(cnt++, cz.getFillingDate());
		ps.setString(cnt++, cz.getSettlementDate());
		ps.setString(cnt++, cz.getSettlementTime());
		ps.setString(cnt++, cz.getOrNumber());
		ps.setDouble(cnt++, cz.getFees());
		ps.setDouble(cnt++, cz.getMsgFee());
		ps.setLong(cnt++, cz.getCases()==null? 0 : cz.getCases().getId());
		ps.setInt(cnt++, cz.getCount());
		ps.setLong(cnt++, cz.getUserDtls()==null? 0 : cz.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, cz.getFormType());
		ps.setLong(cnt++, cz.getChaiman().getId());
		ps.setLong(cnt++, cz.getSecretary().getId());
		ps.setLong(cnt++, cz.getId());
		
		LogU.add(cz.getFillingDate());
		LogU.add(cz.getSettlementDate());
		LogU.add(cz.getSettlementTime());
		LogU.add(cz.getOrNumber());
		LogU.add(cz.getFees());
		LogU.add(cz.getMsgFee());
		LogU.add(cz.getCases()==null? 0 : cz.getCases().getId());
		LogU.add(cz.getCount());
		LogU.add(cz.getUserDtls()==null? 0 : cz.getUserDtls().getUserdtlsid());
		LogU.add(cz.getFormType());
		LogU.add(cz.getChaiman().getId());
		LogU.add(cz.getSecretary().getId());
		LogU.add(cz.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to casefilling : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cz;
	}
	
	public void updateData(){
		String sql = "UPDATE casefilling SET "
				+ "fildate=?,"
				+ "settlementdate=?,"
				+ "settlementtime=?,"
				+ "filorno=?,"
				+ "filfee=?,"
				+ "filmessengerfee=?,"
				+ "caseid=?,"
				+ "filcount=?,"
				+ "userdtlsid=?,"
				+ "formtype=?,"
				+ "luponchairman=?,"
				+ "luponsec=?" 
				+ " WHERE filid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table casefilling");
		
		ps.setString(cnt++, getFillingDate());
		ps.setString(cnt++, getSettlementDate());
		ps.setString(cnt++, getSettlementTime());
		ps.setString(cnt++, getOrNumber());
		ps.setDouble(cnt++, getFees());
		ps.setDouble(cnt++, getMsgFee());
		ps.setLong(cnt++, getCases()==null? 0 : getCases().getId());
		ps.setInt(cnt++, getCount());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getFormType());
		ps.setLong(cnt++, getChaiman().getId());
		ps.setLong(cnt++, getSecretary().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getFillingDate());
		LogU.add(getSettlementDate());
		LogU.add(getSettlementTime());
		LogU.add(getOrNumber());
		LogU.add(getFees());
		LogU.add(getMsgFee());
		LogU.add(getCases()==null? 0 : getCases().getId());
		LogU.add(getCount());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getFormType());
		LogU.add(getChaiman().getId());
		LogU.add(getSecretary().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to casefilling : " + s.getMessage());
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
		sql="SELECT filid FROM casefilling  ORDER BY filid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("filid");
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
		ps = conn.prepareStatement("SELECT filid FROM casefilling WHERE filid=?");
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
		String sql = "UPDATE casefilling set filisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE filid=?";
		
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
	public String getFillingDate() {
		return fillingDate;
	}
	public void setFillingDate(String fillingDate) {
		this.fillingDate = fillingDate;
	}
	public String getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}
	public String getSettlementTime() {
		return settlementTime;
	}
	public void setSettlementTime(String settlementTime) {
		this.settlementTime = settlementTime;
	}
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}
	public double getFees() {
		return fees;
	}
	public void setFees(double fees) {
		this.fees = fees;
	}
	public double getMsgFee() {
		return msgFee;
	}
	public void setMsgFee(double msgFee) {
		this.msgFee = msgFee;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Cases getCases() {
		return cases;
	}
	public void setCases(Cases cases) {
		this.cases = cases;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getFormType() {
		return formType;
	}

	public void setFormType(int formType) {
		this.formType = formType;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public Employee getChaiman() {
		return chaiman;
	}

	public void setChaiman(Employee chaiman) {
		this.chaiman = chaiman;
	}

	public Employee getSecretary() {
		return secretary;
	}

	public void setSecretary(Employee secretary) {
		this.secretary = secretary;
	}
}
