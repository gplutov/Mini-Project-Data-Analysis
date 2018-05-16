package miniProject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


//import java.util.ArrayList;
//import java.awt.image.BufferedImage;
//import javax.imageio.ImageIO;

public class learntree {

	public static void main(String[] args) {	
		

				
		
		int version = -1;//version of algorithem
		int p = -1;// the percent of the training set that should be used for validation 
		int l = -1;// the maximal power of 2 for tree sizes T
		String trainingSetFN = "";// the name/path of the training set file
		String outputTreeFN = "";//the name/path of the file into which the algorithm will output the decision tree	
		
		/*checks validity of arguments*/
		if(args.length != 5)
		{
			System.err.println("invalid arguments");
			System.exit(1);
		}
		version = Integer.parseInt(args[0]);
		p = Integer.parseInt(args[1]); 
		l = Integer.parseInt(args[2]);
		trainingSetFN = args[3];
		outputTreeFN = args[4];
		if(!checkArgs(version,p,l,trainingSetFN,outputTreeFN))
		{
			System.err.println("invalid arguments");
			System.exit(1);
		}

		int[] hashMap = new int[10]; //array that counts the quantity of shows of each label between 0-9.
		int[] hashMapForValid = new int[10];
		int[][] imgsPixels = null;
		int[] imgsLabels = null;
		int[][] imgsPixelsForValid = null;
		int[] imgsLabelsForValid = null;
		int numberOfImagesForValid = 0;
		int numberOfLabelsForValid = 0;

		String [][] bank = null;
		
		String line1 = "";
		String cvsSplitBy = ",";
		int numOfImgsC=0;

		FileReader fr=null;
		FileReader fri=null;
		try {
			fr = new FileReader(trainingSetFN);
		} catch (FileNotFoundException e2) {
			System.err.println("unable to open training set");
			e2.printStackTrace();
			System.exit(1);
		}

		BufferedReader br=null;
		try { 
			br = new BufferedReader(fr);
			while (br.readLine() != null) {
				numOfImgsC++;
			}

		} catch (IOException e) {
			System.err.println("unable to read from training set");
			e.printStackTrace();
			System.exit(1);
		}

		bank = new String[numOfImgsC][785];

		System.out.println("num: " + numOfImgsC);
		
		try {
			fri = new FileReader( trainingSetFN);
		} catch (FileNotFoundException e2) {
			System.err.println("unable to open training set");
			e2.printStackTrace();
			System.exit(1);
			
		}

		BufferedReader bri = null;
		try{  
			bri= new BufferedReader(fri) ;
			int i=0;
			while ( ((line1 = bri.readLine()) != null) ) {
				bank[i++]=line1.split(cvsSplitBy);
			}

		} catch (IOException e) {
			System.err.println("unable to read from training set");
			e.printStackTrace();
			System.exit(1);
		}
		
		//randomized part
		List<String[]> bankList = Arrays.asList(bank);
		Collections.shuffle(bankList);
		bank = bankList.toArray(new String[bankList.size()][745]);
		
		
		
		numberOfImagesForValid = (int)(((double)p/100)*numOfImgsC);
		numberOfLabelsForValid = (int)(((double)p/100)*numOfImgsC);

		int total =numOfImgsC;
		numOfImgsC -= numberOfImagesForValid;

		imgsLabels=new int[numOfImgsC];//example 54000
		imgsPixels = new int[numOfImgsC][784];//example 54000x784

		imgsPixelsForValid = new int[numberOfImagesForValid][784];//6000x784
		imgsLabelsForValid = new int[numberOfLabelsForValid];//6000
		
		for (int i=0; i<numOfImgsC;i++){
			
			imgsLabels[i]= Integer.parseInt(bank[i][0]);
			hashMap[imgsLabels[i]]++;
			for(int y=0;y<784;y++)
			{
				imgsPixels[i][y]=Integer.parseInt(bank[i][y+1]);
				
			}	 
		}
		for (int i=0; i<numberOfImagesForValid;i++){
			
			imgsLabelsForValid[i]= Integer.parseInt(bank[i+numOfImgsC][0]);
			hashMapForValid[imgsLabelsForValid[i]]++;
			for(int y=0;y<784;y++){
				imgsPixelsForValid[i][y]=Integer.parseInt(bank[i+numOfImgsC][y+1]);
			}	 
		}
		//change end     

		try {
			br.close();
			bri.close();
			fr.close();
			fri.close();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		
		
		//create objects
		TrainingImageGroup[] imgGroups = new TrainingImageGroup[10];
		int[] indexCounter = new int[10];
		for(int i=0;i<10;i++)
		{
			imgGroups[i] = new TrainingImageGroup(i,hashMap[i]);
		}
		for(int i=0;i<numOfImgsC;i++)
		{
			int[][] imgPixels = new int[28][28];
			for(int j=0;j<28;j++)
			{
				for(int k=0;k<28;k++)
				{
					imgPixels[j][k] = imgsPixels[i][28*j+k];
				}
			}
			int curLabel = imgsLabels[i];
			imgGroups[curLabel].imgGroup[indexCounter[curLabel]++] = new TrainingImage(curLabel,imgPixels);
		}

		
		//create objects for Validation
		TrainingImageGroup[] imgGroupsForValid = new TrainingImageGroup[10];
		int[] indexCounterForValid = new int[10];

		for(int i=0;i<10;i++)
		{
			imgGroupsForValid[i] = new TrainingImageGroup(i,hashMapForValid[i]);
		}
		for(int i=0;i<numberOfImagesForValid;i++)
		{
			int[][] imgPixels = new int[28][28];
			for(int j=0;j<28;j++)
			{
				for(int k=0;k<28;k++)
				{
					imgPixels[j][k] = imgsPixelsForValid[i][28*j+k];
				}
			}
			int curLabel = imgsLabelsForValid[i];
			imgGroupsForValid[curLabel].imgGroup[indexCounterForValid[curLabel]++] = new TrainingImage(curLabel,imgPixels);

		}
	
		int mostShowLabel = indxOfMaxValue(hashMap); //first label on the tree
		BinaryTree myTree = new BinaryTree();
		//initializing tree.
		myTree.root.label = mostShowLabel;
		myTree.root.name = "answer";
		myTree.root.questionNumber=0;
		myTree.root.amountOfImgs = numOfImgsC ;
		myTree.root.eachLabelAmount = hashMap;
		myTree.root.HL = getNodeAntropty(myTree.root.amountOfImgs,myTree.root.eachLabelAmount);
		myTree.root.nodeImgs = getAllImgs(imgGroups,numOfImgsC);
		
		for(int i=0;i<10;i++)
		{
			myTree.root.accScore[i] = ((double)hashMap[i]/(double)numOfImgsC)*100;
		}
		
		BinaryTree[] trees = new BinaryTree[l+1];//all the solutions tree before validation
		int nextTreesIndex = 0;//next index of tree array
		int numOfSteps = (int) Math.pow(2,l);//total number of steps
		int stepNumber=0;//current step
		int nextX=0,nextY=0;
		int nextPower = 1;
		
		//initialize avarage images
		TrainingImage[] avarageImages= initializeAvarageImages(imgGroups);//Image that represent the avarage pixel;
		myTree.avarageImages = avarageImages;
		
		if(version==1) //version number 1
		{

			
			initAntropyOfSons(null,myTree.root,version);//init the improvement
			LeafList leafList = new LeafList(numOfSteps+1);
			leafList.list[0] = myTree.root;
			//compute the first step and initializing the tree
			computeFirstStepAndInitializeNode(myTree.root,imgGroups);
			//end of initialization
			//start of construction of trees
			while (stepNumber < numOfSteps)
			{
				//check which leaf have the best improvment.
				int bestImprovmentIndexNode = 0;
				double bestImprovment = leafList.list[0].IG * leafList.list[0].amountOfImgs;
				for(int i = 1;i < leafList.size;i++)
				{
					if((leafList.list[i].IG * leafList.list[i].amountOfImgs) > bestImprovment)
					{
						bestImprovment = leafList.list[i].IG * leafList.list[i].amountOfImgs;
						bestImprovmentIndexNode = i;
					}	
				}
				makeFirstQuestion(bestImprovmentIndexNode,myTree,myTree.root,imgGroups,stepNumber,numOfSteps,leafList,null,trees,nextX,nextY,null,null,nextPower,nextTreesIndex);

				stepNumber++;

				if(stepNumber == nextPower)
				{
					//insertToTheTrees
					BinaryTree ansTree = new BinaryTree();
					ansTree.avarageImages = myTree.avarageImages;
					deepCopyTree(ansTree,myTree);
					trees[nextTreesIndex] = ansTree;
					nextTreesIndex++;

					nextPower = nextPower*2;
				}
			}

		}

		else//version number 2 
		{
			
			LeafList leafList = new LeafList(numOfSteps+1);
			leafList.list[0] = myTree.root;
			myTree.root.questionNumber = 1;
			initAntropyOfSons( avarageImages , myTree.root ,2 );//init the improvement
			//compute the first step and initializing the tree    	
			computeFirstStepAndInitializeNode(myTree.root,imgGroups);
			//end of initialization
			while (stepNumber < numOfSteps)
			{
				
				int bestImprovmentIndexNode = 0;
				double bestImprovment = leafList.list[0].IG * leafList.list[0].amountOfImgs;
				for(int i = 1;i < leafList.size;i++)
				{
					if((leafList.list[i].IG * leafList.list[i].amountOfImgs) > bestImprovment)
					{
						bestImprovment = leafList.list[i].IG * leafList.list[i].amountOfImgs;
						bestImprovmentIndexNode = i;
					}	
				}
				if(leafList.list[bestImprovmentIndexNode].questionNumber==0)
				{
					makeFirstQuestion(bestImprovmentIndexNode,myTree,myTree.root,imgGroups,stepNumber,numOfSteps,leafList,null,trees,nextX,nextY,null,null,nextPower,nextTreesIndex);
				}
				else if(leafList.list[bestImprovmentIndexNode].questionNumber>=1 && leafList.list[bestImprovmentIndexNode].questionNumber<=100)	
				{
					makeSecondQuestion(bestImprovmentIndexNode,avarageImages,myTree,myTree.root,imgGroups,stepNumber,numOfSteps,leafList,null,trees,nextX,nextY,null,null,nextPower,nextTreesIndex);
				}
				stepNumber++;

				if(stepNumber == nextPower)
				{
					//insertToTheTrees
					BinaryTree ansTree = new BinaryTree();
					ansTree.avarageImages = myTree.avarageImages;
					deepCopyTree(ansTree,myTree);
					trees[nextTreesIndex] = ansTree;
					nextTreesIndex++;

					nextPower = nextPower*2;
				}
			}


		}  

		//validation
		double minError = 100;
		int indexOfBestTree = 0 ;
		for(int i = 0; i < trees.length ; i++)
		{
			double curError = 0;
			double numberOfError = 0;
			for(int j =0 ; j < imgGroupsForValid.length; j++)
			{
				for(int k = 0; k < imgGroupsForValid[j].size ; k++)
				{
					Node curNode = trees[i].root;
					while(curNode.name == "question")
					{
						if(curNode.questionNumber == 0)
						{
							
							if(imgGroupsForValid[j].imgGroup[k].imgPixels[curNode.pixi][curNode.pixj] > 128)
							{
								curNode = curNode.leftChild;
							}
							else
							{
								curNode = curNode.rightChild;
							}
						}
						if(curNode.questionNumber >= 1 && curNode.questionNumber <=100)
						{
							if(isMinDiffFromNum(avarageImages,curNode.questionNumber-1,imgGroupsForValid[j].imgGroup[k]))
							{
								curNode = curNode.leftChild;
							}
							else
							{
								curNode = curNode.rightChild;
							}
						}

					}
					if(curNode.label != imgGroupsForValid[j].imgGroup[k].label)
					{
						numberOfError++;
					}
				}
			}
			curError = (numberOfError/numberOfImagesForValid)*100;

			if(curError < minError)
			{
				indexOfBestTree = i;
				minError = curError;
			}
		}
		
		//checking total error
		double totalError = 0;
		double numberOfError = 0;
		double numberOfImgsWvalid= 0;
		for(int j =0 ; j < imgGroups.length; j++)
		{
			numberOfImgsWvalid=numberOfImgsWvalid+imgGroups[j].size;
			for(int k = 0; k < imgGroups[j].size ; k++)
			{
				Node curNode = trees[indexOfBestTree].root;
				while(curNode.name == "question")
				{
					if(curNode.questionNumber == 0)
					{
						
						if(imgGroups[j].imgGroup[k].imgPixels[curNode.pixi][curNode.pixj] > 128)
						{
							curNode = curNode.leftChild;
						}
						else
						{
							curNode = curNode.rightChild;
						}
					}
					if(curNode.questionNumber >= 1 && curNode.questionNumber <=100)
					{
						if(isMinDiffFromNum(avarageImages,curNode.questionNumber-1,imgGroups[j].imgGroup[k]))
						{
							curNode = curNode.leftChild;
						}
						else
						{
							curNode = curNode.rightChild;
						}
					}

				}
				if(curNode.label != imgGroups[j].imgGroup[k].label)
				{
					numberOfError++;
				}
			}
		}

		double numberOfErrorInValid = (minError/100)*numberOfImagesForValid;
		totalError = ((numberOfError + numberOfErrorInValid)/(numberOfImagesForValid+numberOfImgsWvalid))*100;


		System.out.println("error: " + Math.round(totalError));
		System.out.println("size: " + trees[indexOfBestTree].size());


		
		FileOutputStream f =null;
		ObjectOutputStream o =null;

		try {

			f = new FileOutputStream(new File(outputTreeFN));

			o = new ObjectOutputStream(f);

			// Write objects to file
			if(f!=null)
				o.writeObject(trees[indexOfBestTree]);
			
			o.close();
			f.close();

			FileInputStream fi = new FileInputStream(new File(outputTreeFN));
			ObjectInputStream oi = new ObjectInputStream(fi);

			BinaryTree pr1 = null;
			try {
				pr1 = (BinaryTree ) oi.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			oi.close();
			fi.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		}
	}


	@SuppressWarnings("unchecked")
	private static void makeSecondQuestion(int bestImprovmentIndexNode,TrainingImage[] avarageImages,BinaryTree myTree,Node node, TrainingImageGroup[] imgGroups, int stepNumber,
			int numOfSteps, LeafList leafList, int[] sizeOfAraayIndexes, BinaryTree[] trees, int nextX, int nextY,
			int[][] indexesByRateX, int[][] indexesByRateY, int nextPower, int nextTreesIndex)
	{

		//start of construction of trees
		//divide the chosen leaf.
		leafList.list[bestImprovmentIndexNode].name = "question";
		leafList.list[bestImprovmentIndexNode].label = -1; 
		//initializing left node
		Node leftNode = new Node();
		leafList.list[bestImprovmentIndexNode].leftChild = leftNode;
		leftNode.label = leafList.list[bestImprovmentIndexNode].choosenLabelLeft; 
		leftNode.name = "answer";

		leftNode.amountOfImgs = leafList.list[bestImprovmentIndexNode].amountOfImgsLeft ; 
		leftNode.eachLabelAmount = leafList.list[bestImprovmentIndexNode].eachLabelAmountLeft;
		leftNode.depth = leafList.list[bestImprovmentIndexNode].depth + 1;

		leftNode.HL = leafList.list[bestImprovmentIndexNode].HLa;
		leftNode.nodeImgs = leafList.list[bestImprovmentIndexNode].leftNodeImgs;
		leftNode.accScore = leafList.list[bestImprovmentIndexNode].accScoreLeft;

		initAntropyOfSons(avarageImages,leftNode,2);//init the improvement
		computeFirstStepAndInitializeNode(leftNode,imgGroups);
		//initializing Right node
		Node rightNode = new Node();
		leafList.list[bestImprovmentIndexNode].rightChild = rightNode;
		rightNode.label = leafList.list[bestImprovmentIndexNode].choosenLabelRight; 
		rightNode.name = "answer"; 
		rightNode.amountOfImgs = leafList.list[bestImprovmentIndexNode].amountOfImgsRight ; 
		rightNode.eachLabelAmount = leafList.list[bestImprovmentIndexNode].eachLabelAmountRight;
		rightNode.HL = leafList.list[bestImprovmentIndexNode].HLb;
		rightNode.nodeImgs = leafList.list[bestImprovmentIndexNode].rightNodeImgs;
		rightNode.accScore = leafList.list[bestImprovmentIndexNode].accScoreRight;    
		initAntropyOfSons(avarageImages,rightNode,2);//init the improvement
		computeFirstStepAndInitializeNode(rightNode,imgGroups);

		//update leafList
		leafList.list[bestImprovmentIndexNode] = leftNode;
		leafList.list[leafList.size] = rightNode;
		leafList.size++;

	}

	private static void makeFirstQuestion(int bestImprovmentIndexNode,BinaryTree myTree,Node root, TrainingImageGroup[] imgGroups, int stepNumber,
			int numOfSteps, LeafList leafList, int[] sizeOfAraayIndexes, BinaryTree[] trees,int nextX,int nextY,int[][] indexesByRateX,int[][] indexesByRateY,int nextPower,int nextTreesIndex)
	{

		//divide the chosen leaf.
		leafList.list[bestImprovmentIndexNode].name = "question";
		leafList.list[bestImprovmentIndexNode].label = -1; 
		//initializing left node
		Node leftNode = new Node();
		leafList.list[bestImprovmentIndexNode].leftChild = leftNode;
		leftNode.label = leafList.list[bestImprovmentIndexNode].choosenLabelLeft; 
		leftNode.name = "answer"; 
		leftNode.questionNumber=0;
		leftNode.amountOfImgs = leafList.list[bestImprovmentIndexNode].amountOfImgsLeft ; 
		leftNode.eachLabelAmount = leafList.list[bestImprovmentIndexNode].eachLabelAmountLeft;
		leftNode.depth = leafList.list[bestImprovmentIndexNode].depth + 1;
		

		leftNode.HL = leafList.list[bestImprovmentIndexNode].HLa;
		leftNode.nodeImgs = leafList.list[bestImprovmentIndexNode].leftNodeImgs;
		leftNode.accScore = leafList.list[bestImprovmentIndexNode].accScoreLeft;

		initAntropyOfSons(null,leftNode,1);//init the improvement
		computeFirstStepAndInitializeNode(leftNode,imgGroups);
		//initializing Right node
		Node rightNode = new Node();
		leafList.list[bestImprovmentIndexNode].rightChild = rightNode;
		rightNode.label = leafList.list[bestImprovmentIndexNode].choosenLabelRight; 
		rightNode.name = "answer"; 
		rightNode.questionNumber=0;
		rightNode.amountOfImgs = leafList.list[bestImprovmentIndexNode].amountOfImgsRight ; 
		rightNode.eachLabelAmount = leafList.list[bestImprovmentIndexNode].eachLabelAmountRight;
		
		rightNode.HL = leafList.list[bestImprovmentIndexNode].HLb;
		rightNode.nodeImgs = leafList.list[bestImprovmentIndexNode].rightNodeImgs;
		rightNode.accScore = leafList.list[bestImprovmentIndexNode].accScoreRight;    
		
		initAntropyOfSons(null,rightNode,1);//init the improvement
		computeFirstStepAndInitializeNode(rightNode,imgGroups);

		//update leafList
		leafList.list[bestImprovmentIndexNode] = leftNode;
		leafList.list[leafList.size] = rightNode;
		leafList.size++;


	}

	
	private static TrainingImage[] initializeAvarageImages(TrainingImageGroup[] imgGroups) {
		TrainingImage[] avarageImages = new TrainingImage[10];
		for(int i=0;i<10;i++)
		{
			avarageImages[i] = new TrainingImage(i,new int[28][28]);
			for(int k=0;k<imgGroups[i].size;k++)
			{
				for(int j=0;j<28;j++)
				{
					for(int t=0;t<28;t++)
					{
						avarageImages[i].imgPixels[j][t] = avarageImages[i].imgPixels[j][t] + imgGroups[i].imgGroup[k].imgPixels[j][t];
					}
				}
			}
			for(int j=0;j<28;j++)
			{
				for(int t=0;t<28;t++)
				{
					avarageImages[i].imgPixels[j][t] = avarageImages[i].imgPixels[j][t]/imgGroups[i].size;
				}
			}
		}

		return avarageImages;
	}
	
	private static double[] copyArray(double [] arr) {
		double[] ans = new double[arr.length];

		for(int j=0;j<arr.length;j++)
		{
			ans[j] = arr[j];
		}

		return ans;
	}

	
	private static void deepCopyTree(BinaryTree ansTree, BinaryTree myTree) {
		ansTree.root = myTree.root.copy();		
	}

	private static void computeFirstStepAndInitializeNode(Node node, TrainingImageGroup[] imgGroups) {

		int maxIndex = 0;
		int maxNumOfImg = 0;
		for(int i=0;i<10;i++)
		{
			if(node.eachLabelAmountLeft[i]>maxNumOfImg)
			{
				maxIndex = i;
				maxNumOfImg = node.eachLabelAmountLeft[i];
			}
			if((double)node.amountOfImgsLeft==0)
				node.accScoreLeft[i] = 0;
			else
				node.accScoreLeft[i] = ((double)node.eachLabelAmountLeft[i]/(double)node.amountOfImgsLeft)*100;
		}
		node.choosenLabelLeft = maxIndex;//initialize

		maxIndex = 0;
		maxNumOfImg = 0;
		for(int i=0;i<10;i++)
		{
			if(node.eachLabelAmountRight[i]>maxNumOfImg)
			{
				maxIndex = i;
				maxNumOfImg = node.eachLabelAmountRight[i];
			}
			if((double)node.amountOfImgsRight==0)
				node.accScoreRight[i] = 0;
			else
				node.accScoreRight[i] = ((double)node.eachLabelAmountRight[i]/(double)node.amountOfImgsRight)*100;
		}

		node.choosenLabelRight = maxIndex;//initialize

	}

	private static void initAntropyOfSons(TrainingImage[] avarageImages,Node node,int version){
		boolean updated = false;
		if(version == 1)
		{
			double bestIG=0;
			double bestHX =0;
			double bestHLa = 0;
			double bestHLb = 0;
			node.pixi=0;
			node.pixj=0;
			int bestamountOfImgsLeft=0;
			int[] besteachLabelAmountLeft=null;
			int bestamountOfImgsRight=0;
			int[] besteachLabelAmountRight=null;
			for(int x=0;x<28;x++)
			{
				for(int y = 0 ;y<28 ;y++)
				{
					for(int i = 0; i<node.amountOfImgs;i++){	
						if(node.nodeImgs[i].imgPixels[x][y] > 128){
							node.amountOfImgsLeft++;
							node.eachLabelAmountLeft[node.nodeImgs[i].label]++;
						}else{
							node.amountOfImgsRight++;
							node.eachLabelAmountRight[node.nodeImgs[i].label]++;
						}	
					}
					node.HLa = getNodeAntropty(node.amountOfImgsLeft,node.eachLabelAmountLeft);
					node.HLb = getNodeAntropty(node.amountOfImgsRight,node.eachLabelAmountRight);
					node.HX = getHX(node.amountOfImgsLeft,node.amountOfImgsRight,node.amountOfImgs,node.HLa,node.HLb);
					node.IG = node.HL - node.HX;
					if(node.amountOfImgs*node.IG > node.amountOfImgs *bestIG)
					{
						updated = true;
						bestIG =node.IG;
						node.pixi = x;
						node.pixj = y;
						bestHLa = node.HLa;
						bestHLb = node.HLb;
						bestHX = node.HX;
						bestamountOfImgsLeft = node.amountOfImgsLeft;
						besteachLabelAmountLeft = node.eachLabelAmountLeft;
						bestamountOfImgsRight = node.amountOfImgsRight;
						besteachLabelAmountRight = node.eachLabelAmountRight;
					}
					if(x==27 && y==27 && !updated)
					{
						bestIG =node.IG;
						node.pixi = x;
						node.pixj = y;
						bestHLa = node.HLa;
						bestHLb = node.HLb;
						bestHX = node.HX;
						bestamountOfImgsLeft = node.amountOfImgsLeft;
						besteachLabelAmountLeft = node.eachLabelAmountLeft;
						bestamountOfImgsRight = node.amountOfImgsRight;
						besteachLabelAmountRight = node.eachLabelAmountRight;
					}
					node.amountOfImgsLeft=0;
					node.eachLabelAmountLeft= new int[10];
				
					node.amountOfImgsRight=0;
					node.eachLabelAmountRight = new int[10];
				}
			} 
			node.HLa = bestHLa;
			node.HLb = bestHLb;
			node.HX = bestHX;
			node.IG = bestIG;
			node.amountOfImgsLeft = bestamountOfImgsLeft;
			node.amountOfImgsRight = bestamountOfImgsRight;
			node.eachLabelAmountLeft = besteachLabelAmountLeft;
			node.eachLabelAmountRight = besteachLabelAmountRight;
			
			node.leftNodeImgs = new TrainingImage[node.amountOfImgsLeft];
			node.rightNodeImgs = new TrainingImage[node.amountOfImgsRight];
			int counterLeft=0;int counterRight=0;
			for(int i = 0; i<node.amountOfImgs;i++)
			{	
				if(node.nodeImgs[i].imgPixels[node.pixi][node.pixj] > 128)
				{
					node.leftNodeImgs[counterLeft] = node.nodeImgs[i];
					counterLeft++;
				}
				else
				{
					node.rightNodeImgs[counterRight] = node.nodeImgs[i];
					counterRight++;
				}
			} 
		}
		if(version == 2)
		{
			double bestIG=0;
			double bestHX =0;
			double bestHLa = 0;
			double bestHLb = 0;
			node.pixi=0;
			node.pixj=0;
			int bestamountOfImgsLeft=0;
			int[] besteachLabelAmountLeft=null;
			int bestamountOfImgsRight=0;
			int[] besteachLabelAmountRight=null;
			for(int quesNum=0;quesNum<=100;quesNum++)
			{
				if(quesNum == 0)
				{
					for(int x=0;x<28;x++)
					{
						for(int y = 0 ;y<28 ;y++)
						{
							for(int i = 0; i<node.amountOfImgs;i++){	
								if(node.nodeImgs[i].imgPixels[x][y] > 128){
									node.amountOfImgsLeft++;
									node.eachLabelAmountLeft[node.nodeImgs[i].label]++;
								}else{
									node.amountOfImgsRight++;
									node.eachLabelAmountRight[node.nodeImgs[i].label]++;
								}	
							}
							node.HLa = getNodeAntropty(node.amountOfImgsLeft,node.eachLabelAmountLeft);
							node.HLb = getNodeAntropty(node.amountOfImgsRight,node.eachLabelAmountRight);
							node.HX = getHX(node.amountOfImgsLeft,node.amountOfImgsRight,node.amountOfImgs,node.HLa,node.HLb);
							node.IG = node.HL - node.HX;
							if(node.amountOfImgs*node.IG > node.amountOfImgs *bestIG)
							{
								updated = true;
								bestIG =node.IG;
								node.pixi = x;
								node.pixj = y;
								bestHLa = node.HLa;
								bestHLb = node.HLb;
								bestHX = node.HX;
								node.questionNumber=quesNum;
								bestamountOfImgsLeft = node.amountOfImgsLeft;
								besteachLabelAmountLeft = node.eachLabelAmountLeft;
								bestamountOfImgsRight = node.amountOfImgsRight;
								besteachLabelAmountRight = node.eachLabelAmountRight;
							}
							
							node.amountOfImgsLeft=0;
							node.eachLabelAmountLeft= new int[10];
						
							node.amountOfImgsRight=0;
							node.eachLabelAmountRight = new int[10];
						}
					}
				}
				else
				{
					for(int i = 0; i<node.amountOfImgs;i++)
					{	

						if(isMinDiffFromNum(avarageImages, quesNum-1, node.nodeImgs[i])){
							node.amountOfImgsLeft++;
							node.eachLabelAmountLeft[node.nodeImgs[i].label]++;
						}else{
							node.amountOfImgsRight++;
							node.eachLabelAmountRight[node.nodeImgs[i].label]++;
						}	
					}
					node.HLa = getNodeAntropty(node.amountOfImgsLeft,node.eachLabelAmountLeft);
					node.HLb = getNodeAntropty(node.amountOfImgsRight,node.eachLabelAmountRight);
					node.HX = getHX(node.amountOfImgsLeft,node.amountOfImgsRight,node.amountOfImgs,node.HLa,node.HLb);
					node.IG = node.HL - node.HX;
					if(node.amountOfImgs*node.IG > node.amountOfImgs *bestIG)
					{
						updated = true;
						bestIG =node.IG;
						bestHLa = node.HLa;
						bestHLb = node.HLb;
						bestHX = node.HX;
						node.questionNumber=quesNum;
						bestamountOfImgsLeft = node.amountOfImgsLeft;
						besteachLabelAmountLeft = node.eachLabelAmountLeft;
						bestamountOfImgsRight = node.amountOfImgsRight;
						besteachLabelAmountRight = node.eachLabelAmountRight;
					}
					if(quesNum==100 && !updated)
					{
						bestIG =node.IG;
						bestHLa = node.HLa;
						bestHLb = node.HLb;
						bestHX = node.HX;
						node.questionNumber=quesNum;
						bestamountOfImgsLeft = node.amountOfImgsLeft;
						besteachLabelAmountLeft = node.eachLabelAmountLeft;
						bestamountOfImgsRight = node.amountOfImgsRight;
						besteachLabelAmountRight = node.eachLabelAmountRight;
					}
					node.amountOfImgsLeft=0;
					node.eachLabelAmountLeft= new int[10];
				
					node.amountOfImgsRight=0;
					node.eachLabelAmountRight = new int[10];
				}				
			}
			
			node.HLa = bestHLa;
			node.HLb = bestHLb;
			node.HX = bestHX;
			node.IG = bestIG;
			node.amountOfImgsLeft = bestamountOfImgsLeft;
			node.amountOfImgsRight = bestamountOfImgsRight;
			node.eachLabelAmountLeft = besteachLabelAmountLeft;
			node.eachLabelAmountRight = besteachLabelAmountRight;
			
			if(node.questionNumber==0)
			{
				node.leftNodeImgs = new TrainingImage[node.amountOfImgsLeft];
				node.rightNodeImgs = new TrainingImage[node.amountOfImgsRight];
				int counterLeft=0;int counterRight=0;
				for(int i = 0; i<node.amountOfImgs;i++)
				{	
					if(node.nodeImgs[i].imgPixels[node.pixi][node.pixj] > 128)
					{
						node.leftNodeImgs[counterLeft] = node.nodeImgs[i];
						counterLeft++;
					}
					else
					{
						node.rightNodeImgs[counterRight] = node.nodeImgs[i];
						counterRight++;
					}
				} 
			}
			else
			{
				node.leftNodeImgs = new TrainingImage[node.amountOfImgsLeft];
				node.rightNodeImgs = new TrainingImage[node.amountOfImgsRight];
				int counterLeft=0;int counterRight=0;
				for(int i = 0; i<node.amountOfImgs;i++){	
					if(isMinDiffFromNum(avarageImages,node.questionNumber-1,node.nodeImgs[i])){
						node.leftNodeImgs[counterLeft] = node.nodeImgs[i];
						counterLeft++;
					}else{
						node.rightNodeImgs[counterRight] = node.nodeImgs[i];
						counterRight++;
					}
				}    	
			}
			
		}

	}

	private static boolean isMinDiffFromNum(TrainingImage[] avarageImages,int num, TrainingImage trainingImage){
		
		//version2
		double[] difs = new double[10];
		for(int i=0;i<10;i++){
			for(int j=0;j<28;j++){
				for(int k=0;k<28;k++){
					difs[i] = difs[i] + Math.pow(trainingImage.imgPixels[j][k]-avarageImages[i].imgPixels[j][k],2);
					//difs[i] = difs[i] + Math.abs(trainingImage.imgPixels[j][k]-avarageImages[i].imgPixels[j][k]); version 1
				}
			}
			difs[i] = Math.sqrt(difs[i]);
		}
		double[] sortedDifs= new double[10];
		sortedDifs = copyArray(difs);
		Arrays.sort(sortedDifs);
		int[] sortedLabels=new int[10];
		for(int i=0;i<10;i++){
			double dif = sortedDifs[i];
			for(int j=0;j<10;j++){
				if(dif == difs[j]){
					sortedLabels[i] = j;
					continue;
				}
			}
		}
		return sortedLabels[num/10]==num%10;
	}

	private static double getHX(int amountOfImgsLeft, int amountOfImgsRight, int amountOfImgs, double hLa, double hLb) {
		if(amountOfImgs==0){
			return 0;
		}
		return ((double)amountOfImgsLeft/(double)amountOfImgs)*(double)hLa + ((double)amountOfImgsRight/(double)amountOfImgs)*(double)hLb;
	}

	private static TrainingImage[] getAllImgs(TrainingImageGroup[] imgGroups,int numberOfImgs) {
		TrainingImage[] ans = new TrainingImage[numberOfImgs];
		int counter = 0;
		for(int i=0;i<10;i++){
			for(int j=0;j<imgGroups[i].size;j++){
				ans[counter] = imgGroups[i].imgGroup[j];
				counter++;
			}
		}
		return ans;
	}

	private static double getNodeAntropty(int amountOfImgs, int[] eachLabelAmount) {
		double ans = 0;
		for(int i =0;i<10 ; i++){
			if(eachLabelAmount[i]!=0){
				ans = ans + ((double)eachLabelAmount[i]/(double)amountOfImgs)*((double)Math.log(((double)amountOfImgs/(double)eachLabelAmount[i]))/(double)Math.log(2));
			}
		}
		return ans;
	}

	private static boolean checkArgs(int version,int p ,int l, String trainingSetFN,String outputTreeFN){
		return (version == 1 || version == 2) && (p >= 0 && p <= 100) && (l >= 0);
	}
	
	private static int indxOfMaxValue(int[] arr) {
		int max = arr[0];
		int indx = 0;
		for (int ktr = 0; ktr < arr.length; ktr++) {
			if (arr[ktr] > max) {
				max = arr[ktr];
				indx = ktr;
			}
		}
		return indx;
	}
}
