package analysis;
import java.util.ArrayList;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;


public class ProfileParseLister extends HTMLEditorKit.ParserCallback{
	
	private ArrayList<String> interests = new ArrayList<String>();
	private String jobTitle = "";
	private String memberFor = "";
	private boolean isJobTitle = false;
	private boolean isMemberFor = false;
	private boolean isInterests = false;
	private boolean isInterest = false;
	private boolean isUserHistory = false;
	
    public void handleText(char[] data, int pos) {
    	String temp = new String(data);
    	if(isJobTitle){
    		jobTitle += temp;
    		isJobTitle = false;
    	}else if(isInterests && isInterest){
    		String interest = new String();
    		interest += temp;
    		if(!temp.equals(", "))
    			interests.add(interest);
    		isInterest = false;
    	}else if(isMemberFor && (temp.contains(" year") || temp.contains(" week"))){
    		memberFor += temp;
    		isMemberFor = false;
    		isUserHistory = false;
    	}
    	
    }

    public ArrayList<String> getInterests() {
		return interests;
	}

    public String getMemberFor() {
		return memberFor;
	}

    public String getJobTitle() {
		return jobTitle;
	}

	/** TODO: handle special case: 
	 * 1- image may be hosted elsewhere <a href= "">
	 * 
	 */
	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {       
		
		//<dd class="profile-profile_interest grid-6 omega"><a href="/profile/profile_interest/webchick">webchick</a>, <a href="/profile/profile_interest/information%20architecture">information architecture</a>, <a href="/profile/profile_interest/interaction%20design">interaction design</a>, <a href="/profile/profile_interest/usability">usability</a></dd> 
		//<dd class="profile-profile_job grid-6 omega">Usability Expert</dd>
		//<dd class="grid-6 omega">4 years 30 weeks</dd> 
		String divValue = (String)a.getAttribute(HTML.Attribute.CLASS);
    	if (t.toString().equals("dd") && divValue != null && divValue.equals("profile-profile_interest grid-6 omega")){
    		isInterests = true;
		}else if (t.toString().equals("dd") &&  divValue != null && divValue.equals("profile-profile_job grid-6 omega")){
    		isJobTitle = true;
    		return;
		}else if (t.toString().equals("dd") && divValue != null && divValue.equals("grid-6 omega") && isUserHistory){
    		isMemberFor = true;
    		return;
    	}else if (t.toString().equals("dl") && divValue != null && divValue.equals("user-member clear-block")){
    		isUserHistory = true;
    		return;
    	}
    	
		String aValue = (String)a.getAttribute(HTML.Attribute.HREF);
    	if (t.toString().equals("a") && aValue != null && isInterests){
    		isInterest = true;
    	}
     	                		
    }
	
    
	public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
	}

	public void handleEndTag(HTML.Tag t, int pos) {
    	if (t.toString().equals("dd") && isInterests){
    		isInterests = false;
    		isInterest = false;
    	}
    }


}
