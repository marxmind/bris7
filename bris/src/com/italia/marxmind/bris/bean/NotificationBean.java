package com.italia.marxmind.bris.bean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.italia.marxmind.bris.controller.Email;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.UserDtls;

@Named("notiBean")
@RequestScoped
public class NotificationBean {
	
	private boolean displayMsg;
	private String totalMsg;
	private String total;
	private String styleButton;
	
	public UserDtls getUser() {
		return Login.getUserLogin().getUserDtls();
	}
	
	
	
	public void loadCountEmailNote() {
		String sql = " AND msgtype=1 AND isopen=0 AND isdeleted=0 AND personcpy=?";
		String[] params = new String[1];
		params[0] = getUser().getUserdtlsid()+"";
		int count = Email.countNewEmail(sql, params);
		if(count>0) {
			displayMsg = true;
			setDisplayMsg(true);
			setTotalMsg(count+" messages.");
			setTotal(count+"");
			setStyleButton("color:red;");
		}else {
			displayMsg = false;
			setStyleButton("color:black;");
			setTotalMsg(count+" message.");
			setTotal(count+"");
		}
	}

	public boolean isDisplayMsg() {
		return displayMsg;
	}

	public void setDisplayMsg(boolean displayMsg) {
		this.displayMsg = displayMsg;
	}

	public String getTotalMsg() {
		return totalMsg;
	}

	public void setTotalMsg(String totalMsg) {
		this.totalMsg = totalMsg;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getStyleButton() {
		return styleButton;
	}

	public void setStyleButton(String styleButton) {
		this.styleButton = styleButton;
	}
	
}
