package analysis;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;


import data.UserProfileInfo;
import data.IssueInfo;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;


public class SocialGraph {

	 public SocialMatrixCell[][] socialMatrix = new SocialMatrixCell[2800][2800];
	 public TreeMap<String, Node> socialGraphNodes = new TreeMap<String, Node>();
	 public String[] designers = {"yoroy", "Roy", "Bojhan", "bojhan", "leisa", "leisareichelt", "cliff", "mark", "Mark"};
	 public DirectedGraph<Node, Edge> socialGraph = new DirectedSparseGraph<Node, Edge>();
	 public ArrayList<IssueAnalysis> allIssueAnalysis = new ArrayList<IssueAnalysis>();
	 
	 ArrayList<UserProfileInfo> authorsInfos = new ArrayList<UserProfileInfo>();
	 
		private String usabilityInterest = "";
		private String triangularConnections = "";
		private String meanPreviousComments = "";
		private String meanMembershipWeeks = "";
		private String meanPageRank = "";
		private String currCommentsOfCreator;
		private String meanExpertise;
		
		/*
		 * (1) Percentage of negative comments
(2) Percentage of positive comments
(7) Avg. # of words
(10) # of words in issue brief
(11) # of non-Drupal links
(12) Characters-to-sentences-ratio
(13) Words-to-sentences-ratio
(20) Avg. duration b/t comments
(23) # of triads in graph
(24) Number of influential participants
(25)Avg. # of total participation duration
(26) Avg. # of participants’ prev. comment
(27) # of participation weeks of creator
(28) # of prev. comments of creator
(29) # of alternate replies*/
		

	public void create(ArrayList<IssueInfo> issueInfos) {
		//Tagger tag = new Tagger();
		//tag.loadSWNDataFile();
		
		for (IssueInfo issueInfo : issueInfos) {
			String issueName = (issueInfo.getLink().replaceFirst("node", "")).substring(2);
			Node issueNode = new Node(Node.THREAD, issueName, issueInfo.getLink(), issueInfo.getNumComments(), issueInfo.isConsensus());
			socialGraphNodes.put(issueName,issueNode);
			socialGraph.addVertex(issueNode);
			issueNode.setIndex();
		
			IssueAnalysis issueAnalysis = createIssueAnalysis(issueInfo);
			allIssueAnalysis.add(issueAnalysis);
		}
	}
	
	public void printAllIssueAnalysis(){
		for (IssueAnalysis issueAnalysis : allIssueAnalysis) {
			System.out.println(issueAnalysis.printString(this));
		}
	}
	
