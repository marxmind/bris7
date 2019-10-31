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
import com.italia.marxmind.bris.utils.DateUtils;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 03/06/2018
 * @version 1.0
 *
 */

public class Cases {

	private long id;
	private String date;
	private String caseNo;
	private String narratives;
	private int status;
	private int type;
	private int kind;
	private int isActive;
	private String otherCaseName;
	
	private String complainants;
	private String comAddress;
	private String respondents;
	private String resAddress;
	
	private UserDtls userDtls;
	
	private CaseFilling filling;
	private List<CaseFilling> caseFilling;
	
	private String statusName;
	private String kindName;
	private String typeName;
	
	private boolean endorsed;
	
	private String witnesses;
	private String witnessAddress;
	private String solutions;
	
	public static String getCaseNumber(){
		String sql = "SELECT caseno FROM cases WHERE caseisactive=1 ORDER BY caseno DESC limit 1";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String caseNo = DateUtils.getCurrentYear() + "-001";
		boolean isExisting = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			caseNo = rs.getString("caseno");
			isExisting = true;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		
		if(isExisting){
		
			String year = caseNo.split("-")[0];
			String num = caseNo.split("-")[1];
			
			if(year.equalsIgnoreCase(DateUtils.getCurrentYear()+"")){
				
				int count = Integer.valueOf(num);
				count +=1;
				String len = count+"";
				
				if(len.length()==1){
					caseNo = year +"-00" + count;
				}else if(len.length()==2){
					caseNo = year +"-0" + count;
				}else if(len.length()==3){
					caseNo = year +"-" + count;
				}
				
			}else{
				caseNo = DateUtils.getCurrentYear() + "-001";
			}
		
		}
		
		}catch(Exception e){e.getMessage();}	
			
