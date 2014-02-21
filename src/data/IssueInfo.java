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
	
	/*Issue Info Analysis*/
	private double replyToRatio;
	private int IRCQuatation;
	private int totalNumWords;
	private ArrayList<Double> durations = new ArrayList<Double>();
	private int numQuestionMarks;
	private int numUsabilityTesting;
	private int numSummaries;
	private int numCodeReviews;
	private int numContatiousWords;
	private int numWes;
	private int numYouIs;
	private int numPlusOnes;
	private int numThanks;
	private int numNegativeWords;
	private int numPositiveWords;
	private int numNeutralWords;
	private double characterToSentenceRatio;
	private double wordToSentenceRatio;
	private int numSentences;
	private int numNegativeExpressions;
	private ArrayList<String> uniqueAuthorNames;
	private ArrayList<String> peopleThankedFor;
	
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
	public ArrayList<String> getPeopleThankedFor() {
		return peopleThankedFor;
	}
	public void setPeopleThankedFor(ArrayList<String> peopleThankedFor) {
		this.peopleThankedFor = peopleThankedFor;
	}
	public ArrayList<String> getUniqueAuthorNames() {
		return uniqueAuthorNames;
	}
	public void setUniqueAuthorNames(ArrayList<String> uniqueAuthorNames) {
		this.uniqueAuthorNames = uniqueAuthorNames;
	}
	public int getNumPlusOnes() {
		return numPlusOnes;
	}
	public void setNumPlusOnes(int numPlusOnes) {
		this.numPlusOnes = numPlusOnes;
	}
	public int getNumThanks() {
		return numThanks;
	}
	public void setNumThanks(int numThanks) {
		this.numThanks = numThanks;
	}
	public int getNumNegativeExpressions() {
		return numNegativeExpressions;
	}
	public void setNumNegativeExpressions(int numNegativeExpressions) {
		this.numNegativeExpressions = numNegativeExpressions;
	}
	public int getNumNegativeWords() {
		return numNegativeWords;
	}
	public void setNumNegativeWords(int numNegativeWords) {
		this.numNegativeWords = numNegativeWords;
	}
	public int getNumPositiveWords() {
		return numPositiveWords;
	}
	public void setNumPositiveWords(int numPositiveWords) {
		this.numPositiveWords = numPositiveWords;
	}
	public int getNumNeutralWords() {
		return numNeutralWords;
	}
	public void setNumNeutralWords(int numNeutralWords) {
		this.numNeutralWords = numNeutralWords;
	}
	public double getCharacterToSentenceRatio() {
		return characterToSentenceRatio;
	}
	public void setCharacterToSentenceRatio(double characterToSentenceRatio) {
		this.characterToSentenceRatio = characterToSentenceRatio;
	}
	public double getWordToSentenceRatio() {
		return wordToSentenceRatio;
	}
	public void setWordToSentenceRatio(double wordToSentenceRatio) {
		this.wordToSentenceRatio = wordToSentenceRatio;
	}
	public int getNumSentences() {
		return numSentences;
	}
	public void setNumSentences(int numSentences) {
		this.numSentences = numSentences;
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
	public double getReplyToRatio(){
		return replyToRatio;
	}
	public void setReplyToRatio(double ratio){
		replyToRatio = ratio;
	}
	public int getIRCQuatation(){
		return IRCQuatation;
	}
	public void setIRCQuatation(int IRCQuatation){
		this.IRCQuatation = IRCQuatation;
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
	public int getNumPatches() {
		int numPatches = 0;
		for (CommentInfo comment : comments) {
			if(comment.getPatchAttached() == true)
				numPatches++;
		}
		return numPatches;
	}
	public int getNumOutsideResources(){
		int result = 0;
		for (int i = 0; i < comments.size(); i++) {
			result += comments.get(i).getNumOutsideResources();
		}		
		return result;
	}
	public int getNumInfluentialAuthors(){
		int result = 0;
		String [] InfluentialAuthors= {"Bojhan", "sun", "yoroy", "webchick", "catch", "David_Rothstein"
				,"Gábor Hojtsy", "Dries", "mgifford", "Everett Zufelt", "jhodgdon", "dww", "eigentor", "Dave Reid", "chx", "Aren Cambre", "casey", "aspilicious", "yched", "moshe weitzman", "bowersox", "Damien Tournoud", "seutje", "Xano", "Rob Loach", "pwolanin", "Jeff Burnz", "tstoeckler", "TheRec", "EvanDonovan", "quicksketch"}; //"Bojhan","sun","webchick","yoroy","catch","David_Rothstein","Gábor Hojtsy","Dries","mgifford","jhodgdon", "Everett Zufelt","eigentor","dww","Dave Reid","Aren Cambre","casey","chx","moshe weitzman","aspilicious","Damien Tournoud","yched","seutje","TheRec","Xano","pwolanin","tstoeckler","Rob Loach","Jeff Burnz","stBorchert","quicksketch"};
		int [] InfComments = new int[InfluentialAuthors.length];
		for (int i = 0; i < comments.size(); i++) {
			for (int j = 0; j < InfluentialAuthors.length; j++) {
				if(comments.get(i).getAuthor() != null && comments.get(i).getAuthor().toLowerCase().equals(InfluentialAuthors[j].toLowerCase())){
					InfComments[j]++;
					break;
				}
			}
		}		
		
		for(int i = 0 ; i < InfComments.length; i++)
			if(InfComments[i] > 0)
				result ++;
		return result;
	}
	public int getNumScreenshots(){
		int result = 0;
		for (int i = 0; i < comments.size(); i++) {
			result += comments.get(i).getNumScreenshots();
		}		
		return result;
	}
	public void setTotalNumWords(int sumNumWords) {
		this.totalNumWords = sumNumWords;
	}
	public int getTotalNumWords() {
		return this.totalNumWords;
	}
	public void setDurations(ArrayList<Double> durations) {
		this.durations = durations;	
	}
	public ArrayList<Double> getDurations(){
		return durations;
	}
	public void setNumQuestionMarks(int numQuestionMarks) {
		this.numQuestionMarks = numQuestionMarks;
	}
	public int getNumQuestionMarks(){
		return numQuestionMarks;
	}
	public String printString(SocialGraph socialGraph) {
		//find the duration of the issue in hours
		double duration = Stats.findDuration(getComments().get(0).getDate(), getComments().get(getComments().size() - 1).getDate());
		//Status + start time + duration + end Time + numComments + numPatches+ numInfluentialAuthors + link to issue + numAuthors later
		String issueInfoPrint = getLink() + "\t"+
		getComments().get(0).getDate().toString() + "\t" + 
		getComments().get(getComments().size() - 1).getDate().getTime() + "\t" + 
		getStatus() + "\t" +
		getComments().get(0).getDate().getTime() +"\t" + 
		duration + "\t"+ 
		(getComments().size() - 1) + "\t" + 
		getNumPatches() + "\t" + 
		getNumInfluentialAuthors()+ "\t"; 
		
		
		String info = socialGraph.findAuthorsInfo(this);
		
		//add num unique authors, usability interests, triangularConnections, mean previous comments commits and the newline, 
		issueInfoPrint +=  info + "\t" + 
		getReplyToRatio() + "\t" + 
		getIRCQuatation() +"\t" + 
		getTotalNumWords() + "\t" + 
		socialGraph.findMeanNumConsensusThreads(this) + "\t" + 
		getNumQuestionMarks() + "\t"+ 
		isPatchSubmittedIntheFirstThreeComments() + "\t" +
		getPriority() + "\t" +
		getNumUsabilityTesting() + "\t" +
		getNumSummaries() + "\t" +
		getNumCodeReviews() + "\t" +
		getNumScreenshots() + "\t" +
		getNumOutsideResources() + "\t" +
		getNumContatiousWords() + "\t" +
		getNumWes() + "\t" +
		getNumYouIs() + "\t" +
		getNumPlusOnes() + "\t" +
		getNumThanks() + "\t" +
		getNumNegativeWords() + "\t" +
		getNumPositiveWords() + "\t" +
		getNumNeutralWords() + "\t" +
		getCharacterToSentenceRatio() + "\t" +
		getWordToSentenceRatio() + "\t" +
		getNumSentences() + "\t" +
		getNumNegativeExpressions() + "\t" +
		Stats.findDuration(getComments().get(getComments().size()-1).getDate(), new Date(System.currentTimeMillis())) + "\t" +
		getPeopleThankedFor() + "\t" +
		getUniqueAuthorNames() + "\t";
		
		issueInfoPrint += "\n";			

		return issueInfoPrint;
	}
		
	public int isPatchSubmittedIntheFirstThreeComments(){
		int patch = 0;
		for (int i = 0; i < comments.size(); i++) {
			if(comments.get(i).getPatchAttached() == true)
				patch = 1;
			if(i==2)
				break;
		}
		return patch;
	}
	public String printDurations() {
		String durationInfo = "";
		for(int i = 0; i < getDurations().size(); i++){
			durationInfo +=  getDurations().get(i)+ "\n";
		}
		return durationInfo;
	}
	public void setNumUsabilityTesting(int numUsabilityTesting) {
		this.numUsabilityTesting = numUsabilityTesting;
	}
	
	public int getNumUsabilityTesting(){
		return numUsabilityTesting;
	}
	public void setNumSummaries(int numSummaries) {
		this.numSummaries = numSummaries;
	}
	public int getNumSummaries(){
		return numSummaries;
	}
	public void setNumCodeReviews(int numCodeReviews) {
		this.numCodeReviews = numCodeReviews;
	}
	public int getNumCodeReviews(){
		return numCodeReviews;
	}
	public void setNumContatiousWords(int numContatiousWords) {
		this.numContatiousWords = numContatiousWords;
	}
	public int getNumContatiousWords(){
		return numContatiousWords;
	}
	public void setNumWes(int numWes) {
		this.numWes = numWes;
	}
	public int getNumWes(){
		return numWes;
	}
	public void setNumYouIs(int numYouIs) {
		this.numYouIs = numYouIs;
	}
	public int getNumYouIs(){
		return numYouIs;
	}
}
