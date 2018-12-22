import java.util.*;

class Process{
	int pid;
	double burst;
	double arr_time;
	double wait_time;
	double tat;
	double remaining_burst;
	boolean entered_ready;

	Process(){}

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
}
class SRT{
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
			System.out.println("Enter the Burst Time and Arrival Time");
			double burst = sc.nextDouble();
			double arr_time = sc.nextDouble();
			if (burst > -1 && arr_time > -1) {
				if (arr_time < min_at) {
					min_at = Math.min(min_at, (int)arr_time);
				}
				tot_time += burst;
				al.add(new Process((i+1),burst,arr_time));				
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
		int scheduled_ft = 0;
		Process last_scheduled = new Process();
		int last_start = min_at;
		System.out.println("Gantt Chart");
		for (int i=min_at; i<(tot_time+min_at); i++) {
			for (int pc=0; pc<no; pc++) {
				if (al.get(pc).getArrivalTime() <= i && !(ready.contains(al.get(pc))) && al.get(pc).getEnteredReady() == false) {
					al.get(pc).setEnteredReady(true);
					addToReady(al.get(pc));
				}
			}
			if (!ready.isEmpty()) {
				sortRemainingBurst(ready,ready.size());
				if (last_scheduled.getPid() == 0) {
					last_scheduled = ready.get(0);
					ready.remove(0);
				}else if (last_scheduled.getRemainingBurst() > ready.get(0).getRemainingBurst()) {
					//Pre empt
					last_scheduled.setRemainingBurst(last_scheduled.getRemainingBurst() - 1);					
					System.out.println(last_start + "  Prempting  Process" + last_scheduled.getPid() + "   " +i);
					addToReady(last_scheduled);
					last_scheduled = ready.get(0);
					ready.remove(0);
					last_start = i;
				}else if (last_scheduled.getRemainingBurst() == 1) {
					scheduled_ft = i;
					System.out.println(last_start + "    Process" + last_scheduled.getPid() + "   " +scheduled_ft);
					last_scheduled.setTAT((double)(scheduled_ft-last_scheduled.getArrivalTime()));
					last_scheduled.setWaiting((double)last_scheduled.getTAT() - last_scheduled.getBurst());
					last_scheduled.setRemainingBurst(0);
					last_start = scheduled_ft;
					last_scheduled = ready.get(0);
					ready.remove(0);
				}else{
					last_scheduled.setRemainingBurst(last_scheduled.getRemainingBurst() - 1);				
				}
			}else{
				//ready empty
					scheduled_ft = last_start + (int)last_scheduled.getRemainingBurst()-1;
					i = scheduled_ft;
					System.out.println(last_start + "    Process" + last_scheduled.getPid() + "   " +scheduled_ft);					
					last_scheduled.setTAT((double)(scheduled_ft-last_scheduled.getArrivalTime()));
					last_scheduled.setWaiting((double)last_scheduled.getTAT() - last_scheduled.getBurst());
					last_scheduled.setRemainingBurst(0);				
			}
			// System.out.println("Time " + i+"Process " +last_scheduled.getPid() + " remaining burst " + last_scheduled.getRemainingBurst());
			// // //Print the ready queue 
			// System.out.print("Time " + i + " Ready ");
			// for (int z=0; z<ready.size();z++ ) {
			// 	System.out.print(ready.get(z).getPid() + "   ");
			// }
			// System.out.println();			
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

    public static void sortRemainingBurst(ArrayList<Process> alp, double length){
    	//Sort as Lowest number Highest Priority
        for(int i = 0 ; i<length; i++){
            for(int j=0; j<length; j++){
                if(alp.get(i).getRemainingBurst() < alp.get(j).getRemainingBurst()){
                    Collections.swap(alp, i, j);
                }
            }
        }
    }
}