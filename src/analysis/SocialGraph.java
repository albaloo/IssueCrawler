package analysis;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import data.AuthorsInfo;
import data.IssueInfo;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;


public class SocialGraph {

	 public SocialMatrixCell[][] socialMatrix = new SocialMatrixCell[2800][2800];
	 public TreeMap<String, Node> socialGraphNodes = new TreeMap<String, Node>();
	 public String[] negativeWords = {"annoyed", "assaulted", "attacked", "avoided", "awful", "awkward", "bad", "beat", "beaten down", "betrayed", "bitter", "bizzare", "blamed", "bored", "boring", "bothered", "bothersome", "bullied", "burdened", "burdensome", "careless", "chaotic", "clueless", "conflicted", "confronted", "confused", "cowardly", "crabby", "cranky", "crap", "crazy", "creepy", "critical", "criticized", "damned", "despicable", "destroyed", "destructive", "disappointed", "disappointing", "disapproved of", "discardable", "discarded", "discouraged", "disgust", "disgusted", "dishonest", "dislike", "disliked", "displeased", "disregarded", "disrespected", "dissatisfied", "doomed", "double-crossed", "doubted", "doubtful", "down", "dreadful", "dumb", "dumped", "dumped on", "emasculated", "embarrassed", "exhausted", "freaked out", "frustrated", "harassed", "hate", "hateful", "helpless", "hesitant", "hideous", "hindered", "hopeless", "horrible", "horrified", "horror", "hostile", "hot-tempered", "humiliated", "hurt", "idiotic", "ignorant", "ignored", "insane", "insulted", "irritated", "jealous", "jerked around", "kept away", "kept out", "left out", "let down", "limited", "lost", "mad", "messed with", "messed up", "messy", "miserable", "misled", "mistaken", "mocked", "numb", "nuts", "obsessed", "obsessive", "offended", "pain", "pathetic", "pissed", "pissed off", "powerless", "pressured", "punished", "pushed", "pushed away", "retarded", "ridiculed", "ridiculous", "screwed", "screwed over", "screwed up", "selfish", "snapped at", "stuck", "stupid", "suffering", "suspicious", "shortsightedness"};
	 public String[] designers = {"yoroy", "Roy", "Bojhan", "bojhan", "leisa", "leisareichelt", "cliff", "mark", "Mark"};
	 public DirectedGraph<Node, Edge> socialGraph = new DirectedSparseGraph<Node, Edge>();
		
	 ArrayList<AuthorsInfo> authorsInfos = new ArrayList<AuthorsInfo>();
	 
		private String usabilityInterest = "";
		private String triangularConnections = "";
		private String meanPreviousComments = "";
		private String meanMembershipWeeks = "";
		private String meanPageRank = "";
		private String currCommentsOfCreator;
		private String meanExpertise;

