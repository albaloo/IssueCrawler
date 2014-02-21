package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import analysis.ContentAnalysis;

public class ContentAnalysisTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindNumCharacters() {
		assertEquals(3, ContentAnalysis.findNumCharacters("abc"));
	}

	@Test
	public void testFindNumSentences() {
		assertEquals(3, ContentAnalysis.findNumSentences("abc. hi! how are you? I'm fine, thank you."));
	}

	@Test
	public void testFindNumPlusOnes() {
		assertEquals(1, ContentAnalysis.findNumPlusOnes("+1. this looks awesome!"));
		assertEquals(0, ContentAnalysis.findNumPlusOnes("+123. this looks awesome!"));
	}

	@Test
	public void testFindNumThanks() {
		assertEquals(1, ContentAnalysis.findNumThanks("thx!"));
	}

	@Test
	public void testFindNumNegativeWords() {
		assertEquals(0, ContentAnalysis.findNumNegativeWords("This isbad!"));
		assertEquals(3, ContentAnalysis.findNumNegativeWords("This is BAd, disappointing, very bad, badal?"));
	}

	@Test
	public void testFindNumPositiveWords() {
		assertEquals(1, ContentAnalysis.findNumPositiveWords("This is good."));
	}
	
	@Test
	public void testFindNumStopWords() {
		assertEquals(4, ContentAnalysis.findNumStopWords("This is IS a bad."));
		assertEquals(1, ContentAnalysis.findNumStopWords("This isa"));
	}
	
	@Test
	public void testFindNumNegativeExpressions() {
		assertEquals(1, ContentAnalysis.findNumNegativeExpressions("I don't know!!"));
	}

	@Test
	public void testFindNumContatiousWords() {
		assertEquals(2, ContentAnalysis.findNumContatiousWords("IMHO, instead of this we should do that!?no?"));
	}

	@Test
	public void testFindNumWes() {
		assertEquals(2, ContentAnalysis.findNumWes("we are goo, aren't we?? well, well!"));
	}

	@Test
	public void testFindNumYouIs() {
		assertEquals(2, ContentAnalysis.findNumYouIs("I are goo, i am aren't we?? well, well!you're not!"));
	}

	@Test
	public void testFindNumQuestionMarks() {
		assertEquals(2, ContentAnalysis.findNumQuestionMarks("I are goo, i am aren't we ? ? well, well!you're not!"));
	}

	@Test
	public void testFindNumUsabilityTestings() {
		assertEquals(1, ContentAnalysis.findNumCommentsMentionedUsabilityTestings("usability testingssss, ui testing, usability test."));
	}

	@Test
	public void testFindNumSummaries() {
		assertEquals(1, ContentAnalysis.findNumCommentsMentionedSummaries("summary, sum"));
	}

	@Test
	public void testFindNumCodeReviews() {
		assertEquals(1, ContentAnalysis.findNumCommentsMentionedCodeReviews("review your code, code review."));
	}

	@Test
	public void testCalculateNumWords() {
		assertEquals(5, ContentAnalysis.calculateNumWords("review your code, code review..."));	
	}

	@Test
	public void testIRCQuotation() {
		assertEquals(true, ContentAnalysis.mentionsIRC("we talked about it in irc."));
		assertEquals(false, ContentAnalysis.mentionsIRC("mirc,"));
	}
	
}
