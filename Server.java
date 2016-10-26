import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataInputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;


/*
 * A chat server that delivers public and private messages.
 */
public class Server {
    
    // Create a socket for the server 
    private static ServerSocket serverSocket = null;
   
    // Create a socket for the user 
    private static Socket userSocket = null;
    
    // Maximum number of users 
    private static int maxUsersCount = 5;
    
    // An array of threads for users
    private static userThread[] threads = null;

    public static void main(String args[]) {
        
        // The default port number.
        int portNumber = 58007;
        
      try{
         // open the serversocket
        serverSocket = new ServerSocket(portNumber);
        
         //intialize the threads array so it isnt null anymore
        // set it to the max Count 5
        
        userThread[] threads = new userThread[maxUsersCount];
        
        while (true) {
            
            
            userSocket = serverSocket.accept();
            
            // int k will keep a sort of running tally of how many users are present
            int k = 0;
            
            for(k = 0; k < maxUsersCount; k++){
                
                // check if there are too many users
                if(k == (maxUsersCount-1)){
                    //open a temporary stream to tell the user sock that they cant connect
               
                    PrintStream output_stream = new PrintStream(userSocket.getOutputStream());
                    output_stream.println("Too many users at this time; Pleas try again later!!");
                    output_stream.close();
                    userSocket.close(); 
               
                }   
                // otherwise iterate through the whole array and find the first empty spot to put the thread
                
                else if(threads[k] == null){
                    
                    // synchronize the threads to make sure you dont over run other threads
                    synchronized(threads){
                        
                        (threads[k] = new userThread(userSocket, threads)).start();
                        
                    }
                    break;
                    
                }
            }
            
           
            }
        
      }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host"
                                   + e);
        }
    }
}

/*
 * Threads
 */

class userThread extends Thread {
    
    private String userName = null;
    private BufferedReader input_stream = null;
    private PrintStream output_stream = null;
    private Socket userSocket = null;
    private final userThread[] threads;
    private int maxUsersCount;

    // only relevant for Part IV: adding friendship
    ArrayList<String> friends = new ArrayList<String>();
    ArrayList<String> friendrequests = new ArrayList<String>();  //keep track of sent friend requests 
    //

    public userThread(Socket userSocket, userThread[] threads) {
        this.userSocket = userSocket;
        this.threads = threads;
        maxUsersCount = threads.length;
    }
    
    public void run() {

 /*
  * Create input and output streams for this client, and start conversation.
  */
       try{
            // create and input stream, attached to socket
            input_stream = new BufferedReader(new InputStreamReader(this.userSocket.getInputStream())); 
               
             //creates output stream attached to this client
             output_stream = new PrintStream(userSocket.getOutputStream());
             
             output_stream.println("Welcome to the chatroom!");
             output_stream.println("Please enter your name:");
            
             // this while loop will keep looping and getting input until the user enters the correct username
             
             boolean correctUserName = false;
             
             while(correctUserName == false){
                 
             
             String input_from_user = input_stream.readLine();
             
                 
             this.userName = input_from_user;
            
             if(this.userName.charAt(0) == '@'){
                 
                 output_stream.println("You can not start a username with @ character. Please try again.");
                 //loop again until they get it right
                 correctUserName = false;
                 
             }
             
             //broadcast to everyone that the new user has entered the chat
             else{
                 
                 for(int i = 0; i < threads.length-1; i++){
                     
                   
                     // check the threads and the usernames arent null   
                     if(threads[i] != null && threads[i].userName !=null){
                      
                          synchronized(threads){
                     
                         threads[i].output_stream.println(this.userName + " has joined the chat!!");
                          }
                     }
                     else
                         continue;
                 }
                 
                 //finally breakout of the while loop if they entered the right username
                 correctUserName = true;
             }

             }
              
             output_stream.println("Please Enter your messages. \n" + "If you would like to direct message a user, type @ + their username\n"
                                  + " Type LogOut to close!");
             
            // if the username has been set up, make a converstion
             // unless you set this value to false (ie someone logs out) then keep looping
             
             boolean conversation = true;
                 
             while(conversation == true) {
                 
                 //keep getting input from the user
                 String input_from_user = input_stream.readLine();
                 
                 // if the input is the log out phase break them out
                 
                 if(input_from_user.startsWith("LogOut")){
                      
                     // first broadcast it to everyone
                     for(int i = 0; i < maxUsersCount; i++){
                     
                         if(threads[i] != null && threads[i].userName != this.userName)
                             
                             synchronized(threads){
                            
                             threads[i].output_stream.println(this.userName + " is leaving the chat");
                             
                     
                         }
                         // then send out the message that will tell the user to break the connection
                         output_stream.println("### Bye "+ this.userName+" ###");
                         
                         // no more conversation is happening! time to leave the while loop
                         conversation = false;   
                     }
                 }
                 
                 // direct messsaing fucntionality
                 else if (input_from_user.charAt(0) == '@'){
                    
                      for(int i = 0; i < threads.length-1; i++){
                          
                          // go through the threads and if the username matches the direct message send it to that thread!
                          if(threads[i] != null && threads[i].userName != this.userName){
                             
                              // if you come to a thread that starts with the username that matches the dm, send them the message
                              if(input_from_user.startsWith("@"+threads[i].userName)){
                                        
                                  threads[i].output_stream.println(input_from_user);
                                  break;
                              }
                          }
                      }
                 }
                 // otherwise just keep listening and getting input and broadcasing messages
                 else{
                     
                     for(int i = 0; i < maxUsersCount; i++){
                      
                         if(threads[i] != null){
                             
                             
                             synchronized(threads){
                                 // the convention to print out the username and their input, like the example server
                                 threads[i].output_stream.println("<" + this.userName + "> "+ input_from_user);
                             }
                         }
                         else
                             continue;
                     }
                 
                 }  
          }
       }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host"
                                   + e);
        }
    }
}

//                 
                 


