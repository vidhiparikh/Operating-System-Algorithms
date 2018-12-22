import java.util.*;

class Process{
	int pid;
	double burst;
	double arr_time;
	double wait_time;
	double tat;
	boolean entered_ready;

	Process(int p, double b, double a){
		this.pid = p;
		this.burst = b;
		this.arr_time = a;
		this.wait_time = 0;
	}

	public void setWaiting(double waiting){
		this.wait_time = waiting;
	}
	public void setTAT(double tat){
		this.tat = tat;
	}
	public void setEnteredReady(boolean ready){
		this.entered_ready = ready;
	}

	public int getPid(){return this.pid;}
	public double getBurst(){return this.burst;}	
	public double getArrivalTime(){return this.arr_time;}	
	public double getWaitingTime(){return this.wait_time;}
	public double getTAT(){return this.tat;}
	public boolean getEnteredReady(){return this.entered_ready;}
}
class SJF{
	public static ArrayList<Process> ready = new ArrayList<Process>();
	public static void main(String[] args) {
		int min_at = 999999;
		ArrayList<Process> al = new ArrayList<Process>();
		Scanner sc = new Scanner(System.in);
		int no,tot_time=0;
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
		System.out.println();
		System.out.println("Gantt Chart");		
		boolean scheduled = false;
		boolean scheduled_burst_one = false;
		int scheduled_ft = 0;
		for (int i=min_at; i<(tot_time+min_at); i++) {
			for (int pc=0; pc<no; pc++) {
				if (al.get(pc).getArrivalTime() <= i && !(ready.contains(al.get(pc))) && al.get(pc).getEnteredReady() == false) {
					al.get(pc).setEnteredReady(true);
					addToReady(al.get(pc));
				}
			}
			//Printing Ready Queue at each time
			// System.out.print("Time " + i + " Ready ");
			// for (int z=0; z<ready.size();z++ ) {
			// 	System.out.print(ready.get(z).getPid() + "   ");
			// }
			// System.out.println();

			if (!scheduled) {
				// System.out.println("Scheduling");
				if (!(ready.isEmpty())){
					sortBurst(ready, ready.size());
					Process p = ready.get(0);
					ready.remove(0);
					scheduled = true;
					if (p.getBurst() == 1) {
						scheduled_ft = (int)(i+p.getBurst());
						System.out.println(i+"    Process "+p.getPid() + "    " + (int)(i+p.getBurst()));
						p.setTAT((double)(scheduled_ft-p.getArrivalTime()));
						p.setWaiting((double)p.getTAT() - p.getBurst());
						scheduled_burst_one = true;
					}else{
						scheduled_ft = (int)(i+p.getBurst()-1);
						System.out.println(i+"    Process "+p.getPid() + "    " + (int)(i+p.getBurst()));
						p.setTAT((double)(scheduled_ft-p.getArrivalTime()+1));
						p.setWaiting((double)p.getTAT() - p.getBurst());	
						scheduled_burst_one = false;
					}
				}
			}
			if (scheduled_burst_one == true && (i == scheduled_ft-1)) {
				// System.out.println("Inside scheduled false");
				scheduled = false;
			}else if (scheduled_burst_one == false && i == scheduled_ft) {
				// System.out.println("Inside scheduled false");
				scheduled = false;				
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

    public static void sort(ArrayList<Process> alp, double length){
        for(int i = 0 ; i<length; i++){
            for(int j=0; j<length; j++){
                if(alp.get(i).getArrivalTime() < alp.get(j).getArrivalTime()){
                    Collections.swap(alp, i, j);
                }
            }
        }
    }	

    public static void sortBurst(ArrayList<Process> alp, double length){
        for(int i = 0 ; i<length; i++){
            for(int j=0; j<length; j++){
                if(alp.get(i).getBurst() < alp.get(j).getBurst()){
                    Collections.swap(alp, i, j);
                }
            }
        }
    }	
	
	public static void addToReady(Process p){
		ready.add(p);
	}
}