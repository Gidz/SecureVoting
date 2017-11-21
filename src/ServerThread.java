//: MultiJabberServer.java
// A server that uses multithreading to handle
// any number of clients.
import java.io.*;
import java.net.*;

class ServerThread extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
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
                System.out.println("Received: " + str);
                out.writeUTF("ACK");
            }
            System.out.println("closing...");
        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch(IOException e) {}
        }
    }
}

