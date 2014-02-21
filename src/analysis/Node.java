package analysis;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import data.CommentInfo;
import data.IssueInfo;

public class Node {

	public static int THREAD = 0;
	public static int AUTHOR = 1;
	private static int INDEX_NUMBER = 0;

	private int type;
	private int numComments;
	private String name;
	private String link;
	private int index = 0;
	private double outgoingEdges = 0;
	private Double rank;
	private boolean consensus;

	private TreeMap<String, SocialMatrixCell> authorsRepliedTo = new TreeMap<String, SocialMatrixCell>();// authors whom I replied to
	private TreeMap<String, SocialMatrixCell> participatingThreads = new TreeMap<String, SocialMatrixCell>();

	public Node(int type, String name, String link, int numComments,
			boolean consensus) {
		this.type = type;
		this.name = name;
		this.link = link;
		this.numComments = numComments;
		this.consensus = consensus;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getNumComments() {
		return numComments;
	}

	public void setNumComments(int numComments) {
		this.numComments = numComments;
	}

	public boolean isConsensus() {
		return consensus;
	}

	public void setConsensus(boolean consensus) {
		this.consensus = consensus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setOutgoingEdges(double outgoingEdges) {
		this.outgoingEdges = outgoingEdges;
	}

	public double getOutgoingEdges() {
		return outgoingEdges;
	}

	public void addThread(String name, CommentInfo commentInfo) {
		SocialMatrixCell cell = participatingThreads.get(name);
		if (cell != null) {
			cell.addComment(commentInfo);
			participatingThreads.put(name, cell);
		} else
			participatingThreads.put(name, new SocialMatrixCell(commentInfo));
	}

	public void addThreads(TreeMap<String, SocialMatrixCell> threads) {
		Set<String> keys = threads.keySet();
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			SocialMatrixCell value = (SocialMatrixCell) threads.get(key);

			SocialMatrixCell masterValue = participatingThreads.get(key);
			if (masterValue != null)
				value.addComments(masterValue.getComments());

			participatingThreads.put(key, value);
		}
	}

	public void addReplier(String name, CommentInfo commentInfo) {
		SocialMatrixCell cell = authorsRepliedTo.get(name);
		if (cell != null) {
			cell.addComment(commentInfo);
			authorsRepliedTo.put(name, cell);
		} else
			authorsRepliedTo.put(name, new SocialMatrixCell(commentInfo));
	}

	public void addRepliers(TreeMap<String, SocialMatrixCell> repliers) {
		Set<String> keys = repliers.keySet();
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			SocialMatrixCell value = (SocialMatrixCell) repliers.get(key);

			SocialMatrixCell masterValue = authorsRepliedTo.get(key);
			if (masterValue != null)
				value.addComments(masterValue.getComments());

			authorsRepliedTo.put(key, value);
		}
	}

	public TreeMap<String, SocialMatrixCell> getThreads() {
		return participatingThreads;
	}

	public TreeMap<String, SocialMatrixCell> getRepliers() {
		return authorsRepliedTo;
	}

	public void addNumComments(int num) {
		numComments += num;

	}

	public void setIndex() {
		index = INDEX_NUMBER;
		INDEX_NUMBER++;
	}

	public int getIndex() {
		return index;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

	public Double getRank() {
		return rank;
	}

	// TODO: why we are adding one link for the ones that have replies?
	public void updateRepliersAndThreads(IssueInfo issueInfo,
			CommentInfo commentInfo, double duration) {
		if (commentInfo.getReceiverNames().size() != 0) {
			for (String receiver : commentInfo.getReceiverNames()) {
				this.addReplier(receiver, commentInfo);
			}
		} else
			this.addThread(
					(issueInfo.getLink().replaceFirst("node", "")).substring(2),
					commentInfo);
	}

}
