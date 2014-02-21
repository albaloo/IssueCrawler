package data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import analysis.SocialGraph;
import analysis.Stats;


public class IssueInfo {
	
	//TODO: numfollowers
	
	/*Issue Info Data*/
	private int numPages = 1;
	private String content;
	private String link;
	private String status;
	private String category;
	private String priority;
	private String version;
	private Date date;
	private String title;
	private String project;
	private String component;
	private String assigned;
	private ArrayList<String> tags = new ArrayList<String>();
	private int numComments;
	private ArrayList<Date> commits = new ArrayList<Date>();
	private ArrayList<CommentInfo> comments = new ArrayList<CommentInfo>();
	
	public IssueInfo(String link, int numPages) {
		super();
		this.link = link;
		this.numPages= numPages; 
	}	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(String dateStr) {			
		this.date = convertToDate(dateStr);
	}	
	private Date convertToDate(String dateStr){
		Date date = new Date();
		
		dateStr = dateStr.replace(" at ", " ");
		DateFormat df = new SimpleDateFormat("MMMMMMMM d, yyyy h:mmaa");
		
		try{
			date = df.parse(dateStr);
		}catch(ParseException e){
			e.printStackTrace();
		}
		return date;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public int getNumPages() {
		return numPages;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAssigned() {
		return assigned;
	}
	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	public void addTags(String tag) {
		this.tags.add(tag);
	}
	
	public ArrayList<Date> getCommits(){
		return commits;
	}
	public void addCommits(Date commit){
		commits.add(commit);
	}
	public void setCommits(ArrayList<Date> commits){
		this.commits = commits;
	}
	public ArrayList<CommentInfo> getComments(){
		return comments;
	}
	public void addComments(CommentInfo comment){
		comments.add(comment);
	}
	public void setComments(ArrayList<CommentInfo> comments){
		this.comments = comments;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getNumComments() {
		return numComments;
	}
	public void setNumComments(int numComments) {
		this.numComments = numComments;
	}
	public int getPriorityNum(){
		if (priority.equals("critical"))
			return 3;
		else if (priority.equals("normal"))
			return 2;
		else if (priority.equals("minor"))
			return 1;
		else return -1;
	}
	public int getStatusNum(){
		if (status.equals("active"))
			return 1;
		else if (status.equals("needs work"))
			return 1;
		else if (status.equals("needs review"))
			return 1;
		else if (status.equals("reviewed &amp; tested by the community"))
			return 1;
		else if (status.startsWith("reviewe"))
			return 1;
		else if (status.equals("patch (to be ported)"))
			return 1;
		else if (status.equals("fixed"))
			return 2;
		else if (status.equals("postponed"))
			return 1;
		else if (status.equals("postponed (maintainer needs more info)"))
			return 1;
		else if (status.equals("closed (duplicate)"))
			return 3;
		else if (status.equals("closed (won&#039;t fix)"))
			return 3;
		else if (status.equals("closed (works as designed)"))
			return 3;
		else if (status.equals("closed (cannot reproduce)"))
			return 3;
		else if (status.equals("closed (fixed)"))
			return 3;
		else if (status.startsWith("close"))
			return 3;
		else return -1;
	}
	public boolean isConsensus(){
		if(getStatusNum() == 2 || getStatusNum() == 3)
			return true;
		else
			return false;
	}
}
