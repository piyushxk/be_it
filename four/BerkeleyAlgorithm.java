import java.util.*;
import java.io.*;
import java.net.*;
public class BerkeleyAlgorithm {
	private static final int PORT = 1024;
	public static void main(String[] args) throws Exception{
		// initialize connection server with port
		ServerSocket serverSocket = new ServerSocket(PORT);
		
		List<Long> timeDiffs = new ArrayList<Long>();
		
		Thread timeServerThread = new Thread(new Runnable(){
			public void run() {
				while(true) {
					try {
						Socket clientSocket = serverSocket.accept();
						ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
						Date clientTime = (Date) in.readObject();
						
						ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
						
						out.writeObject(new Date());
						
						long timeDiff = (new Date().getTime() - clientTime.getTime())/2;
						
						timeDiffs.add(timeDiff);
						
						in.close();
						out.close();
						
						clientSocket.close();
						
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		timeServerThread.start();
		
		Thread timeClientThread = new Thread(new Runnable(){
			public void run() {
				while(true) {
					try {
						Socket socket = new Socket("localhost", PORT);
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
						out.writeObject(new Date());

						ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
						Date clientTime = (Date) in.readObject();						
						
						long timeDiff = (new Date().getTime() - clientTime.getTime())/2;
						
						timeDiffs.add(timeDiff);
						
						in.close();
						out.close();
						
						socket.close();
						
						Thread.sleep(1000);
						
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		timeClientThread.start();
		Thread.sleep(10000);
		
		long sumTimeDiff = 0;
		
		for(Long timeDiff : timeDiffs) {
			sumTimeDiff+=timeDiff;
		}
		
		long avg = sumTimeDiff/timeDiffs.size();
		
		System.out.println("Avg Time Diff : " + avg);
		
		Calendar calender = Calendar.getInstance();
		
		calender.setTime(new Date());
		calender.add(Calendar.MILLISECOND , (int)avg);
		
		System.out.println("Adjusted Time : " + calender.getTime());
		
	}
}
