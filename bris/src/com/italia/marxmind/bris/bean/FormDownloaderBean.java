package com.italia.marxmind.bris.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.marxmind.bris.controller.Forms;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reader.ReadConfig;

/**
 * 
 * @author mark italia
 * @since 02/01/2018
 * @version 1.0
 *
 */
@ManagedBean(name="formBean", eager=true)
@ViewScoped
public class FormDownloaderBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5456679341221111L;

	private String FORM_FOLDER = Bris.PRIMARY_DRIVE.getName() + File.separator + Bris.APP_FOLDER.getName() + File.separator + "forms" + File.separator + ReadConfig.value(Bris.BARANGAY) + "-" + ReadConfig.value(Bris.MUNICIPALITY) + File.separator;
	
	private StreamedContent formFile;
	private List<Forms> forms;
	private List<Forms> dataForms;
	private Forms selectedForms;
	
	private String searchParam;
	
	@PostConstruct
	public void init(){
		try {
			File file = new File(FORM_FOLDER);
			String[] fileList = file.list();
			forms = Collections.synchronizedList(new ArrayList<>());
			dataForms = Collections.synchronizedList(new ArrayList<>());
			int id=1;
			for(String fileName : fileList){
				Forms form = new Forms();
				form.setId(id++);
				form.setFileName(fileName);
				String ext = fileName.split("\\.")[1];
				form.setExt(ext);
				dataForms.add(form);
				forms.add(form);
			}
		}catch(Exception e) {}
	}
	
	public void searchForm(){
		List<Forms> formTmp = Collections.synchronizedList(new ArrayList<Forms>());
		if(getSearchParam()!=null && !getSearchParam().isEmpty()){
		//forms = Collections.synchronizedList(new ArrayList<>());
			for(Forms f : getDataForms()){
				if(f.getFileName().contains(getSearchParam())){
					formTmp.add(f);
				}
			}
			if(formTmp!=null){
				forms = Collections.synchronizedList(new ArrayList<>());
				forms = formTmp;
			}
		}else{
			init();
		}
	}
	
	public void formDownload(String fileName){
		System.out.println("formDownload " + fileName);
		try{
			InputStream is = new FileInputStream(FORM_FOLDER + fileName);
			String ext = fileName.split("\\.")[1];
			formFile = new DefaultStreamedContent(is, "application/"+ext,fileName);
			//is.close();
		}catch(FileNotFoundException e){
			
		}catch(IOException eio){}
		
	}

	public StreamedContent getFormFile() {
		return formFile;
	}

	public List<Forms> getForms() {
		return forms;
	}

	public void setForms(List<Forms> forms) {
		this.forms = forms;
	}

	public String getSearchParam() {
		return searchParam;
	}

	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}

	public Forms getSelectedForms() {
		return selectedForms;
	}

	public void setSelectedForms(Forms selectedForms) {
		this.selectedForms = selectedForms;
	}

	public List<Forms> getDataForms() {
		return dataForms;
	}

	public void setDataForms(List<Forms> dataForms) {
		this.dataForms = dataForms;
	}
	
}