	public void create(ArrayList<IssueInfo> issueInfos) {
		//Tagger tag = new Tagger();
		//tag.loadSWNDataFile();
		
	for (IssueInfo issueInfo : issueInfos) {
		String issueName = (issueInfo.getLink().replaceFirst("node", "")).substring(2);
		Node issueNode = new Node(Node.THREAD, issueName, issueInfo.getLink(), issueInfo.getNumComments(), issueInfo.isConsensus());
		socialGraphNodes.put(issueName,issueNode);
		socialGraph.addVertex(issueNode);
		issueNode.setIndex();
	
		ArrayList<Node> tempAuthorNodes = new ArrayList<Node>();
		ArrayList<Double> durations = new ArrayList<Double>();
		
		//Go through the comments in each issue
		int numReplyTo = 0;
		Date prevCommentTime = null;
		Date currCommentTime = null;
		if(issueInfo.getComments().size() > 0)
			prevCommentTime = issueInfo.getComments().get(0).getDate();
		int IRCQuotations = 0;
		int sumNumWords = 0;
		int numQuestionMarks = 0;
		int numUsabilityTesting = 0;
		int numSummaries = 0;
		int numCodeReviews = 0;
		int numContatiousWords = 0;
		int numWes = 0;
		int numYouIs = 0;
		int numPlusOnes = 0;
		int numThanks = 0;
		int numNegativeWords = 0;
		int numNegativeExpressions = 0;
		int numPositiveWords = 0;
		int numNeutralWords = 0;
		double characterToSentenceRatio = 0;
		double wordToSentenceRatio = 0;
		int numSentences = 0;
		int numCharacters = 0;
		
		
		ArrayList<Integer> receivers = new ArrayList<Integer>();
		ArrayList<String> thankedFor = new ArrayList<String>();
		
		for(int i = 0; i < issueInfo.getComments().size(); i++){
			if(issueInfo.getComments().get(i).getAuthor() == null){
				System.out.println("salam");
			}else{
				Node currentAuthorsNode = null;
				currCommentTime = issueInfo.getComments().get(i).getDate();
				Double duration = new Double(Stats.findDuration(prevCommentTime, currCommentTime));
				if(duration > 0)
					durations.add(duration);
				else if (durations.size() > 0)
					durations.set(durations.size()-1, duration + durations.get(durations.size()-1));
				sumNumWords += calculateNumWords(issueInfo.getComments().get(i).getPlainContent());
				if(IRCQuotation(issueInfo.getComments().get(i).getPlainContent()))
					IRCQuotations ++;
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
				
				ArrayList<String> tempReceivers = currentAuthorsNode.updateRepliersAndThreads(issueInfo, issueInfo.getComments().get(i),Stats.findDuration(prevCommentTime, currCommentTime));
				numReplyTo += tempReceivers.size();
				//find number of ideas
				receivers.addAll(findUniqueReceivers(tempReceivers, receivers));
				prevCommentTime = currCommentTime;
				if(issueInfo.getComments().get(i).getPlainContent()!= null){
				numQuestionMarks += findNumQuestionMarks(issueInfo.getComments().get(i).getPlainContent());
				numUsabilityTesting += findNumUsabilityTestings(issueInfo.getComments().get(i).getPlainContent());
				numSummaries += findNumSummaries(issueInfo.getComments().get(i).getPlainContent());
				numCodeReviews += findNumCodeReviews(issueInfo.getComments().get(i).getPlainContent());
				numContatiousWords += findNumContatiousWords(issueInfo.getComments().get(i).getPlainContent());
				numYouIs += findNumYouIs(issueInfo.getComments().get(i).getPlainContent());
				numWes += findNumWes(issueInfo.getComments().get(i).getPlainContent());
				numPlusOnes += findNumPlusOnes(issueInfo.getComments().get(i).getPlainContent());
				numThanks += findNumThanks(issueInfo.getComments().get(i).getPlainContent());
				//try {
					System.out.println("issue: " + issueInfo.getLink() + " comment: " + i + " author: " + issueInfo.getComments().get(i).getAuthor());
					//tag.tagTheComment(issueInfo.getComments().get(i).getPlainContent());
				/*} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}*/
				//numNegativeWords += tag.getNumNegativeWords();//findNumNegativeWords(issueInfo.getComments().get(i).getPlainContent());
				//numPositiveWords += tag.getNumPositiveWords();
				//numNeutralWords += tag.getNumNeutralWords();
				numSentences += findNumSentences(issueInfo.getComments().get(i).getPlainContent());
				numCharacters += findNumCharacters(issueInfo.getComments().get(i).getPlainContent());
				numNegativeExpressions += findNumNegativeExpressions(issueInfo.getComments().get(i).getPlainContent());
				thankedFor.addAll(findPeopleThankedFor(issueInfo.getComments().get(i).getPlainContent(), tempAuthorNodes));
				}
			}
		}
		
		addNodesToSocialGraph(tempAuthorNodes);
		double replyToRatio = (double)numReplyTo;// / issueInfo.getComments().size();
		issueInfo.setReplyToRatio(replyToRatio);
		issueInfo.setDurations(durations);
		issueInfo.setIRCQuatation(IRCQuotations);
		issueInfo.setTotalNumWords(sumNumWords);
		issueInfo.setNumQuestionMarks(numQuestionMarks);
		issueInfo.setNumUsabilityTesting(numUsabilityTesting);
		issueInfo.setNumSummaries(numSummaries);
		issueInfo.setNumCodeReviews(numCodeReviews);
		issueInfo.setNumContatiousWords(numContatiousWords);
		issueInfo.setNumWes(numWes);
		issueInfo.setNumYouIs(numYouIs);
		issueInfo.setNumPlusOnes(numPlusOnes);
		issueInfo.setNumThanks(numThanks);
		issueInfo.setNumNegativeWords(numNegativeWords);
		issueInfo.setNumPositiveWords(numPositiveWords);
		issueInfo.setNumNeutralWords(numNeutralWords);
		issueInfo.setCharacterToSentenceRatio((double)numCharacters/numSentences);
		issueInfo.setWordToSentenceRatio((double)sumNumWords/numSentences);
		issueInfo.setNumSentences(numSentences);
		issueInfo.setNumNegativeExpressions(numNegativeExpressions);
		issueInfo.setUniqueAuthorNames(findAuthorsList(tempAuthorNodes));
		issueInfo.setPeopleThankedFor(thankedFor);
	}
}
	
