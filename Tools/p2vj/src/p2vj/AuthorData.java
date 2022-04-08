package p2vj;
public class AuthorData extends UserEditableData{
	
	AuthorData(){
		super();
		add("Name","");
		add("Website","");
		add("WebsiteName","");
		//add("MailAdress","");
		add("Publisher","");
		add("PublisherWebsite","");
	}
	
	public String getAuthorName(){
		return data.get("Name");
	}
	public String getPublisherName(){
		return data.get("Publisher");
	}
	public String getMailAddress(){
		return data.get("MailAdress");
	}
	public String getAuthorWebsite(){
		return data.get("Website");
	}
	public String getAuthorWebsiteName(){
		return data.get("WebsiteName");
	}
	public String getPublisherWebsite(){
		return data.get("PublisherWebsite");
	}

	public void setAuthorName(String s){
		if(s == null){
			s = "";
		}
		data.put("Name",s);
	}
	public void setPublisherName(String s){
		if(s == null){
			s = "";
		}
		data.put("Publisher",s);
	}
	public void setMailAddress(String s){
		if(s == null){
			s = "";
		}
		data.put("MailAdress",s);
	}
	public void setAuthorWebsite(String s){
		if(s == null){
			s = "";
		}
		data.put("Website",s);
	}
	public void setAuthorWebsiteName(String s){
		if(s == null){
			s = "";
		}
		data.put("WebsiteName",s);
	}
	public void setPublisherWebsite(String s){
		if(s == null){
			s = "";
		}
		data.put("PublisherWebsite",s);
	}
	public void add(String l,String def){
		data.put(l,def);
		order.add(l);
	}
	
}


