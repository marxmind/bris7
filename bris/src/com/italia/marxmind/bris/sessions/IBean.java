package com.italia.marxmind.bris.sessions;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author mark italia
 * @since 10/01/2016
 * @version 1.0
 *
 */
public class IBean {

	/**
	 * Remove and invalidate user session
	 */
	public static void removeBean(){
		try{
		HttpSession session = SessionBean.getSession();
		String[] beans = {
				"featuresBean","clearBean","auserBean","customerBean","bizBean", "mainBean"
				};
		for(String bean : beans){
			FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(bean);
		}
		session.invalidate();
		}catch(Exception e){}
	}
	
}
