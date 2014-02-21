package analysis;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class Stats {

	public static double findDuration(Date firstDate, Date secondDate){
		double duration = secondDate.getTime() - firstDate.getTime();
		duration = duration * 0.001;//in ms
		duration = duration / 60.00;//in minute
		duration = duration / 60.00; //in hours
		duration = duration / 24;
		duration = duration / 7;
		return duration;
	}
	public static Double findMean(ArrayList<Integer> array, int counter){
		double mean = 0;
		for(int i = 0; i < array.size(); i++)
			mean += array.get(i);
		
		mean = mean/(float) counter;
		
		return mean;
	}
	
	public static Double findStdev(ArrayList<Integer> array, int counter, double mean){
		float variance = 0;
		for(int i = 0; i < array.size(); i++)
			variance += Math.pow((mean - array.get(i)), 2);
		variance = variance / (float)(counter - 1);
		double Stdev = Math.sqrt(variance);
		return Stdev;
	}
	
	public static int findMedian(ArrayList<Integer> values){
		Collections.sort(values);
		if (values.size() == 0)
			return 0;
 
		if (values.size() % 2 == 1)
			return values.get((values.size()+1)/2-1);
		else
		{
			int lower = values.get(values.size()/2-1);
			int upper = values.get(values.size()/2);
			return (lower + upper) / 2;
		}
    }	
    
	public static Double findMean_D(ArrayList<Double> array, int counter){
		double mean = 0;
		for(int i = 0; i < array.size(); i++)
			mean += array.get(i);
		
		mean = mean/(float) counter;
		
		return mean;
	}
	
	public static Double findStdev_D(ArrayList<Double> array, int counter, double mean){
		float variance = 0;
		for(int i = 0; i < array.size(); i++)
			variance += Math.pow((mean - array.get(i)), 2);
		variance = variance / (float)(counter - 1);
		double Stdev = Math.sqrt(variance);
		return Stdev;
	}
	
	public static double findMedian_D(ArrayList<Double> values){
		Collections.sort(values);
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

}
