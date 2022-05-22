/**
 * 
 */
package assignment3;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NavigableSet;
import java.util.TreeSet;

import lombok.Data;

@Data
public class Server {
	
	// TreeSet as automatically sorted
	private static NavigableSet<String> tokenList = new TreeSet<String>();

	// TOKENLIST METHODS
	 synchronized static void add(String s) {
            tokenList.add(s);
    }
    static synchronized void remove(String s) {
            tokenList.remove(s);
    }
    synchronized static boolean isEmpty() {
            return tokenList.isEmpty();
    }
    
    static synchronized boolean isFull() {
    	if (size() >= 10 ) {
    		return true;
    	}
    	return false;
    }
    synchronized static boolean contains(String s) {
            return tokenList.contains(s);
    }
    static synchronized int size() {
            return tokenList.size();
    }
    
    synchronized static NavigableSet<String> getTokenList() {
    	return tokenList;
    }
	
  
    
// end METHODS

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		
	    ServerSocket server = new ServerSocket(6666); 
	    
			while(true) {
				
				Socket s = null;
				
				try {
					
					// socket object to receive requests
					s = server.accept();
					
					System.out.println("Client "+s+": connected");
					
					// establish input and output streams
					DataInputStream instream = new DataInputStream(s.getInputStream());
					DataOutputStream outstream = new DataOutputStream(s.getOutputStream()); 

					// create a new instance of thread object
					Thread t = new Thread(new Handler(s, instream, outstream));
					
					// starts the thread objects
					t.start();
					Thread.sleep(200);
					
				}
				catch (Exception e){
					s.close();
					e.printStackTrace();
				}
			}
		}
	
	}
				
	class Handler extends Thread {
		
		final DataInputStream instream;
		final DataOutputStream outstream;
		final Socket s;
		
		// Constructor
		public Handler(Socket s, DataInputStream instream, DataOutputStream outstream) {
			this.s = s;
			this.instream = instream;
			this.outstream = outstream;
		}
		
		public void sender(String response) throws IOException {
			// standardises message to send to client and to print on server console
			 outstream.writeUTF(response);
			 System.out.println("Server sent response '" +response+ "' to client");
		}

		@Override
		public void run() {
			String response;
			String request = null;
			
			while (Server.isFull() == false && request != "QUIT") {
			
				try {
					// Ask client for command
					response = "Please enter a command to be sent to the server: \n RETRIEVE or SUBMIT [SUBMIT token]\n" + 
							"Type QUIT to terminate connection.";
					sender(response);
					
					// read in response and assign it to variable
					request = instream.readUTF();
					
					System.out.println("Received from client: " + request);
					
					if (request.equals("QUIT")) {
						this.s.close();
						break;
					}
					else if(request.equals("RETRIEVE")) {   
				        if(Server.isEmpty()) {
				        	response = "ERROR - There are no tokens submitted";
				        	sender(response);
				        	break;
				        }
				        else {
				        	response = Server.getTokenList().toString();
				        	sender(response);
					   }    
					}
					
					else if (request.startsWith("SUBMIT")) {
					    	String[] token = request.split(" ");
				            if (Server.isFull()) {
				            	response = "ERROR - maximim number of tokens reached";
				            	sender(response);
				            	break;
				            }
				            else {
				                response = "OK";
				                sender(response);
				                if (Server.contains(token[1])) {
				                	break;
					            }
				                Server.add(token[1]);
				                break;
				            }
					} 
					else {
					        response = "Unknown request!" ;
					        sender(response);
					}
			} catch(IOException e) {
				e.printStackTrace();
			}
				
			try {
				// close resources 
				this.instream.close();
				this.outstream.close();	
			} 
			catch(IOException e) {
				e.printStackTrace();
			}
		}
			
	}
}		