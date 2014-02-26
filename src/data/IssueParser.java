package data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import data.CommentInfo;
import data.ReplyToLink;


public class IssueParser{
	private IssueInfo currentIssue;
	private ArrayList<CommentInfo> currentComments = new ArrayList<CommentInfo>();
    
    public void parseIssue(IssueInfo issue, String filePath, String SUFFIX) throws IOException{
    	File threadFile = new File(filePath + IssueQueueParser.FindIssueNameFromPartialLink(issue.getLink()) +SUFFIX +".txt");
		Document doc = Jsoup.parse(threadFile, null);
		
		currentIssue = issue;
		parseComments(doc);
		
		// Go to the next page if any
		if (issue.getNumPages() > 1) {
			threadFile = new File(filePath + IssueQueueParser.FindIssueNameFromPartialLink(issue.getLink()) +SUFFIX +"-p2.txt");
			doc = Jsoup.parse(threadFile, null);
			parseComments(doc);
		}
		
		for (CommentInfo commentInfo : currentComments) {
			if(isNotSubscription(commentInfo)){
				issue.addComments(commentInfo);
				if(isCommit(commentInfo))
					issue.addCommits(commentInfo.getDate());
			}
		}

    }

    private void parseComments(Document doc) throws IOException {
		CommentInfo currentComment;
		Elements comments = doc.select("div[class~=(comment|comment comment-by-node-author) clearfix]");

		for (Element src : comments) {
			currentComment = new CommentInfo();
			Element number = src.select("a[class=permalink]").first();
			if(number == null){
				number = src.select("a[class=active]").first();
			}
			currentComment.setCommentNumber(number.text());
			
			Element participant = src.select("div.submitted > a").first();
			if (participant != null) {
				currentComment.setAuthor(new String(participant.text()));
				currentComment.setAuthorLink(new String(participant
						.attr("href")));
			}
			Element date = src.select("div.submitted > time").first();
			if(date == null){
				date = src.select("div.submitted > em").first();
			}
			currentComment.setDate(date.text());

			Element contentBlock = src.select("div.content").first();
			Element content = contentBlock.select("div[class^=field field-name-comment-body]").first();
			if(content == null){
				content = contentBlock.select("div[class=clear-block]").first();
			}
			currentComment.setContent(content.text());
						
			boolean hasPatch = hasPatch(contentBlock);
			currentComment.setPatchAttached(hasPatch);
			currentComment.setImages(findImages(contentBlock));
			
			ArrayList<ReplyToLink> receivers = findReplyReceivers(content.text(), currentIssue.getLink(), currentComment.getDate()); 
			
			ArrayList<String> receiverNames = null;
			if(!receivers.isEmpty())
				receiverNames = findAndupdateReceivers(receivers, currentComment.getCommentNumber());
			
			//Set receiver names
			if(receiverNames != null)
				currentComment.setReceiverNames(receiverNames);
			
			currentComments.add(currentComment);
		}
	}
    
	
	private int updateCommentReplyToCount(double num){
		for (int i = currentComments.size()-1; i >=0 ; i--) {
			if(currentComments.get(i).getCommentNumber() == num){
				//currentComments.get(i).increaseNumRepliesRecieved();
				return i;
			}
		}
		return -1;
	}
	
	private int updateCommentReplyToCount(String participant){
		for (int i = currentComments.size()-1; i >=0 ; i--) {
			if(currentComments.get(i).getAuthor() == participant || (currentComments.get(i).getAuthor()!= "" && currentComments.get(i).getAuthor()!= null && currentComments.get(i).getAuthor().startsWith(participant) && participant.length() >= 5)){
				//currentComments.get(i).increaseNumRepliesRecieved();
				return i;
			}
		}
		return -1;
	}

