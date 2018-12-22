import java.util.*;
import java.io.*;
import java.nio.file.*;
class Semaphore{
	public boolean flag = true;
	public void semWait(){
		if (this.flag == true) {
			this.flag = false;			
		}
	}

	public void semSignal(){
		if (this.flag == false) {
			this.flag = true;
		}
	}
}
class FileBuffer{
	BufferedWriter writer;
	String filename;
	FileBuffer(String filename){
		try{
			this.filename = filename;
			writer = new BufferedWriter(new FileWriter(new File(filename)));			
		}catch(Exception e){
			System.out.println("Error in constructor file buffer " + e);
		}
	}
	public void read(){
		try{
			String currentLine;
			System.out.println("Read started");
			String data = new String(Files.readAllBytes(Paths.get(filename)));
			Thread.sleep(1000);
			System.out.println("- "+ data +" - Read from file");
		}catch(Exception e){
			System.out.println("Error in File buffer read " + e);
		}
	}

	public void write(String s){
		try{
			System.out.println("Write started");			
			writer.write(s);
			Thread.sleep(1000);
			System.out.println("Write successful");			
			writer.flush();
		}catch(Exception e){
			System.out.println("Error while writing to file" + e);
		}
	}
}
class Reader extends Thread{
	Semaphore wrt;
	Semaphore mutex;
	FileBuffer buf;
	//int readCount;
	//int no_of_readers;
	
	Reader(Semaphore mutex, Semaphore wrt, FileBuffer b){
		this.buf = b;
		this.mutex = mutex;
		this.wrt = wrt;
		//this.readCount=readCount;
		//this.no_of_readers = no_of_readers;
	}
	public void run(){
		try{
			while(true){
				System.out.println("Reader request to read"+Thread.currentThread().getId());
				if (mutex.flag) {
					
						mutex.semWait();
						ReadWrite.readCount++;
						//System.out.println("Performing read operation by reader "+Thread.currentThread().getId());
						 
						//System.out.println(ReadWrite.readCount+"readers reading");
						//System.out.println(readCount+"readers reading");
						if (ReadWrite.readCount == 1) {
							wrt.semWait();
						}
						mutex.semSignal();
						System.out.println("Performing read operation by reader "+Thread.currentThread().getId());
						buf.read();
						mutex.semWait();
						
						System.out.println("Done with read operation by reader " +Thread.currentThread().getId());
						ReadWrite.readCount--;
						
						if (ReadWrite.readCount == 0) {
							wrt.semSignal();
						}
						//System.out.println("Done with read operation by reader " +Thread.currentThread().getId());
						mutex.semSignal();	

					int sleep = (int) (Math.random() * 10000);
					Thread.sleep(sleep);
				}
				else{
					System.out.println("Reader"+Thread.currentThread().getId()+"must WAIT!!");
				}
				
			}
		}catch(Exception e){
			System.out.println("Error in Reader");
		}
	}
}
class Writer extends Thread{
	Semaphore wrt;
	Semaphore mutex;
	FileBuffer buf;
	Writer(Semaphore wrt, Semaphore mutex, FileBuffer buf){
		this.wrt = wrt;
		this.mutex=mutex;
		this.buf=buf;
	}
	public void run(){
		try{
			while(true){
				System.out.println("Writer request to write.");
				if (wrt.flag) {
						wrt.semWait();
						mutex.semWait();
						String text = "Random";
						System.out.println("Performing write operation");
						buf.write(text);
						System.out.println("Done with write operation");
						mutex.semSignal();
						wrt.semSignal();
						
					
				int sleep = (int) (Math.random() * 10000);
				Thread.sleep(sleep);
			}
			else{
						System.out.println("Writer must WAIT!!");
					}
			}
		}catch(Exception e){
			System.out.println("Error in Writer");
		}    
	}
}
class ReadWrite{
	 public static int readCount = 0;
	public static void main(String[] args) throws InterruptedException{
		Semaphore wrt = new Semaphore();
		Semaphore mutex = new Semaphore();
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the file name with extension");
		String filename = sc.nextLine();
		FileBuffer buffer= new FileBuffer(filename);
		//int no_of_readers = ((int)(Math.random()*5));
		int i;
		
		Writer t2 = new Writer(wrt,mutex,buffer);
		System.out.println("enter the number of readers you want");
		int rand = sc.nextInt();
		System.out.println(""+rand);
		for(i=0;i<rand;i++){
			Reader t1 = new Reader(mutex,wrt,buffer);
			t1.start();
			
		}
		t2.start();
	}
}