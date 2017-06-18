package mainPackage;

public class Reference {
	private Endpoint e1;
	private Endpoint e2;
	
	public Reference(Endpoint e1){
		this.e1 = e1;
		this.e2 = null;
	}
	
	public void setE2(Endpoint e){
		this.e2 = e;
	}
	
	public Endpoint getE1(){
		return this.e1;
	}
	
	public Endpoint getE2(){
		return this.e2;
	}
	
	public String toString(){
		return "reference{"+this.e1.toString()+"-"+this.e2.toString()+"}\n";
	}
}

class Endpoint{
	private String structuredType;
	private String name;
	private boolean multiple;
	private boolean mandantory;
	
	public Endpoint(String name, String type, boolean multiple, boolean mandantory){
		this.structuredType = type;
		this.name = name;
		this.mandantory = mandantory;
		this.multiple = multiple;
	}
	
	public String toString(){
		String key = "";

		if (this.mandantory == true)
			key += "[1..";
		else
			key += "[0..";

		if (this.multiple == false)
			key += "1]";
		else
			key += "2]";
		
		return this.structuredType + ".default."+ this.name + key;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getType(){
		return this.structuredType;
	}
}