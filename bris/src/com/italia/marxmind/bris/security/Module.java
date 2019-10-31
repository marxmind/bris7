package com.italia.marxmind.bris.security;
/**
 * 
 * @author mark italia
 * @since 01/24/2017
 * @version 1.0
 *
 */
public enum Module {

	BRIS(1,"BRIS");
	
	
	private int id;
	private String name;
	
	public static String moduleName(int id){
		
		for(Module m : Module.values()){
			if(id==m.getId()){
				return m.getName();
			}
		}
		return Module.BRIS.getName();
	}
	
	public static int moduleId(String name){
		
		for(Module m : Module.values()){
			if(name.equalsIgnoreCase(m.getName())){
				return m.getId();
			}
		}
		return Module.BRIS.getId();
	}
	
	public static Module selected(int id){
		for(Module m : Module.values()){
			if(id==m.getId()){
				return m;
			}
		}
		return Module.BRIS;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private Module(int id, String name){
		this.id = id;
		this.name = name;
	}
	
}
