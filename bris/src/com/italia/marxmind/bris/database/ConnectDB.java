package com.italia.marxmind.bris.database;



import java.sql.Connection;
import java.sql.DriverManager;

import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.security.SecureChar;
/**
 * 
 * @author mark italia
 * @since 09/27/2016
 * 
 * @revised
 * 04/25/2020
 *
 */
public class ConnectDB {

	public static Connection getConnection(){
		Connection conn = null;
		Conf conf = Conf.getInstance();
		try{
			String driver = conf.getDatabaseDriver();
			Class.forName(driver);
			String db_url = conf.getDatabaseUrl();
			String dbName = conf.getDatabaseName();
			String timeZone = conf.getDatabaseTimeZone();
			String ssl = conf.getDatabaseSSL();
			String port = conf.getDatabasePort();
			String url = db_url + ":" + port + "/" +dbName+ "?"+timeZone+"&" + ssl;
			String u_name = conf.getDatabaseUserName();
			String pword = conf.getDatabasePassword();
			/*
			String driver = ReadConfig.value(Bris.DB_DRIVER);
			Class.forName(driver);
			String db_url = ReadConfig.value(Bris.DB_URL);
			String port = ReadConfig.value(Bris.DB_PORT);
			       port = SecureChar.decode(port);
			String url = db_url + ":" + port + "/" +ReadConfig.value(Bris.DB_NAME)+ "?serverTimezone=UTC&" + ReadConfig.value(Bris.DB_SSL);
			String u_name = ReadConfig.value(Bris.USER_NAME);
				   u_name = SecureChar.decode(u_name);
				   u_name = u_name.replaceAll("mark", "");
				   u_name = u_name.replaceAll("rivera", "");
				   u_name = u_name.replaceAll("italia", "");
			String pword = ReadConfig.value(Bris.USER_PASS);
				   pword =  SecureChar.decode(pword);
				   pword = pword.replaceAll("mark", "");
				   pword = pword.replaceAll("rivera", "");
				   pword = pword.replaceAll("italia", "");
			*/	   
				   
			conn = DriverManager.getConnection(url, u_name, pword);
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void close(Connection conn){
		try{
			if(conn!=null){
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Connection c = getConnection();
		System.out.println("Successfully connected" + c.toString());
	}
	
}
