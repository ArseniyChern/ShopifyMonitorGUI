package mainPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.bazaarvoice.jolt.JsonUtils;

public class StructuredTypeObject {

	private String name;
	private ArrayList<Feature> features = new ArrayList<Feature>();

	public StructuredTypeObject(JSONObject json) {
		
		JSONArray featuresList = (JSONArray) json.get("features");
		this.name = (String) json.get("persistentName");
		for (int i = 0; i < featuresList.size(); i++) {
			JSONObject f = (JSONObject) featuresList.get(i);

			Feature feat = new Feature(f, App.enumList, this.name);

			if (feat.getDataType() != null) {
				features.add(feat);
			}	
		}
	}

	public String toString() {
		String result = "type:" + this.name + "{\n";
		result += "group:default{\n";

		for (int i = 0; i < this.features.size(); i++) {
			result += this.features.get(i).toString();
		}
		result += "}\n}\n";
		return result;

	}

	class Feature {
		private String dataType;
		private boolean mandantory;
		private boolean multiple;
		private String name;

		public Feature(JSONObject f, ArrayList<EnumObject> enums, String st) {
			String type = (String) f.get("type");
			
			this.name = (String) f.get("persistentName");
			this.multiple = (boolean) f.get("multiple");
			this.mandantory = (boolean) f.get("mandatory");
			this.dataType = "--1234";
			switch (type) {
			case "integer":
				this.dataType = "INT";
				break;
			case "string":
				this.dataType = "STRING";
				break;
			case "richtext":
				this.dataType = "RICHTEXT";
				break;
			case "date_time":
				this.dataType = "DATETIME";
				break;
			case "boolean":
				this.dataType = "BOOLEAN";
				break;
			case "date":
				this.dataType = "DATE";
				break;
			case "double":
				this.dataType = "NUMERIC";
				break;
			}
			
			if(type.startsWith("de.iteratec")) {
				
				this.dataType =(String)f.get("name");
				this.mandantory = (boolean) f.get("mandatory");
				this.multiple = (boolean)f.get("multiple");
				this.name = (String)f.get("name");
			} else if(this.dataType.equals("--1234"))  {
				System.out.println(type);
			}
		}

		public String getDataType() {
			return this.dataType;
		}

		public String toString() {
			String key = "";

			if (this.mandantory == true)
				key += "[1..";
			else
				key += "[0..";

			if (this.multiple == false)
				key += "1]";
			else
				key += "2]";

			return this.name + key + ":" + this.dataType + "\n";
		}

	}

}
