package ee402;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Scanner;

public class ClientInfo implements Serializable {
	
	

	private int temp;
	private String name;
	private Date readingdate;
	private int sample;
	
	
	public ClientInfo() { //used to set up an initial oldclientinfo
		sample = -1;
	}
	
	public ClientInfo(ClientInfo oldClientinfo) {
		sample = oldClientinfo.getSample() + 1;//increments samplenum
		name = MyClient.getName(); //sets name of client in object
		readingdate = new Date(); //gets date and time
		temp = GetTemp(); //gets cpu temp
	}
	
	 private Integer GetTemp() {
	    	try {
	    		Scanner sysfile = new Scanner(new File("/sys/class/thermal/thermal_zone0/temp")); //sets up scanner on temp file
	    		String temperature = sysfile.nextLine();	//reads first line (temperature)
	    		System.out.println(temperature); 
	    		sysfile.close();//closes scanner
	    		return Integer.valueOf(temperature); //returns temperature as type int
	    	}
	    	catch (Exception e) {
	    		System.out.println("XX. Exception Occurred on retrieving temp:" +  e.toString());
	    		return 0;
	    	}
	    	   	
	    }

	 
	private int getSample() {
		return sample;
	}
		 
	public Integer returnTemp() {
		return temp;
	}

	public String returnName() {
		return name;
	}

	public String returnTime() { 
		return readingdate.toString();
	}
	

}
