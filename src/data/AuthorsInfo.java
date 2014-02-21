import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;


public class AuthorsInfo {

	private String author;
	private String authorLink;
	private int comments;
	private int threads;
	private ArrayList<Date> dates = new ArrayList<Date>();
	private int longestThread = 0; //The largest thread they have participated in.
	private int membershipWeeks = 0;
	private String jobTitle = "";
	private ArrayList<String> interests = new ArrayList<String>();
	
	public AuthorsInfo(String author, String authorLink) {
		this.author = author;
		this.authorLink = authorLink;
		comments = 0;
		threads = 0;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorLink() {
		return authorLink;
	}

	public void setAuthorLink(String authorLink) {
		this.authorLink = authorLink;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public void addComments() {
		this.comments++;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public void addThreads() {
		this.threads++;
	}

	public void addDate(Date date){
		dates.add(date);
	}

	public void addDates(ArrayList<Date> dates2) {
		dates.addAll(dates2);
	}
	public ArrayList<Date> getDates(){
		return dates;
	}
	
	public Date getDate(int i){
		return dates.get(i);
	}
	
	public int getLongestThread(){
		return longestThread;
	}
	
	public void setLongestThread(int longestThread){
		if(longestThread > this.longestThread)
			this.longestThread = longestThread;
	}
		
	public void parse (String spec, HTMLEditorKit.ParserCallback lister) throws IOException{
		Reader r = null;
           if (spec.indexOf("://") > 0) {
           URL u;
		try {
			u = new URL(spec);
		   Object content = u.getContent();
		   
           if (content instanceof InputStream) {
               r = new InputStreamReader((InputStream)content);
           }
           else if (content instanceof Reader) {
               r = (Reader)content;
           }
           else {
               try {
				throw new Exception("Bad URL content type.");
               } catch (Exception e) {
				e.printStackTrace();
               }
           }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
          }
           else {
           r = new FileReader(spec);
          }
       
       HTMLEditorKit.Parser parser;
       parser = new ParserDelegator();
       parser.parse(r, lister, true);
       r.close();
   }

	public void parseProfile() {
		ProfileParseLister lister = new ProfileParseLister();
		try{
			parse(getAuthorLink(), lister);
		}catch(IOException e){
			System.out.println("secure link/access denied");
		}
		interests = lister.getInterests();
		setJobTitle(lister.getJobTitle());
		try{
		setMembershipWeeks(findMembershipWeeks(lister.getMemberFor()));
		}catch( NumberFormatException e){
			System.out.println("lister.getMemberFor(): " + lister.getMemberFor());
			System.out.println("AuthorLink: "+ getAuthorLink());
		}
	}

	private int findMembershipWeeks(String value){
		int result = 0;
		int years = 0;
		int weeks = 0;
		
			if(value == "" || value == null)
				return result;
			else{
				int spaceIndex = value.indexOf(" ");
				if(value.startsWith("over"))
					value = value.substring(spaceIndex+1);
				spaceIndex = value.indexOf(" ");
				int yearIndex = value.indexOf("year");
				
				if(spaceIndex != -1 && yearIndex != -1){
					years = Integer.parseInt(value.substring(0, spaceIndex));
					
					if(yearIndex+9 < value.length()){
						value = value.substring(yearIndex);
						spaceIndex = value.indexOf(" ");
						int weekIndex = value.indexOf(" week");
						if(weekIndex != -1 && spaceIndex != -1){
							weeks = Integer.parseInt(value.substring(spaceIndex+1,weekIndex));
						}
					}
				}else{
					int weekIndex = value.indexOf(" week");
					if(weekIndex != -1 && spaceIndex != -1)
						weeks = Integer.parseInt(value.substring(0,weekIndex));
				}
			}
		
		result = years*52+weeks;
		return result;
	}

	public int matchingInterest(ArrayList<String> otherInterests){
		int result = 0;
		for (String interest : interests) {
			for (String otherInterest : otherInterests) {
				if(interest.equals(otherInterest))
					result++;
			}
		}
		return result;
	}

	public ArrayList<String> getInterests (){
		return interests;
	}
	
	public boolean hasInterest(String interest) {
		for (String inter : interests) {
			if(inter.equals(interest))
				return true;
		}
		return false;
	}

public boolean sharedInterest(AuthorsInfo other){
	for (String interest : interests) {
		for (String otherInterest : other.interests) {	
			if(interest.equals(otherInterest))
				return true;
		}
	}
	return false;
}
public void setMembershipWeeks(int membershipWeeks) {
		this.membershipWeeks = membershipWeeks;
	}

	public int getMembershipWeeks() {
		return membershipWeeks;
	}


public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getJobTitle() {
		return jobTitle;
	}


//Extract authors infos
class HTMLParseListerAuthors extends HTMLEditorKit.ParserCallback
{   
	private ArrayList<CommentInfo> foundCommentInfos = new ArrayList<CommentInfo>();
	private CommentInfo currentCommentInfo;
//	private String currentContent;
//	private String currentPlainContent;
	private String nextPage;
	private boolean pagerNext = false;
//	private boolean isComment = false;
//	private boolean isTitle = false;
	private boolean isCommentNumber = false;
    private boolean isAuthor = false;
    private boolean isAuthorURL = false;
    private boolean isDate = false;
    
    public void handleText(char[] data, int pos) {
    	String temp = new String(data);
    	if(isAuthor){
    		currentCommentInfo.setAuthor(temp);
    		isAuthor = false;
    	}else if(isCommentNumber){
    		currentCommentInfo.setCommentNumber(temp);
    		isCommentNumber = false;
    	}else if(isDate){
    		currentCommentInfo.setDate(temp);
    		isDate = false;
    		foundCommentInfos.add(currentCommentInfo);
    	}
    	
    }

    public ArrayList<CommentInfo> getCommentInfos() {
		return foundCommentInfos;
	}

    public int getNumCommentInfos() {
		return foundCommentInfos.size();
	}
    
	public String getNextPage() {
		return nextPage;
	}

	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {       

		String divValue = (String)a.getAttribute(HTML.Attribute.CLASS);
		if (t.toString().equals("div") && /*isComment == true &&*/ divValue != null && divValue.equals("submitted")){
    		isAuthorURL = true;
    		currentCommentInfo = new CommentInfo();
		}
		String aValue = (String)a.getAttribute(HTML.Attribute.HREF);
    	if (t.toString().equals("a") && aValue != null && isAuthorURL){
    		currentCommentInfo.setAuthorLink("http://drupal.org" + aValue);
    		isAuthor = true;
    		//isAuthorURL = false;
    	}
    
    	if (t.toString().equals("em") && isAuthorURL){
    		isDate = true;
    		isAuthorURL = false;
    	}
    		
    	if(t.toString().equals("a") && pagerNext == true){
    		nextPage = (String)a.getAttribute(HTML.Attribute.HREF);
    		pagerNext = false;
    	}
    	                		
    }
	
    
	public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
	}

	public void handleEndTag(HTML.Tag t, int pos) {
    }
}


}