
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.PageRank;

public class CalculateVariables {

	private ArrayList<IssueParseLister> innerListers = new ArrayList<IssueParseLister>();
	ArrayList<Double> durations = new ArrayList<Double>();
	ArrayList<Date> startDates = new ArrayList<Date>();
	ArrayList<IssueInfo> issueInfos = new ArrayList<IssueInfo>();
	
	SocialGraph socialGraph = new SocialGraph();
	String suffix = "";//-performance";
	
	File fileConsensus=new File("consensus-allvariables" + suffix +".txt");
    FileOutputStream fopConsensus;

	File fileNonConsensus=new File("non-consensus-allvariables" + suffix +".txt");
    FileOutputStream fopNonConsensus;

    File fileAuthors=new File("authors" + suffix +".txt");
    FileOutputStream fopAuthors;
        
    File fileDurations=new File("consensus-durations" + suffix +".txt");
    FileOutputStream fopDurations;
    
	DateFormat df = new SimpleDateFormat("MMM d yyy - h:mmaa");
	Date cutoffDate;
	
	public static void main(String [] args) {
		CalculateVariables demo = new CalculateVariables();
		demo.parsaAll();
	}
	
	public void parsaAll(){
		//URL of issues to parse
		
		//String spec = "http://drupal.org/project/issues/search/drupal?version[0]=7.x&issue_tags=Usability%2C%20d7ux";//args[0];
		String spec= "http://drupal.org/project/issues/search/drupal?issue_tags=Usability";
		//String spec = "http://drupal.org/project/issues/search/drupal?issue_tags=Performance";
		//String spec_d7ux = "http://drupal.org/project/issues/search?text=&projects=&assigned=&submitted=&participant=&categories%5B%5D=bug&categories%5B%5D=feature&issue_tags_op=or&issue_tags=d7ux";
		
	    try {
			initFiles();
			cutoffDate = df.parse("Feb 4 2009 - 4:17pm");
			if(fileConsensus.exists() && fileNonConsensus.exists() && fileAuthors.exists()){
				//Parse all the issues
				parseAllIssues(spec);
				//Sort the issue infos based on start time
				Collections.sort(issueInfos, new IssueInfoTimeComparator());
				//Go through the issues and print out their information
	
				//Social Matrix
				socialGraph.create(issueInfos);
				socialGraph.fillSocialMatrix();
				
				//printSocialMatrix();
				System.out.println("social graph started");
				socialGraph.fillSocialGraph();
				pageRank();
				System.out.println("print started");
				printIssueInfos();
				printAuthorsInfo();	
				System.out.println("print done");
				closeFiles();
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void closeFiles() throws IOException {
		fopConsensus.close();
		fopNonConsensus.close();
		fopAuthors.close();
		fopDurations.close();
	}

	private void printAuthorsInfo() throws IOException {
		for (AuthorsInfo authorsInfo : socialGraph.authorsInfos) {
			String temp = authorsInfo.getAuthor() + "\t" + authorsInfo.getAuthorLink() + "\t" + authorsInfo.getComments() + "\t" + authorsInfo.getThreads() + "\t" + authorsInfo.getMembershipWeeks() + "\t" + authorsInfo.getJobTitle() + "\t";
			ArrayList<Date> minMaxDate = findMinMaxDates(authorsInfo.getDates());
			double duration = Stats.findDuration(minMaxDate.get(0), minMaxDate.get(1));
			temp+= duration + "\t" + minMaxDate.get(0).getTime()+ "\t" + minMaxDate.get(1).getTime() + "\t"+ minMaxDate.get(0) + "\t" + minMaxDate.get(1)+ "\t" + authorsInfo.getInterests() +"\n";
			//if(authorsInfo.getLongestThread() > 25){
				fopAuthors.write(temp.getBytes());
				fopAuthors.flush();
			//}else{
				//fopLess25Authors.write(temp.getBytes());
				//fopLess25Authors.flush();
			//}
		}
	}

	private void parseAllIssues(String spec) {
		while(spec != null && !spec.equals("")){
			//Lister to parse the search page for the issues
			IssueQueueParseLister lister = new IssueQueueParseLister();
			parse(spec, lister);
			//Parse inside of each issue
			parseFoundLinks(lister.getFoundIssues());
			//Go to the next page if any
			if(lister.getNextPage() == null || lister.getNextPage().equals(""))
				break;
			spec = "http://drupal.org/" + lister.getNextPage();
			System.out.println("spec: " + spec);
		}
	}

	private void initFiles() throws FileNotFoundException {
		fopConsensus = new FileOutputStream(fileConsensus);
		fopNonConsensus = new FileOutputStream(fileNonConsensus);
		fopAuthors = new FileOutputStream(fileAuthors);
		fopDurations = new FileOutputStream(fileDurations);
	}
	
	private void printIssueInfos() throws ParseException, IOException{
	
		boolean cutoff_c = false;
		boolean cutoff_n = false;
	
		for (IssueInfo issueInfo : issueInfos) {
			String issueInfoPrint = issueInfo.printString(socialGraph);
			String durationInfo = issueInfo.printDurations();
			if(!issueInfo.isConsensus()){
					fopNonConsensus.write(issueInfoPrint.getBytes());
					if(issueInfo.getComments().get(0).getDate().getTime() > cutoffDate.getTime() && !cutoff_n){
						cutoff_n = true;
						String temp = "-------------";
						fopNonConsensus.write(temp.getBytes());
					}
					//fopAuthors.write(AuthorInfoPrint.getBytes());
					fopNonConsensus.flush();
				
			}else if(issueInfo.isConsensus()){
				fopDurations.write(durationInfo.getBytes());
				fopDurations.flush();
					fopConsensus.write(issueInfoPrint.getBytes());
					if(issueInfo.getComments().get(0).getDate().getTime() > cutoffDate.getTime() && !cutoff_c){
						cutoff_c = true;
						String temp = "-------------";
						fopConsensus.write(temp.getBytes());
					}
				//	fopConsensus.write(AuthorInfoPrint.getBytes());
					fopConsensus.flush();
			}else
				System.out.println("issue: ");
			
		}
	}

	
	
		
	private void pageRank() throws IOException{
		File filePageRank = new File("pageRank" + suffix +".txt");
		FileOutputStream fopPageRank = new FileOutputStream(filePageRank);
		
		Transformer<Edge, Double> wtTransformer = new Transformer<Edge,Double>() {
	        public Double transform(Edge link) {
	            return link.getWeight();
	        }       
		};
		PageRank<Node, Edge> ranker = new PageRank<Node, Edge>(socialGraph.socialGraph,wtTransformer,0.15);
		ranker.evaluate();
		
		Set<String> keyNodes1 = socialGraph.socialGraphNodes.keySet();
	    for (Iterator<String> i = keyNodes1.iterator(); i.hasNext();) {
	      String keyNode1 = (String) i.next();
	      Node node1 = (Node) socialGraph.socialGraphNodes.get(keyNode1);
	      node1.setRank(ranker.getVertexScore(node1));
	      String value = node1.getName() + "\t" + Double.toString(ranker.getVertexScore(node1)) + "\n";
	      fopPageRank.write(value.getBytes());
	    }
	    System.out.println("pageRankDone");
	    fopPageRank.flush();
	    fopPageRank.close();
	}
	
	public void printSocialMatrix() throws IOException{
		File fileMatrix = new File("matrix" + suffix +".txt");
		FileOutputStream fopMatrix = new FileOutputStream(fileMatrix);
		
		File fileAtts = new File("attributes" + suffix +".txt");
		FileOutputStream fopAtts = new FileOutputStream(fileAtts);
		
		String printAtr = "";
	
		Set<String> keyNodes = socialGraph.socialGraphNodes.keySet();
	    for (Iterator<String> i = keyNodes.iterator(); i.hasNext();) {
	      String keyNode = (String) i.next();
	      Node node = (Node) socialGraph.socialGraphNodes.get(keyNode);
	      printAtr = node.getName() + "\t" + node.getType() + "\t" + node.getNumComments() + "\t"+  node.getLink() + "\t" + "\n";
	      fopAtts.write(printAtr.getBytes());
		}
		
		String printStr = "\t";
		keyNodes = socialGraph.socialGraphNodes.keySet();
	    for (Iterator<String> i = keyNodes.iterator(); i.hasNext();) {
	      String keyNode = (String) i.next();
	      Node node = (Node) socialGraph.socialGraphNodes.get(keyNode);
	      printStr += node.getName() + "\t";
		}
		printStr += "\n";
		fopMatrix.write(printStr.getBytes());
	
		keyNodes = socialGraph.socialGraphNodes.keySet();
	    for (Iterator<String> i = keyNodes.iterator(); i.hasNext();) {
	      String keyNode1 = (String) i.next();
	      Node node1 = (Node) socialGraph.socialGraphNodes.get(keyNode1);
	      Set<String> keyNodes2 = socialGraph.socialGraphNodes.keySet();
			printStr = node1.getName() + "\t";
		    for (Iterator<String> j = keyNodes2.iterator(); j.hasNext();) {
		      String keyNode2 = (String) j.next();
		      Node node2 = (Node) socialGraph.socialGraphNodes.get(keyNode2);
		      
		      printStr += socialGraph.socialMatrix[node1.getIndex()][node2.getIndex()] + "\t";
			}
			printStr += "\n";
			fopMatrix.write(printStr.getBytes());
		
		}
	
		
		fopMatrix.flush();
		fopMatrix.close();
		
		fopAtts.flush();
		fopAtts.close();
	}
	
	private ArrayList<Date> findMinMaxDates(ArrayList<Date> dates){
		ArrayList<Date> result = new ArrayList<Date>();
		
		Date minDate = dates.get(0);
		Date maxDate = dates.get(0);
		for (Date date : dates) {
			if(date.getTime() > maxDate.getTime())
				maxDate = date;
			if(date.getTime() < minDate.getTime())
				minDate = date;
		}
		
		result.add(minDate);
		result.add(maxDate);
		
		return result;
	}
	
	public void parseFoundLinks(ArrayList<IssueInfo> foundIssues){
		for (IssueInfo issue : foundIssues) {
			IssueParseLister lister = new IssueParseLister();
			parse("http://drupal.org" + issue.getLink(), lister);
			String prevPage = "";
			while(lister.getNextPage() != null && !lister.getNextPage().equals("") && !lister.getNextPage().equals(prevPage)){
				prevPage = lister.getNextPage();
				parse("http://drupal.org" + lister.getNextPage(), lister);
			}
			innerListers.add(lister);
			
			//TODO: update issue info based on innerlister
			for (CommentInfo commentInfo : lister.getCommentInfos()) {
				if(isNotSubscription(commentInfo)){
					issue.addComments(commentInfo);
					if(isCommit(commentInfo))
						issue.addCommits(commentInfo.getDate());
				}
			}
			
			if (!issue.getStatus().equals("closed (duplicate)"))
				issueInfos.add(issue);
		}
					
					
	}
	
	private boolean isNotSubscription(CommentInfo commentInfo) {
		String text = commentInfo.getPlainContent();
		if (text == null)
			return true;
		String [] words = text.split("\\s+");
		if(words.length < 12 && text.contains("subscrib"))
			return false;
		return true;
	}

	private boolean isCommit(CommentInfo commentInfo) {
		String text = commentInfo.getPlainContent();
		String author = commentInfo.getAuthor();
		if (text == null)
			return false;
		if(text.contains("Committed to CVS HEAD"))
			return true;
		if((text.contains("Committed") || text.contains("committed")) && text.contains("HEAD") && (author.equals("Dries") || author.equals("webchick")))
			return true;
		return false;
	}
	
	public void parse (String spec, HTMLEditorKit.ParserCallback lister){
		Reader r;
		try {
           if (spec.indexOf("://") > 0) {
           URL u = new URL(spec);
           Object content = u.getContent();
               if (content instanceof InputStream) {
               r = new InputStreamReader((InputStream)content);
           }
               else if (content instanceof Reader) {
               r = (Reader)content;
           }
               else {
               throw new Exception("Bad URL content type.");
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
       catch (Exception e) {
       System.err.println("Error: " + e);
       e.printStackTrace(System.err);
   }

	}
}

class DateComparator implements Comparator<IssueParseLister>{
	   public int compare(IssueParseLister a, IssueParseLister b) {
		   if(a.getCommentInfos().get(0).getDate().getTime() > b.getCommentInfos().get(0).getDate().getTime())
			   return 1;
		   else if(a.getCommentInfos().get(0).getDate().getTime() == b.getCommentInfos().get(0).getDate().getTime())
			   return 0;
		   else
			   return -1;
	       // now determine which if x > y return 1  x == y return 0  x < y return -1
	   }
	}

class IssueInfoPriorityComparator implements Comparator<IssueInfo>{
	   public int compare(IssueInfo a, IssueInfo b) {
		   if(a.getPriorityNum() > b.getPriorityNum())
			   return 1;
		   else if(a.getPriorityNum() == b.getPriorityNum())
			   return 0;
		   else
			   return -1;
	       // now determine which if x > y return 1  x == y return 0  x < y return -1
	   }
	}


class IssueInfoStatusComparator implements Comparator<IssueInfo>{
	   public int compare(IssueInfo a, IssueInfo b) {
		   if(a.getStatusNum() > b.getStatusNum())
			   return 1;
		   else if(a.getStatusNum() == b.getStatusNum())
			   return 0;
		   else
			   return -1;
	       // now determine which if x > y return 1  x == y return 0  x < y return -1
	   }
	}

class IssueInfoTimeComparator implements Comparator<IssueInfo>{
	   public int compare(IssueInfo a, IssueInfo b) {
		   if(a.getComments().get(0).getDate().getTime() > b.getComments().get(0).getDate().getTime())
			   return 1;
		   else if(a.getComments().get(0).getDate().getTime() == b.getComments().get(0).getDate().getTime())
			   return 0;
		   else
			   return -1;
	       // now determine which if x > y return 1  x == y return 0  x < y return -1
	   }
	}
