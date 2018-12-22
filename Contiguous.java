import java.util.*;
class Block{
	int id;
	private boolean allocated = false;

	Block(int id){
		this.id = id;
	}

	public boolean getAllocated(){return this.allocated;}
	public int getId(){return this.id;}

	public void setAllocated(boolean allocated){
		this.allocated = allocated;
	}
}

class File{
	String fileName;
	int startBlockId;
	int length;
	File(String fileName, int startBlockId, int length){
		this.fileName = fileName;
		this.startBlockId = startBlockId;
		this.length = length;
	}

	public String getFileName(){return this.fileName;}
	public int getStartBlockId(){return this.startBlockId;}
	public int getLength(){return this.length;}
}

class Contiguous{
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
			disk.add(new Block(i+1));
		}
	}

	public static void create(String fileName, int length){
		int file_blocks = 0;
		if (length > (disk_space * 10)) {
			System.out.println("Cannot allocate since file size is greater than disk space");
			return;
		}else{
			file_blocks = ((length * 1024)/block_size);
		}
		//Find continuous blocks  
		int startBlockPointer = 0;
		int endBlockPointer = 0;
		int foundLen = 1;
		boolean flag = false;
		for (int i = 0; i<no_of_block; i++) {
			boolean found = false;
			//Find the first empty
			if (!disk.get(i).getAllocated()) {
				startBlockPointer = i;
				endBlockPointer = i;
	
				//check the rest until the next allocated or until the file_blocks
				while(!found && endBlockPointer < no_of_block){
					if (disk.get(endBlockPointer).getAllocated()) {
						// System.out.println(endBlockPointer + " adjust");
						startBlockPointer = 0;
						i = endBlockPointer + 1;
						endBlockPointer = 0;
						foundLen = 0; 
						found = true;
					}else if (foundLen == file_blocks) {
						//Found reqd size adjust the start and end allocate later
						found = true;
						flag = true;
					}else{
						endBlockPointer++;
						foundLen++;					
					}
				}
			}
			if (flag) {
				break;
			}
		}
		// System.out.println("Start " + startBlockPointer + " End " + endBlockPointer);
		//Allocate file
		//First make FAT record
		fat.add(new File(fileName, startBlockPointer, file_blocks));

		for (int i = startBlockPointer; i<=endBlockPointer; i++) {
			disk.get(i).setAllocated(true);
			// System.out.println(disk.get(i).getId() + " allocated");
		}
	}

	public static void delete(String fileName){
		//find the record from FAT
		int start = 0, length = 0;
		for (int i=0; i<fat.size(); i++) {
			if (fat.get(i).getFileName().equals(fileName)) {
				start = fat.get(i).getStartBlockId();
				length = fat.get(i).getLength();
				fat.remove(i);
			}
		}
		for (int i=start; i < (start+length); i++) {
			disk.get(i).setAllocated(false);
		}
	}

	public static void printDisk(){
		for (int i=0; i<no_of_block; i++) {
			System.out.print(disk.get(i).getId() + " " + disk.get(i).getAllocated() + "\t");
			if ((i+1)%5 == 0) {
				System.out.println();
			}
		}
	}

	public static void printFAT(){
		System.out.println("======================FAT======================");
		for (int i=0; i<fat.size(); i++) {
			System.out.println(fat.get(i).getFileName() + " " + fat.get(i).getStartBlockId() + " " + fat.get(i).getLength());
		}
	}
}