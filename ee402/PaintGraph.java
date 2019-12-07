package ee402;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("serial")
public class PaintGraph extends JPanel{
	
		private static ArrayList<Queue<Integer>> GraphTemps = new ArrayList<Queue<Integer>>();
		private static ArrayList<int[]> prevTemps = new ArrayList<int[]>();
		private static boolean[] graphcontrols = {true, true, true, true, true};

	
	    public PaintGraph(){
	    	setBackground(Color.WHITE);

	    }   
	    
	    public static void setGraph(int graphlabel) {
	    	graphcontrols[graphlabel] = !graphcontrols[graphlabel];
	    }
	    
	    
	    

    	protected void paintComponent(Graphics g) {
    		super.paintComponent(g);
    		int currentTemp;
    		int[] currentAry = new int[20];
    		GraphTemps = MyServer.getAllTemps();
    		
    		for(int ii=20; ii<66; ii=ii+5) {
    			g.drawString("" + ii, 0, ii*10); //primitive Y axis
    		}

			int prevMax = 0; //max value
			int prevMin = 100000;//min value
			int finalavg = 0;//average
			int totalentries = 0;//entries to average across
			Queue<Integer> avg = new LinkedList<Integer>();	//Queue used to average
    		
    		
    		for(int j=0; j<prevTemps.size(); j++) { //for all clients with temps submitted
    			
    			
    			 if(graphcontrols[j]==true) g.setColor(ServerGui.returnColor(j));//if boolean true shows in color
    			 else g.setColor(Color.WHITE);//if false is same as background
    				
    			 
    			for(int jj=1; jj<prevTemps.get(j).length; jj++) {
    				g.drawLine((jj-1)*52, prevTemps.get(j)[jj-1], (jj)*52, prevTemps.get(j)[jj]);
    				if(prevTemps.get(j)[jj] > prevMax) prevMax = prevTemps.get(j)[jj];
    				if(prevTemps.get(j)[jj] < prevMin && prevTemps.get(j)[jj]>0) prevMin = prevTemps.get(j)[jj];
    				if(prevTemps.get(j)[jj] > 0) avg.add(prevTemps.get(j)[jj]);
    			}

    		}
    		
    		if (!avg.isEmpty()) totalentries = avg.size();
    		
    		while(!avg.isEmpty()) {
    			finalavg = finalavg + avg.remove();
    		}
    		if (totalentries!=0) finalavg = finalavg/totalentries;
    		
			g.setColor(Color.BLACK);
			g.drawLine(0, prevMax, 1024, prevMax);
			g.drawString("Max", 1000, prevMax);
			g.drawLine(0, prevMin, 1024, prevMin);
			g.drawString("Min", 975, prevMin);
			g.drawLine(0, finalavg, 1024, finalavg);
			g.drawString("Avg", 950, finalavg);
    		

    		for(int i=0; i<GraphTemps.size(); i++) {  //for each graph array	
    			if(prevTemps.size()<GraphTemps.size()) {//runs after new client returns data
    				prevTemps.add(currentAry);//adds irrelevent or empty array to initialize
    			}
    			
    			
    			currentAry = prevTemps.get(i);
    			while(GraphTemps.get(i).size()>0)	//while that array has entries
    			{
    				currentTemp = GraphTemps.get(i).remove()/100; //get the entry
        			if(prevTemps.get(i).length>=20) { //if the array is full
        				for(int k=0; k<19; k++) {
        					currentAry[k] = currentAry[k+1];//moves all values up the array	
        				}
        				currentAry[19] = currentTemp;//adds final value
        			}
        			else currentAry[currentAry.length] = currentTemp;//adds value
        			prevTemps.set(i, currentAry);
    			}
    		}
    		

		}
	}