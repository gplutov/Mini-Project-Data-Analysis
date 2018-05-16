package miniProject;

public class LeafList {
	public Node[] list;
	public int size;

	public LeafList(int ListSize){
		this.list = new Node[ListSize];
		this.size=1;		
	}
}