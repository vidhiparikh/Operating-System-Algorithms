import java.util.*;
class Node{
	int distance;
	boolean executed = false;

	Node(int distance){
		this.distance = distance;
	}

	int getDistance(){
		return this.distance;
	}

	void setExecuted(boolean executed){
		this.executed = executed;
	}

	boolean getExecuted(){
		return this.executed;
	}
}

public class SCAN{
	static ArrayList<Node> tape = new ArrayList<Node>();
	static int count;
	static int currentPointer;
	public static void main(String[] args) {
		int totalCovered = 0;
		int limit;
		int prevPointer;
		int flag;
		Scanner scn = new Scanner(System.in);
		System.out.println("Enter no of node in the sequence");
		count = scn.nextInt();
		System.out.println("Enter the cylinder limit");
		limit = scn.nextInt();
		System.out.println("Enter the sequence");
		for(int i=0;i<count;i++){
			System.out.println("Enter the disk no");
			int distance = scn.nextInt();
			if(distance > limit-1 || distance < 0){
				System.out.println("Not accepting");
				count--;
			}else{
				tape.add(new Node(distance));
			}
		}

		System.out.println("The sequence given  is :");
		for(int i=0;i<count;i++){
			Node temp = tape.get(i);
			System.out.print(temp.getDistance() + " -> ");
		}

		System.out.println();
		do{
			System.out.println("Enter the current head position :");
			currentPointer = scn.nextInt();
		}while(currentPointer > limit-1 || currentPointer < 0);

		do{
			System.out.println("Enter the prev head position :");
			prevPointer = scn.nextInt();
		}while(prevPointer > limit-1 || prevPointer < 0);

		if(currentPointer > prevPointer){
			System.out.println("moving left to right");
			flag = 1;
		}else{
			System.out.println("moving right to left");
			flag = 0;
		}
		sort();
		int index = findIndex(currentPointer);
		if(flag == 0){
			List<Node> sublist = tape.subList(0,index+1);		
			for(int i=sublist.size()-1;i>=0;i--){
				int len = sublist.get(i).getDistance();
				System.out.println("ACCESS " + len);
				int distance = currentPointer - len;
				distance = Math.abs(distance);
				totalCovered += distance;
				currentPointer = len;
			}
			System.out.println("ACCESS 0");
			int distance = currentPointer - 0 ;
			distance = Math.abs(distance);
			totalCovered += distance;
			currentPointer = 0;
			sublist = tape.subList(index+1,count);
			for(int i=0;i<sublist.size();i++){
				int len = sublist.get(i).getDistance();
				System.out.println("ACCESS " + len);
				int tempdistance = currentPointer - len;
				tempdistance = Math.abs(tempdistance);
				totalCovered += tempdistance;
				currentPointer = len;
			}
		}else if(flag == 1){
			List<Node> sublist = tape.subList(index+1,count);
			for(int i=0;i<sublist.size();i++){
				int len = sublist.get(i).getDistance();
				System.out.println("ACCESS " + len);
				int distance = currentPointer - len;
				distance = Math.abs(distance);
				totalCovered += distance;
				currentPointer = len;
			}
			System.out.println("ACCESS " + (limit-1));
			int distance = currentPointer - limit - 1 ;
			distance = Math.abs(distance);
			totalCovered += distance;
			currentPointer = limit-1;
			sublist = tape.subList(0,index+1);		
			for(int i=sublist.size()-1;i>=0;i--){
				int len = sublist.get(i).getDistance();
				System.out.println("ACCESS " + len);
				int tempdistance = currentPointer - len;
				tempdistance = Math.abs(tempdistance);
				totalCovered += tempdistance;
				currentPointer = len;
			}
		}

		System.out.println("Total Covered = " + totalCovered);
		System.out.println("Average ="+(totalCovered/count));
		
	}

	static void sort(){
		for(int i=0;i<count;i++){
			for(int j=0;j<count;j++){
				if(tape.get(i).getDistance() < tape.get(j).getDistance() ){
					Collections.swap(tape,i,j);
				}
			}
		}
	}

	static int findIndex(int currentPointer){
		int index =0;
		for(int i=0;i<count;i++){
			if(tape.get(i).getDistance() > currentPointer){
				index = i-1;
				break;
			}				
		}
		return index;
	}

}