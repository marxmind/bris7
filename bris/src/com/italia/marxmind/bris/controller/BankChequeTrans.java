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
 * @author Mark Italia
 * @since 12/14/2017
 * @version 1.0
 *
 */
public class BankChequeTrans {

	private long id;
	private BankChequeRpt chequeRpt;
	private Chequedtls chequedtls;
	private int isAcative;
	
	public static List<BankChequeTrans> retrieve(String sqlAdd, String[] params){
		List<BankChequeTrans> trans = new ArrayList<BankChequeTrans>();
		
		String tableTran = "tran";
		String tableBank = "bnk";
		String tableChk = "chk";
		
		String sql = "SELECT * FROM bankchequetrans " + tableTran + ", bankchequerpt " + tableBank + ", cheques " + tableChk  + " WHERE " + 
				tableTran + ".bankchkid=" + tableBank + ".bankchkid AND " +
				tableTran +".chkid="+tableChk + ".chkid";
		
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
			
			BankChequeTrans tran = new BankChequeTrans();
			try{tran.setId(rs.getLong("btid"));}catch(NullPointerException e){}
			try{tran.setIsAcative(rs.getInt("bankisactivetrans"));}catch(NullPointerException e){}
			
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
			tran.setChequeRpt(bnk);
			
			Chequedtls chk = new Chequedtls();
			try{chk.setId(rs.getLong("chkid"));}catch(NullPointerException e){}
			try{chk.setDateTrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{chk.setStatus(rs.getInt("status"));}catch(NullPointerException e){}
			try{chk.setCheckNo(rs.getString("checkno"));}catch(NullPointerException e){}
			try{chk.setAmount(rs.getDouble("amount"));}catch(NullPointerException e){}
			try{chk.setIssueTo(rs.getString("issueto"));}catch(NullPointerException e){}
			try{chk.setIsActive(rs.getInt("isactivechk"));}catch(NullPointerException e){}
			try{chk.setCheckAmount(Currency.formatAmount(rs.getDouble("amount")));}catch(NullPointerException e){}
			try{chk.setDvNo(rs.getString("dvno"));}catch(NullPointerException e){}
			tran.setChequedtls(chk);
			
			trans.add(tran);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static BankChequeTrans retrieve(long id){
		BankChequeTrans tran = new BankChequeTrans();
		
		String tableTran = "tran";
		String tableBank = "bnk";
		String tableChk = "chk";
		
		String sql = "SELECT * FROM bankchequetrans " + tableTran + ", bankchequerpt " + tableBank + ", cheques " + tableChk  + " WHERE " + 
				tableTran + ".bankchkid=" + tableBank + ".bankchkid AND " +
				tableTran +".chkid="+tableChk + ".chkid AND " + tableTran + ".btid=" + id + " AND " + tableTran +".bankisactivetrans=1";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{tran.setId(rs.getLong("btid"));}catch(NullPointerException e){}
			try{tran.setIsAcative(rs.getInt("bankisactivetrans"));}catch(NullPointerException e){}
			
			BankChequeRpt bnk = new BankChequeRpt();
			try{bnk.setId(rs.getLong("bankchkid"));}catch(NullPointerException e){}
			try{bnk.setDateTrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{bnk.setDateApplying(rs.getString("dateapplying"));}catch(NullPointerException e){}
			try{bnk.setCtaxNo(rs.getString("ctaxno"));}catch(NullPointerException e){}
			try{bnk.setCtaxIssuedAt(rs.getString("ctaxissuedat"));}catch(NullPointerException e){}
			try{bnk.setCtaxIssuedDate(rs.getString("ctaxissueddate"));}catch(NullPointerException e){}
			try{bnk.setIsActive(rs.getInt("bankrptisactive"));}catch(NullPointerException e){}
			tran.setChequeRpt(bnk);
			
			Chequedtls chk = new Chequedtls();
			try{chk.setId(rs.getLong("chkid"));}catch(NullPointerException e){}
			try{chk.setDateTrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{chk.setStatus(rs.getInt("status"));}catch(NullPointerException e){}
			try{chk.setCheckNo(rs.getString("checkno"));}catch(NullPointerException e){}
			try{chk.setAmount(rs.getDouble("amount"));}catch(NullPointerException e){}
			try{chk.setIssueTo(rs.getString("issueto"));}catch(NullPointerException e){}
			try{chk.setIsActive(rs.getInt("isactivechk"));}catch(NullPointerException e){}
			try{chk.setCheckAmount(Currency.formatAmount(rs.getDouble("amount")));}catch(NullPointerException e){}
			tran.setChequedtls(chk);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return tran;
	}
	
	public static BankChequeTrans save(BankChequeTrans ed){
		if(ed!=null){
			
			long id = BankChequeTrans.getInfo(ed.getId() ==0? BankChequeTrans.getLatestId()+1 : ed.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ed = BankChequeTrans.insertData(ed, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ed = BankChequeTrans.updateData(ed);
			}else if(id==3){
				LogU.add("added new Data ");
				ed = BankChequeTrans.insertData(ed, "3");
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
	
	public static BankChequeTrans insertData(BankChequeTrans bnk, String type){
		String sql = "INSERT INTO bankchequetrans ("
				+ "btid,"
				+ "bankchkid,"
				+ "chkid,"
				+ "bankisactivetrans) " 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table bankchequetrans");
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
		
		ps.setLong(cnt++, bnk.getChequeRpt()==null? 0 : bnk.getChequeRpt().getId());
		ps.setLong(cnt++, bnk.getChequedtls()==null? 0 : bnk.getChequedtls().getId());
		ps.setInt(cnt++, bnk.getIsAcative());
		
		LogU.add(bnk.getChequeRpt()==null? 0 : bnk.getChequeRpt().getId());
		LogU.add(bnk.getChequedtls()==null? 0 : bnk.getChequedtls().getId());
		LogU.add(bnk.getIsAcative());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to bankchequetrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return bnk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO bankchequetrans ("
				+ "btid,"
				+ "bankchkid,"
				+ "chkid,"
				+ "bankisactivetrans) " 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table bankchequetrans");
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
		
		ps.setLong(cnt++, getChequeRpt()==null? 0 : getChequeRpt().getId());
		ps.setLong(cnt++, getChequedtls()==null? 0 : getChequedtls().getId());
		ps.setInt(cnt++, getIsAcative());
		
		LogU.add(getChequeRpt()==null? 0 : getChequeRpt().getId());
		LogU.add(getChequedtls()==null? 0 : getChequedtls().getId());
		LogU.add(getIsAcative());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to bankchequetrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static BankChequeTrans updateData(BankChequeTrans bnk){
		String sql = "UPDATE bankchequetrans SET "
				+ "bankchkid=?,"
				+ "chkid=?," 
				+ " WHERE btid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bankchequetrans");
		
		ps.setLong(cnt++, bnk.getChequeRpt()==null? 0 : bnk.getChequeRpt().getId());
		ps.setLong(cnt++, bnk.getChequedtls()==null? 0 : bnk.getChequedtls().getId());
		ps.setInt(cnt++, bnk.getIsAcative());
		
		LogU.add(bnk.getChequeRpt()==null? 0 : bnk.getChequeRpt().getId());
		LogU.add(bnk.getChequedtls()==null? 0 : bnk.getChequedtls().getId());
		LogU.add(bnk.getIsAcative());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bankchequetrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return bnk;
	}
	
	public void updateData(){
		String sql = "UPDATE bankchequetrans SET "
				+ "bankchkid=?,"
				+ "chkid=?," 
				+ " WHERE btid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bankchequetrans");
		
		ps.setLong(cnt++, getChequeRpt()==null? 0 : getChequeRpt().getId());
		ps.setLong(cnt++, getChequedtls()==null? 0 : getChequedtls().getId());
		ps.setInt(cnt++, getIsAcative());
		
		LogU.add(getChequeRpt()==null? 0 : getChequeRpt().getId());
		LogU.add(getChequedtls()==null? 0 : getChequedtls().getId());
		LogU.add(getIsAcative());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bankchequetrans : " + s.getMessage());
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
		sql="SELECT btid FROM bankchequetrans  ORDER BY btid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("btid");
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
		ps = conn.prepareStatement("SELECT btid FROM bankchequetrans WHERE btid=?");
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
		String sql = "UPDATE bankchequetrans set bankisactivetrans=0 WHERE btid=?";
		
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
	public BankChequeRpt getChequeRpt() {
		return chequeRpt;
	}
	public void setChequeRpt(BankChequeRpt chequeRpt) {
		this.chequeRpt = chequeRpt;
	}
	public Chequedtls getChequedtls() {
		return chequedtls;
	}
	public void setChequedtls(Chequedtls chequedtls) {
		this.chequedtls = chequedtls;
	}
	public int getIsAcative() {
		return isAcative;
	}
	public void setIsAcative(int isAcative) {
		this.isAcative = isAcative;
	}
	
}
