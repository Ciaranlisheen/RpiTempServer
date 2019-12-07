package ee402;

import java.net.*;
import java.util.*;
import java.io.*;

public class MyServer extends Thread 
{
	private static int portNumber = 5050;
	private static int SampleTime = 3;
	private static ArrayList<Queue<Integer>> AllTemps = new ArrayList();
	private static int ConCounter = 0;
	private static ArrayList<String> Names = new ArrayList<String>();
	
	public void run() {
		
		boolean listening = true;
        ServerSocket serverSocket = null;
        
        // Set up the Server Socket
        try 
        {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("New Server has started listening on port: " + portNumber );
        } 
        catch (IOException e) 
        {
            System.out.println("Cannot listen on port: " + portNumber + ", Exception: " + e);
            System.exit(1);
        }
        
        // Server is now listening for connections or would not get to this point
        while (listening) // almost infinite loop - loop once for each client request
        {
            Socket clientSocket = null;
            try{
            	System.out.println("**. Listening for a connection...");
                clientSocket = serverSocket.accept();
                System.out.println("00. <- Accepted socket connection from a client: ");
                System.out.println("    <- with address: " + clientSocket.getInetAddress().toString());
                System.out.println("    <- and port number: " + clientSocket.getPort());
            } 
            catch (IOException e){
                System.out.println("XX. Accept failed: " + portNumber + e);
                listening = false;   // end the loop - stop listening for further client requests
            }	
            
            if(ConCounter<5) {
            	 MyConnectionHandler con = new MyConnectionHandler(clientSocket, ConCounter);
                 con.start();
                 ConCounter++;
            }
            else System.out.println("Already at max clients");
            
            System.out.println("02. -- Finished communicating with client:" + clientSocket.getInetAddress().toString());
        }
        // Server is no longer listening for client connections - time to shut down.
        try 
        {
            System.out.println("04. -- Closing down the server socket gracefully.");
            serverSocket.close();
        } 
        catch (IOException e) 
        {
            System.err.println("XX. Could not close server socket. " + e.getMessage());
        }
    }
	
	public static void setSample(int sam) { // sets a new sample period
		SampleTime = sam; 					// is ran frum GUI on user input
		System.out.println("Server time set to: " + SampleTime);
	}
	
	
	public static int getSampleTime() {
		return SampleTime; //returns sample time to connection handler
	}
	
	public static void getQueue(Queue<Integer> Q, int Label) {//used to store temperature data incoming
		if(AllTemps.size()<Label+1) {	//when a new member is adding to alltemps	
			AllTemps.add(Q);			//adds a queue of temp readings to an arraylist of queues
		}
		else {
			AllTemps.set(Label, Q); //when an old member is updating
		}		
	}

	public static ArrayList<Queue<Integer>> getAllTemps() {
		return AllTemps; //returns all temps, for the graph
	}
	
	public static void addName(String Name) {
		if (ConCounter > Names.size()) {
			Names.add(Name);
		}
	}
	
	public static String giveName(int Label) {
		if (Names.size()>Label) {
			return Names.get(Label);
		}
		else
			return "Empty";
	}

}