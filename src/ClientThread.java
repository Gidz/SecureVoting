//: MultiJabberClient.java
// Client that tests the MultiJabberServer
// by starting up multiple clients.
import java.net.*;
import java.io.*;
import java.util.Scanner;

class ClientThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static int counter = 0;
    private int id = counter++;
    private static int threadcount = 0;
    public static int threadCount() {
        return threadcount;
    }
    public ClientThread(InetAddress addr) {
        System.out.println("Making client " + id);
        threadcount++;
        try {
            socket =
                    new Socket(addr, Server.PORT);
        } catch(IOException e) {
            // If the creation of the socket fails,
            // nothing needs to be cleaned up.
        }
        try {
            in =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()));
            // Enable auto-flush:
            out =
                    new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            socket.getOutputStream())), true);
            start();
        } catch(IOException e) {
            // The socket should be closed on any
            // failures other than the socket
            // constructor:
            try {
                socket.close();
            } catch(IOException e2) {}
        }
        // Otherwise the socket will be closed by
        // the run() method of the thread.
    }
    public void run() {
        try {
            String toSend;

            while(true) {
                System.out.print("> ");
                Scanner sc = new Scanner(System.in);
                toSend = sc.nextLine();

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(toSend);

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                System.out.println(dis.readUTF());
            }

        }
        catch(IOException e) {
        }
        finally {
            // Always close it:
            try {
                socket.close();
            } catch(IOException e) {}
            threadcount--; // Ending this thread
        }
    }
}

