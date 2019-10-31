package com.italia.marxmind.bris.addons.fetch;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.database.ConnectDB;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 07/12/2019
 *
 */
public class DBXMLConverter {
	
	
	public static void main(String[] args) {
		String path = "C:" + File.separator + "bris" + File.separator + "fetchXML" + File.separator;
		String[] columns = new String[3];
		columns[0]="cuscardno";
		columns[1]="borndate";
		columns[2]="photoid";
		convertDBToXml(true,"customer","customerid","fullname",columns,path,1);
		
	}
	
	private static String[] tableFields(ResultSet rs)  throws SQLException{
			if (rs == null) {
		      return new String[0];
		    }
			ResultSetMetaData rsMetaData = rs.getMetaData();
		    int numberOfColumns = rsMetaData.getColumnCount();
		    String[] fields = new String[numberOfColumns];
		    int col = 0;
		    for (int i = 1; i < numberOfColumns + 1; i++) {
			      String columnName = rsMetaData.getColumnName(i);
			      fields[col++] = columnName;
		    }
		return fields;
	}
	/**
	 * 
	 * @param tableName
	 * @param saveLocation
	 * @parama put 0 if no limit
	 * @return filename of xml is the table name
	 */
	public static Object convertDBToXml(boolean isDefineColumns,String tableName,String fieldNameAsId, String fieldNameAsName, String[] columns, String saveLocation, int limitSizeToFetch) {
		String sql ="";
		String xml ="";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		
		if(isDefineColumns) {
			String fs = fieldNameAsId +","+fieldNameAsName;
			for(String f : columns) {
					fs += ","+f;
			}
			
			sql = "SELECT "+ fs +" FROM "+ tableName;
			
			if(limitSizeToFetch>0) {
				sql +=" limit " + limitSizeToFetch;
			}
			
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			xml = "<?xml version=\"1.0\" ?>\n";
			xml += "<add>\n";
			while(rs.next()) {
				xml += "\t<doc>\n";
				
				xml += "\t\t<field name=\"id\">" + rs.getString(fieldNameAsId) + "</field>\n";
				xml += "\t\t<field name=\"name\">" + rs.getString(fieldNameAsName) + "</field>\n";
				
				for(String col : columns) {
					xml += "\t\t<field name=\""+ col + "\">" + rs.getString(col) + "</field>\n";
				}
				
				xml += "\t</doc>\n";
			}
			xml += "</add>\n";
			rs.close();
			ps.close();
			ConnectDB.close(conn);
		}else {	
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement("SELECT * FROM "+ tableName +" limit 1");
			
			rs = ps.executeQuery();
			
			String[] flds = tableFields(rs);
			
			rs.close();
			ps.close();
			ConnectDB.close(conn);
			
			conn = null;
			rs = null;
			ps = null;
			
			conn = ConnectDB.getConnection();
			sql = "SELECT * FROM "+ tableName;
			if(limitSizeToFetch>0) {
				sql +=" limit " + limitSizeToFetch;
			}
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			xml = "<?xml version=\"1.0\" ?>\n";
			xml += "<add>\n";
			while(rs.next()) {
				xml += "\t<doc>\n";
				int cnt=1;
				for(String s : flds) {
					if(cnt==1) {
						xml += "\t\t<field name=\"id\">" + rs.getString(s) + "</field>\n";
						xml += "\t\t<field name=\"name\">" + rs.getString(fieldNameAsName) + "</field>\n";
					}
					
					xml += "\t\t<field name=\""+ s + "\">" + rs.getString(s) + "</field>\n";
					cnt++;
				}
				xml += "\t</doc>\n";
			}
			xml += "</add>\n";
			
			rs.close();
			ps.close();
			ConnectDB.close(conn);
		}
		
		
			File file = new File(saveLocation);
			
			if(!file.isDirectory()) {
				file.mkdir();
			}
		
			File xmlFile = new File(saveLocation + tableName + ".xml");
			PrintWriter pw = new PrintWriter(new FileWriter(xmlFile));
			pw.println(xml);
			pw.flush();
			pw.close();
			System.out.println("completed saving xml");
		
		}catch(Exception e){e.getMessage();}
		
		
		
		return null;
	}
	
	
}