	//find receivers, updates them, and returns a list of their names.
	private ArrayList<String> findAndupdateReceivers(ArrayList<ReplyToLink> receivers, double currentCommentNumber){
		ArrayList<String> receiverNames = new ArrayList<String>();
		ArrayList<Integer> commentNumberIndexes = new ArrayList<Integer>();
		for (ReplyToLink ppLink : receivers) {
			if(ppLink.type.equals("Number")){
				double num = Double.parseDouble(ppLink.receiver);
				if(num == -1 || num >= currentCommentNumber){
					System.out.println("Error: " + num + " in thread: " + currentIssue.getLink());
				}else{
					commentNumberIndexes.add(updateCommentReplyToCount(num));
				}
				
			}else{
				commentNumberIndexes.add(updateCommentReplyToCount(ppLink.receiver));
			}
		}
		
		//remove duplicates
		Set<Integer> set = new HashSet<Integer>();
		for(int i = 0; i < commentNumberIndexes.size(); i++)
			set.add(commentNumberIndexes.get(i));
			
		for (Integer i : set) {
		  if(i != -1){
			  currentComments.get(i).increaseNumRepliesRecieved();
			  if(currentComments.get(i).getAuthor() != null && !currentComments.get(i).getAuthor().equals(""))
				  receiverNames.add(currentComments.get(i).getAuthor());
		  }
		}
		
		return receiverNames;
	}
	
	private boolean hasPatch(Element block){
		Elements pass_attachments = block.select("td[class^=pift-pass]");
		Elements fail_attachments = block.select("td[class^=pift-fail]");
		
		if(!pass_attachments.isEmpty() || !fail_attachments.isEmpty())
			return true;
		else
			return false;
	}
	
	private ArrayList<String> findImages(Element block){
		ArrayList<String> images = new ArrayList<String>();
		//<div class="field field-name-comment-body field-type-text-long field-label-hidden"><div class="field-items"><div class="field-item even"><p>Hi folks,</p>
		//<p><img src="http://drupal.org/files/issues/ContentBlock.jpg" alt="">
		Elements inlineImages = block.select("div[class^=field field-name-comment-body] img");
		if(inlineImages != null && !inlineImages.isEmpty())
		for (Element element : inlineImages) {
			if(!element.hasClass("file-icon") && !images.contains(element.attr("src")))
				images.add(element.attr("src"));
		}
		
		//<div class="field field-name-field-issue-changes field-type-nodechanges-revision-diff field-label-hidden"><div class="field-items"><div class="field-item even"><table class="nodechanges-field-changes">
		//<a href="https://drupal.org/files/issues/password-strength-meter.png" type="image/png; length=31424" title="password-strength-meter.png">password-strength-meter.png</a>
		Elements attachedImages = block.select("div[class^=field field-name-field-issue-changes] a[href$=.png]");
		if(attachedImages != null && !attachedImages.isEmpty())
			for (Element element : attachedImages) {
				if(!images.contains(element.attr("href")))
					images.add(element.attr("href"));
			}
		
		attachedImages = block.select("div[class^=field field-name-field-issue-changes] a[href$=.jpg]");
		if(attachedImages != null && !attachedImages.isEmpty())
			for (Element element : attachedImages) {
				if(!images.contains(element.attr("href")))
					images.add(element.attr("href"));
			}
		
		return images;
	}

	public static ArrayList<ReplyToLink> findReplyReceivers(String content, String thread, Date date){
		ArrayList<ReplyToLink> result = new ArrayList<ReplyToLink>();
		if(content != null){
			ArrayList<String> receiverNames = checkForAtSign(content);
			ArrayList<String> receiverCommentNumbers = checkForNumberSign(content);
			
			if(receiverNames.size() != 0)
				for (String receiver : receiverNames) 
					result.add(new ReplyToLink(receiver, thread, date, "Name"));
				
			if(receiverCommentNumbers.size() != 0)
				for (String receiver : receiverCommentNumbers) 
					result.add(new ReplyToLink(receiver, thread, date, "Number"));
		}
		return result;
	}
		
	public static  boolean isCommentNumber(String value){
		String number = "";
		
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if(chars[i] >= '0' && chars[i] <= '9'){
				number += chars[i];
			}else
				return false;
		}
			
		if(number != "" && number.length()<4)
			return true;
			
