package miniProject;


public class BinaryTree implements java.io.Serializable {

	public Node root;
	public TrainingImage[] avarageImages;
	private static final long serialVersionUID = 1L;
	public BinaryTree() {
		this.root = new Node();
		this.avarageImages = null;
	}

	// All nodes are visited in ascending order
	// Recursion is used to go to one node and
	// then go to its child nodes and so forth
	public int size() {
		return(size(root)); 
	}
	public Node findNode(int label) {
		// Start at the top of the tree
		Node focusNode = root;
		// While we haven't found the Node
		// keep looking
		while (focusNode.label != label) {
			// If we should search to the left
			if (label < focusNode.label) {
				// Shift the focus Node to the left child
				focusNode = focusNode.leftChild;
			} else {
				// Shift the focus Node to the right child
				focusNode = focusNode.rightChild;
			}
			// The node wasn't found
			if (focusNode == null)
				return null;
		}
		return focusNode;
	}
	private int size(Node node) { 
		if (node == null) 
			return(0); 
		else { 
			return(size(node.leftChild) + 1 + size(node.rightChild)); 
		}
	}

	//change
	@Override
	public String toString(){
		return toString (root);
	}

	private String toString(Node root){
		String result = "";
		if (root == null)
			return "";
		result += toString(root.leftChild);
		result += toString(root.rightChild);
		result += root.toString();
		return result;
	}//end change
}

class Node implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	public int label;
	public String name;
	public int amountOfImgs;
	public int[] eachLabelAmount;
	public Node leftChild;
	public Node rightChild;
	public int choosenLabelLeft;
	public int choosenLabelRight;
	public double HL; //antropy of the node
	public double HX; //antropy of both sons
	public double IG; //improvement
	public int amountOfImgsLeft;
	public int amountOfImgsRight;
	public int[] eachLabelAmountLeft;
	public int[] eachLabelAmountRight;
	public double HLa;//antropy of the left son
	public double HLb;//antropy of the right son
	public TrainingImage[] nodeImgs;
	public TrainingImage[] leftNodeImgs;
	public TrainingImage[] rightNodeImgs;
	public double[] accScore;
	public double[] accScoreLeft;
	public double[] accScoreRight;
	public int depth;
	public int questionNumber;
	public int pixi;
	public int pixj;

	public Node() {
		this.label = -1;
		this.name = null;
		this.leftChild = null;//>128
		this.rightChild = null;//<=128
		this.amountOfImgs = 0;
		this.eachLabelAmount = null;
		this.choosenLabelLeft = -1;
		this.choosenLabelRight = -1;
		this.HL=0;//antropy of the node
		this.HX=0;//antropy of both sons
		this.IG=0;//improvement
		this.amountOfImgsLeft=0;
		this.amountOfImgsRight=0;
		this.eachLabelAmountLeft = new int[10];
		this.eachLabelAmountRight = new int[10];
		this.HLa = 0;//antropy of the left son
		this.HLb=0;//antropy of the right son
		this.nodeImgs = null;
		this.leftNodeImgs = null;
		this.rightNodeImgs = null;
		this.accScore = new double[10];
		this.accScoreLeft = new double[10];
		this.accScoreRight = new double[10];
		this.depth = 1;
		this.questionNumber = 0;
		this.pixi=0;
		this.pixj=0;
	}
	
	@SuppressWarnings("unchecked")
	public Node copy(){
		Node newNode = new Node();
		newNode.label = this.label;
		newNode.name = this.name;
		newNode.amountOfImgs = this.amountOfImgs;
		newNode.eachLabelAmount = this.eachLabelAmount ;
		newNode.choosenLabelLeft = this.choosenLabelLeft;
		newNode.choosenLabelRight = this.choosenLabelRight ;
		newNode.HL = this.HL;//antropy of the node
		newNode.HX = this.HX;//antropy of both sons
		newNode.IG = this.IG;//improvement
		newNode.amountOfImgsLeft = this.amountOfImgsLeft;
		newNode.amountOfImgsRight = this.amountOfImgsRight;
		newNode.eachLabelAmountLeft = this.eachLabelAmountLeft;
		newNode.eachLabelAmountRight = this.eachLabelAmountRight;
		newNode.HLa = this.HLa;//antropy of the left son
		newNode.HLb = this.HLb;//antropy of the right son
		newNode.nodeImgs = this.nodeImgs ;
		newNode.leftNodeImgs = this.leftNodeImgs ;
		newNode.rightNodeImgs = this.rightNodeImgs ;
		newNode.accScore = this.accScore;
		newNode.accScoreLeft = this.accScoreLeft ;
		newNode.accScoreRight = this.accScoreRight;
		newNode.depth = this.depth;
		newNode.questionNumber = this.questionNumber;
		newNode.pixi=this.pixi;
		newNode.pixj = this.pixj;

		if(this.leftChild != null){
			newNode.leftChild = this.leftChild.copy();
		}
		if(this.rightChild != null){
			newNode.rightChild = this.rightChild.copy();
		}

		return newNode;
	}

	@Override
	public String toString() {
		return name + " has the label " + label + " ";
	}
}