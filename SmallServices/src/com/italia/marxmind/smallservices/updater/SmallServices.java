package com.italia.marxmind.smallservices.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class SmallServices {

	
	public static void main(String[] args) {
		
		//run age update
		SmallServices.createLogFile("====================START RUNNING SERVICES=================");
		SmallServices.createLogFile("Running birthday services.....");
		BirthdayUpdater.updateBirthday();
		SmallServices.createLogFile("End Running birthday services.....");
		
		//run update
		SmallServices.createLogFile("Running OR services.....");
		ORUpdate.updateOR();
		SmallServices.createLogFile("End Running OR services.....");
		
		//run update
		SmallServices.createLogFile("Running Cedula services.....");
		CedulaUpdate.updateCedula();
		SmallServices.createLogFile("End Running Cedula services.....");
		
		
		//run clearing of images not in use
		//SmallServices.createLogFile("Running clearing images services.....");
		//PictureHouseKeeping.clearingImages();
		//SmallServices.createLogFile("End Running clearing images services.....");
		
		SmallServices.createLogFile("====================END RUNNING SERVICES===================");
	}

	public static void createLogFile(String value){
		String FILE_LOG_NAME = "small_service_log";
		String FILE_LOG_TMP_NAME = "tmp_small_service_log";
		String EXT = ".log";
		
		String logpath = ReadConfig.value(Bris.APP_LOG_PATH);
			
			try{
			String finalFile = logpath + FILE_LOG_NAME + "-" + DateUtils.getCurrentDateMMDDYYYYPlain() + EXT;
	        String tmpFileName = logpath + FILE_LOG_TMP_NAME + "-" + DateUtils.getCurrentDateMMDDYYYYPlain() + EXT;
	        
	        File originalFile = new File(finalFile);
	        
	        //check log directory
	        File logdirectory = new File(ReadConfig.value(Bris.APP_LOG_PATH));
	        if(!logdirectory.isDirectory()){
	        	logdirectory.mkdir();
	        }
	        
	        if(!originalFile.exists()){
	        	originalFile.createNewFile();
	        }
	        
	        BufferedReader br = new BufferedReader(new FileReader(originalFile));
		
	        File tempFile = new File(tmpFileName);
	        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
	        
	        
	        String line = null;
	        // Read from the original file and write to the new
	        while ((line = br.readLine()) != null) {
	            pw.println(line);
	        }
	       
	        pw.println(DateUtils.getCurrentDateMMDDYYYYTIME()  + " : " + value);
	        pw.flush();
	        pw.close();
	        br.close();

	        // Delete the original file
	        if (!originalFile.delete()) {
	            System.out.println("Could not delete file");
	            return;
	        }

	        // Rename the new file to the filename the original file had.
	        if (!tempFile.renameTo(originalFile))
	            System.out.println("Could not rename file");
			
			}catch(Exception e){e.getMessage();}
			
	}
	
}
