import java.util.*;

class Process{
	int pid;
	double burst;
	double arr_time;
	double wait_time;
	double tat;
	double start_time;
	double remaining_burst;
	boolean entered_ready;

	Process(int p, double b, double a){
		this.pid = p;
		this.burst = b;
		this.arr_time = a;
		this.remaining_burst = b;
	}

	public void setWaiting(double waiting){
		this.wait_time = waiting;
	}
	public void setTAT(double tat){
		this.tat = tat;
	}
	public void setStart(double start){
		this.start_time = start;
	}
	public void setRemainingBurst(double time){
		this.remaining_burst = time;
	}
	public void setEnteredReady(boolean ready){
		this.entered_ready = ready;
	}

	public int getPid(){return this.pid;}
	public double getBurst(){return this.burst;}	
	public double getArrivalTime(){return this.arr_time;}	
	public double getWaitingTime(){return this.wait_time;}
	public double getTAT(){return this.tat;}
	public double getStartTime(){return this.start_time;}
	public double getRemainingBurst(){return this.remaining_burst;}	
	public boolean getEnteredReady(){return this.entered_ready;}

}
class RR{
	static ArrayList<Process> ready = new ArrayList<Process>();
	public static void main(String[] args) {
		int min_at = 999999;
		ArrayList<Process> al = new ArrayList<Process>();
		Scanner sc = new Scanner(System.in);
		int no,tot_time=0,quantum;
		System.out.println("Enter the no of processes");
		no = sc.nextInt();
		for (int i=0; i<no; i++) {
			System.out.println("Enter the information for process " + (i+1));
			System.out.println("Enter the Process Id, Burst Time and Arrival Time");
			int pid = sc.nextInt();
			double burst = sc.nextDouble();
			double arr_time = sc.nextDouble();
			if (pid > -1 && burst > -1 && arr_time > -1) {
				if (arr_time < min_at) {
					min_at = Math.min(min_at, (int)arr_time);
				}
				tot_time += burst;
				al.add(new Process(pid,burst,arr_time));				
			}else{
				System.out.println("Please enter value greater than 0");
			}
		}
		System.out.println("Process Info is:");
		for (int i=0; i<no; i++) {
			System.out.print("P"+(i+1) + "\t" +al.get(i).getPid()+ "\t" + al.get(i).getBurst()+ "\t" + al.get(i).getArrivalTime());
			System.out.println();
		}
		System.out.println("Enter the Quantum");
		quantum = sc.nextInt();
		boolean scheduled = false;
		int scheduled_ft = 0;
		int context_switch = 0;
		for (int i=min_at; i<(tot_time+min_at); i+=quantum) {
			for (int l =i ;l<=(i+quantum);l++ ) {
				for (int pc=0; pc<no; pc++) {
					if (al.get(pc).getArrivalTime() <= l && !(ready.contains(al.get(pc))) && al.get(pc).getEnteredReady() == false) {
						al.get(pc).setEnteredReady(true);
						addToReady(al.get(pc));
					}
				}
			}
			// //Print the ready queue 
			// System.out.print("Time " + i + " Ready ");
			// for (int z=0; z<ready.size();z++ ) {
			// 	System.out.print(ready.get(z).getPid() + "   ");
			// }
			// System.out.println();			
			if (!ready.isEmpty()){
				if (!scheduled) {
					Process p = ready.get(0);					
					ready.remove(0);
					scheduled = true;
					// System.out.println(p.getPid() + " Found");
					if(p.getRemainingBurst() > 0 && p.getRemainingBurst() > quantum){
						p.setRemainingBurst(p.getRemainingBurst() - quantum);
						addToReady(p);
						System.out.println(i+"    Process "+p.getPid() + "    " + (i+quantum));						
					}else if (p.getRemainingBurst() > 0 && p.getRemainingBurst() < quantum) {
						//Last cycle for process
						scheduled_ft = i+(int)p.getRemainingBurst();
						System.out.println(i+"    Process "+p.getPid() + "    " + scheduled_ft);						
						p.setTAT((double)(scheduled_ft-p.getArrivalTime()));
						p.setWaiting((double)p.getTAT() - p.getBurst());						
						p.setRemainingBurst(0);
						i = scheduled_ft-quantum;
					}else{
						scheduled_ft = i+quantum;
						System.out.println(i+"    Process "+p.getPid() + "    " + scheduled_ft);						
						p.setTAT((double)(scheduled_ft-p.getArrivalTime()));
						p.setWaiting((double)p.getTAT() - p.getBurst());						
						p.setRemainingBurst(0);
					}
					scheduled = false;
				}
			}
		}
		double avg_wt=0,avg_tat=0;
		System.out.println("\nProcess Info after waiting and TAT is:");
		for (int i=0; i<no; i++) {
			avg_wt += al.get(i).getWaitingTime();
			avg_tat += al.get(i).getTAT();
			System.out.print("P"+ al.get(i).getPid() + "\t" + (int)al.get(i).getWaitingTime()+ "\t" + (int)al.get(i).getTAT());
			System.out.println();
		}
		System.out.println("Average waiting time = "+(avg_wt/no) + " and Average TAT " + (avg_tat/no));		
	}
	
	public static void addToReady(Process p){
		ready.add(p);
	}
}