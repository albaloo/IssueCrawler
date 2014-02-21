package analysis;
import java.util.Date;

public class Edge {

	private Double weight = 0.0;
	private SocialMatrixCell cell;
	
	public Edge(Double weight, SocialMatrixCell cell) {
		super();
		this.weight = weight;
		this.cell = cell;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	public int getNumConsensusThreads(Date date){
		return cell.getNumConsensusThreads(date);
	}
}

