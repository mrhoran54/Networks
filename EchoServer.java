/* Author: Megan Horan mrhoran@bu.edu
 * Citation: skeleton code provided by prof Matta
 * Date: 9/27/16
 * 
 */
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.*;


import java.io.*;
import java.net.*;

/*
 * An echo server that simply echoes messages back.
 */
public class EchoServer {
    
    // Create a socket for the server 
    private static ServerSocket serverSocket = null;
    // Create a socket for the user 
    private static Socket userSocket = null;
    private static BufferedReader input_stream = null;
    private static PrintStream output_stream = null;

    public static void main(String args[]) {
        
        // The default port number.
        int portNumber = 8000;
        if (args.length < 1) {
            System.out.println("Usage: java Server <portNumber>\n"
                                   + "Now using port number=" + portNumber + "\n");
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }
        // try to connect to the server socket
        try{
            
                serverSocket = new ServerSocket(portNumber); 
            
        }
        catch (IOException e) {
                System.out.println(e);
            }
        
        while (true) {
        try {
            /*
             * Create a user socket for accepted connection 
             */
                 userSocket = serverSocket.accept(); 
                
                // create and input stream, attached to socket
                input_stream =
                    new BufferedReader(new
                    InputStreamReader(userSocket.getInputStream())); 
               
                //creates output stream attached to socket
                output_stream =
                    new PrintStream(userSocket.getOutputStream());
                
                 //output_stream.println(input_stream.readLine());
                String inputLine;
                 
                while((inputLine = input_stream.readLine()) != null){
                    System.out.print(inputLine);
                    output_stream.println(inputLine);
                }
                
                
             /*
              * Close the output stream, close the input stream, close the socket.
              */
             input_stream.close();
             output_stream.close();
             userSocket.close(); 
                }
         catch (IOException e) {
                System.out.println(e);
            }
            
        }
    }
}
