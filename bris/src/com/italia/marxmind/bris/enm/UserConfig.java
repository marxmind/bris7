package com.italia.marxmind.bris.enm;
/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 09/04/2019
 *
 */
public enum UserConfig {
	
	FileName(1, "fileName"),
	DoNotNotifyAgainToday(2, "donotnotifyagaintoday"),
	ThemSelected(3,"themeSelected"),
	VersionUsing(4,"versionUsing"),
	Barangay(5,"barangay");
	
	private int id;
	private String fieldName;
	
	public int getId(){
		return id;
	}
	
	public String getFieldName(){
		return fieldName;
	}
	
	private UserConfig(int id, String fieldName){
		this.id = id;
		this.fieldName = fieldName;
	}
	
	public static String typeName(int id){
		for(UserConfig type : UserConfig.values()){
			if(id==type.getId()){
				return type.getFieldName();
			}
		}
		return UserConfig.FileName.getFieldName();
	}
	public static int typeId(String fieldName){
		for(UserConfig type : UserConfig.values()){
			if(fieldName.equalsIgnoreCase(type.getFieldName())){
				return type.getId();
			}
		}
		return UserConfig.FileName.getId();
	}
	
}
