package com.italia.marxmind.bris.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.marxmind.bris.enm.Bris;

/**
 * 
 * @author mark italia
 * @since 09/09/2017
 * @version 1.0
 *
 */

@Deprecated
public class Themes {

	private int id;
	private String styleName;
	
	private static final String THEMES_FILE = Bris.PRIMARY_DRIVE.getName() + Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + Bris.SEPERATOR.getName() +
			Bris.APP_CONFIG_FLDR.getName() + Bris.SEPERATOR.getName() +
			Bris.THEMES.getName();
	
	private static final String APPLICATION_FILE = Bris.PRIMARY_DRIVE.getName() + Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + Bris.SEPERATOR.getName() +
			Bris.APP_CONFIG_FLDR.getName() + Bris.SEPERATOR.getName() +
			Bris.APP_CONFIG_FILE.getName();
	
	public Themes() {}
	
	public Themes(int id, String styleName) {
		this.id = id;
		this.styleName = styleName;
	}
	
	 public static void updateThemes(Bris[] tag, String[] value){
			
			
		}
	
	public static List<Themes> readThemesXML(){
    	List<Themes> themes = Collections.synchronizedList(new ArrayList<Themes>());
		/*
		 * try { File fXmlFile = new File(THEMES_FILE); DocumentBuilderFactory dbFactory
		 * = DocumentBuilderFactory.newInstance(); DocumentBuilder dBuilder =
		 * dbFactory.newDocumentBuilder(); Document doc = dBuilder.parse(fXmlFile);
		 * //optional, but recommended //read this -
		 * http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with
		 * -java-how-does-it-work doc.getDocumentElement().normalize(); NodeList nList =
		 * doc.getElementsByTagName("style");
		 * //System.out.println("----------------------------");
		 * 
		 * for (int temp = 0; temp < nList.getLength(); temp++) { Node nNode =
		 * nList.item(temp); //System.out.println("\nCurrent Element :" +
		 * nNode.getNodeName()); if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 * 
		 * Element eElement = (Element) nNode;
		 * 
		 * Themes theme = new Themes();
		 * theme.setId(Integer.valueOf(eElement.getAttribute("id")));
		 * theme.setStyleName(eElement.getElementsByTagName("name").item(0).
		 * getTextContent());
		 * 
		 * themes.add(theme); } } } catch (Exception e) { e.printStackTrace(); }
		 */
    	return themes;
    }
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStyleName() {
		return styleName;
	}
	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}
	
}
