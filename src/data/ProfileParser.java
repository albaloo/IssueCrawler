package data;
import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import analysis.CalculateVariables;


public class ProfileParser{
	public void parseProfile(UserProfileInfo currentParticipant, String filePath, String SUFFIX) throws IOException{
    	File threadFile = new File(filePath + FindAuthorLinkFromPartialLink(currentParticipant.getProfileLink()) +SUFFIX +".txt");
		Document doc = Jsoup.parse(threadFile, null);
		
		Element infoBlock = doc.select("div[class=main]").first();
		
		if(infoBlock != null){
			Elements infos= infoBlock.select("h3");
			
			for (Element info : infos) {
				if(info.text().equals("Personal information")){
					Element dl = info.nextElementSibling();
					Elements dts = dl.select("dt");
					for (Element dt : dts){
						if(dt.text().equals("First or given name")){
							Element dd = dt.nextElementSibling();
							currentParticipant.setFirstName(dd.text());
						}else if(dt.text().equals("Last name or surname")){
							Element dd = dt.nextElementSibling();
							currentParticipant.setLastName(dd.text());
						}else if(dt.text().equals("Interests")){
							Element dd = dt.nextElementSibling();
							String[] interests= dd.text().split(", ");
							for (String string : interests) {
								if(string != "")
									currentParticipant.addInterest(string);
							}
						}		
					}
					
				}else if(info.text().equals("Work")){
					Element dl = info.nextElementSibling();
					Elements dts = dl.select("dt");
					for (Element dt : dts){
						if(dt.text().equals("Job title")){
							Element dd = dt.nextElementSibling();
							currentParticipant.setJobTitle(dd.text());
						}	
					}
					
				}else if(info.text().equals("History")){
					Element dl = info.nextElementSibling();
					Elements dts = dl.select("dt");
					for (Element dt : dts){
						if(dt.text().equals("Member for")){
							Element dd = dt.nextElementSibling();
							currentParticipant.setMembershipWeeks(dd.text());
						}	
					}
				}
			}
		}

    }
	
	public static String FindAuthorLinkFromPartialLink(String link) {
		String name = (link.replaceFirst("user", "")).substring(2);
		int index = name.indexOf('#');
		if (index > 0)
			name = name.substring(0, index);
		return name;
	}
	
	public static void main(String[] args) {
		ProfileParser parser = new ProfileParser();
		UserProfileInfo currentParticipant = new UserProfileInfo("ParisLiakos", "/user/4166");//"/user/4166");
		try {
			parser.parseProfile(currentParticipant, CalculateVariables.profileListFileName, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(currentParticipant);
	}
	

}