	private IssueAnalysis createIssueAnalysis(IssueInfo issueInfo){
		IssueAnalysis currentIssueAnalysis = new IssueAnalysis(issueInfo);
		
		ArrayList<Node> tempAuthorNodes = new ArrayList<Node>();
		ArrayList<Double> durations = new ArrayList<Double>();
		
		//Go through the comments in each issue
		int numReplyTo = 0;
		Date prevCommentTime = null;
		Date currCommentTime = null;
		if(issueInfo.getComments().size() > 0)
			prevCommentTime = issueInfo.getComments().get(0).getDate();
		
		int sumNumWords = 0;	
		int IRCQuotations = 0;
		int numQuestionMarks = 0;
		int numUsabilityTesting = 0;
		int numSummaries = 0;
		int numCodeReviews = 0;
		int numContatiousWords = 0;
		
		int numWes = 0;
		int numYouIs = 0;
		int numPlusOnes = 0;
		int numThanks = 0;

		int numNegativeExpressions = 0;
		
		int numPositiveWords = 0;
		int numStopWords = 0;
		int numNegativeWords = 0;
		
		int numNegativeComments = 0;
		int numPositiveComments = 0;
		
		int numSentences = 0;
		int numCharacters = 0;
		
		
		//ArrayList<Integer> receivers = new ArrayList<Integer>();
		ArrayList<String> thankedFor = new ArrayList<String>();
		
		for(int i = 0; i < issueInfo.getComments().size(); i++){
			if(issueInfo.getComments().get(i).getAuthor() == null){
				System.out.println("Author is null");
			}else{
				Node currentAuthorsNode = null;
				
				//duration
				currCommentTime = issueInfo.getComments().get(i).getDate();
				Double duration = new Double(Stats.findDuration(prevCommentTime, currCommentTime));
				if(duration > 0)
					durations.add(duration);
				else if (durations.size() > 0)
					durations.set(durations.size()-1, duration + durations.get(durations.size()-1));
				
				//look for the node
				for (int j = 0; j < tempAuthorNodes.size(); j++) {
					if(tempAuthorNodes.get(j).getName().equals(issueInfo.getComments().get(i).getAuthor())){
						currentAuthorsNode = tempAuthorNodes.get(j); 
						break;
					}
				}
				
				if(currentAuthorsNode == null || currentAuthorsNode.equals(null)){
					currentAuthorsNode = new Node(Node.AUTHOR, issueInfo.getComments().get(i).getAuthor(), issueInfo.getComments().get(i).getAuthorLink(), 1, false);
					tempAuthorNodes.add(currentAuthorsNode);
				}else{
					currentAuthorsNode.addNumComments(1);
				}
				
				currentAuthorsNode.updateRepliersAndThreads(issueInfo, issueInfo.getComments().get(i),Stats.findDuration(prevCommentTime, currCommentTime));
				numReplyTo += issueInfo.getComments().get(i).getReceiverNames().size();
				//receivers.addAll(findUniqueReceivers(issueInfo.getComments().get(i).getReceiverNames(), receivers));

				prevCommentTime = currCommentTime;
				
				String currentContent = issueInfo.getComments().get(i).getContent();
				if (currentContent != null) {
					sumNumWords += ContentAnalysis.calculateNumWords(currentContent);
					
					//IRC
					if(ContentAnalysis.mentionsIRC(currentContent))
						IRCQuotations ++;

					numQuestionMarks += ContentAnalysis
							.findNumQuestionMarks(currentContent);
					numUsabilityTesting += ContentAnalysis
							.findNumCommentsMentionedUsabilityTestings(currentContent);
					numSummaries += ContentAnalysis
							.findNumCommentsMentionedSummaries(currentContent);
					numCodeReviews += ContentAnalysis
							.findNumCommentsMentionedCodeReviews(currentContent);
					numContatiousWords += ContentAnalysis
							.findNumContatiousWords(currentContent);
					numYouIs += ContentAnalysis.findNumYouIs(currentContent);
					numWes += ContentAnalysis.findNumWes(currentContent);
					numPlusOnes += ContentAnalysis
							.findNumPlusOnes(currentContent);
					numThanks += ContentAnalysis.findNumThanks(currentContent);
					numSentences += ContentAnalysis
							.findNumSentences(currentContent);
					numCharacters += ContentAnalysis
							.findNumCharacters(currentContent);
					numNegativeExpressions += ContentAnalysis
							.findNumNegativeExpressions(currentContent);
					
					numNegativeWords = ContentAnalysis
							.findNumNegativeWords(currentContent);
					numPositiveWords = ContentAnalysis
							.findNumPositiveWords(currentContent);
					numStopWords = ContentAnalysis
							.findNumStopWords(currentContent);
					
					double positiveRatio = numPositiveWords/(sumNumWords - numStopWords);
					double negativeRatio = numNegativeWords/(sumNumWords - numStopWords);
					
					if(positiveRatio > negativeRatio)
						numPositiveComments++;
					else
						numNegativeComments++;
					// try {
					// tag.tagTheComment(issueInfo.getComments().get(i).getPlainContent());
					/*
					 * } catch (IOException e) { e.printStackTrace(); } catch
					 * (ClassNotFoundException e) { e.printStackTrace(); }
					 */
					// numNegativeWords +=
					// tag.getNumNegativeWords();//findNumNegativeWords(issueInfo.getComments().get(i).getPlainContent());
					// numPositiveWords += tag.getNumPositiveWords();
					// numNeutralWords += tag.getNumNeutralWords();
					// thankedFor.addAll(ContentAnalysis.findPeopleThankedFor(issueInfo.getComments().get(i).getContent(),
					// tempAuthorNodes));
				}
			}
		}
		
		addNodesToSocialGraph(tempAuthorNodes);
		double replyToRatio = (double)numReplyTo;// / issueInfo.getComments().size();
		currentIssueAnalysis.setReplyToRatio(replyToRatio);
		currentIssueAnalysis.setDurations(durations);
		currentIssueAnalysis.setIRCQuatation(IRCQuotations);
		currentIssueAnalysis.setTotalNumWords(sumNumWords);
		currentIssueAnalysis.setNumQuestionMarks(numQuestionMarks);
		currentIssueAnalysis.setNumUsabilityTesting(numUsabilityTesting);
		currentIssueAnalysis.setNumSummaries(numSummaries);
		currentIssueAnalysis.setNumCodeReviews(numCodeReviews);
		currentIssueAnalysis.setNumContatiousWords(numContatiousWords);
		currentIssueAnalysis.setNumWes(numWes);
		currentIssueAnalysis.setNumYouIs(numYouIs);
		currentIssueAnalysis.setNumPlusOnes(numPlusOnes);
		currentIssueAnalysis.setNumThanks(numThanks);
		currentIssueAnalysis.setPercentageOfNegativeComments(numNegativeComments/issueInfo.getComments().size());
		currentIssueAnalysis.setPercentageOfPositiveComments(numPositiveComments/issueInfo.getComments().size());
		currentIssueAnalysis.setCharacterToSentenceRatio((double)numCharacters/numSentences);
		currentIssueAnalysis.setWordToSentenceRatio((double)sumNumWords/numSentences);
		currentIssueAnalysis.setNumSentences(numSentences);
		currentIssueAnalysis.setNumNegativeExpressions(numNegativeExpressions);
		currentIssueAnalysis.setUniqueAuthorNames(findAuthorsList(tempAuthorNodes));
		currentIssueAnalysis.setPeopleThankedFor(thankedFor);
		
		return currentIssueAnalysis;
	}

