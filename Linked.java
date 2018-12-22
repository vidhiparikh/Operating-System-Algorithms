import java.util.*;
import java.util.Random;
class Block{
	int id;
	private boolean allocated = false;
	int next;

	Block(int id){
		this.id = id;
		this.next = -2;
	}

	public boolean getAllocated(){return this.allocated;}
	public int getId(){return this.id;}
	public int getNext(){return this.next;}

	public void setAllocated(boolean allocated){
		this.allocated = allocated;
	}

	public void setNext(int next){
		this.next = next;
	}	
}

class File{
	String fileName;
	int startBlockId;
	int endBlockId;
	int length;
	File(String fileName, int startBlockId, int endBlockId, int length){
		this.fileName = fileName;
		this.startBlockId = startBlockId;
		this.endBlockId = endBlockId;
		this.length = length;
	}

	public String getFileName(){return this.fileName;}
	public int getStartBlockId(){return this.startBlockId;}
	public int getEndBlockId(){return this.endBlockId;}
	public int getLength(){return this.length;}
}

class Linked{
	static int disk_space = 4; 
	static int block_size = 512; //in bytes	
	static int no_of_block = 0;

	static ArrayList<File> fat = new ArrayList<File>();
	static ArrayList<Block> disk = new ArrayList<Block>();
	static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		int ch;
		calculateAndAllocate();
		while(true){
			System.out.println("Enter your choice");
			System.out.println("1. Create \t 2. Delete \t 3. Print \t 4.Exit");
			ch = sc.nextInt();
			switch (ch) {
				case 1:
				//Take details of file
				System.out.println("Enter the file name");
				String fileName = sc.next();
				System.out.println("Enter the file size in kilobytes");
				int length = sc.nextInt();				
				create(fileName, length);
				break;

				case 2:
				System.out.println("Enter the file name");
				String file_name = sc.next();				
				delete(file_name);
				break;

				case 3:
				printDisk();
				printFAT();
				break;			

				case 4:
				System.exit(0);
				break;

				default:
				System.out.println("Please make a valid choice");
				break;
			}			
		}
	}

	public static void calculateAndAllocate(){
		//in mb's is defined by no of 1024, initially 40kb
		no_of_block = (int)((disk_space * 1024 * 10)/block_size);
		// System.out.println(no_of_block + " no of blocks in disk"); 
		for (int i=0; i<no_of_block; i++) {
			disk.add(new Block(i));
		}
	}

	public static void create(String fileName, int length){
		int file_blocks = 0;
		Random rand = new Random();
		ArrayList<Integer> block_list = new ArrayList<Integer>();
		ArrayList<Integer> duplicate_list = new ArrayList<Integer>();

		if (length > (disk_space * 10)) {
			System.out.println("Cannot allocate since file size is greater than disk space");
			return;
		}else{
			file_blocks = ((length * 1024)/block_size);
		}
		
		//Find blocks
		int foundlen = 0;
		int rand_next = 0;
		boolean flag = false;
		int loop_count = 0;
		//find a random block list which is not allocated
		for (int i=0; i<file_blocks; i++){
			duplicate_list.clear();
			while(true){
				rand_next = rand.nextInt(no_of_block);
				//takes care no duplicate random number is generated
				if (!duplicate_list.contains(rand_next)) {
					duplicate_list.add(rand_next);

					//checks whether its allocated					
					if (!disk.get(rand_next).getAllocated()) {
					
						//If not allocated insert into our list						
						block_list.add(rand_next);
						disk.get(rand_next).setAllocated(true);
						break;
					}					
				}
				if (duplicate_list.size() >= no_of_block) {
					flag = true;
					break;
				}
			}
			if (flag) {
				System.out.println("No enough space to allocate");
				// System.out.println(block_list);
				for (int k=0; k<block_list.size(); k++) {
					disk.get(block_list.get(k)).setAllocated(false);
				}
				return;
			}
		}

		// System.out.println(block_list);
		//Allocate file
		//First make FAT record
		int startBlockPointer = block_list.get(0);
		int endBlockPointer = block_list.get(block_list.size()-1);
		fat.add(new File(fileName, startBlockPointer, endBlockPointer, file_blocks));

		for (int i = 0; i<block_list.size(); i++) {
			int block_no = block_list.get(i);
			if (i == block_list.size()-1) {
				disk.get(block_no).setNext(-1);
			}else{
				int next_block_no = block_list.get(i+1);
				disk.get(block_no).setNext(next_block_no);
			}
		}
	}

	public static void delete(String fileName){
		//find the record from FAT
		int start = 0, end = 0, length = 0;
		for (int i=0; i<fat.size(); i++) {
			if (fat.get(i).getFileName().equals(fileName)) {
				start = fat.get(i).getStartBlockId();
				end = fat.get(i).getEndBlockId();
				length = fat.get(i).getLength();
				fat.remove(i);
			}
		}
		for (int i=0; i < length; i++) {
			int next = disk.get(start).getNext();
			disk.get(start).setAllocated(false);
			disk.get(start).setNext(-2);
			start = next;
		}
	}

	public static void printDisk(){
		for (int i=0; i<no_of_block; i++) {
			System.out.print(disk.get(i).getId() + " " + disk.get(i).getAllocated() + " " + disk.get(i).getNext() +"\t\t");
			if ((i+1)%5 == 0) {
				System.out.println();
			}
		}
	}

	public static void printFAT(){
		System.out.println("======================FAT======================");
		for (int i=0; i<fat.size(); i++) {
			System.out.println(fat.get(i).getFileName() + " " + fat.get(i).getStartBlockId() + " " + fat.get(i).getEndBlockId());
		}
	}
}