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

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

import com.italia.marxmind.bris.controller.BrisFile;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reader.ReadConfig;

/**
 * 
 * @author mark italia
 * @since 02/02/2018
 * @version 1.0
 *
 */
@ManagedBean(name="explorerBean", eager=true)
@ViewScoped
public class FileExplorerBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3540043241L;
	private String FILE_FOLDERS = Bris.PRIMARY_DRIVE.getName() + File.separator + Bris.APP_FOLDER.getName() + File.separator + "files" + File.separator + ReadConfig.value(Bris.BARANGAY) + "-" + ReadConfig.value(Bris.MUNICIPALITY) + File.separator;
	private TreeNode root;
	private TreeNode selected;
	
	private List<BrisFile> brisFiles;
	
	private StreamedContent documentFile;
	
	@PostConstruct
	public void init(){
		
		root = new DefaultTreeNode("BRIS", null);
		
		File folders = new File(FILE_FOLDERS);
		File[] flds = folders.listFiles();
		
		TreeNode documents = new DefaultTreeNode();
		TreeNode forms = new DefaultTreeNode();
		TreeNode doc = new DefaultTreeNode();
		String ext = "docx";
		String type = "document";
		for(File file : flds){
			
			if(file.isDirectory()){
				//documents = new DefaultTreeNode(file.getParent().substring(file.getParent().lastIndexOf(File.separator)+1), root);
				documents = new DefaultTreeNode();
				documents = new DefaultTreeNode(file.getName(), root);
				
				for(File f : file.listFiles()){
					if(f.isDirectory()){
						forms = new DefaultTreeNode(f.getName(), documents);
						for(File fx : f.listFiles()){
							if(fx.isDirectory()){
								TreeNode dir = new DefaultTreeNode(fx.getName(), forms);
								for(File fz : fx.listFiles()){
									type = filterExtType(fz);
									TreeNode dz = new DefaultTreeNode(type,fz.getName(), dir);
								}
							}else{
								type = filterExtType(fx);
								doc = new DefaultTreeNode(type,fx.getName(), forms);
							}
						}
					}else{
						type = filterExtType(f);
						doc = new DefaultTreeNode(type,f.getName(), documents);
					}
				}
				
			}else{
				
				type = filterExtType(file);
				doc = new DefaultTreeNode(type,file.getName(), documents);
				
			}
		}
		
		/*for(String folderName : folders.list()){
			TreeNode documents = new DefaultTreeNode(folderName, root);
		}*/
		
		/*TreeNode documents = new DefaultTreeNode("Documents", root);
		TreeNode pictures = new DefaultTreeNode("Pictures", root);
		TreeNode songs = new DefaultTreeNode("Songs", root);
		TreeNode videos = new DefaultTreeNode("Videos", root);*/
	
		/*TreeNode work = new DefaultTreeNode("Work", documents);
		TreeNode primefaces = new DefaultTreeNode("PrimeFaces", documents);
		
		//Documents
		TreeNode expenses = new DefaultTreeNode("document", "Expenses.doc", work);
		TreeNode resume = new DefaultTreeNode("document", "Resume.doc", work);
		TreeNode refdoc = new DefaultTreeNode("document", "RefDoc.pages", primefaces);
		//Pictures
		TreeNode barca = new DefaultTreeNode("picture", "barcelona.jpg", pictures);
		TreeNode primelogo = new DefaultTreeNode("picture", "logo.jpg", pictures);
		TreeNode optimus = new DefaultTreeNode("picture", "optimus.png", pictures);*/
		
	}
	
	private String filterExtType(File file){
		String type = "document";
		String ext = file.getName().split("\\.")[1];
		if("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext) || 
				"xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext) || "txt".equalsIgnoreCase(ext)){
			type = "document";
		}else if("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext) || "png".equalsIgnoreCase(ext)){
			type = "picture";
		}else if("mov".equalsIgnoreCase(ext) || "avi".equalsIgnoreCase(ext) || "mp3".equalsIgnoreCase(ext) ||
				"mp4".equalsIgnoreCase(ext) || "3gp".equalsIgnoreCase(ext)){
			type = "mp3";
		}
		
		return type;
	}
	
	public void onNodeSelect(NodeSelectEvent event) {
		String node = event.getTreeNode().getData().toString();
		System.out.println("onNodeSelect>> " + node);
		//this.selected = (DefaultTreeNode)event.getTreeNode();
		displayFiles((DefaultTreeNode)event.getTreeNode());
		documentClick((DefaultTreeNode)event.getTreeNode());
		
	}
	public void onNodeExpand(NodeExpandEvent event) {
		String node = event.getTreeNode().getData().toString();
		System.out.println("onNodeExpand>> " + node);
		displayFiles((DefaultTreeNode)event.getTreeNode());
		
	}
	public void onNodeCollapse(NodeCollapseEvent event) {
		String node = event.getTreeNode().getData().toString();
		System.out.println("onNodeCollapse>> " + node);
		
		displayFiles((DefaultTreeNode)event.getTreeNode());
		
	}
	
	public void displayFiles(TreeNode node){
		brisFiles = Collections.synchronizedList(new ArrayList<BrisFile>());
		
		System.out.println("Parent "+node.getParent());
		System.out.println("Type "+node.getType());
		
		for(TreeNode n : node.getChildren()){
			
			String[] names = new String[0];
			String name = "Unkown";
			String ext = "folder";
			try{
				names = n.getData().toString().split("\\.");
				name = names[0];
				ext = names[1];
			}catch(Exception e){}
			
			if("Unkown".equalsIgnoreCase(name)){
				name = n.getData().toString();
			}
			
			int id = 1;
			BrisFile file  = new BrisFile();
			file.setId(id++);
			file.setName(name);
			file.setExt(ext);
			file.setPath("");
			file.setIcon(icon(ext));
			file.setObject(n);
			brisFiles.add(file);
		}
		
	}
	
	private String icon(String ext){
		String type = "bris.png";
		
		if("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext) ){
			type = "doc.png";
		}else if("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)){
			type = "xls.png";
		}else if("txt".equalsIgnoreCase(ext)){
			type = "txt.png";
		}else if("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext) || "png".equalsIgnoreCase(ext)){
			type = "jpg.png";
		}else if("mov".equalsIgnoreCase(ext) || "avi".equalsIgnoreCase(ext) ||
				"mp4".equalsIgnoreCase(ext) || "3gp".equalsIgnoreCase(ext)){
			type = "vid.png";
		}else if("mp3".equalsIgnoreCase(ext)){
			type = "mp3.png";
		}else if("folder".equalsIgnoreCase(ext)){
			type = "dir.png";	
		}
		return type;
	}
	
	public void documentClick(TreeNode node){
		//TreeNode node = (TreeNode)file.getObject();
		//root.setRowKey(node.getRowKey());
		System.out.println("Update tree>> ");
		 
		String fil = FILE_FOLDERS + node.getParent() + File.separator + node.getData().toString();
		//String fil = FILE_FOLDERS + node.getData().toString();
		System.out.println("Parent "+node.getParent() + " == " + fil);
		try{
			InputStream is = new FileInputStream(fil);
			String ext = node.getData().toString().split("\\.")[1];
			documentFile = new DefaultStreamedContent(is, "application/"+ext,node.getData().toString());
			//is.close();
		}catch(FileNotFoundException e){
			System.out.println("FileNotFoundException " + e.getMessage());
		}catch(IOException eio){
			System.out.println("IOException " + eio.getMessage());
		}
	}
	
	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelected() {
		return selected;
	}

	public void setSelected(TreeNode selected) {
		this.selected = selected;
	}

	public List<BrisFile> getBrisFiles() {
		return brisFiles;
	}

	public void setBrisFiles(List<BrisFile> brisFiles) {
		this.brisFiles = brisFiles;
	}

	public StreamedContent getDocumentFile() {
		return documentFile;
	}

	public void setDocumentFile(StreamedContent documentFile) {
		this.documentFile = documentFile;
	}

}
