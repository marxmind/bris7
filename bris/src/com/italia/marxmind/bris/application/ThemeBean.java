package com.italia.marxmind.bris.application;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.bris.controller.ThemesDecoder;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.sessions.SessionBean;
/**
 * 
 * @author mark italia
 * @since 04/09/2017
 * @version 1.0
 *
 */
//@ApplicationScoped
//@ManagedBean(name="themeBean", eager=true)
@Named
@SessionScoped
public class ThemeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 147868854437557L;

	public String getApplicationTheme(){
	 
		String theme = "nova-colored";
		System.out.println("Applying theme...");
		try{
			/*theme = ReadConfig.value(Bris.THEME_STYLE);
		    theme = ThemesDecoder.themeDecode(theme);*/
			HttpSession session = SessionBean.getSession();
			theme = session.getAttribute("theme").toString();
			System.out.println("Theme " + theme + " has been applied...");
		}catch(Exception e){}
		return theme;
	}
	
	/*
	private List<Themes> themes;
	private Themes theme;
	
	@PostConstruct
	public void init() {
		themes = Collections.synchronizedList(new ArrayList<Themes>());
		for(Themes theme : Themes.readThemesXML()){
			
			System.out.println("Themes : " + theme.getStyleName());
			themes.add(new Themes(theme.getId(),ThemesDecoder.decodeEncryptedThemes(theme.getStyleName())));
		}
	}
	
	public void saveTheme(){
		int size = 1;
		Bris[] tag = new Bris[size];
		String[] value = new String[size];
		int i=0;
		tag[i] = Bris.THEME_STYLE; value[i] = ThemesDecoder.themeEncode(getTheme().getId());// getThemeData().get(getThemeId()).getStyleName(); i++;
		
		Themes.updateThemes(tag, value);
		Application.addMessage(1, "The theme style " + ThemesDecoder.decodeEncryptedThemes(getTheme().getStyleName()) + " has been successfully applied.","");
	}

	public List<Themes> getThemes() {
		return themes;
	}

	public void setThemes(List<Themes> themes) {
		this.themes = themes;
	}

	public Themes getTheme() {
		return theme;
	}

	public void setTheme(Themes theme) {
		this.theme = theme;
	}*/
}

