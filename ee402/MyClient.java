package ee402;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MyClient {
	
	private static int portNumber;
	private static String serverIP;
	private static int SampleTime;
	private static String name;
    private Socket socket = null;
    private ObjectOutputStream os = null;
    private ObjectInputStream is = null;

	// the constructor expects the IP address of the server - the port is fixed
    public MyClient(String IP, String portnum, String Name) {
    	File oldTrigger=new File("/sys/class/leds/led0/trigger"); //resets the led trigger to none
    	oldTrigger.delete();
		File LEDtrigger=new File("/sys/class/leds/led0/trigger");
		FileWriter triggerwriter;
		
		serverIP = IP; 
		portNumber = Integer.valueOf(portnum);
		name = Name;
		
		try {
			triggerwriter = new FileWriter(LEDtrigger,false);
		    triggerwriter.write("none");
		    triggerwriter.close();
		} catch (IOException e) {
			System.out.println("Trigger not set on LED");
			e.printStackTrace();
		}
    	if (!connectToServer(serverIP)) {
    		System.out.println("XX. Failed to open socket connection to: " + serverIP);            
    	}
    }

    private boolean connectToServer(String serverIP) {
    	try { // open a new socket to the server 
    		this.socket = new Socket(serverIP,portNumber); //connect to socket of server pc
    		this.os = new ObjectOutputStream(this.socket.getOutputStream());
    		this.is = new ObjectInputStream(this.socket.getInputStream());
    		
    		
    		System.out.println("00. -> Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort()); //outputs servers details
    		System.out.println("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort()); //outputs client socket details
    	} 
    	
        catch (Exception e) { // if it cannot connect
        	System.out.println("XX. Failed to Connect to the Server at port: " + portNumber);
        	System.out.println("    Exception: " + e.toString());	
        	return false;
        }
		return true;
    }

    
    
    private void SendTemp() {
    	
    	ClientInfo newClientinfo; 
    	ClientInfo oldClientinfo = new ClientInfo();
    	
    	while(!socket.isClosed()) {
    		newClientinfo = new ClientInfo(oldClientinfo);
    		
        	this.send(newClientinfo); //sends the temperature to the server
        	try{
        		SampleTime = (int) receive();//receives reply from the server including sample time
        		System.out.println("05. <- The Sample Time is: ");
        		System.out.println("    <- " + SampleTime);
        	}
        	catch (Exception e){ //if there is an error this code will close the socket
        		try {
    				socket.close();
    			} catch (IOException e1) {
    				System.out.println("socket could not close");
    				e1.printStackTrace();
    			}
        		System.out.println("XX. There was an invalid object sent back from the server");
        	}
        	
        	try {
				TimeUnit.MILLISECONDS.sleep((1000*SampleTime)-500); //sleeps for the sample time -.5 seconds from led flash
			} catch (InterruptedException e) {
				System.out.println("did not wait long enough");
				e.printStackTrace();
			}	
    	}
    	
    }
    		

    	
    // method to send a generic object.
    private void send(Object o) {
		try {
			File LEDbrightness=new File("/sys/class/leds/led0/brightness");//opens led file
			FileWriter Brightnesswriter = new FileWriter(LEDbrightness,false); //to write to file
		    Brightnesswriter.write("255");//sets led on
		    Brightnesswriter.close();//closes writer
		    System.out.println("02. -> Sending an object...");
		    
		    os.writeObject(o);//writes object so socket
		    os.flush();//flushes socket(sends object)
		    
		    System.out.println("Current temp is: " + ((ClientInfo) o).returnTemp()); //prints temp
		    System.out.println("at: " +  ((ClientInfo) o).returnTime());  //prints time
		    TimeUnit.MILLISECONDS.sleep(500); //so the led flash can be seen	    
		    
		    LEDbrightness.delete();//to replace file
		    File Offbrightness=new File("/sys/class/leds/led0/brightness");//replacement file
		    FileWriter Offbrightnesswriter = new FileWriter(Offbrightness,false);//writer to new file
		    Offbrightnesswriter.write("0");//sets led off
		    Offbrightnesswriter.close();//closes writer   
		} 
	    catch (Exception e) {
	    	try {
				socket.close();
			} catch (IOException e1) {
				System.out.println("socket could not close");
				e1.printStackTrace();
			}
		    System.out.println("XX. Exception Occurred on Sending:" +  e.toString());
		}
    }

    // method to receive a generic object.
    private Object receive() 
    {
		Object o = null;
		try {
			System.out.println("03. -- About to receive an object...");
		    o = is.readObject();
		    System.out.println("04. <- Object received...");
		} 
	    catch (Exception e) {
	    	try {
				socket.close();
			} catch (IOException e1) {
				System.out.println("socket could not close");
				e1.printStackTrace();
			}
		    System.out.println("XX. Exception Occurred on Receiving:" + e.toString());
		}
		return o;
    }
    
	public static String getName() {
		return name;
	}

    public static void main(String args[]) 
    {
    	System.out.println("**. My client to send Temp");
    	if(args.length==3){ // checks if an argument has been input
    		MyClient theApp = new MyClient(args[0], args[1], args[2]); //connects to server at input ip
    		theApp.SendTemp(); // sends temperature
		}
    	else
    	{
    		System.out.println("Error: you must provide the address of the server");
    		System.out.println("Usage is:  java Client x.x.x.x  (e.g. java Client 192.168.7.2)");
    		System.out.println("      or:  java Client hostname (e.g. java Client localhost)");
    	}    
    	System.out.println("**. End of Application.");
    }


}