		return caseNo;
	}
	
	public static List<Cases> retrieve(String sqlAdd, String[] params){
		List<Cases> czs = new ArrayList<>();
		
		String tableCase = "ciz";
		String tableUser = "usr";
		String sql = "SELECT * FROM cases " + tableCase + ", userdtls " + tableUser + " WHERE " +
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
			
			try{caz.setWitnesses(rs.getString("witnesses"));}catch(NullPointerException e){}
			try{caz.setWitnessAddress(rs.getString("witnessaddress"));}catch(NullPointerException e){}
			try{caz.setSolutions(rs.getString("filsolution"));}catch(NullPointerException e){}
			
			try{caz.setOtherCaseName(rs.getString("othername"));}catch(NullPointerException e){}
			
			try{caz.setStatusName(CaseStatus.typeName(rs.getInt("casestatus")));}catch(NullPointerException e){}
			
			if(CaseKind.OTHERS.getId()==caz.getKind()){
				try{caz.setKindName(rs.getString("othername"));}catch(NullPointerException e){}
			}else{
				try{caz.setKindName(CaseKind.typeName(rs.getInt("casekind")));}catch(NullPointerException e){}
			}
			
			try{caz.setTypeName(rs.getInt("casetype")==1? "INVITATION" : "SUMMON");}catch(NullPointerException e){}
			
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
			caz.setUserDtls(user);
			
			czs.add(caz);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return czs;
	}
	
	public static Cases save(Cases cz){
		if(cz!=null){
			
			long id = Cases.getInfo(cz.getId() ==0? Cases.getLatestId()+1 : cz.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				cz = Cases.insertData(cz, "1");
			}else if(id==2){
				LogU.add("update Data ");
				cz = Cases.updateData(cz);
			}else if(id==3){
				LogU.add("added new Data ");
				cz = Cases.insertData(cz, "3");
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
	
	public static Cases insertData(Cases cz, String type){
		String sql = "INSERT INTO cases ("
				+ "caseid,"
				+ "casedate,"
				+ "caseno,"
				+ "complainants,"
				+ "comaddress,"
				+ "respondents,"
				+ "resaddress,"
				+ "filcomplaints,"
				+ "casestatus,"
				+ "casetype,"
				+ "casekind,"
				+ "caseisactive,"
				+ "userdtlsid,"
				+ "othername,"
				+ "witnesses,"
				+ "witnessaddress,"
				+ "filsolution)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table cases");
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
		
		ps.setString(cnt++, cz.getDate());
		ps.setString(cnt++, cz.getCaseNo());
		ps.setString(cnt++, cz.getComplainants());
		ps.setString(cnt++, cz.getComAddress());
		ps.setString(cnt++, cz.getRespondents());
		ps.setString(cnt++, cz.getResAddress());
		ps.setString(cnt++, cz.getNarratives());
		ps.setInt(cnt++, cz.getStatus());
		ps.setInt(cnt++, cz.getType());
		ps.setInt(cnt++, cz.getKind());
		ps.setInt(cnt++, cz.getIsActive());
		ps.setLong(cnt++, cz.getUserDtls()==null? 0 : cz.getUserDtls().getUserdtlsid());
		ps.setString(cnt++, cz.getOtherCaseName());
		ps.setString(cnt++, cz.getWitnesses());
		ps.setString(cnt++,cz.getWitnessAddress());
		ps.setString(cnt++, cz.getSolutions());
		
		LogU.add(cz.getDate());
		LogU.add(cz.getCaseNo());
		LogU.add(cz.getComplainants());
		LogU.add(cz.getComAddress());
		LogU.add(cz.getRespondents());
		LogU.add(cz.getResAddress());
		LogU.add(cz.getNarratives());
		LogU.add(cz.getStatus());
		LogU.add(cz.getType());
		LogU.add(cz.getKind());
		LogU.add(cz.getIsActive());
		LogU.add(cz.getUserDtls()==null? 0 : cz.getUserDtls().getUserdtlsid());
		LogU.add(cz.getOtherCaseName());
		LogU.add(cz.getWitnesses());
		LogU.add(cz.getWitnessAddress());
		LogU.add(cz.getSolutions());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to cases : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cz;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO cases ("
				+ "caseid,"
				+ "casedate,"
				+ "caseno,"
				+ "complainants,"
				+ "comaddress,"
				+ "respondents,"
				+ "resaddress,"
				+ "filcomplaints,"
				+ "casestatus,"
				+ "casetype,"
				+ "casekind,"
				+ "caseisactive,"
				+ "userdtlsid,"
				+ "othername,"
				+ "witnesses,"
				+ "witnessaddress,"
				+ "filsolution)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table cases");
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
		
		ps.setString(cnt++, getDate());
		ps.setString(cnt++, getCaseNo());
		ps.setString(cnt++, getComplainants());
		ps.setString(cnt++, getComAddress());
		ps.setString(cnt++, getRespondents());
		ps.setString(cnt++, getResAddress());
		ps.setString(cnt++, getNarratives());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getType());
		ps.setInt(cnt++, getKind());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setString(cnt++, getOtherCaseName());
		ps.setString(cnt++, getWitnesses());
		ps.setString(cnt++,getWitnessAddress());
		ps.setString(cnt++, getSolutions());
		
		LogU.add(getDate());
		LogU.add(getCaseNo());
		LogU.add(getComplainants());
		LogU.add(getComAddress());
		LogU.add(getRespondents());
		LogU.add(getResAddress());
		LogU.add(getNarratives());
		LogU.add(getStatus());
		LogU.add(getType());
		LogU.add(getKind());
		LogU.add(getIsActive());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getOtherCaseName());
		LogU.add(getWitnesses());
		LogU.add(getWitnessAddress());
		LogU.add(getSolutions());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to cases : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Cases updateData(Cases cz){
		String sql = "UPDATE cases SET "
				+ "casedate=?,"
				+ "caseno=?,"
				+ "complainants=?,"
				+ "comaddress=?,"
				+ "respondents=?,"
				+ "resaddress=?,"
				+ "filcomplaints=?,"
				+ "casestatus=?,"
				+ "casetype=?,"
				+ "casekind=?,"
				+ "userdtlsid=?,"
				+ "othername=?,"
				+ "witnesses=?,"
				+ "witnessaddress=?,"
				+ "filsolution=?" 
				+ " WHERE caseid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table cases");
		
		ps.setString(cnt++, cz.getDate());
		ps.setString(cnt++, cz.getCaseNo());
		ps.setString(cnt++, cz.getComplainants());
		ps.setString(cnt++, cz.getComAddress());
		ps.setString(cnt++, cz.getRespondents());
		ps.setString(cnt++, cz.getResAddress());
		ps.setString(cnt++, cz.getNarratives());
		ps.setInt(cnt++, cz.getStatus());
		ps.setInt(cnt++, cz.getType());
		ps.setInt(cnt++, cz.getKind());
		ps.setLong(cnt++, cz.getUserDtls()==null? 0 : cz.getUserDtls().getUserdtlsid());
		ps.setString(cnt++, cz.getOtherCaseName());
		ps.setString(cnt++, cz.getWitnesses());
		ps.setString(cnt++,cz.getWitnessAddress());
		ps.setString(cnt++, cz.getSolutions());
		ps.setLong(cnt++, cz.getId());
		
		LogU.add(cz.getDate());
		LogU.add(cz.getCaseNo());
		LogU.add(cz.getComplainants());
		LogU.add(cz.getComAddress());
		LogU.add(cz.getRespondents());
		LogU.add(cz.getResAddress());
		LogU.add(cz.getNarratives());
		LogU.add(cz.getStatus());
		LogU.add(cz.getType());
		LogU.add(cz.getKind());
		LogU.add(cz.getUserDtls()==null? 0 : cz.getUserDtls().getUserdtlsid());
		LogU.add(cz.getOtherCaseName());
		LogU.add(cz.getWitnesses());
		LogU.add(cz.getWitnessAddress());
		LogU.add(cz.getSolutions());
		LogU.add(cz.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to cases : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cz;
	}
	
	public void updateData(){
		String sql = "UPDATE cases SET "
				+ "casedate=?,"
				+ "caseno=?,"
				+ "complainants=?,"
				+ "comaddress=?,"
				+ "respondents=?,"
				+ "resaddress=?,"
				+ "filcomplaints=?,"
				+ "casestatus=?,"
				+ "casetype=?,"
				+ "casekind=?,"
				+ "userdtlsid=?,"
				+ "othername=?,"
				+ "witnesses=?,"
				+ "witnessaddress=?,"
				+ "filsolution=?" 
				+ " WHERE caseid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table cases");
		
		ps.setString(cnt++, getDate());
		ps.setString(cnt++, getCaseNo());
		ps.setString(cnt++, getComplainants());
		ps.setString(cnt++, getComAddress());
		ps.setString(cnt++, getRespondents());
		ps.setString(cnt++, getResAddress());
		ps.setString(cnt++, getNarratives());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getType());
		ps.setInt(cnt++, getKind());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setString(cnt++, getOtherCaseName());
		ps.setString(cnt++, getWitnesses());
		ps.setString(cnt++, getWitnessAddress());
		ps.setString(cnt++, getSolutions());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDate());
		LogU.add(getCaseNo());
		LogU.add(getComplainants());
		LogU.add(getComAddress());
		LogU.add(getRespondents());
		LogU.add(getResAddress());
		LogU.add(getNarratives());
		LogU.add(getStatus());
		LogU.add(getType());
		LogU.add(getKind());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getOtherCaseName());
		LogU.add(getWitnesses());
		LogU.add(getWitnessAddress());
		LogU.add(getSolutions());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to cases : " + s.getMessage());
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
		sql="SELECT caseid FROM cases  ORDER BY caseid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("caseid");
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
		ps = conn.prepareStatement("SELECT caseid FROM cases WHERE caseid=?");
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
		String sql = "UPDATE cases set caseisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE caseid=?";
		
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getNarratives() {
		return narratives;
	}

	public void setNarratives(String narratives) {
		this.narratives = narratives;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
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

	public String getComplainants() {
		return complainants;
	}

	public void setComplainants(String complainants) {
		this.complainants = complainants;
	}

	public String getComAddress() {
		return comAddress;
	}

	public void setComAddress(String comAddress) {
		this.comAddress = comAddress;
	}

	public String getRespondents() {
		return respondents;
	}

	public void setRespondents(String respondents) {
		this.respondents = respondents;
	}

	public String getResAddress() {
		return resAddress;
	}

	public void setResAddress(String resAddress) {
		this.resAddress = resAddress;
	}

	public List<CaseFilling> getCaseFilling() {
		return caseFilling;
	}

	public void setCaseFilling(List<CaseFilling> caseFilling) {
		this.caseFilling = caseFilling;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getKindName() {
		return kindName;
	}

	public void setKindName(String kindName) {
		this.kindName = kindName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public boolean isEndorsed() {
		return endorsed;
	}

	public void setEndorsed(boolean endorsed) {
		this.endorsed = endorsed;
	}

	public String getOtherCaseName() {
		return otherCaseName;
	}

	public void setOtherCaseName(String otherCaseName) {
		this.otherCaseName = otherCaseName;
	}

	public CaseFilling getFilling() {
		return filling;
	}

	public void setFilling(CaseFilling filling) {
		this.filling = filling;
	}

	public String getWitnesses() {
		return witnesses;
	}

	public void setWitnesses(String witnesses) {
		this.witnesses = witnesses;
	}

	public String getWitnessAddress() {
		return witnessAddress;
	}

	public void setWitnessAddress(String witnessAddress) {
		this.witnessAddress = witnessAddress;
	}

	public String getSolutions() {
		return solutions;
	}

	public void setSolutions(String solutions) {
		this.solutions = solutions;
	}

	
	
}
