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
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.LogU;
import com.italia.marxmind.bris.utils.Numbers;

public class TransmittalTrans {
	
	private long id;
	private int isActive;
	private Transmittal transmittal;
	private BankChequeRpt pbc;
	private Chequedtls chequedtls;
	private Timestamp timestamp;
	
	public static List<TransmittalTrans> retrieve(String sqlAdd, String[] params){
		List<TransmittalTrans> trans = new ArrayList<TransmittalTrans>();
		
		String tableTransactions = "trans";
		String tableTransmittal = "tran";
		String tablePbc = "pbc";
		String tableCheck = "chk";
		String sql = "SELECT * FROM transmittalchks "+ tableTransactions +", transmittal " + tableTransmittal + ", bankchequerpt " + tablePbc + ", cheques " + tableCheck  + " WHERE " + 
				tableTransactions + ".transid=" + tableTransmittal + ".transid AND " + 
				tableTransactions + ".bankchkid=" + tablePbc +".bankchkid AND " +
				tableTransactions + ".chkid=" + tableCheck +".chkid AND " + tableTransactions + ".tranchkid=1 ";
		
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
			
			TransmittalTrans tr = new TransmittalTrans();
			try{tr.setId(rs.getLong("tranchkid"));}catch(NullPointerException e){}
			try{tr.setIsActive(rs.getInt("tranchkisactive"));}catch(NullPointerException e){}
			
			Transmittal tran = new Transmittal();
			try{tran.setId(rs.getLong("transid"));}catch(NullPointerException e){}
			try{tran.setDateTrans(rs.getString("transdate"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("transisactive"));}catch(NullPointerException e){}
			Employee cap = new Employee();
			try{cap.setId(rs.getLong("captain"));}catch(NullPointerException e){}
			tran.setCaptain(cap);
			Employee treas = new Employee();
			try{treas.setId(rs.getLong("treas"));}catch(NullPointerException e){}
			tran.setTreasurer(treas);
			tr.setTransmittal(tran);
			
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
			tr.setPbc(bnk);
			
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
			tr.setChequedtls(chk);
			
			trans.add(tr);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static TransmittalTrans retrieve(long id){
		TransmittalTrans tr = new TransmittalTrans();
		
		String tableTransactions = "trans";
		String tableTransmittal = "tran";
		String tablePbc = "pbc";
		String tableCheck = "chk";
		String sql = "SELECT * FROM transmittalchks "+ tableTransactions +", transmittal " + tableTransmittal + ", bankchequerpt " + tablePbc + ", cheques " + tableCheck  + " WHERE " + 
				tableTransactions + ".transid=" + tableTransmittal + ".transid AND " + 
				tableTransactions + ".bankchkid=" + tablePbc +".bankchkid AND " +
				tableTransactions + ".chkid=" + tableCheck +".chkid AND " + tableTransactions + ".tranchkid=1 AND " + tableTransactions +".tranchkid="+ id;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{tr.setId(rs.getLong("tranchkid"));}catch(NullPointerException e){}
			try{tr.setIsActive(rs.getInt("tranchkisactive"));}catch(NullPointerException e){}
			
			Transmittal tran = new Transmittal();
			try{tran.setId(rs.getLong("transid"));}catch(NullPointerException e){}
			try{tran.setDateTrans(rs.getString("transdate"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("transisactive"));}catch(NullPointerException e){}
			Employee cap = new Employee();
			try{cap.setId(rs.getLong("captain"));}catch(NullPointerException e){}
			tran.setCaptain(cap);
			Employee treas = new Employee();
			try{treas.setId(rs.getLong("treas"));}catch(NullPointerException e){}
			tran.setTreasurer(treas);
			tr.setTransmittal(tran);
			
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
			tr.setPbc(bnk);
			
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
			tr.setChequedtls(chk);
			
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return tr;
	}
	
	public static TransmittalTrans save(TransmittalTrans tr){
		if(tr!=null){
			
			long id = TransmittalTrans.getInfo(tr.getId() ==0? TransmittalTrans.getLatestId()+1 : tr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				tr = TransmittalTrans.insertData(tr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				tr = TransmittalTrans.updateData(tr);
			}else if(id==3){
				LogU.add("added new Data ");
				tr = TransmittalTrans.insertData(tr, "3");
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
	
	public static TransmittalTrans insertData(TransmittalTrans bnk, String type){
		String sql = "INSERT INTO transmittalchks ("
				+ "tranchkid,"
				+ "transid,"
				+ "bankchkid,"
				+ "chkid,"
				+ "tranchkisactive) " 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transmittalchks");
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
		
		
		ps.setLong(cnt++, bnk.getTransmittal()==null? 0 : bnk.getTransmittal().getId());
		ps.setLong(cnt++, bnk.getPbc()==null? 0 : bnk.getPbc().getId());
		ps.setLong(cnt++, bnk.getChequedtls()==null? 0 : bnk.getChequedtls().getId());
		ps.setInt(cnt++, bnk.getIsActive());
		
		LogU.add(bnk.getTransmittal()==null? 0 : bnk.getTransmittal().getId());
		LogU.add(bnk.getPbc()==null? 0 : bnk.getPbc().getId());
		LogU.add(bnk.getChequedtls()==null? 0 : bnk.getChequedtls().getId());
		LogU.add(bnk.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transmittalchks : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return bnk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO transmittalchks ("
				+ "tranchkid,"
				+ "transid,"
				+ "bankchkid,"
				+ "chkid,"
				+ "tranchkisactive) " 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transmittalchks");
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
		
		
		ps.setLong(cnt++, getTransmittal()==null? 0 : getTransmittal().getId());
		ps.setLong(cnt++, getPbc()==null? 0 : getPbc().getId());
		ps.setLong(cnt++, getChequedtls()==null? 0 : getChequedtls().getId());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getTransmittal()==null? 0 : getTransmittal().getId());
		LogU.add(getPbc()==null? 0 : getPbc().getId());
		LogU.add(getChequedtls()==null? 0 : getChequedtls().getId());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transmittalchks : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static TransmittalTrans updateData(TransmittalTrans bnk){
		String sql = "UPDATE transmittalchks SET "
				+ "transid=?,"
				+ "bankchkid=?,"
				+ "chkid=?," 
				+ " WHERE tranchkid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table transmittalchks");
		
		ps.setLong(cnt++, bnk.getTransmittal()==null? 0 : bnk.getTransmittal().getId());
		ps.setLong(cnt++, bnk.getPbc()==null? 0 : bnk.getPbc().getId());
		ps.setLong(cnt++, bnk.getChequedtls()==null? 0 : bnk.getChequedtls().getId());
		ps.setLong(cnt++, bnk.getId());
		
		LogU.add(bnk.getTransmittal()==null? 0 : bnk.getTransmittal().getId());
		LogU.add(bnk.getPbc()==null? 0 : bnk.getPbc().getId());
		LogU.add(bnk.getChequedtls()==null? 0 : bnk.getChequedtls().getId());
		LogU.add(bnk.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to transmittalchks : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return bnk;
	}
	
	public void updateData(){
		String sql = "UPDATE transmittalchks SET "
				+ "transid=?,"
				+ "bankchkid=?,"
				+ "chkid=?," 
				+ " WHERE tranchkid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table transmittalchks");
		
		ps.setLong(cnt++, getTransmittal()==null? 0 : getTransmittal().getId());
		ps.setLong(cnt++, getPbc()==null? 0 : getPbc().getId());
		ps.setLong(cnt++, getChequedtls()==null? 0 : getChequedtls().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getTransmittal()==null? 0 : getTransmittal().getId());
		LogU.add(getPbc()==null? 0 : getPbc().getId());
		LogU.add(getChequedtls()==null? 0 : getChequedtls().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to transmittalchks : " + s.getMessage());
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
		sql="SELECT tranchkid FROM transmittalchks  ORDER BY tranchkid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("tranchkid");
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
		ps = conn.prepareStatement("SELECT tranchkid FROM transmittalchks WHERE tranchkid=?");
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
		String sql = "UPDATE transmittalchks set tranchkisactive=0 WHERE tranchkid=?";
		
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
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Transmittal getTransmittal() {
		return transmittal;
	}
	public void setTransmittal(Transmittal transmittal) {
		this.transmittal = transmittal;
	}
	public BankChequeRpt getPbc() {
		return pbc;
	}
	public void setPbc(BankChequeRpt pbc) {
		this.pbc = pbc;
	}
	public Chequedtls getChequedtls() {
		return chequedtls;
	}
	public void setChequedtls(Chequedtls chequedtls) {
		this.chequedtls = chequedtls;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
