package analysis;

import java.util.ArrayList;
import java.util.Date;

import data.CommentInfo;
import data.IssueInfo;

public class IssueAnalysis {
	
	private IssueInfo issueInfo;

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
	
	public IssueAnalysis(IssueInfo issueInfo) {
		super();
		this.issueInfo = issueInfo; 
	}	
	public IssueInfo getIssueInfo(){
		return issueInfo;
	}
	public void setIssueInfor(IssueInfo issueInfo){
		this.issueInfo = issueInfo;
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
	public String printString(SocialGraph socialGraph) {
		//find the duration of the issue in hours
		double duration = Stats.findDuration(issueInfo.getComments().get(0).getDate(), issueInfo.getComments().get(issueInfo.getComments().size() - 1).getDate());
		//Status + start time + duration + end Time + numComments + numPatches+ numInfluentialAuthors + link to issue + numAuthors later
		String issueInfoPrint = issueInfo.getLink() + "\t"+
		issueInfo.getComments().get(0).getDate().toString() + "\t" + 
		issueInfo.getComments().get(issueInfo.getComments().size() - 1).getDate().getTime() + "\t" + 
		issueInfo.getStatus() + "\t" +
		issueInfo.getComments().get(0).getDate().getTime() +"\t" + 
		duration + "\t"+ 
		(issueInfo.getComments().size() - 1) + "\t" + 
		getNumPatches() + "\t" + 
		getNumInfluentialAuthors()+ "\t"; 
		
		
		String info = socialGraph.findAuthorsInfo(issueInfo);
		
		//add num unique authors, usability interests, triangularConnections, mean previous comments commits and the newline, 
		issueInfoPrint +=  info + "\t" + 
		getReplyToRatio() + "\t" + 
		getIRCQuatation() +"\t" + 
		getTotalNumWords() + "\t" + 
		socialGraph.findMeanNumConsensusThreads(issueInfo) + "\t" + 
		getNumQuestionMarks() + "\t"+ 
		isPatchSubmittedIntheFirstThreeComments() + "\t" +
		issueInfo.getPriority() + "\t" +
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
		Stats.findDuration(issueInfo.getComments().get(issueInfo.getComments().size()-1).getDate(), new Date(System.currentTimeMillis())) + "\t" +
		getPeopleThankedFor() + "\t" +
		getUniqueAuthorNames() + "\t";
		
		issueInfoPrint += "\n";			

		return issueInfoPrint;
	}
		
	public int isPatchSubmittedIntheFirstThreeComments(){
		int patch = 0;
		for (int i = 0; i < issueInfo.getComments().size(); i++) {
			if(issueInfo.getComments().get(i).getPatchAttached() == true)
				patch = 1;
			if(i==2)
				break;
		}
		return patch;
	}
	public int getNumPatches() {
		int numPatches = 0;
		for (CommentInfo comment : issueInfo.getComments()) {
			if(comment.getPatchAttached() == true)
				numPatches++;
		}
		return numPatches;
	}
	public int getNumOutsideResources(){
		int result = 0;
		for (int i = 0; i < issueInfo.getComments().size(); i++) {
			result += issueInfo.getComments().get(i).getNumOutsideResources();
		}		
		return result;
	}
	public int getNumInfluentialAuthors(){
		int result = 0;
		String [] InfluentialAuthors= {"Bojhan", "sun", "yoroy", "webchick", "catch", "David_Rothstein"
				,"Gábor Hojtsy", "Dries", "mgifford", "Everett Zufelt", "jhodgdon", "dww", "eigentor", "Dave Reid", "chx", "Aren Cambre", "casey", "aspilicious", "yched", "moshe weitzman", "bowersox", "Damien Tournoud", "seutje", "Xano", "Rob Loach", "pwolanin", "Jeff Burnz", "tstoeckler", "TheRec", "EvanDonovan", "quicksketch"}; //"Bojhan","sun","webchick","yoroy","catch","David_Rothstein","Gábor Hojtsy","Dries","mgifford","jhodgdon", "Everett Zufelt","eigentor","dww","Dave Reid","Aren Cambre","casey","chx","moshe weitzman","aspilicious","Damien Tournoud","yched","seutje","TheRec","Xano","pwolanin","tstoeckler","Rob Loach","Jeff Burnz","stBorchert","quicksketch"};
		int [] InfComments = new int[InfluentialAuthors.length];
		for (int i = 0; i < issueInfo.getComments().size(); i++) {
			for (int j = 0; j < InfluentialAuthors.length; j++) {
				if(issueInfo.getComments().get(i).getAuthor() != null && issueInfo.getComments().get(i).getAuthor().toLowerCase().equals(InfluentialAuthors[j].toLowerCase())){
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
		for (int i = 0; i < issueInfo.getComments().size(); i++) {
			result += issueInfo.getComments().get(i).getNumScreenshots();
		}		
		return result;
	}
	
}
