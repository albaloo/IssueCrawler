import java.util.ArrayList;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;


public class IssueParseLister extends HTMLEditorKit.ParserCallback{
	
	private ArrayList<CommentInfo> foundCommentInfos = new ArrayList<CommentInfo>();
	private CommentInfo currentCommentInfo;
	private String currentPlainContent = " ";
	private String nextPage;
	private boolean pagerNext = false;
	private boolean isComment = false;
    private boolean isAuthor = false;
    private boolean isAuthorURL = false;
    private boolean isContent = false;
    private boolean isP = false;
    private boolean isDate = false;
    private boolean isIssue = false;
    private boolean isIssueP = false;
    private boolean isIssueContent = false;
    private boolean isPossiblyScreenshot = false;
    private boolean isNumFollowers = false;
    private boolean isCommentTitle = false;
    private boolean isCommentNumber = false;
    
    public void handleText(char[] data, int pos) {
    	String temp = new String(data);
    	if(isAuthor){
    		currentCommentInfo.setAuthor(temp);
    		isAuthor = false;
    	}else if(isContent && isP){
    			currentPlainContent += temp;
    	}else if(isDate){
    		currentCommentInfo.setDate(temp);
    		isDate = false;
    		foundCommentInfos.add(currentCommentInfo);
    		//foundCommentInfos.add(currentCommentInfo);
    	}else if(isIssue && isIssueP){
    		currentPlainContent += temp;
    	}else if (isNumFollowers){
    		currentCommentInfo.setNumFollowers(temp);
    		isNumFollowers = false;
    	}else if(isCommentNumber){
    		currentCommentInfo.setCommentNumber(temp);
    		isCommentNumber = false;
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

	/** TODO: handle special case: 
	 * 1- image may be hosted elsewhere <a href= "">
	 * 
	 */
	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {       
		
		String divClass = (String)a.getAttribute(HTML.Attribute.CLASS);
		String divID = (String)a.getAttribute(HTML.Attribute.ID);
    	if (t.toString().equals("div") && divClass != null && divClass.equals("comment-inner")){
    		isComment = true;
    		return;
		}else if (t.toString().equals("div") && /*isComment == true &&*/ divClass != null && divClass.equals("submitted")){
    		isAuthorURL = true;
		}else if (t.toString().equals("div") && isComment == true && divClass != null && divClass.equals("content")){
    		isContent = true;
    		currentPlainContent = " ";
    		isComment = false;
    		return;
    	}else if (t.toString().equals("p") && isContent){
    		isP = true;
    	}else if (t.toString().equals("tr") && divClass != null && divClass.equals("pift-pass odd")){
    		//Patch
    		currentCommentInfo.setPatchAttached(true);
    	}else if (t.toString().equals("tr") && divClass != null && divClass.equals("pift-fail odd")){
    		//Patch
    		currentCommentInfo.setPatchAttached(true);
    	}else if(t.toString().equals("div") && divClass != null && divClass.equals("project-issue")){
    		//<div class="project-issue">
    		currentPlainContent = "";
    		isIssue = true;
    	}else if(t.toString().equals("p") && isIssue){
    		isIssueP = true;
    	}else if (t.toString().equals("tr") && divClass != null && (divClass.equals(" odd") || divClass.equals(" even"))){
    		//Patch
    		isPossiblyScreenshot = true;
    	}else if(t.toString().equals("div") && divClass != null && divClass.equals("project-issue-follow-count")){
    		//<div class="project-issue-follow-count">27 followers</div>
    		isNumFollowers = true;
    	}else if(t.toString().equals("div") && divID != null && divID.equals("content-inner")){
    		currentCommentInfo = new CommentInfo();
    	}
    	
    	//<p><img src="http://img.skitch.com/20081108-g3qhmc2jtccfxj2itkk3rw3349.png" alt="Big Ugly box" /></p>
    	if(t.toString().equals("img"))
    		currentCommentInfo.addScreenshot();
    
    	String h3Value = (String)a.getAttribute(HTML.Attribute.CLASS);
    	if (t.toString().equals("h3") && isComment == true && h3Value != null && h3Value.equals("comment-title")){
    		isCommentTitle = true;
    		currentCommentInfo = new CommentInfo();
    		return;
    	}
    	
    
    	
		String aValue = (String)a.getAttribute(HTML.Attribute.HREF);
    	if (t.toString().equals("a") && aValue != null && isAuthorURL){
    		currentCommentInfo.setAuthorLink("http://drupal.org" + aValue);
    		isAuthor = true;
    		//isAuthorURL = false;
    	}else if(t.toString().equals("a") && aValue != null && isPossiblyScreenshot && (aValue.contains(".png") || aValue.contains(".jpeg"))){
    		currentCommentInfo.addScreenshot();
    		isPossiblyScreenshot = false;
    		//href.attr("href").contains("drupal") || href.attr("href").startsWith("/") || href.attr("href").startsWith("#") || href.attr("href").equals("") || href.attr("href").startsWith("@"))){
    	}else if(t.toString().equals("a") && aValue != null && !(aValue.contains("@") || aValue.contains("#") || aValue.startsWith("/") || aValue.contains("drupal") || aValue.contains("Drupal") || aValue.equals("") || aValue.contains(".png") || aValue.contains(".jpeg")) && currentCommentInfo != null){ //aValue.contains(".png") || aValue.contains(".jpeg")
    		currentCommentInfo.addOutsideResources();
    	}else if (t.toString().equals("a") && aValue != null && isCommentTitle){
    		currentCommentInfo.setTitle("http://drupal.org" + aValue);
    		isCommentTitle = false;
    		isCommentNumber = true;
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
    	if (t.toString().equals("div") && isContent && isP){
    		isContent = false;
    		isP = false;
    		currentCommentInfo.setPlainContent(currentPlainContent);
    	}else if(t.toString().equals("div") && isIssue && isIssueP){
    		isIssue = false;
    		isIssueP = false;
    		currentCommentInfo.setPlainContent(currentPlainContent);
    	}
    }

}