	private ArrayList<String> findAuthorsList(ArrayList<Node> tempAuthorNodes) {
		ArrayList<String> authors = new ArrayList<String>();
		for (Node node : tempAuthorNodes) {
			authors.add(node.getName());
		} 
		return authors;
	}	 
	
	public static double Median(ArrayList<Double> values)
	{
		//TODO: sort is required
	    //Collections.sort(values);
		if (values.size() == 0)
			return 0;
	 
	    if (values.size() % 2 == 1)
	    	return values.get((values.size()+1)/2-1);
	    else
	    {
	    	double lower = values.get(values.size()/2-1);
	    	double upper = values.get(values.size()/2);
	 
		return (lower + upper) / 2.0;
	    }	
	}
	
	private void addNodesToSocialGraph(ArrayList<Node> tempAuthorNodes){
		//Add the threads
		for (Node tempAuthorNode : tempAuthorNodes) {
			boolean authorFound = false;
			Set<String> keyNodes = socialGraphNodes.keySet();
		    for (Iterator<String> i = keyNodes.iterator(); i.hasNext();) {
		    	String keyNode = (String) i.next();
		    	Node node = (Node) socialGraphNodes.get(keyNode);
		    	if(node.getName().equals(tempAuthorNode.getName())){
		    		node.addRepliers(tempAuthorNode.getRepliers());
		    		node.addThreads(tempAuthorNode.getThreads());
		    		if(node.getType() == Node.AUTHOR)
		    			node.addNumComments(tempAuthorNode.getNumComments());
		    		authorFound = true;
		    		break;
		    	}
			}
			if(!authorFound){
				socialGraphNodes.put(tempAuthorNode.getName().toLowerCase(),tempAuthorNode);
				socialGraph.addVertex(tempAuthorNode);
				tempAuthorNode.setIndex();
			}
		}
		
	}
	