	 private int findNumCharacters(String plainContent) {
		plainContent.length();
		return 0;
	}

	private int findNumSentences(String plainContent) {
		String[] sentences = plainContent.split("[\\.\\,\\?\\!]");
		return sentences.length;
	}

	private ArrayList<String> findAuthorsList(ArrayList<Node> tempAuthorNodes) {
		ArrayList<String> authors = new ArrayList<String>();
		for (Node node : tempAuthorNodes) {
			authors.add(node.getName());
		} 
		return authors;
	}

	private int findNumPlusOnes(String content){
			if(content != null && !content.equals(""))
				if(content.contains("+1 ") || content.contains("+1.") || content.contains("+1ing") || content.contains("+1-ed"))
					return 1;
				
			return 0;
		}
		
	 private int findNumThanks(String content){
			if(content != null && !content.equals(""))
				if(content.contains("Thanks") || content.contains("thanks") || content.contains("Thank you") || content.contains("thank you"))
					return 1;
				
			return 0;
		}
	
	 private ArrayList<String> findPeopleThankedFor(String content, ArrayList<Node> tempAuthors){
		 ArrayList<String> thankedFor = new ArrayList<String>();
		 
		 if(content != null && !content.equals("")){
			 String[] sentences = content.split("[\\.\\!\\?]");
			 
			 for (String sentence : sentences) {
				 sentence = sentence.trim();
				 sentence = sentence.toLowerCase();
				 if(sentence.contains("thanks") || sentence.contains("thank you") || sentence.contains("thx")|| sentence.contains("great work")|| sentence.contains("great job") || sentence.contains("that's great") || sentence.contains("good job")){
					 boolean found = false;
					 for (Node node : tempAuthors) {
							String[] names = (node.getName().toLowerCase()).split("[\\_\\-\\ ]");
							String[] words = sentence.split("[\\,\\ \\:\\@]");
							//if(sentence.contains(node.getName()) || sentence.contains(names[0])){
							for(int i = 0; i < words.length; i++){
								if(words[i].equals(names[0]) || words[i].equals(node.getName().toLowerCase())){
									thankedFor.add(node.getName());
									found = true;
								}
							}
						}
					 if(!found){
						 thankedFor.add(sentence);
					 }
					 /*if (sentence.contains("thanks to a")){
						 int byIndex = sentence.indexOf("by");
						 if (byIndex >=0 ){
							 String[] words = (sentence.substring(byIndex+3)).split("[\\,\\ ]");
							 if(words.length>0)
								 thankedFor.add(words[0]);
						 }
					 }else if (sentence.contains("thanks to")){
						 int index = sentence.indexOf("thanks to");
						 if (index >= 0){
							 String[] words = (sentence.substring(index+10)).split("[\\,\\ ]");
							 if(words.length>0)
								 thankedFor.add(words[0]);
						 }
					 }else if (sentence.contains("@")){
						 int index = sentence.indexOf("@");
						 if (index >= 0){
							 String[] words = (sentence.substring(index+1)).split("[\\,\\ ]");
							 if(words.length>0)
								 thankedFor.add(words[0]);
						 }
					 }else{
						for (Node node : tempAuthors) {
							String[] words = (node.getName()).split("[\\_\\-\\ ]");
							if(sentence.contains(node.getName()) || sentence.contains(words[0])){
								thankedFor.add(node.getName());
							}
						}
					 }*/
				 }
			 }
		 }
	
		 return thankedFor;
	}
	 
	 
	private int findNumNegativeWords(String content){
		 int numNegativeWords = 0;
		 if(content != null && !content.equals("")){
			 String[] sentences = content.split("[\\.\\!\\?]");
			 for (String sentence : sentences) {
				 sentence = sentence.trim();
				 sentence = sentence.toLowerCase();
				 for (int i = 0; i < negativeWords.length; i++)
					 if (sentence.contains(negativeWords[i].toLowerCase()) && !(sentence.contains("doesn't") || sentence.contains("doesn't") || sentence.contains("does not")|| sentence.contains("do not") || sentence.contains("don't")|| sentence.contains("is not") || sentence.contains("isn't") || sentence.contains("are't") || sentence.contains("are not") || sentence.contains("not") || sentence.contains("i")))
						 numNegativeWords++;
			 }
		 }
	
		 return numNegativeWords;
	}
	
