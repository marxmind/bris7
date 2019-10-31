package com.italia.marxmind.bris.enm;

public enum UserAccess {

	DEVELOPER(1, "Developer"),
	CAPTAIN(2, "Kapitan"),
	KAGAWAD(3, "Kagawad"),
	TREASURER(4, "Treasurer"),
	SECRETRARY(5, "Secretary"),
	ADMINISTRATOR(6, "Administrator"),
	RECORDKEEPER(7,"Record Keeper"),
	CLERK(8, "Clerk");
	
	private int id;
	private String name;
	
	private UserAccess(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static int statusId(String name){
		for(UserAccess s : UserAccess.values()){
			if(name.equalsIgnoreCase(s.getName())){
				return s.getId();
			}
		}
		return 1;
	}
	
	public static String statusName(int id){
		
		for(UserAccess s : UserAccess.values()){
			if(id==s.getId()){
				return s.getName();
			}
		}
		
		return "";
	}
	
	public int getId() {
		return id;
	}	public String getName() {
		return name;
	}	
	
}
