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

public class SSTF{
	static ArrayList<Node> tape = new ArrayList<Node>();
	static int count;
	static int currentPointer;
	public static void main(String[] args) {
		int totalCovered = 0;
		Scanner scn = new Scanner(System.in);
		System.out.println("Enter no of node in the sequence");
		count = scn.nextInt();
		System.out.println("Enter the sequence");
		for(int i=0;i<count;i++){
			System.out.println("Enter the disk no");
			int distance = scn.nextInt();
			tape.add(new Node(distance));
		}

		System.out.println("The sequence given  is :");
		for(int i=0;i<count;i++){
			Node temp = tape.get(i);
			System.out.print(temp.getDistance() + " -> ");
		}

		System.out.println();
		System.out.println("Enter the current head position :");
		currentPointer = scn.nextInt();
	
		for(int i=0;i<count;i++){
			int next = calculateMinDistance(currentPointer);
			int distance = currentPointer - tape.get(next).getDistance();
			distance = Math.abs(distance);
			totalCovered += distance;
			currentPointer = tape.get(next).getDistance();
			System.out.println("current position is at " + currentPointer);
			tape.get(next).setExecuted(true);
		}

		System.out.println("Total Covered = " + totalCovered);
		System.out.println("Average ="+(totalCovered/count));
	}

	public static int calculateMinDistance(int currentPointer){
		int index = 0 , min = 999999;
		for(int i=0;i<count;i++){
			Node temp = tape.get(i);
			if(temp.getExecuted() == false){
				int distance = currentPointer - temp.getDistance();
				distance = Math.abs(distance);
				if(distance < min){
					min = distance;
					index = i;
				}
			}
		}
		return index;		
	}
}