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
 * @since 09/21/2017
 * @version 1.0
 *
 */
public class AccessAllowed {

	private int id;
	private UserDtls userDtls;
	private Features features;
	private int isActiveAccess;
	private Timestamp timestamp;
	
	public static List<AccessAllowed> retrieve(String sqlAdd, String[] params){
		List<AccessAllowed> acs = new ArrayList<AccessAllowed>();
		
		String tableAc = "ac";
		String tableUser = "usr";
		String tableFeat = "fet";
		
		String sql = "SELECT * FROM accessallowed " + tableAc + ", userdtls " + tableUser + ", features " + tableFeat + " WHERE " +
				tableAc + ".userdtlsid="+ tableUser + ".userdtlsid AND " +
				tableAc + ".fid=" + tableFeat + ".fid ";
		
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
		
		//System.out.println("AccessAllowed SQL : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			AccessAllowed ac = new AccessAllowed();
			try{ac.setId(rs.getInt("accid"));}catch(NullPointerException e){}
			try{ac.setIsActiveAccess(rs.getInt("isactiveaccess"));}catch(NullPointerException e){}
			try{ac.setTimestamp(rs.getTimestamp("timestampacc"));}catch(NullPointerException e){}
			
			Features fet = new Features();
			try{fet.setId(rs.getInt("fid"));}catch(NullPointerException e){}
			try{fet.setModuleName(rs.getString("modulename"));}catch(NullPointerException e){}
			try{fet.setIsActive(rs.getInt("isActive"));}catch(NullPointerException e){}
			try{fet.setMonthExpiration(rs.getString("monthexp"));}catch(NullPointerException e){}
			try{fet.setActivationCode(rs.getString("activationcode"));}catch(NullPointerException e){}
			ac.setFeatures(fet);
			
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
			ac.setUserDtls(user);
			
			acs.add(ac);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return acs;
	}
	
	public static AccessAllowed save(AccessAllowed rep){
		if(rep!=null){
			
			long id = AccessAllowed.getInfo(rep.getId() ==0? AccessAllowed.getLatestId()+1 : rep.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				rep = AccessAllowed.insertData(rep, "1");
			}else if(id==2){
				LogU.add("update Data ");
				rep = AccessAllowed.updateData(rep);
			}else if(id==3){
				LogU.add("added new Data ");
				rep = AccessAllowed.insertData(rep, "3");
			}
			
		}
		return rep;
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
	
	public static AccessAllowed insertData(AccessAllowed rep, String type){
		String sql = "INSERT INTO accessallowed ("
				+ "accid,"
				+ "isactiveaccess,"
				+ "userdtlsid,"
				+ "fid)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table accessallowed");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			rep.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			rep.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setInt(cnt++, rep.getIsActiveAccess());
		ps.setLong(cnt++, rep.getUserDtls()==null? 0 : rep.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, rep.getFeatures()==null? 0 : rep.getFeatures().getId());
		
		
		LogU.add(rep.getIsActiveAccess());
		LogU.add(rep.getUserDtls()==null? 0 : rep.getUserDtls().getUserdtlsid());
		LogU.add(rep.getFeatures()==null? 0 : rep.getFeatures().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to accessallowed : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rep;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO accessallowed ("
				+ "accid,"
				+ "isactiveaccess,"
				+ "userdtlsid,"
				+ "fid)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table accessallowed");
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
		
		ps.setInt(cnt++, getIsActiveAccess());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getFeatures()==null? 0 : getFeatures().getId());
		
		
		LogU.add(getIsActiveAccess());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getFeatures()==null? 0 : getFeatures().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to accessallowed : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static AccessAllowed updateData(AccessAllowed rep){
		String sql = "UPDATE accessallowed SET "
				+ "isactiveaccess=?,"
				+ "userdtlsid=?,"
				+ "fid=?" 
				+ " WHERE accid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table accessallowed");
		
		ps.setInt(cnt++, rep.getIsActiveAccess());
		ps.setLong(cnt++, rep.getUserDtls()==null? 0 : rep.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, rep.getFeatures()==null? 0 : rep.getFeatures().getId());
		ps.setInt(cnt++, rep.getId());
		
		LogU.add(rep.getIsActiveAccess());
		LogU.add(rep.getUserDtls()==null? 0 : rep.getUserDtls().getUserdtlsid());
		LogU.add(rep.getFeatures()==null? 0 : rep.getFeatures().getId());
		LogU.add(rep.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to accessallowed : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rep;
	}
	
	public void updateData(){
		String sql = "UPDATE accessallowed SET "
				+ "isactiveaccess=?,"
				+ "userdtlsid=?,"
				+ "fid=?" 
				+ " WHERE accid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table accessallowed");
		
		ps.setInt(cnt++, getIsActiveAccess());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getFeatures()==null? 0 : getFeatures().getId());
		ps.setInt(cnt++, getId());
		
		LogU.add(getIsActiveAccess());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getFeatures()==null? 0 : getFeatures().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to accessallowed : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT accid FROM accessallowed  ORDER BY accid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("accid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static int getInfo(int id){
		boolean isNotNull=false;
		int result =0;
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
		ps = conn.prepareStatement("SELECT accid FROM accessallowed WHERE accid=?");
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
		String sql = "UPDATE accessallowed set isactiveaccess=0 WHERE accid=?";
		
		
		String[] params = new String[1];
		params[0] =getId()+"";
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
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public Features getFeatures() {
		return features;
	}
	public void setFeatures(Features features) {
		this.features = features;
	}
	public int getIsActiveAccess() {
		return isActiveAccess;
	}
	public void setIsActiveAccess(int isActiveAccess) {
		this.isActiveAccess = isActiveAccess;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