	private int findNumNegativeExpressions(String content){
		 int numNegativeExpressions = 0;
		 if(content != null && !content.equals("")){
			 String[] sentences = content.split("[\\.\\!\\?]");
			 for (String sentence : sentences) {
				 sentence = sentence.trim();
				 sentence = sentence.toLowerCase();
				 if ((sentence.contains("i don't know") || sentence.contains("i don't think") || sentence.contains("i don't agree")|| sentence.contains("i'm not aware") || sentence.contains("i don't believe")|| sentence.contains("i don't want") || sentence.contains("i do not know") || sentence.contains("i do not think") || sentence.contains("i do not believe") || sentence.contains("i am not aware")))
						 numNegativeExpressions++;
			 }
		 }
	
		 return numNegativeExpressions;
	}
	
	private int findNumContatiousWords(String content) {
		int result = 0;
		if(content != null && !content.equals("")){
			if(content.contains("IMHO"))
				result ++;
			if(content.contains("IMO"))
				result++;
			content = content.toLowerCase();
			if(content.contains("instead"))
				result ++;
			if(content.contains("maybe"))
				result++;
			if(content.contains("rather"))
				result++;
			if(content.contains("opinion"))
				result++;
			if(content.contains("idea"))
				result++;
		}
		return result;
	}

	private int findNumWes(String content) {
		int result = 0;
		if(content != null && !content.equals("")){
		content = content.toLowerCase();
		String[] sentences = content.split("[\\.\\!\\?]");
			for (String sentence : sentences) {
				sentence = sentence.trim();
				String[] words = sentence.split("[\\,\\ \\:\\@]");
				for (String word : words) {
					if(word.equals("we"))
						result ++;
				}
			}		
		}
		return result;
	}

	private int findNumYouIs(String content) {
		int result = 0;
		if(content != null && !content.equals("")){
			if(content.contains("IMHO"))
				result ++;
			if(content.contains("IMO"))
				result++;
			content = content.toLowerCase();
			String[] sentences = content.split("[\\.\\!\\?]");
			for (String sentence : sentences) {
				sentence = sentence.trim();
				String[] words = sentence.split("[\\,\\ \\:\\@]");
				for (String word : words) {
					if(word.equals("you")||word.equals("i"))
						result ++;
				}
			}		
		}
		return result;
	}

