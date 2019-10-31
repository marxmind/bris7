package com.italia.marxmind.bris.application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.enm.Bris;

/**
 * 
 * @author mark italia
 * @since 07/29/2018
 * @version 1.0
 *
 */

@ManagedBean(name="help", eager=true)
@ViewScoped
public class Help implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 156586734643L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private final String HELP_FOLDER = Bris.PRIMARY_DRIVE.getName() + Bris.SEPERATOR.getName() + Bris.APP_FOLDER.getName() + Bris.SEPERATOR.getName() + "help" + Bris.SEPERATOR.getName();
	private int id;
	private String contents;
	private String fileName;
	private List<Help> helps = Collections.synchronizedList(new ArrayList<Help>());
	private String searchHelp;
	
	//private TagCloudModel tagHelp;
	
	public static void main(String[] args) {
		Help hp = new Help();
		System.out.println(hp.retrieve().get(0).getFileName());
	}
	
	public void loadHelp() {
		retrieve();
		/*tagHelp = new DefaultTagCloudModel();
		int index = 1;
		for(Help hp : helps) {
			tagHelp.addTag(new DefaultTagCloudItem(hp.getFileName(), "card.xhtml", index++));
		}*/
	}
	
	private List<Help> retrieve(){
		helps = Collections.synchronizedList(new ArrayList<Help>());
		String sql = "SELECT * FROM helpmanual";
		
		if(getSearchHelp()!=null && !getSearchHelp().isEmpty()) {
			sql += " WHERE contentwords like '%" + getSearchHelp().replace("--", "") +"%'";
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		System.out.println("SQL help: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			Help hp = new Help();
			hp.setId(rs.getInt("manid"));
			hp.setContents(rs.getString("contentwords"));
			hp.setFileName(rs.getString("filename"));
			helps.add(hp);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return helps;
	}
	
	public void showHelp(Help hp) {
		try{
		 String REPORT_NAME = hp.getFileName();	
  		 File file = new File(HELP_FOLDER, REPORT_NAME + ".pdf");
		 FacesContext faces = FacesContext.getCurrentInstance();
		 ExternalContext context = faces.getExternalContext();
		 HttpServletResponse response = (HttpServletResponse)context.getResponse();
			
	     BufferedInputStream input = null;
	     BufferedOutputStream output = null;
	     
	     try{
	    	 
	    	 // Open file.
	            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

	            // Init servlet response.
	            response.reset();
	            response.setHeader("Content-Type", "application/pdf");
	            response.setHeader("Content-Length", String.valueOf(file.length()));
	            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
	            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

	            // Write file contents to response.
	            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	            int length;
	            while ((length = input.read(buffer)) > 0) {
	                output.write(buffer, 0, length);
	            }

	            // Finalize task.
	            output.flush();
	    	 
	     }finally{
	    	// Gently close streams.
	            close(output);
	            close(input);
	     }
	     
	     // Inform JSF that it doesn't need to handle response.
	        // This is very important, otherwise you will get the following exception in the logs:
	        // java.lang.IllegalStateException: Cannot forward after response has been committed.
	        faces.responseComplete();
	        
		}catch(Exception ioe){
			ioe.printStackTrace();
		}
	}
	
	private void close(Closeable resource) {
	    if (resource != null) {
	        try {
	            resource.close();
	        } catch (IOException e) {
	            // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
	            // know that this will generally only be thrown when the client aborted the download.
	            e.printStackTrace();
	        }
	    }
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public List<Help> getHelps() {
		return helps;
	}


	public void setHelps(List<Help> helps) {
		this.helps = helps;
	}


	public String getSearchHelp() {
		return searchHelp;
	}


	public void setSearchHelp(String searchHelp) {
		this.searchHelp = searchHelp;
	}

	/*public TagCloudModel getTagHelp() {
		if(tagHelp==null) {
			tagHelp = new DefaultTagCloudModel();
			tagHelp.addTag(new DefaultTagCloudItem("BRIS HELP", "main.xhtml", 1));
		}
		return tagHelp;
	}

	public void setTagHelp(TagCloudModel tagHelp) {
		this.tagHelp = tagHelp;
	}*/
	
}
