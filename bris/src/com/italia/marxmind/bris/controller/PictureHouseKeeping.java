package com.italia.marxmind.bris.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reader.ReadConfig;

public class PictureHouseKeeping {

	private final static String IMAGE_PATH = ReadConfig.value(Bris.APP_IMG_FILE) ;
	private static String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private static String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private static String IMAGES = IMAGE_PATH + BARANGAY + "-" + MUNICIPALITY + File.separator;
	public static void main(String[] args) {
		clearingImages();
	}
	
	private static void clearingImages(){
		File file = new File(IMAGES);
		String[] fileList = file.list();
		List<String> fileDeletion = new ArrayList<>();
		
		String unusedImagesPath = IMAGES + "UnusedImages" + File.separator;
		File fileDir = new File(unusedImagesPath);
		if(!fileDir.isDirectory()){
			fileDir.mkdirs();
		}
		
		for(String fileName : fileList){
			String photoId = fileName.split("\\.")[0];
			
			boolean isExisting = Customer.isImageInUsed(photoId);
			if(!isExisting){
				transferingPicture(fileName,unusedImagesPath);
				fileDeletion.add(fileName);
			}
			
		}
		
		if(fileDeletion!=null && fileDeletion.size()>0){
			for(String fileName : fileDeletion){
				if(isSuccessfullyDeleted(fileName)){
					System.out.println("Filename : " + fileName + " is successfully deleted");
				}
			}
		}
		
	}
	
	private static void transferingPicture(String fileName , String unusedImagesPath){
		
		File oldFile = new File(IMAGES + fileName);
		File newFile = new File(unusedImagesPath + fileName);
		
		 try{
 			Files.copy(oldFile.toPath(), (newFile).toPath(),
 			        StandardCopyOption.REPLACE_EXISTING);
 			System.out.println("Successfully transferred: " + fileName);
 		}catch(IOException e){}
		
	}
	
	private static boolean isSuccessfullyDeleted(String fileName){
		File file = new File(IMAGES + fileName);
		try{
			file.delete();
			return true;
		}catch(Exception e){}
		
		return false;
	}
	
}
