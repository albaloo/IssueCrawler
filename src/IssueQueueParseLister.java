import java.util.ArrayList;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;


public class IssueQueueParseLister extends HTMLEditorKit.ParserCallback {   
    private ArrayList<IssueInfo> foundIssues = new ArrayList<IssueInfo>();
    private String nextPage = new String("");
    boolean pagerNext = false;
    private IssueInfo currentIssue;
    private boolean isTdStatus = false;
    private boolean isTdPriority = false;
    private boolean isTdCategory = false;
    private boolean isTdVersion = false;
    private boolean isTdNumReplies = false;
   
    
    public void handleText(char[] data, int pos) {
    	String temp = new String(data);
    	if(isTdStatus){
    		currentIssue.setStatus(temp);
    		isTdStatus = false;
    	}else if(isTdPriority){
    		currentIssue.setPriority(temp);
    		isTdPriority = false;
    	}else if(isTdCategory){
    		currentIssue.setCategory(temp);
    		isTdCategory = false;
    	}else if(isTdVersion ){
    		currentIssue.setVersion(temp);
    		isTdVersion = false;
    	}else if(isTdNumReplies ){
    		currentIssue.setNumComments(Integer.parseInt(temp));
    		foundIssues.add(currentIssue);
    		isTdNumReplies = false;
    	}
    	
    }

    public String getNextPage() {
		return nextPage;
	}

	public void handleComment(char[] data, int pos) {
    }

    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {       
    	String value = (String)a.getAttribute(HTML.Attribute.HREF);
    	if(value != null && value != "" && value.startsWith("/node")){
    		currentIssue = new IssueInfo();
    		currentIssue.setLink(value);
    	}
    	        	
    	String liValue = (String)a.getAttribute(HTML.Attribute.CLASS);
    	if (t.toString().equals("li") && liValue != null && liValue.equals("pager-next"))
    		pagerNext = true;
    	
    	if(t.toString().equals("a") && pagerNext == true){
    		nextPage = (String)a.getAttribute(HTML.Attribute.HREF);
    		pagerNext = false;
    	}

    	String tdValue = (String)a.getAttribute(HTML.Attribute.CLASS);
    	if (t.toString().equals("td") && tdValue != null){
    		if(isTdNumReplies){
    			currentIssue.setNumComments(0);
        		foundIssues.add(currentIssue);
        		isTdNumReplies = false;
    		}			
    		if(tdValue.equals("views-field views-field-sid")){
    			isTdStatus = true;
    		}
    		else if(tdValue.equals("views-field views-field-priority")){
        		isTdPriority = true;
    		}else if(tdValue.equals("views-field views-field-category")){
        		isTdCategory = true;
    		}else if(tdValue.equals("views-field views-field-version")){
        		isTdVersion = true;
    		}else if(tdValue.equals("views-field views-field-comment-count")){
        		isTdNumReplies = true;
    		}				
    	}
    }

    public void handleEndTag(HTML.Tag t, int pos) {
    }

    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
    }

    public void handleError(String errorMsg, int pos){
    }
    
    public ArrayList<IssueInfo> getFoundIssues(){
    	return foundIssues;
    }
    
    public int getNumOfFoundIssues(){
    	return foundIssues.size();
    }

}
