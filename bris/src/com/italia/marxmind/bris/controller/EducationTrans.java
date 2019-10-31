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
 * @author mark italia
 * @since 10/16/2017
 * @version 1.0
 *
 */

public class EducationTrans {

	private long id;
	private String dateTrans;
	private int isActive;
	private int levelId;
	private int isPresentEducation;
	private Timestamp timestamp;
	
	private Education education;
	private Customer customer;
	private UserDtls userDtls;
	
	public static List<EducationTrans> retrieve(String sqlAdd, String[] params){
		List<EducationTrans> eds = new ArrayList<EducationTrans>();
		
		String tableTran = "trn";
		String tableEd = "ed";
		String tableCus = "cz";
		String tableUser = "usr";
		
		
		String sql = "SELECT * FROM educationtrans "+ tableTran +", educations " + tableEd  + ", customer " + tableCus + ", userdtls " + tableUser +" WHERE " + 
				tableTran + ".edid=" + tableEd + ".edid AND " +
				tableTran + ".customerid=" + tableCus + ".customerid AND " +
				tableTran + ".userdtlsid=" + tableUser + ".userdtlsid";
						
		
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
		
		//System.out.println("SQL Education : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			EducationTrans tran = new EducationTrans();
			try{tran.setId(rs.getLong("edtranid"));}catch(NullPointerException e){}
			try{tran.setDateTrans(rs.getString("eddatetrans"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isactiveedtran"));}catch(NullPointerException e){}
			try{tran.setLevelId(rs.getInt("levelid"));}catch(NullPointerException e){}
			try{tran.setIsPresentEducation(rs.getInt("ispresented"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestampedtran"));}catch(NullPointerException e){}
			
			Education ed = new Education();
			try{ed.setId(rs.getInt("edid"));}catch(NullPointerException e){}
			try{ed.setName(rs.getString("educationname"));}catch(NullPointerException e){}
			try{ed.setIsActive(rs.getInt("isactiveed"));}catch(NullPointerException e){}
			try{ed.setTimestamp(rs.getTimestamp("timestamped"));}catch(NullPointerException e){}
			tran.setEducation(ed);
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{cus.setCivilStatus(rs.getInt("civilstatus"));}catch(NullPointerException e){}
			try{cus.setPhotoid(rs.getString("photoid"));}catch(NullPointerException e){}
			
			if("1".equalsIgnoreCase(cus.getGender())){
				cus.setGenderName("Male");
			}else{
				cus.setGenderName("Female");
			}
			
			try{cus.setBirthdate(rs.getString("borndate"));}catch(NullPointerException e){}
			try{Customer emergency = new Customer();
			emergency.setCustomerid(rs.getLong("emeperson"));
			cus.setEmergencyContactPerson(emergency);}catch(NullPointerException e){}
			try{cus.setRelationship(rs.getInt("relid"));}catch(NullPointerException e){}
			
			Purok pur = new Purok();
			try{pur.setId(rs.getLong("purid"));}catch(NullPointerException e){}
			cus.setPurok(pur);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			tran.setCustomer(cus);
			
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
			
			eds.add(tran);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return eds;
	}
	
	public static EducationTrans retrieve(long id){
		EducationTrans tran = new EducationTrans();
		
		String tableTran = "trn";
		String tableEd = "ed";
		String tableCus = "cz";
		String tableUser = "usr";
		
		
		String sql = "SELECT * FROM educationtrans "+ tableTran +", educations " + tableEd  + ", customer " + tableCus + ", userdtls " + tableUser +" WHERE " + 
				tableTran + ".edid=" + tableEd + ".edid AND " +
				tableTran + ".customerid=" + tableCus + ".customerid AND " +
				tableTran + ".userdtlsid=" + tableUser + ".userdtlsid AND " + tableTran + ".isactiveedtran=1 AND " + tableTran + ".edtranid=" + id;
						
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{tran.setId(rs.getLong("edtranid"));}catch(NullPointerException e){}
			try{tran.setDateTrans(rs.getString("eddatetrans"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isactiveedtran"));}catch(NullPointerException e){}
			try{tran.setLevelId(rs.getInt("levelid"));}catch(NullPointerException e){}
			try{tran.setIsPresentEducation(rs.getInt("ispresented"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestampedtran"));}catch(NullPointerException e){}
			
			Education ed = new Education();
			try{ed.setId(rs.getInt("edid"));}catch(NullPointerException e){}
			try{ed.setName(rs.getString("educationname"));}catch(NullPointerException e){}
			try{ed.setIsActive(rs.getInt("isactiveed"));}catch(NullPointerException e){}
			try{ed.setTimestamp(rs.getTimestamp("timestamped"));}catch(NullPointerException e){}
			tran.setEducation(ed);
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{cus.setCivilStatus(rs.getInt("civilstatus"));}catch(NullPointerException e){}
			try{cus.setPhotoid(rs.getString("photoid"));}catch(NullPointerException e){}
			
			if("1".equalsIgnoreCase(cus.getGender())){
				cus.setGenderName("Male");
			}else{
				cus.setGenderName("Female");
			}
			
			try{cus.setBirthdate(rs.getString("borndate"));}catch(NullPointerException e){}
			try{Customer emergency = new Customer();
			emergency.setCustomerid(rs.getLong("emeperson"));
			cus.setEmergencyContactPerson(emergency);}catch(NullPointerException e){}
			try{cus.setRelationship(rs.getInt("relid"));}catch(NullPointerException e){}
			
			Purok pur = new Purok();
			try{pur.setId(rs.getLong("purid"));}catch(NullPointerException e){}
			cus.setPurok(pur);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			tran.setCustomer(cus);
			
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
	
	public static EducationTrans save(EducationTrans ed){
		if(ed!=null){
			
			long id = EducationTrans.getInfo(ed.getId() ==0? EducationTrans.getLatestId()+1 : ed.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ed = EducationTrans.insertData(ed, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ed = EducationTrans.updateData(ed);
			}else if(id==3){
				LogU.add("added new Data ");
				ed = EducationTrans.insertData(ed, "3");
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
	
	public static EducationTrans insertData(EducationTrans ed, String type){
		String sql = "INSERT INTO educationtrans ("
				+ "edtranid,"
				+ "eddatetrans,"
				+ "isactiveedtran,"
				+ "edid,"
				+ "levelid,"
				+ "ispresented,"
				+ "customerid,"
				+ "userdtlsid) " 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table educationtrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			ed.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			ed.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, ed.getDateTrans());
		ps.setInt(cnt++, ed.getIsActive());
		ps.setInt(cnt++, ed.getEducation().getId());
		ps.setInt(cnt++, ed.getLevelId());
		ps.setInt(cnt++, ed.getIsPresentEducation());
		ps.setLong(cnt++, ed.getCustomer().getCustomerid());
		ps.setLong(cnt++, ed.getUserDtls().getUserdtlsid());
		
		LogU.add(ed.getDateTrans());
		LogU.add(ed.getIsActive());
		LogU.add(ed.getEducation().getId());
		LogU.add(ed.getLevelId());
		LogU.add(ed.getIsPresentEducation());
		LogU.add(ed.getCustomer().getCustomerid());
		LogU.add(ed.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to educationtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ed;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO educationtrans ("
				+ "edtranid,"
				+ "eddatetrans,"
				+ "isactiveedtran,"
				+ "edid,"
				+ "levelid,"
				+ "ispresented,"
				+ "customerid,"
				+ "userdtlsid) " 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table educationtrans");
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
		ps.setInt(cnt++, getEducation().getId());
		ps.setInt(cnt++, getLevelId());
		ps.setInt(cnt++, getIsPresentEducation());
		ps.setLong(cnt++, getCustomer().getCustomerid());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getIsActive());
		LogU.add(getEducation().getId());
		LogU.add(getLevelId());
		LogU.add(getIsPresentEducation());
		LogU.add(getCustomer().getCustomerid());
		LogU.add(getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to educationtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static EducationTrans updateData(EducationTrans ed){
		String sql = "UPDATE educationtrans SET "
				+ "eddatetrans=?,"
				+ "edid=?,"
				+ "levelid=?,"
				+ "ispresented=?,"
				+ "customerid=?,"
				+ "userdtlsid=?" 
				+ " AND edtranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table educationtrans");
		
		ps.setString(cnt++, ed.getDateTrans());
		ps.setInt(cnt++, ed.getEducation().getId());
		ps.setInt(cnt++, ed.getLevelId());
		ps.setInt(cnt++, ed.getIsPresentEducation());
		ps.setLong(cnt++, ed.getCustomer().getCustomerid());
		ps.setLong(cnt++, ed.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, ed.getId());
		
		LogU.add(ed.getDateTrans());
		LogU.add(ed.getEducation().getId());
		LogU.add(ed.getLevelId());
		LogU.add(ed.getIsPresentEducation());
		LogU.add(ed.getCustomer().getCustomerid());
		LogU.add(ed.getUserDtls().getUserdtlsid());
		LogU.add(ed.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to educationtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ed;
	}
	
	public void updateData(){
		String sql = "UPDATE educationtrans SET "
				+ "eddatetrans=?,"
				+ "edid=?,"
				+ "levelid=?,"
				+ "ispresented=?,"
				+ "customerid=?,"
				+ "userdtlsid=?" 
				+ " AND edtranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table educationtrans");
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getEducation().getId());
		ps.setInt(cnt++, getLevelId());
		ps.setInt(cnt++, getIsPresentEducation());
		ps.setLong(cnt++, getCustomer().getCustomerid());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getEducation().getId());
		LogU.add(getLevelId());
		LogU.add(getIsPresentEducation());
		LogU.add(getCustomer().getCustomerid());
		LogU.add(getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to educationtrans : " + s.getMessage());
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
		sql="SELECT edtranid FROM educationtrans  ORDER BY edtranid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("edtranid");
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
		ps = conn.prepareStatement("SELECT edtranid FROM educationtrans WHERE edtranid=?");
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
		String sql = "UPDATE educationtrans set isactiveedtran=0 WHERE edtranid=?";
		
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
	public int getLevelId() {
		return levelId;
	}
	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}
	public int getIsPresentEducation() {
		return isPresentEducation;
	}
	public void setIsPresentEducation(int isPresentEducation) {
		this.isPresentEducation = isPresentEducation;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public Education getEducation() {
		return education;
	}
	public void setEducation(Education education) {
		this.education = education;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public UserDtls getUserDtls() {
		return userDtls;
	}

	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	
}
