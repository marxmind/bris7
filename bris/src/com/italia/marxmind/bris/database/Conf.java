package com.italia.marxmind.bris.database;

import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.security.SecureChar;

/**
 * 
 * @author Mark Italia
 * @since 2020/4/25
 * @version 1.0
 *
 */
public class Conf {
	
	private static volatile Conf conf;
	private String databaseName;
	private String databasePort;
	private String databaseUrl;
	private String databaseDriver;
	private String databaseSSL;
	private String databaseTimeZone;
	private String databaseUserName;
	private String databasePassword;
	
	private Conf() {
		System.out.println("initializing database information...");
	}
	
	public static Conf getInstance() {
		
		if(conf == null) {
			synchronized(Conf.class) {
				if(conf ==  null) {
					conf = new Conf();
					conf.readConf();//reading configuration on dbconf
				}
			}
		}
		
		return conf;
	}
	
	public static void refreshDBInformation() {
		conf=null;
	}
	
	private void readConf() {
		try {
			
			String driver = ReadConfig.value(Bris.DB_DRIVER);
			String u_name = ReadConfig.value(Bris.USER_NAME);
				   u_name = SecureChar.decode(u_name);
				   u_name = u_name.replaceAll("mark", "");
				   u_name = u_name.replaceAll("rivera", "");
				   u_name = u_name.replaceAll("italia", "");
			String db_url = ReadConfig.value(Bris.DB_URL);
			String port = ReadConfig.value(Bris.DB_PORT);
		       port = SecureChar.decode(port);
			String pword =  ReadConfig.value(Bris.USER_PASS);
			   pword = SecureChar.decode(pword);
			   pword = pword.replaceAll("mark", "");
			   pword = pword.replaceAll("rivera", "");
			   pword = pword.replaceAll("italia", "");   
			conf.setDatabaseName(ReadConfig.value(Bris.DB_NAME));
			conf.setDatabaseDriver(driver);
			conf.setDatabaseUrl(db_url);
			conf.setDatabasePort(port);
			conf.setDatabaseSSL(ReadConfig.value(Bris.DB_SSL));
			conf.setDatabaseTimeZone(Bris.DB_TIMEZONE.getName());
			conf.setDatabaseUserName(u_name);
			conf.setDatabasePassword(pword);
			
		}catch(Exception e) {
			System.out.println("Configuration file was not set. To set configuration file call the class InitDB.getInstance.setPathFileLocation('your file configuration')");
		}
	}
	
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getDatabasePort() {
		return databasePort;
	}
	public void setDatabasePort(String databasePort) {
		this.databasePort = databasePort;
	}
	public String getDatabaseUrl() {
		return databaseUrl;
	}
	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}
	public String getDatabaseDriver() {
		return databaseDriver;
	}
	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}
	public String getDatabaseSSL() {
		return databaseSSL;
	}
	public void setDatabaseSSL(String databaseSSL) {
		this.databaseSSL = databaseSSL;
	}
	public String getDatabaseTimeZone() {
		return databaseTimeZone;
	}
	public void setDatabaseTimeZone(String databaseTimeZone) {
		this.databaseTimeZone = databaseTimeZone;
	}
	public String getDatabaseUserName() {
		return databaseUserName;
	}
	public void setDatabaseUserName(String databaseUserName) {
		this.databaseUserName = databaseUserName;
	}
	public String getDatabasePassword() {
		return databasePassword;
	}
	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}
	
	
	
}
