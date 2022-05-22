/**
 * 
 */
package assignment3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import lombok.Data;

/**
 * @author j
 *
 */
@Data
public class Client {
	
	public static void main(String[] args) throws IOException {
		
		
		
		try {
			Scanner scn = new Scanner(System.in); 	
			InetAddress ip = InetAddress.getByName("localhost");
			
			// establish connection with port
			Socket s = new Socket(ip, 6666); 
			s.setSoTimeout(100000); // to stop potential perpetual block
			System.out.println("Client is connected to server");
			
			// establish input and output streams
			DataInputStream instream = new DataInputStream(s.getInputStream());
			DataOutputStream outstream = new DataOutputStream(s.getOutputStream());
			String request = null;
			
			while (true) {
				System.out.println(instream.readUTF());
				request = scn.nextLine().toUpperCase();
				System.out.println("Client: sent '" +request+ "' to server");
				outstream.writeUTF(request);

				// If client sends exit,close this connection
				// and then break from the while loop
				if (request.equals("QUIT"))
				{
					System.out.println("Connection terminated : " + s);
					s.close();
					break;
				}
				
				
				// printing response from server
				String response = instream.readUTF();
				System.out.println("Client: received message from server: " +response);

			}
				// closes resources
				scn.close();
				instream.close();
				outstream.close();
				
			} catch(Exception e){
				e.printStackTrace();
			}
	}
}



