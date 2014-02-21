package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class IssueQueueParser {   
    private ArrayList<IssueInfo> issues = new ArrayList<IssueInfo>();
    private ArrayList<IssueInfo> duplicates = new ArrayList<IssueInfo>();
    
    private IssueInfo currentIssue;
   
    private void loadIssueInfos(String fileName) throws IOException {	
		String currentLine = "";
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		while ((currentLine = br.readLine()) != null) {
			int index = currentLine.indexOf(',');
			if(index != -1){
				String link = currentLine.substring(0, index);
				int numPages = Integer.parseInt(currentLine.substring(index+2));
				issues.add(new IssueInfo(link, numPages));
			}
		}
		br.close();
	}
	
	private void parseIssueInfos(String filePath, String SUFFIX) throws IOException {
		for (IssueInfo issue : issues) {
			File threadFile = new File(filePath + FindIssueNameFromPartialLink(issue.getLink()) +SUFFIX +".txt");
			Document doc = Jsoup.parse(threadFile, null);
			
			currentIssue = issue;
			parseThreadMetaData(doc);
			
			if (!issue.getStatus().equals("closed (duplicate)"))
				duplicates.add(issue);
		}
	}
	
	private void parseThreadMetaData(Document doc) throws IOException {
		//Find thread title	
		Element title = doc.getElementById("page-subtitle");
		currentIssue.setTitle(title.text());
		
		//Find the thread initator info block
		Element threadInitiatorBlock = doc.getElementById("node-"
				+ FindIssueNameFromPartialLink(currentIssue.getLink()));

		if (threadInitiatorBlock != null) {
			if (threadInitiatorBlock.select("div.submitted > a") != null) {
				if (threadInitiatorBlock.select("div.submitted > a").first() != null) {
					Element date = threadInitiatorBlock.select(
							"div.submitted > time").first();
					currentIssue.setDate(date.text());
				}
			}
		}

		Element issueSummaryTable = doc.getElementById("block-project-issue-issue-metadata");
		
		Element status = issueSummaryTable.select("div[class^=field field-name-field-issue-status]").first();
		if(status != null)
			currentIssue.setStatus(status.select("div[class=field-item even]").first().text());
		
		Element project = issueSummaryTable.select("div[class^=field field-name-field-project]").first();
		if(project != null)
			currentIssue.setProject(project.select("div[class=field-item even]").first().text());
		
		Element version = issueSummaryTable.select("div[class^=field field-name-field-issue-version]").first();
		if(version != null)
			currentIssue.setVersion(version.select("div[class=field-item even]").first().text());
		
		Element component = issueSummaryTable.select("div[class^=field field-name-field-issue-component]").first();
		if(component != null)
			currentIssue.setComponent(component.select("div[class=field-item even]").first().text());
		
		Element priority = issueSummaryTable.select("div[class^=field field-name-field-issue-priority]").first();
		if(priority != null)
			currentIssue.setPriority(priority.select("div[class=field-item even]").first().text());
		
		Element category = issueSummaryTable.select("div[class^=field field-name-field-issue-category]").first();
		if(category != null)
			currentIssue.setCategory(category.select("div[class=field-item even]").first().text());
		
		Element assigned = issueSummaryTable.select("div[class^=field field-name-field-issue-assigned]").first();
		if(assigned != null)
			currentIssue.setAssigned(assigned.select("div[class=field-item even]").first().text());
		
		Element taxonomy = issueSummaryTable.select("div[class^=field field-name-taxonomy]").first();
		if(taxonomy != null){
		Elements tags = taxonomy.select("div[class=field-items]").first().select("a");
			for (Element tag : tags) {
				currentIssue.addTags(tag.text());
			}
		}
			
		Element issue = doc.select("div[class=field field-name-body field-type-text-with-summary field-label-hidden]").first();
		Elements paragraphs = issue.select("p");
		String content = "";
		for (Element paragraph : paragraphs) {
			content += paragraph.text();
		}
		
		currentIssue.setContent(content);
		System.out.println("thread: " + currentIssue);
	}
	
	public static String FindIssueNameFromPartialLink(String link) {
		String name = (link.replaceFirst("node", "")).substring(2);
		int index = name.indexOf('#');
		if (index > 0)
			name = name.substring(0, index);
		return name;
	}
    
	private void removeDuplicates(){
		for (IssueInfo issue : duplicates) {
			issues.remove(issue);
		}
	}
	
	public ArrayList<IssueInfo> loadAndParseIssues(String issueListFileName, String allIssuefilesPath, String SUFFIX) throws IOException{
		loadIssueInfos(issueListFileName);
		parseIssueInfos(allIssuefilesPath, SUFFIX);
		removeDuplicates();
    	return issues;
    }  
	
    public ArrayList<IssueInfo> getIssues(){
    	return issues;
    }
    
    public int getNumOfIssues(){
    	return issues.size();
    }

}
