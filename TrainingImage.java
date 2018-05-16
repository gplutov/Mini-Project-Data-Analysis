package miniProject;

public class TrainingImage implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	public int label;
	public int[][] imgPixels;

	public TrainingImage(int label,int[][] imgPixels) {
		this.label = label;
		this.imgPixels = imgPixels;
	}
}