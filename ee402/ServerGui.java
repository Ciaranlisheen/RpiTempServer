package ee402;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class ServerGui extends JFrame implements ActionListener {

	
	 static JLabel LSampleTime = new JLabel("SampleTime:");
	 static JFrame myGui = new JFrame("Temperature Server");
     static JPanel ControlPanel = new JPanel();
     static JPanel graphWindow;
     static JPanel clients = new JPanel();
     static JPanel SamplePanel = new JPanel();
     static JButton StartStop = new JButton("Start");
     static JButton SetSample = new JButton("Set");
     static JButton GraphControl = new JButton("0");
     static JTextField SampleField = new JTextField();
     static String[] Colors = {"Red", "Green", "Blue", "Pink", "Orange", "Black"}; 
     static JComboBox[] ColorList = {new JComboBox(), new JComboBox(), new JComboBox(), new JComboBox(), new JComboBox()};
     static JLabel[] Names = {new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel()};
     
     
     MyServer server = new MyServer();
     
     public ServerGui()
     {
    	 	myGui.setResizable(false);//cannot be resized
            myGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//exit on close
    	 	ControlPanel.setLayout(new GridLayout(15,1,10,10));//control panel on left uses this layout
    	 	SamplePanel.setLayout(new GridLayout(1,2,10,10));//sample control within control panel
    	 	
    	 	
             
            StartStop.setPreferredSize(new Dimension(240, 40));//dimension of start button
            StartStop.addActionListener(this);//action listners for buttons
            SetSample.addActionListener(this); 
            
            clients.setLayout(new GridLayout(2,10,0,0));//layout for clients along the bottom
            for(int i=0; i<5; i++) {
            	Names[i] = new JLabel(MyServer.giveName(i));
            	clients.add(Names[i]);
            	GraphControl = new JButton("" + (i+1));//buttons fpr clients
            	GraphControl.addActionListener(this);
            	clients.add(GraphControl);//add buttons
            }
            for(int i=0; i<5; i++) {
            	clients.add(new JLabel(""));//labels for clients
            	ColorList[i] = new JComboBox(Colors);
            	ColorList[i].addActionListener(this);
            	clients.add(ColorList[i]);//add buttons
            }
       	 	graphWindow = new PaintGraph();//paint graph
       	 	graphWindow.setPreferredSize(new Dimension(1024, 768));//set graph dimensions
       	 	myGui.add(clients, BorderLayout.SOUTH); //add client controls
       	 	myGui.add(graphWindow, BorderLayout.WEST);//add graph window
       	 	myGui.add(ControlPanel);//add start and sample control
       	 	ControlPanel.add(StartStop, BorderLayout.EAST);//add start button
       	 	ControlPanel.add(LSampleTime);//add sample label
       	 	ControlPanel.add(SamplePanel);//add sample panel
       	 	SamplePanel.add(SampleField);//add sample tfield
       	 	SamplePanel.add(SetSample);  //add sample button
       	 	myGui.pack();        
       	 	myGui.setVisible(true);  
            
            while(true) {//loops forever server runs in separate thread
            	 graphWindow = new PaintGraph();//update graph
            	 for(int i=0; i<5; i++) {
                 	Names[i].setText(MyServer.giveName(i));
                 }
            	 myGui.validate();
            	 myGui.repaint();
            }
                
     }
     
     
     public void actionPerformed(ActionEvent e)
     {
             if (e.getActionCommand().equals("Start"))  server.start();//starts server
             else if(e.getActionCommand().equals("Set")) MyServer.setSample(Integer.valueOf(SampleField.getText()));//sets new sample time
             else if(e.getActionCommand().equals("1")) PaintGraph.setGraph(0);//turns on off client 1 graph
             else if(e.getActionCommand().equals("2")) PaintGraph.setGraph(1);//^^ for client 2
             else if(e.getActionCommand().equals("3")) PaintGraph.setGraph(2);//so on
             else if(e.getActionCommand().equals("4")) PaintGraph.setGraph(3);
             else if(e.getActionCommand().equals("5")) PaintGraph.setGraph(4);               
     }
     
     public static void main(String[] args)
     {
             new ServerGui();//starts the Gui
     }
     
     
     public static Color returnColor(int clientlabel) {
		String chosenColor;
		chosenColor = (String) ColorList[clientlabel].getSelectedItem();//if boolean true shows in color
    	
    	switch(chosenColor) {//switch for each client
		case "Red": return Color.RED;	
		case "Green": return Color.GREEN;
		case "Blue": return Color.BLUE;
		case "Pink": return Color.PINK;
		case "Orange": return Color.ORANGE;
		case "Black": return Color.BLACK;
		default: return Color.BLACK;

    	}
 	 
     }
}
