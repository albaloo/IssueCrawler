package analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class Tagger {

	private String pathToSWN = "SentiWordNet_3.0.0_20120510.txt";
	private HashMap<String, Double> allWords = new HashMap<String, Double> ();
	private int numPositiveWords = 0;
	private int numNegativeWords = 0;
	private int numNeutralWords = 0;

	public int getNumPositiveWords() {
		return numPositiveWords;
	}

	public void setNumPositiveWords(int numPositiveWords) {
		this.numPositiveWords = numPositiveWords;
	}

	public int getNumNegativeWords() {
		return numNegativeWords;
	}

	public void setNumNegativeWords(int numNegativeWords) {
		this.numNegativeWords = numNegativeWords;
	}

	public int getNumNeutralWords() {
		return numNeutralWords;
	}

	public void setNumNeutralWords(int numNeutralWords) {
		this.numNeutralWords = numNeutralWords;
	}

	public static void main(String[] args) {
		Tagger tag = new Tagger();
		tag.loadSWNDataFile();
		try {
			tag.tagTheComment("Thought about this some more and here's a much more performant version which also gets us real permalinks for comments at last. Links to comments are now formatted as node/$nid/comment/$cid#comment-cid - this means we don't need to run comment_get_display_page() just to format the links. Comment module now defines a menu callback for node/%node/comment/%cid - and when that path is visited, it runs comment_get_display_page and shows you the correct page of comment links (and the fragment already in the link gets you to the actual comment). I was going to do this with drupal_goto() but chx suggested menu_execute_active_handler() which saves the extra bootstrap and redirect. So the recent comments block and anything else linking to comments just needs to define the link correctly, and all the hard work is done in the callback. It also means you can link to a comment using that format, and even if users change their comment settings or you change from threaded to flat or whatever, you'll always get taken to the right place. Converted all the same places to use this, tests still pass. Also ran an EXPLAIN on the comment_get_display_page() query and it's not bad at all - so considering this only runs when you actually want to visit the comment, not just when a link to one is displayed, it seems pretty good.");		
			} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void tagTheComment(String comment) throws IOException, ClassNotFoundException{
		numPositiveWords = 0;
		numNegativeWords = 0;
		numNeutralWords = 0;

		MaxentTagger tagger = new MaxentTagger("taggers/wsj-0-18-bidirectional-distsim.tagger");
		String[] sentences = comment.split("[\\.\\,\\?\\!]");
		//This/DT is/VBZ a/DT sample/NN sentence/NN
		for (String sentence : sentences) {
			String tagged = tagger.tagString(sentence);
			String[] words = tagged.split(" ");
			for (String word : words) {
				if(word!= null && !word.equals("") &&!word.equals("_")){
					String[] tags = word.split("_");
					if(tags.length>1){
						double polarity = 0;
						if(tags[1].startsWith("JJ"))
							polarity = findPolarity(tags[0],"a");
						else if(tags[1].startsWith("NN"))
							polarity = findPolarity(tags[0],"n");
						else if(tags[1].startsWith("VB"))
							polarity = findPolarity(tags[0],"v");
			
						if (polarity > 0.25 && !tagged.contains(" n't_RB ") && !tagged.contains(" not_RB ")){
						System.out.println("word: " + tags[0]);
							numPositiveWords++;
						}else if (polarity < -0.25 && !tagged.contains(" n't_RB ") && !tagged.contains(" not_RB ")){
						System.out.println("word: " + tags[0]);
							numNegativeWords++;
						}else{
							numNeutralWords++;
						}
					}
				}
			}
		}
		System.out.println("positive: " + numPositiveWords + " negative: " + numNegativeWords + " neutral: " + numNeutralWords);
		//System.out.println(tagged);
	}
	

	public void loadSWNDataFile(){
		HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
		try{
			BufferedReader csv =  new BufferedReader(new FileReader(pathToSWN));
			String line = "";			
			while((line = csv.readLine()) != null)
			{
				String[] data = line.split("\t");
				Double score = Double.parseDouble(data[2])-Double.parseDouble(data[3]);
				String[] words = data[4].split(" ");
				for(String w:words)
				{
					String[] w_n = w.split("#");
					w_n[0] += "#"+data[0];
					int index = Integer.parseInt(w_n[1])-1;
					if(_temp.containsKey(w_n[0]))
					{
						Vector<Double> v = _temp.get(w_n[0]);
						if(index>v.size())
							for(int i = v.size();i<index; i++)
								v.add(0.0);
						v.add(index, score);
						_temp.put(w_n[0], v);
					}
					else
					{
						Vector<Double> v = new Vector<Double>();
						for(int i = 0;i<index; i++)
							v.add(0.0);
						v.add(index, score);
						_temp.put(w_n[0], v);
					}
				}
			}
			Set<String> temp = _temp.keySet();
			for (Iterator<String> iterator = temp.iterator(); iterator.hasNext();) {
				String word = (String) iterator.next();
				Vector<Double> v = _temp.get(word);
				double score = 0.0;
				double sum = 0.0;
				for(int i = 0; i < v.size(); i++)
					score += ((double)1/(double)(i+1))*v.get(i);
				for(int i = 1; i<=v.size(); i++)
					sum += (double)1/(double)i;
				score /= sum;
				allWords.put(word, score);
			}
		}
		catch(Exception e){e.printStackTrace();}		
	}

	public double findPolarity(String word, String pos){
		//pos: n for noun files, v for verb files, a for adjective files, r for adverb files.
		Double polarity = allWords.get(word+"#"+pos);
		if (polarity == null)
			return 0;
		else
			return polarity;
	}
	
	private String printScore(double score){
		String sent = "";				
		if(score>=0.75)
			sent = "strong_positive";
		else
		if(score > 0.25 && score<=0.5)
			sent = "positive";
		else
		if(score > 0 && score>=0.25)
			sent = "weak_positive";
		else
		if(score < 0 && score>=-0.25)
			sent = "weak_negative";
		else
		if(score < -0.25 && score>=-0.5)
			sent = "negative";
		else
		if(score<=-0.75)
			sent = "strong_negative";
		return sent;
	}

}
