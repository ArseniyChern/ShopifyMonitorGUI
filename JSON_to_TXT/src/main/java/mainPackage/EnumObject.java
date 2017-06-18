package mainPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class EnumObject {

	private String name;
	private String origin_name;
	private Map<String,String> literals = new HashMap<String,String>();
	
	public EnumObject(String name, JSONArray literals){
		this.origin_name = name;
		this.name = convert(name);
		
		for(int i = 0; i < literals.size(); i++){
			JSONObject lit = (JSONObject) literals.get(i);
			String litName = (String) lit.get("name");
			String newName = convert(litName);
			this.literals.put(litName, newName);
		}
	}
	
	public String getOriginalName(){
		return this.origin_name;
	}
	
	public String getNewName(){
		return this.name;
	}
	
	private String convert(String name){
		this.origin_name = name;
		name = name.replace(" ", "_");
		name = name.replace("ß", "ss");
		name = name.replace("(", "_");
		name = name.replace(")", "");
		return name;
	}
	
	public String toString(){
		String result = "enum:"+this.name+"{";
		Iterator<String> iter = this.literals.values().iterator();
		while(iter.hasNext()){
			String litName = iter.next();
			if(!result.endsWith("{")){
				result += ",";
			}
			result += litName; 
		}
		result += "}\n";
		return result;
	}
}