	private ArrayList<Integer> findUniqueReceivers(ArrayList<String> temp, ArrayList<Integer> receivers){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (String value : temp) {
			Integer num = new Integer(Node.isCommentNumber(value));
			if(num != -1 && !receivers.contains(num))
					result.add(num);
		}
		return result;
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

	 public int findNumPreviousComments(AuthorsInfo authorInfo) {
			int result = 0;
			Node firstNode = null;
			int firstIndex = -1;
			if(authorInfo.getAuthor() != null)
				firstNode = socialGraphNodes.get(authorInfo.getAuthor().toLowerCase());
			if (firstNode == null)
			   	  System.out.println("findNumComments: Errrrrrror: " + authorInfo.getAuthor());
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

	 private int findNumQuestionMarks(String content){
			int result = 0;
			if(content != null && !content.equals(""))
			for(int i =0; i < content.length(); i++)
				if(content.charAt(i) == '?')
					result++;
			return result;
		}
	 private int findNumUsabilityTestings(String content){
			if(content != null && !content.equals(""))
				if(content.contains("Usability Testing") || content.contains("usability testing") || content.contains("User Testing") || content.contains("user testing"))
					return 1;
				
			return 0;
		}
	 private int findNumSummaries(String content){
			if(content != null && !content.equals(""))
				if(content.contains("Summary") || content.contains("summary") || content.contains("summarize"))
					return 1;
				
			return 0;
		}
	 private int findNumCodeReviews(String content){
			if(content != null && !content.equals(""))
				if(content.contains("Code Review") || content.contains("code review") || content.contains("review") || content.contains("reviewed"))
					return 1;
				
			return 0;
		}
		private int calculateNumWords(String content){
			if(content != null){
				String[] arr = content.split(" ");
				return arr.length;
			}else
				return 0;
		}
		private boolean IRCQuotation(String content) {
			if (content != null && (content.toLowerCase().contains(" irc ")|| content.toLowerCase().contains(" irc,") || content.toLowerCase().contains(" irc.") || content.toLowerCase().contains(" irc)")))
				return true;
			return false;
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
			ArrayList<AuthorsInfo> tempAuthors = new ArrayList<AuthorsInfo>();
			ArrayList<AuthorsInfo> authorNames = new ArrayList<AuthorsInfo>();
			ArrayList<Integer> expertise = new ArrayList<Integer>();
			
			//Go thourgh the authors in each issue
			for(int i = 0; i < issueInfo.getComments().size(); i++){
				String creator = null;
				if(issueInfo.getComments().get(0).getAuthor() != null)
					creator = issueInfo.getComments().get(0).getAuthor();
				if(issueInfo.getComments().get(i).getAuthor() != null){
					if(issueInfo.getComments().get(i).getAuthor().equals(creator))
						numCurCommentsOfCreator++;
					
					AuthorsInfo currentAuthorsInfo = null;
					//look for the author
					for (int j = 0; j < tempAuthors.size(); j++) {
						if(tempAuthors.get(j).getAuthor().equals(issueInfo.getComments().get(i).getAuthor())){
							currentAuthorsInfo = tempAuthors.get(j); 
							break;
						}
					}
					if(currentAuthorsInfo == null || currentAuthorsInfo.equals(null)){
						numUniqueAuthors ++;
						currentAuthorsInfo = new AuthorsInfo(issueInfo.getComments().get(i).getAuthor(), issueInfo.getComments().get(i).getAuthorLink());
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
			for (AuthorsInfo tempAuthor : tempAuthors) {
				boolean authorFound = false;
				for (int k = 0; k < authorsInfos.size(); k++) {
					if(authorsInfos.get(k).getAuthor().equals(tempAuthor.getAuthor())){
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
						if(!tempAuthor.getAuthor().equals("System Message"))
							expertise.addAll(findExpertise(authorsInfos.get(k)));
						previousComments.add(findNumPreviousComments(authorsInfos.get(k)));
						pageRanks.add(socialGraphNodes.get(authorsInfos.get(k).getAuthor().toLowerCase()).getRank());
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
					if(!tempAuthor.getAuthor().equals("System Message"))
						expertise.addAll(findExpertise(tempAuthor));
					pageRanks.add(socialGraphNodes.get(tempAuthor.getAuthor().toLowerCase()).getRank());
				}
			}
			
			//Check for Triangular Connections
			for (int i = 0; i < authorNames.size(); i++) {
				for (int j = i+1; j < authorNames.size(); j++) {
					for (int k = j+1; k < authorNames.size(); k++) {
						int firstIndex = socialGraphNodes.get(authorNames.get(i).getAuthor().toLowerCase()).getIndex();
						int secondIndex = socialGraphNodes.get(authorNames.get(j).getAuthor().toLowerCase()).getIndex();
						int thirdIndex = socialGraphNodes.get(authorNames.get(k).getAuthor().toLowerCase()).getIndex();
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

		private ArrayList<Integer> findExpertise(AuthorsInfo authorInfo){
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
