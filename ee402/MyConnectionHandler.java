package ee402;

import java.net.*;
import java.util.*;
import java.io.*;

public class MyConnectionHandler extends Thread
{
	private int clientLabel;
    private Socket clientSocket = null;				// Client socket object
    private ObjectInputStream is = null;			// Input stream
    private ObjectOutputStream os = null;			// Output stream
    private ClientInfo currentData = null;
    private Queue<Integer> temps = new LinkedList<>(); //que of temps

    
	// The constructor for the connection handler
    public MyConnectionHandler(Socket clientSocket, int conCounter) {
        this.clientSocket = clientSocket;
        this.clientLabel = conCounter;
    }

    // Will eventually be the thread execution method - can't pass the exception back
    public void run() {
         try {
            this.is = new ObjectInputStream(clientSocket.getInputStream());
            this.os = new ObjectOutputStream(clientSocket.getOutputStream());
            while (this.readTemp()) {}
         } 
         catch (IOException e) 
         {
        	System.out.println("XX. There was a problem with the Input/Output Communication:");
            e.printStackTrace();
         }
    }
    
    // Receive and process incoming string commands from client socket 
    private boolean readTemp() {        
        try {
            currentData = (ClientInfo) is.readObject(); //gets obj from client
                       
            if(temps.size()<20) {
            	temps.add(currentData.returnTemp()); //add temp to queue
            }
            else {
            	temps.remove();	//removes one entry
            	temps.add(currentData.returnTemp()); //adds temp to que
            }
            MyServer.addName(currentData.returnName());
        } 
        
        catch (Exception e){    // catch a general exception
        	this.closeSocket();
        	System.out.println("error recieving");
            return false;
        }

        System.out.println("01. <- Received this Temperature from the client " + currentData.returnTemp());
        this.send(MyServer.getSampleTime()); //Returns the sample time
        MyServer.getQueue(temps, clientLabel); //updates temp data in server
        return true;
    }  

    // Send a generic object back to the client (the sample time)
    private void send(Object o) {
        try {
            System.out.println("02. -> Sending (" + o +") to the client.");
            this.os.writeObject(o);
            this.os.flush();
        } 
        catch (Exception e) {
            System.out.println("XX." + e.getStackTrace());
        }
    }
    
    // Send a pre-formatted error message to the client 
    public void sendError(String message) { 
        this.send("Error:" + message);	//remember a String IS-A Object!
    }
   
    
    // Close the client socket 
    public void closeSocket() { //gracefully close the socket connection
        try {
            this.os.close();
            this.is.close();
            this.clientSocket.close();
        } 
        catch (Exception e) {
            System.out.println("XX. " + e.getStackTrace());
        }
    }
    
    //public Queue<Integer> returntemps() {
	//	return temps;
    //}
    
}