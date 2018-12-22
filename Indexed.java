import java.util.*;
import java.util.Random;

class Block{
	int id;
	private boolean allocated = false;
	boolean isIndex;
	ArrayList<Integer> list = new ArrayList<Integer>();

	Block(int id){
		this.id = id;
		this.isIndex = false;
	}

	public boolean getAllocated(){return this.allocated;}
	public int getId(){return this.id;}
	public boolean isIndex(){return this.isIndex;}
	public ArrayList<Integer> getBlocks(){return this.list;}
	public String printIndexBlock(){
		String temp = "";
		for (int i=0; i<list.size(); i++) {
			temp += list.get(i) + " ";
		}
		return temp;
	}

	public void addBlock(int block_no){
		list.add(block_no);
	}

	public void deleteBlocks(){
		list.clear();
	}

	public void setAllocated(boolean allocated){
		this.allocated = allocated;
	}

	public void setIsIndex(boolean isIndex){
		this.isIndex = isIndex;
	}	
}

class File{
	String fileName;
	int indexBlockId;
	int length;
	File(String fileName, int indexBlockId, int length){
		this.fileName = fileName;
		this.indexBlockId = indexBlockId;
		this.length = length;
	}

	public String getFileName(){return this.fileName;}
	public int getIndexBlockId(){return this.indexBlockId;}
	public int getLength(){return this.length;}
}

class Indexed{
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
		for (int i=0; i<file_blocks+1; i++){
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
		int indexBlockId = block_list.get(0);
		block_list.remove(0);
		//First make FAT record
		fat.add(new File(fileName, indexBlockId, file_blocks));

		disk.get(indexBlockId).setIsIndex(true);
		for (int i=0; i<block_list.size(); i++) {
			disk.get(indexBlockId).addBlock(block_list.get(i));		
		}

	}

	public static void delete(String fileName){
		//find the record from FAT
		int index = 0, length = 0;
		for (int i=0; i<fat.size(); i++) {
			if (fat.get(i).getFileName().equals(fileName)) {
				index = fat.get(i).getIndexBlockId();
				length = fat.get(i).getLength();
				fat.remove(i);
			}
		}
		disk.get(index).setAllocated(false);
		disk.get(index).setIsIndex(false);
		ArrayList<Integer> list = disk.get(index).getBlocks();
		for (int i=0; i<list.size(); i++) {
			disk.get(list.get(i)).setAllocated(false);
		}
		disk.get(index).deleteBlocks();
	}

	public static void printDisk(){
		for (int i=0; i<no_of_block; i++) {
			System.out.print(disk.get(i).getId() + " " + disk.get(i).getAllocated() +"\t\t");
			if ((i+1)%5 == 0) {
				System.out.println();
			}
		}
		for (int i=0; i<no_of_block; i++) {
			if (disk.get(i).isIndex()) {
				System.out.println(disk.get(i).getId() +" -> " + disk.get(i).printIndexBlock());
			}
		}
	}

	public static void printFAT(){
		System.out.println("======================FAT======================");
		for (int i=0; i<fat.size(); i++) {
			System.out.println(fat.get(i).getFileName() + " " + fat.get(i).getIndexBlockId());
		}
	}
}