package test;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import analysis.CalculateVariables;

public class CalculateVariblesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		CalculateVariables demo = new CalculateVariables();
		try {
			demo.calculateVaribalesForTestIssue("C:\\Users\\rzilouc2\\Documents\\GitHub\\IssueCrawler\\src\\test\\all-threads-.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
