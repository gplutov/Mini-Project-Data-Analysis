package miniProject;

public class TrainingImageGroup {
	
	int label;
	TrainingImage[] imgGroup;//all the images in the group
	int size; //amount of images
	double[][] ratePix; // percents out of 100 of the bigger then 128 in each pixel
	public TrainingImageGroup(int label,int size) {
		this.label = label;
		this.size = size;
		this.imgGroup = new TrainingImage[size];
		this.ratePix = new double[28][28];   
	}
}