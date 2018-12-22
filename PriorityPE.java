import java.util.*;

class Process{
	int pid;
	double burst;
	double arr_time;
	double wait_time;
	double tat;
	double remaining_burst;
	int priority;
	boolean entered_ready;

	Process(){}

	Process(int p, double b, double a, int priority){
		this.pid = p;
		this.burst = b;
		this.arr_time = a;
		this.remaining_burst = b;
		this.priority = priority;		
	}

	public void setWaiting(double waiting){
		this.wait_time = waiting;
	}
	public void setTAT(double tat){
		this.tat = tat;
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
	public double getRemainingBurst(){return this.remaining_burst;}	
	public boolean getEnteredReady(){return this.entered_ready;}
	public int getPriority(){return this.priority;}

}
class PriorityPE{
	static ArrayList<Process> ready = new ArrayList<Process>();
	public static void main(String[] args) {
		int min_at = 999999;
		ArrayList<Process> al = new ArrayList<Process>();
		Scanner sc = new Scanner(System.in);
		int no,tot_time=0;
		System.out.println("Enter the no of processes");
		no = sc.nextInt();
		for (int i=0; i<no; i++) {
			System.out.println("Enter the information for process " + (i+1));
			System.out.println("Enter the Burst Time, Arrival Time and Priority");
			double burst = sc.nextDouble();
			double arr_time = sc.nextDouble();
			int priority = sc.nextInt();			
			if (burst > -1 && arr_time > -1 && priority > -1) {
				if (arr_time < min_at) {
					min_at = Math.min(min_at, (int)arr_time);
				}
				tot_time += burst;
				al.add(new Process((i+1),burst,arr_time,priority));				
			}else{
				System.out.println("Please enter value greater than 0");
			}
		}
		System.out.println("Process Info is:");
		for (int i=0; i<no; i++) {
			System.out.print("P"+(i+1) + "\t" +al.get(i).getPid()+ "\t" + al.get(i).getBurst()+ "\t" + al.get(i).getArrivalTime());
			System.out.println();
		}
		boolean scheduled = false;
		boolean after_burst_over = false;
		int scheduled_ft = 0;
		int last_start = min_at;
		Process last_scheduled = new Process();
		System.out.println("Gantt Chart");
		for (int i=min_at; i<(tot_time+min_at+1); i++) {
			for (int pc=0; pc<no; pc++) {
				if (al.get(pc).getArrivalTime() <= i && !(ready.contains(al.get(pc))) && al.get(pc).getEnteredReady() == false) {
					al.get(pc).setEnteredReady(true);
					addToReady(al.get(pc));
				}
			}
			// //Print the ready queue 
			// System.out.print("Time " + i + " Ready ");
			// for (int z=0; z<ready.size();z++ ) {
			// 	System.out.print(ready.get(z).getPid() + "   ");
			// }
			// System.out.println();
			if (!ready.isEmpty()) {
				sortPriority(ready,ready.size());
				if (!scheduled) {
					//Initial Allocation
					last_scheduled = ready.get(0);
					ready.remove(0);
					scheduled = true;
				}else if (scheduled && last_scheduled.getPriority() > ready.get(0).getPriority()) {
					System.out.println(last_start+"    Process "+last_scheduled.getPid() + "    " + i);
					// System.out.println("Prempting " + last_scheduled.getPid() + " taking " + ready.get(0).getPid());
					last_scheduled.setRemainingBurst(last_scheduled.getRemainingBurst() - 1);
					addToReady(last_scheduled);
					last_scheduled = ready.get(0);
					ready.remove(0);
					last_start = i;
				}else if (last_scheduled.getRemainingBurst() == 1) {
					scheduled_ft = i;												
					System.out.println(last_start+"    Process "+last_scheduled.getPid() + "    " + scheduled_ft);											
					last_scheduled.setTAT((double)(scheduled_ft-last_scheduled.getArrivalTime()));
					last_scheduled.setWaiting((double)last_scheduled.getTAT() - last_scheduled.getBurst());						
					last_scheduled.setRemainingBurst(0);
					last_start = i;
					after_burst_over = true;
				}else if (after_burst_over) {
					last_scheduled = ready.get(0);
					ready.remove(0);
					if (last_scheduled.getRemainingBurst() == 1) {
						scheduled_ft = i;												
						System.out.println(last_start+"    Process "+last_scheduled.getPid() + "    " + scheduled_ft);											
						last_scheduled.setTAT((double)(scheduled_ft-last_scheduled.getArrivalTime()));
						last_scheduled.setWaiting((double)last_scheduled.getTAT() - last_scheduled.getBurst());						
						last_scheduled.setRemainingBurst(0);
						last_start = i;
						last_scheduled = ready.get(0);
						ready.remove(0);						
					}else{
						last_scheduled.setRemainingBurst(last_scheduled.getRemainingBurst() - 1);						
					}
					scheduled = true;
					after_burst_over = false;	
				}else{
					last_scheduled.setRemainingBurst(last_scheduled.getRemainingBurst() - 1);					
				}
			}else{
				//For the last process since ready is empty
				if (last_scheduled.getRemainingBurst() == 1) {
					System.out.println(last_start+"    Process "+last_scheduled.getPid() + "    " + i);
					scheduled_ft = i;
					last_scheduled.setTAT((double)(scheduled_ft-last_scheduled.getArrivalTime()));
					last_scheduled.setWaiting((double)last_scheduled.getTAT() - last_scheduled.getBurst());						
					last_scheduled.setRemainingBurst(0);					
				}else{
					last_scheduled.setRemainingBurst(last_scheduled.getRemainingBurst() - 1);										
				}
			}
			// System.out.println("Last scheduled " + last_scheduled.getPid() + " rem burst" + last_scheduled.getRemainingBurst());
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

    public static void sortPriority(ArrayList<Process> alp, double length){
    	//Sort as Lowest number Highest Priority
        for(int i = 0 ; i<length; i++){
            for(int j=0; j<length; j++){
                if(alp.get(i).getPriority() < alp.get(j).getPriority()){
                    Collections.swap(alp, i, j);
                }
            }
        }
    }	

}