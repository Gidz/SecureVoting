//: MultiJabberServer.java
// A server that uses multithreading to handle
// any number of clients.
import java.io.*;
import java.net.*;

class ServerThread extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    public static String question = "Should pineapple be allowed on pizza ?";
    public ServerThread(Socket s)
            throws IOException {
        socket = s;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        // If any of the above calls throw an 
        // exception, the caller is responsible for
        // closing the socket. Otherwise the thread
        // will close it.
        start(); // Calls run()
    }
    public void run() {
        try {
            while (true) {
                String str = in.readUTF();

                if (str.equals("END")) break;

                if(str.equals("JOIN"))
                {
                    out.writeUTF(question);
                    continue;
                }

                System.out.println("Received: " + str);
//                out.writeUTF("ACK");
                BulletinBoard.storage.add(Integer.parseInt(str));
                int[] tally = BulletinBoard.count();
                System.out.println("YES: "+tally[0]);
                System.out.println("NO: "+tally[1]);
            }


        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch(IOException e) {}
        }
    }
}

