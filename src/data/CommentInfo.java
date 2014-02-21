package data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentInfo {
	// TODO: is consensus??
	private String title;
	private double commentNumber;
	private String author;
	private String authorLink;
	private String content;
	private ArrayList<String> images = new ArrayList<String>();
	private Date date;
	private Boolean patchAttached = false;
	private int screenshot = 0;
	private int numOutsideResources = 0;
	private int numRepliesRecieved = 0;

	public int getNumRepliesRecieved() {
		return numRepliesRecieved;
	}

	public void setNumRepliesRecieved(int numRepliesRecieved) {
		this.numRepliesRecieved = numRepliesRecieved;
	}

	public void increaseNumRepliesRecieved() {
		this.numRepliesRecieved++;
	}

	public int getNumScreenshots() {
		return screenshot;
	}

	public void setScreenshot(int screenshot) {
		this.screenshot = screenshot;
	}

	public void addScreenshot() {
		this.screenshot++;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getCommentNumber() {
		return commentNumber;
	}

	public void setCommentNumber(String commentNumber) {
		this.commentNumber = toNumber(commentNumber);
	}

	private double toNumber(String num) {
		num = num.substring(1);
		if (num == null || num.equals(""))
			return 0.0;
		else
			return Double.parseDouble(num);
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

	public void addImage(String img) {
		if (!images.contains(img))
			images.add(img);
	}

	public void setImages(ArrayList<String> images) {
		this.images = images;
	}

	public int getNumImages() {
		return images.size();
	}

	public String getImageAt(int index) {
		return images.get(index);
	}

	public ArrayList<String> getImages() {
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

	public void setPatchAttached(Boolean patchAttached) {
		this.patchAttached = patchAttached;
	}

	public Boolean getPatchAttached() {
		return patchAttached;
	}

	private Date convertToDate(String dateStr) {
		Date date = new Date();

		dateStr = dateStr.replace(" at ", " ");
		DateFormat df = new SimpleDateFormat("MMMMMMMM d, yyyy h:mmaa");

		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public void addOutsideResources() {
		this.numOutsideResources++;
	}

	public int getNumOutsideResources() {
		return numOutsideResources;
	}
}