	public void fillSocialMatrix(){
		for (int i = 0; i < socialMatrix.length; i++)
			for(int j = 0; j < socialMatrix.length; j++)
				socialMatrix[i][j] = new SocialMatrixCell();
	
		Set<String> keyNodes = socialGraphNodes.keySet();
	    for (Iterator<String> i = keyNodes.iterator(); i.hasNext();) {
	      String keyNode = (String) i.next();
	      Node node = (Node) socialGraphNodes.get(keyNode);
	      double outgoingEdges = 0;
			if(node.getRepliers().size()!= 0){
				Set<String> keys = node.getRepliers().keySet();
			    for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			      String key = (String) iter.next();
			      SocialMatrixCell value = (SocialMatrixCell) node.getRepliers().get(key);
			      
			      Node findNode = socialGraphNodes.get(key.toLowerCase());
			      if (findNode == null)
			    	  System.out.println("fill Social Matrix: Errrrrrror: " + key);
			      else{
			    	  int index = findNode.getIndex();
			    	  socialMatrix[node.getIndex()][index].addComments(value.getComments());
			    	  outgoingEdges += value.calculateValue();
			      }
			    }
			}
			if(node.getThreads().size() != 0){
				Set<String> keys = node.getThreads().keySet();
			    for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			      String key = (String) iter.next();
			      SocialMatrixCell value = (SocialMatrixCell) node.getThreads().get(key);
			      
			      Node findNode = socialGraphNodes.get(key);
			      if (findNode == null)
			    	  System.out.println("fill Social Matrix, threads: Errrrrrror: " + key);
			      else{
			    	  int index = socialGraphNodes.get(key).getIndex();
			    	  socialMatrix[node.getIndex()][index].addComments(value.getComments());
			    	  outgoingEdges += value.calculateValue();
			      }
			    }
			}
			node.setOutgoingEdges(outgoingEdges);
		}
	}
	
	public void fillSocialGraph(){
		Set<String> keyNodes1 = socialGraphNodes.keySet();
	    for (Iterator<String> i = keyNodes1.iterator(); i.hasNext();) {
	      String keyNode1 = (String) i.next();
	      Node node1 = (Node) socialGraphNodes.get(keyNode1);
	      Set<String> keyNodes2 = socialGraphNodes.keySet();
		    for (Iterator<String> j = keyNodes2.iterator(); j.hasNext();) {
		      String keyNode2 = (String) j.next();
		      Node node2 = (Node) socialGraphNodes.get(keyNode2);
		      
		      Double value = (double)socialMatrix[node1.getIndex()][node2.getIndex()].calculateValue()/(double)node1.getOutgoingEdges();
		      if(value > 0)
		    	  socialGraph.addEdge(new Edge(value, socialMatrix[node1.getIndex()][node2.getIndex()]), node1, node2);
			}
		}
	}
	
	 public double findMeanNumConsensusThreads(IssueInfo issueInfo) {
		double result = 0;
		Node firstNode = null;
		for(int i = 0; i < issueInfo.getComments().size(); i++){
			int firstIndex = -1;
			firstNode = null;
			if(issueInfo.getComments().get(i).getAuthor() != null)
				firstNode = socialGraphNodes.get(issueInfo.getComments().get(i).getAuthor().toLowerCase());
			if (firstNode == null)
		    	  System.out.println("getMeanNumConsensus: Errrrrrror: " + issueInfo.getComments().get(i).getAuthor());
		    else
		    	  firstIndex = firstNode.getIndex();
			if(firstIndex != -1)
				for(int j = 0; j < socialMatrix.length; j++)
					result += socialMatrix[firstIndex][j].getNumConsensusThreads(issueInfo.getComments().get(i).getDate());
		}
		result = result / (double)issueInfo.getComments().size();
		return result;
	}

	 public int findNumPreviousComments(UserProfileInfo authorInfo) {
			int result = 0;
			Node firstNode = null;
			int firstIndex = -1;
			if(authorInfo.getUserName() != null)
				firstNode = socialGraphNodes.get(authorInfo.getUserName().toLowerCase());
			if (firstNode == null)
			   	  System.out.println("findNumComments: Errrrrrror: " + authorInfo.getUserName());
			   else
			   	  firstIndex = firstNode.getIndex();
			if(firstIndex != -1)
				for(int j = 0; j < socialMatrix.length; j++){
					int index = authorInfo.getDates().size()-1;
					if(index >= 0){
						Date date = authorInfo.getDate(index);
						result += socialMatrix[firstIndex][j].getNumPreviousComments(date);
					}
				}
			return result;
		}

	 

		public String findAuthorsInfo(IssueInfo issueInfo) {
			usabilityInterest = "";
			triangularConnections = "";
			meanPreviousComments  = "";
			meanMembershipWeeks = "";
			meanPageRank = "";
			currCommentsOfCreator = "";
			meanExpertise = "";
			
			int usabilityInterestNum = 0;
			int triangularConnectionsNum = 0;
			int numUniqueAuthors = 0;
			int numCurCommentsOfCreator = 0;;
			ArrayList<UserProfileInfo> tempAuthors = new ArrayList<UserProfileInfo>();
			ArrayList<UserProfileInfo> authorNames = new ArrayList<UserProfileInfo>();
			ArrayList<Integer> expertise = new ArrayList<Integer>();
			
			//Go thourgh the authors in each issue
			for(int i = 0; i < issueInfo.getComments().size(); i++){
				String creator = null;
				if(issueInfo.getComments().get(0).getAuthor() != null)
					creator = issueInfo.getComments().get(0).getAuthor();
				if(issueInfo.getComments().get(i).getAuthor() != null){
					if(issueInfo.getComments().get(i).getAuthor().equals(creator))
						numCurCommentsOfCreator++;
					
					UserProfileInfo currentAuthorsInfo = null;
					//look for the author
					for (int j = 0; j < tempAuthors.size(); j++) {
						if(tempAuthors.get(j).getUserName().equals(issueInfo.getComments().get(i).getAuthor())){
							currentAuthorsInfo = tempAuthors.get(j); 
							break;
						}
					}
					if(currentAuthorsInfo == null || currentAuthorsInfo.equals(null)){
						numUniqueAuthors ++;
						currentAuthorsInfo = new UserProfileInfo(issueInfo.getComments().get(i).getAuthor(), issueInfo.getComments().get(i).getAuthorLink());
						tempAuthors.add(currentAuthorsInfo);
					}
					currentAuthorsInfo.addComments();
					currentAuthorsInfo.addDate(issueInfo.getComments().get(i).getDate());
					currentAuthorsInfo.setLongestThread(issueInfo.getComments().size());
				}
			}
			
			int counter = 0;
		
			ArrayList<Integer> membershipWeeks = new ArrayList<Integer>();
			ArrayList<Integer> previousComments = new ArrayList<Integer>();
			ArrayList<Double> pageRanks = new ArrayList<Double>();
			
			//Add the threads
			for (UserProfileInfo tempAuthor : tempAuthors) {
				boolean authorFound = false;
				for (int k = 0; k < authorsInfos.size(); k++) {
					if(authorsInfos.get(k).getUserName().equals(tempAuthor.getUserName())){
						authorsInfos.get(k).setComments(authorsInfos.get(k).getComments() + tempAuthor.getComments());
						authorsInfos.get(k).addThreads();
						authorsInfos.get(k).addDates(tempAuthor.getDates());
						authorsInfos.get(k).setLongestThread(tempAuthor.getLongestThread());
						authorFound = true;
						if(authorsInfos.get(k).hasInterest("usability"))
							usabilityInterestNum++;
						authorNames.add(authorsInfos.get(k));
						counter++;
						membershipWeeks.add(new Integer(authorsInfos.get(k).getMembershipWeeks()));
						if(!tempAuthor.getUserName().equals("System Message"))
							expertise.addAll(findExpertise(authorsInfos.get(k)));
						previousComments.add(findNumPreviousComments(authorsInfos.get(k)));
						pageRanks.add(socialGraphNodes.get(authorsInfos.get(k).getUserName().toLowerCase()).getRank());
						break;
					}
				}
				if(!authorFound){
					tempAuthor.addThreads();
					tempAuthor.parseProfile();
					authorsInfos.add(tempAuthor);
					if(tempAuthor.hasInterest("usability"))
						usabilityInterestNum++;
					authorNames.add(tempAuthor);
					counter++;
					previousComments.add(findNumPreviousComments(tempAuthor)); 
					membershipWeeks.add(new Integer(tempAuthor.getMembershipWeeks()));
					if(!tempAuthor.getUserName().equals("System Message"))
						expertise.addAll(findExpertise(tempAuthor));
					pageRanks.add(socialGraphNodes.get(tempAuthor.getUserName().toLowerCase()).getRank());
				}
			}
			
			//Check for Triangular Connections
			for (int i = 0; i < authorNames.size(); i++) {
				for (int j = i+1; j < authorNames.size(); j++) {
					for (int k = j+1; k < authorNames.size(); k++) {
						int firstIndex = socialGraphNodes.get(authorNames.get(i).getUserName().toLowerCase()).getIndex();
						int secondIndex = socialGraphNodes.get(authorNames.get(j).getUserName().toLowerCase()).getIndex();
						int thirdIndex = socialGraphNodes.get(authorNames.get(k).getUserName().toLowerCase()).getIndex();
						Date firstDateOfIssue = issueInfo.getComments().get(0).getDate();
						//&& authorNames.get(i).sharedInterest(authorNames.get(j))
						if(((!socialMatrix[firstIndex][secondIndex].isEmpty(firstDateOfIssue) || !socialMatrix[secondIndex][firstIndex].isEmpty(firstDateOfIssue))) && ((!socialMatrix[secondIndex][thirdIndex].isEmpty(firstDateOfIssue) || !socialMatrix[thirdIndex][secondIndex].isEmpty(firstDateOfIssue))) && ((!socialMatrix[firstIndex][thirdIndex].isEmpty(firstDateOfIssue) || !socialMatrix[thirdIndex][firstIndex].isEmpty(firstDateOfIssue))))
							triangularConnectionsNum++;
					}	
				}	
			}
			
			if(tempAuthors.size() > 0){
				currCommentsOfCreator = Integer.toString(numCurCommentsOfCreator);
			}
			triangularConnections += Integer.toString(triangularConnectionsNum);
			usabilityInterest += Integer.toString(usabilityInterestNum);
			double meanPreviousCommentsNum = Stats.findMean(previousComments, counter);
			meanPreviousComments += Double.toString(meanPreviousCommentsNum);
			double meanMembershipWeeksNum = Stats.findMean(membershipWeeks, counter);
			meanMembershipWeeks += Double.toString(meanMembershipWeeksNum);
			double meanPageRankNum = Stats.findMean_D(pageRanks, counter);
			meanPageRank += Double.toString(meanPageRankNum);
			double meanExpertiseNum = Stats.findMean(expertise, expertise.size());
			meanExpertise += Double.toString(meanExpertiseNum);
			
			String result = numUniqueAuthors + "\t" + 
			usabilityInterest + "\t" + 
			triangularConnections + "\t" + 
			meanPreviousComments + "\t" + 
			meanMembershipWeeks + "\t" + 
			meanPageRank + "\t" +
			currCommentsOfCreator + "\t" +
			meanExpertise;
			
			return result;
		}

		private ArrayList<Integer> findExpertise(UserProfileInfo authorInfo){
			ArrayList<Integer> result = new ArrayList<Integer>();
			Date currentDate = new Date(System.currentTimeMillis());
			
			for(int i = 0; i < authorInfo.getDates().size(); i++){
				double duration = currentDate.getTime() - authorInfo.getDates().get(0).getTime(); 
				duration = duration * 0.001;//in ms
				duration = duration / 60.00;//in minute
				duration = duration / 60.00; //in hours
				duration = duration / 24;
				duration = duration / 7;
				int res = authorInfo.getMembershipWeeks() - ((int)duration);
				result.add(res);
				if(res < 0)
					res = 0;
			}
			return result;
		}
}
