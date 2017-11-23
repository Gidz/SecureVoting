import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static final int PORT = 8080;
    static final long endTime = 60;
    static final String exitMessage = "The Elections have ended. No further votes will be accepted";

    static long start = System.currentTimeMillis();

    public static void main(String[] args)
            throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Server Started");
        try {

            /* The timing mechanism */
            /////////////////////////

            Thread t = new Thread() {
                public void run() {
                    long end = start + (endTime * 1000); // 60 seconds * 1000 ms/sec
                    int i=0;
                    while (System.currentTimeMillis() < end)
                    {
                        //Busy wait
                    }
                    System.out.println(exitMessage);
                    System.exit(0);
                }
            };
            t.start();

            while(true) {
                // Blocks until a connection occurs:
                Socket socket = s.accept();
                try {
                        new ServerThread(socket);
                } catch(IOException e) {
                    // If it fails, close the socket,
                    // otherwise the thread will close it:
                    socket.close();
                }
            }
        } finally {
            System.out.println("The server is shutting down");
            s.close();
        }
    }
}