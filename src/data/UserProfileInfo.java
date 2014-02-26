package data;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import analysis.CalculateVariables;

public class UserProfileInfo {

	private String username;
	private String profileLink;
	private int comments;
	private int threads;
	private ArrayList<Date> dates = new ArrayList<Date>();
	private int longestThread = 0; // The largest thread they have participated
									// in.
	private int membershipWeeks = 0;
	private String jobTitle = "";
	private ArrayList<String> interests = new ArrayList<String>();
	private String firstName;
	private String lastName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public UserProfileInfo(String author, String authorLink) {
		this.username = author;
		this.profileLink = authorLink;
		comments = 0;
		threads = 0;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String author) {
		this.username = author;
	}

	public String getProfileLink() {
		return profileLink;
	}

	public void setProfileLink(String authorLink) {
		this.profileLink = authorLink;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public void addComments() {
		this.comments++;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public void addThreads() {
		this.threads++;
	}

	public void addDate(Date date) {
		dates.add(date);
	}

	public void addDates(ArrayList<Date> dates2) {
		dates.addAll(dates2);
	}

	public ArrayList<Date> getDates() {
		return dates;
	}

	public Date getDate(int i) {
		return dates.get(i);
	}

	public int getLongestThread() {
		return longestThread;
	}

	public void setLongestThread(int longestThread) {
		if (longestThread > this.longestThread)
			this.longestThread = longestThread;
	}


	public void parseProfile() {
		ProfileParser parser = new ProfileParser();
		try {
			parser.parseProfile(
						this,
						CalculateVariables.profileListFileName,
						CalculateVariables.SUFFIX);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int findMembershipWeeks(String value) {
		int result = 0;
		int years = 0;
		int months = 0;
		int weeks = 0;

		if (value == "" || value == null)
			return result;
		else {
			int yearIndex = value.indexOf("year");
			if(yearIndex >= 0){
				String yearString = value.substring(0, yearIndex);
				years = findNumber(yearString);
				value = value.substring(yearIndex+3);
			}
			
			int monthIndex = value.indexOf("month");
			if(monthIndex >= 0){
				String monthString = value.substring(0, monthIndex);
				months = findNumber(monthString);
				value = value.substring(monthIndex+3);
			}
			
			int weekIndex = value.indexOf("week");
			if(weekIndex >= 0){
				String weekString = value.substring(0, weekIndex);
				weeks = findNumber(weekString);
				value = value.substring(weekIndex+3);
			}
			
		}

		result = years * 52 + months * 4 + weeks;
		return result;
	}
	
	private int findNumber(String value){
		String result = "" ;
		for (char c: value.toCharArray()) {
			if(c>='0' && c <='9')
				result += c;
		}
		return Integer.parseInt(result);
	}

	public int matchingInterest(ArrayList<String> otherInterests) {
		int result = 0;
		for (String interest : interests) {
			for (String otherInterest : otherInterests) {
				if (interest.equals(otherInterest))
					result++;
			}
		}
		return result;
	}

	public ArrayList<String> getInterests() {
		return interests;
	}

	public boolean hasInterest(String interest) {
		for (String inter : interests) {
			if (inter.equals(interest))
				return true;
		}
		return false;
	}

	public boolean sharedInterest(UserProfileInfo other) {
		for (String interest : interests) {
			for (String otherInterest : other.interests) {
				if (interest.equals(otherInterest))
					return true;
			}
		}
		return false;
	}
	
	public void addInterest(String interest){
		interests.add(interest);
	}

	public void setMembershipWeeks(String membershipWeeks) {
		this.membershipWeeks = findMembershipWeeks(membershipWeeks);
	}

	public int getMembershipWeeks() {
		return membershipWeeks;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	@Override
	public String toString() {
		String print = "";
		print += "firstName: " + firstName + "\t" + "lastName: " + lastName + "\t" + "membershipWeeks: " + membershipWeeks + "\t";
		print += "jobTitle: " + jobTitle + "\t" + "interests: ";
		for (String interest : interests) {
			print += interest + ",\t";
		}
		return print;
	}

}