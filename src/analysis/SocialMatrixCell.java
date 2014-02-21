package analysis;
import java.util.ArrayList;
import java.util.Date;

import data.CommentInfo;


public class SocialMatrixCell {

	private ArrayList<CommentInfo> comments = new ArrayList<CommentInfo>();
	private double value = 0;

	private double findNumWords(String commentContent){
		if(commentContent != null){
			String[] arr = commentContent.split(" ");
			return arr.length;
		}else
			return 0;
	}

	public SocialMatrixCell(CommentInfo commentInfo) {
		this.comments.add(commentInfo);
	}

	public SocialMatrixCell() {
	}

	public double getValue() {
		return value;
	}

	public double calculateValue(){
		value = 0;
		for (int i = 0; i < comments.size(); i++) {
			value += findNumWords(comments.get(i).getContent());
		}
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	public ArrayList<CommentInfo> getComments() {
		return comments;
	}

	public void setComments(ArrayList<CommentInfo> comments) {
		this.comments = comments;
	}
	
	public void addComment(CommentInfo comment){
		this.comments.add(comment);
	}
	
	public void addComments(ArrayList<CommentInfo> comments){
		this.comments.addAll(comments);
	}
	
	public boolean isEmpty(Date date){
		if(comments.size() > 0 && commentsDateLessthanFirstDate(date))
			return false;
		else
			return true;
	}
	
	private boolean commentsDateLessthanFirstDate(Date date){
		for (int i = 0; i < comments.size(); i++) {
			if(comments.get(i).getDate().getTime() < date.getTime())
				return true;
		}
		
		return false;
	}
	
	public int getNumConsensusThreads(Date date){
		int result = 0;
		for (int i = 0; i < comments.size(); i++) {
		//TODO:	if(comments.get(i).isConsensus() && comments.get(i).getDate().getTime() < date.getTime())
				result ++;
		}
		return result;
	}
	
	public int getNumPreviousComments(Date date){
		int result = 0;
		for (int i = 0; i < comments.size(); i++) {
			if(comments.get(i).getDate().getTime() < date.getTime())
				result ++;
		}
		return result;
	}
}