		return false;
	}
	
	private static ArrayList<String> checkForAtSign(String comment){
		ArrayList<String> result = new ArrayList<String>();
		int index = comment.indexOf("@");
		if(index != -1 && index+1 < comment.length() && comment.charAt(index+1)== ' ')//handling @ #34 case
			index = index+1;
		int endIndex = Integer.MAX_VALUE;	
		int indexSpace = comment.indexOf(" ", index+1);
		int indexColon = comment.indexOf(":", index+1);
		int indexComma = comment.indexOf(",", index+1);
		int indexDot = comment.indexOf(". ", index+1);
		int indexSlash = comment.indexOf("/", index+1);
		
		if(indexSpace != -1)
			endIndex = Math.min(endIndex, indexSpace);
		
		if(indexColon != -1)
			endIndex = Math.min(endIndex, indexColon);
		
		if(indexComma != -1)
			endIndex = Math.min(endIndex, indexComma);
		
		if(indexDot != -1)
			endIndex = Math.min(endIndex, indexDot);
		
		if(indexSlash != -1)
			endIndex = Math.min(endIndex, indexSlash);
		
		while(index != -1 && endIndex != Integer.MAX_VALUE){
			String temp = comment.substring(index+1, endIndex);
			if(!temp.contains("@"))
				result.add(temp);
			comment = comment.substring(endIndex);
			index = comment.indexOf("@");
			if(index != -1 && index+1 < comment.length() && comment.charAt(index+1)== ' ')//handling @ #34 case
				index = index+1;
			
			endIndex = Integer.MAX_VALUE;
			indexSpace = comment.indexOf(" ", index+1);
			indexColon = comment.indexOf(":", index+1);
			indexComma = comment.indexOf(",", index+1);
			indexDot = comment.indexOf(". ", index+1);
			indexSlash = comment.indexOf("/", index+1);
			
			if(indexSpace != -1)
				endIndex = Math.min(endIndex, indexSpace);
			
			if(indexColon != -1)
				endIndex = Math.min(endIndex, indexColon);
		
			if(indexComma != -1)
				endIndex = Math.min(endIndex, indexComma);
			
			if(indexDot != -1)
				endIndex = Math.min(endIndex, indexDot);
			
			if(indexSlash != -1)
				endIndex = Math.min(endIndex, indexSlash);
			
		}
		return result;
	}
	
	private static ArrayList<String> checkForNumberSign(String comment){
		ArrayList<String> result = new ArrayList<String>();
		int index = comment.indexOf("#");
		int endIndex = Integer.MAX_VALUE;
		
		int indexSpace = comment.indexOf(" ", index);
		int indexColon = comment.indexOf(":", index);
		int indexComma = comment.indexOf(",", index);
		int indexDot = comment.indexOf(".", index);
		int indexSlash = comment.indexOf("/", index);
		
		if(indexSpace != -1)
			endIndex = Math.min(endIndex, indexSpace);
		
		if(indexColon != -1)
			endIndex = Math.min(endIndex, indexColon);
		
		if(indexComma != -1)
			endIndex = Math.min(endIndex, indexComma);
		
		if(indexDot != -1)
			endIndex = Math.min(endIndex, indexDot);
		
		if(indexSlash != -1)
			endIndex = Math.min(endIndex, indexSlash);
		
		while(index != -1 && endIndex != Integer.MAX_VALUE){
			String temp = comment.substring(index+1, endIndex);
			if(isCommentNumber(temp))
				result.add(temp);
			comment = comment.substring(endIndex);
			index = comment.indexOf("#");
			endIndex = Integer.MAX_VALUE;
			
			indexSpace = comment.indexOf(" ", index);
			indexColon = comment.indexOf(":", index);
			indexComma = comment.indexOf(",", index);
			indexDot = comment.indexOf(".", index);
			indexSlash = comment.indexOf("/", index);
			
			if(indexSpace != -1)
				endIndex = Math.min(endIndex, indexSpace);
			
			if(indexColon != -1)
				endIndex = Math.min(endIndex, indexColon);
		
			if(indexComma != -1)
				endIndex = Math.min(endIndex, indexComma);
			
			if(indexDot != -1)
				endIndex = Math.min(endIndex, indexDot);
			
			if(indexSlash != -1)
				endIndex = Math.min(endIndex, indexSlash);
			
		}
		return result;
	}
	
	 private boolean isNotSubscription(CommentInfo commentInfo) {
			String text = commentInfo.getContent();
			if (text == null)
				return true;
			String [] words = text.split("\\s+");
			if(words.length < 12 && text.contains("subscrib"))
				return false;
			return true;
		}

	    private boolean isCommit(CommentInfo commentInfo) {
			String text = commentInfo.getContent();
			String author = commentInfo.getAuthor();
			if (text == null)
				return false;
			if(text.contains("Committed to CVS HEAD"))
				return true;
			if((text.contains("Committed") || text.contains("committed")) && text.contains("HEAD") && (author.equals("Dries") || author.equals("webchick")))
				return true;
			return false;
		}
    
}
