package com.italia.marxmind.bris.bean;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.bris.application.ClientInfo;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.sessions.IBean;
import com.italia.marxmind.bris.sessions.SessionBean;
import com.italia.marxmind.bris.utils.DateUtils;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @since 11/27/2019
 * @version 1.0
 *
 */

@Named
@RequestScoped
public class LogoutBacking {
	
	public String logout() {
		logUserOut();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }
	
	private void logUserOut(){
		String sql = "SELECT * FROM login WHERE username=? and logid=?";
		HttpSession session = SessionBean.getSession();
		String userid = session.getAttribute("userid").toString();
		String username = session.getAttribute("username").toString();
		String[] params = new String[2];
    	params[0] = username;
    	params[1] = userid;
    	Login in = null;
    	try{in = Login.retrieve(sql, params).get(0);}catch(Exception e){}
		ClientInfo cinfo = new ClientInfo();
		if(in!=null){
			in.setLastlogin(DateUtils.getCurrentDateMMDDYYYYTIME());
			in.setIsOnline(0);
			in.setClientip(cinfo.getClientIP());
			in.setClientbrowser(cinfo.getBrowserName());
			in.save();
		}
		LogU.add("The user " + username + " was logging out to the application.");
		
	}
	
}
