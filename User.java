import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;

import java.net.UnknownHostException;

public class User extends Thread {
     
    // The user socket
    private static Socket userSocket = null;
   // The output stream
    private static PrintStream output_stream = null;
    // The input stream
    private static BufferedReader input_stream = null;
    
    private static BufferedReader inputLine = null;
    
    private static boolean closed = false;
    
   
    public static void main(String[] args) {
        
        // The default port.
        int portNumber = 8000;
        // The default host.
        String host = "localhost";


        /*
         * Open a socket on a given host and port. Open input and output streams.
         */
        if (args.length < 2){
            System.out.println("usage: java <host> <portnumber> \n" + "Now using host =" +host +",portnumnbers");
        } else{
            
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }
        
        try{
            
             inputLine = new BufferedReader( new InputStreamReader(System.in));

             userSocket = new Socket(host, portNumber);
       
             //creates output stream attached to socket
            output_stream = new PrintStream(userSocket.getOutputStream());
            
            // create and input stream, attached to socket
            input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream())); 
               
           
        }
          
         catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host"
                                   + host);
        }
                 
 /* If everything has been initialized then create a listening thread to 
  * read from the server. 
  * Also send any user’s message to server until user logs out.
  */  
        try{
             // create a new thread
             new Thread(new User()).start();
             
             while(closed == false){
                 
                 
                 String input_from_user = inputLine.readLine();
                 
                 output_stream.println(input_from_user);
                 //System.out.println(input_from_user);
                 
                 if(input_from_user == "LogOut"){
                     output_stream.println("Now ending the connection");
                     closed = true;
                     break;
                 }
                     
          
             }
            output_stream.close();
            input_stream.close();
            userSocket.close();
            
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host"
                                   + host);
        }
        
    }
    
    public void run() {
        /*
         * Keep on reading from the socket till we receive “### Bye …” from the
         * server. Once we received that then we want to break and close the connection.
         */
          try{
          
              while(closed == false){
            
                  String input = input_stream.readLine();
                  
                  // print out output from the server
                  System.out.println(input);
                  
          
                  if(input.startsWith("### Bye ")){
              
                      closed = true;
                      break;
                  }
            
                   
          } 
            input_stream.close();
            output_stream.close();   
            userSocket.close();
            
          }
            
          
           catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host"
                                   + e);
        }
        
        }      
}
