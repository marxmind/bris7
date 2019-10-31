package com.italia.marxmind.bris.enm;

/**
 * 
 * @author mark italia
 * @since 10/26/2017
 * @version 1.0
 *
 */
public enum MenuStyle {
	
	DEFAULT(0,"DEFAULT"),
	DOCK(1, "DOCK"),
	STACK(2,"STACK"),
	SLIDE(3, "SLIDE"),
	TAB(4, "TAB"),
	BOOTSTRAP(5, "BOOT");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private MenuStyle(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(MenuStyle type : MenuStyle.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return MenuStyle.DOCK.getName();
	}
	public static int typeId(String name){
		for(MenuStyle type : MenuStyle.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return MenuStyle.DOCK.getId();
	}
	
}

