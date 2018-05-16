package miniProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class predict {
	
	public static void main(String[] args) {
		
		String inputTreeFN = "";
		String testSetFN = "";
		BinaryTree tree =null;
		String [][] bank = null;
		String line1 = "";
		String cvsSplitBy = ",";
		int numOfImgsC=0;			
		int[][] imgsPixels = null;	//holding the pics
		int[] imgsLabels = null;
		
		FileReader fr=null;
		FileReader fri=null;
		
		inputTreeFN =args[0];
		testSetFN = args[1];
		
		FileInputStream fi =null;
		ObjectInputStream oi =null;
		
		//tree reading
		try {
			 fi = new FileInputStream(new File(inputTreeFN));
			 oi = new ObjectInputStream(fi);

			
			try {
				tree = (BinaryTree ) oi.readObject();
			} catch (ClassNotFoundException e) {
				
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
		
		//testset reading
		
		try {
			fr = new FileReader(testSetFN);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

		BufferedReader br=null;
		try { 
			br = new BufferedReader(fr);

			while (br.readLine() != null) {
				numOfImgsC++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		bank = new String[numOfImgsC][785];
		imgsLabels=new int[numOfImgsC];
		
		try {
			fri = new FileReader(testSetFN);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

		BufferedReader bri = null;
		try{  
			bri= new BufferedReader(fri) ;
			int i=0;
			while ( ((line1 = bri.readLine()) != null) ) {

				bank[i++]=line1.split(cvsSplitBy);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		imgsPixels = new int[numOfImgsC][784];
		
		for (int i=0; i<numOfImgsC;i++){
			imgsLabels[i]= Integer.parseInt(bank[i][0]);
			for(int y=0;y<784;y++){
				imgsPixels[i][y]=Integer.parseInt(bank[i][y+1]);
			}	 
		}
		
		TrainingImage[] avarageImages = tree.avarageImages;

		
		for(int k = 0; k < numOfImgsC ; k++)
		{
			Node curNode = tree.root;
			while(curNode.name.equals("question"))
			{
				if(curNode.questionNumber == 0)
				{

					if(imgsPixels[k][28*curNode.pixi+curNode.pixj] > 128)
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
					if(isMinDiffFromNum(avarageImages,curNode.questionNumber-1,imgsPixels,k))
					{
						curNode = curNode.leftChild;
					}
					else
					{
						curNode = curNode.rightChild;
					}
				}

			}
			System.out.println(curNode.label);
		}
		
		
		
		
	}
private static boolean isMinDiffFromNum(TrainingImage[] avarageImages,int num, int[][]imgsPixels,int index){
		
		//version2
		double[] difs = new double[10];
		for(int i=0;i<10;i++){
			for(int j=0;j<28;j++){
				for(int k=0;k<28;k++){
					difs[i] = difs[i] + Math.pow(imgsPixels[index][28*j+k]-avarageImages[i].imgPixels[j][k],2);
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
private static double[] copyArray(double [] arr) {
	double[] ans = new double[arr.length];

	for(int j=0;j<arr.length;j++)
	{
		ans[j] = arr[j];
	}

	return ans;
}


}