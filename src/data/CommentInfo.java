import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentInfo {
		private String title;
		private int commentNumber;
		private String author;
		private String authorLink;
		private String content;
		private String plainContent;
		private ArrayList<String> images = new ArrayList<String>();
		private Date date;
		private Boolean patchAttached = false;
		private Boolean isConsensus = false;
		private int screenshot = 0;
		private int numFollowers = 0;
		private int numOutsideResources = 0;
		
		public Boolean isConsensus() {
			return isConsensus;
		}
		public void setConsensus(Boolean isConsensus) {
			this.isConsensus = isConsensus;
		}
		public int getNumScreenshots() {
			return screenshot;
		}
		public void setScreenshot(int screenshot) {
			this.screenshot = screenshot;
		}
		public void addScreenshot(){
			this.screenshot++;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getCommentNumber(){
			return commentNumber;
		}
		public void setCommentNumber(String commentNumber){
			commentNumber = commentNumber.substring(1);
			this.commentNumber = Integer.parseInt(commentNumber);
		}
		public String getAuthor() {
			return author;
		}
		public void setAuthor(String author) {
			this.author = author;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getPlainContent() {
			return plainContent;
		}
		public void setPlainContent(String plainContent) {
			this.plainContent = plainContent;
		}
		public void addImage(String img){
			if(!images.contains(img))
				images.add(img);
		}
		public int getNumImages(){
			return images.size();
		}
		public String getImageAt(int index){
			return images.get(index);
		}
		public ArrayList<String> getImages(){
			return images;
		}
		public String getAuthorLink() {
			return authorLink;
		}
		public void setAuthorLink(String link) {
			this.authorLink = link;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(String dateStr) {
			
			this.date = convertToDate(dateStr);
		}	
		public void setPatchAttached(Boolean patchAttached){
			this.patchAttached = patchAttached;
		}
		public Boolean getPatchAttached(){
			return patchAttached;
		}
		public int getNumFollowers(){
			return numFollowers;
		}
		public void setNumFollowers(String numFollowers){
			if(numFollowers!=null && !numFollowers.equals("")){
				String[] words = numFollowers.split(" ");
				this.numFollowers = Integer.parseInt(words[0]);
			}
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

		public void addOutsideResources() {
			this.numOutsideResources ++;
		}
		public int getNumOutsideResources(){
			return numOutsideResources;
		}
}
		