import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

public class Server {
    static final int PORT = 8080;
    static final long endTime = 60;
    static final String exitMessage = "The Elections have ended. No further votes will be accepted";
    static ArrayList<BigInteger> sk,pk;
    static long start = System.currentTimeMillis();

    public static void main(String[] args)
            throws IOException, NoSuchProviderException, NoSuchAlgorithmException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Server Started");

        //Generate public and private keys
        ElGamalScheme.KeyGen(200);
        pk = ElGamalScheme.getPublicKey();
        sk = ElGamalScheme.getPrivateKey();

        try {

            /* The timer mechanism. Implemented by busy wait. Can be optimized further.
             * This will be run on a separate thread.
             * The thread keeps checking for the time and shuts down
             * the server once the time set is elapsed.
             * The ending time should be set in seconds by ENDTIME variable declared above.
             * */
            //Store the public and private key pair in separate files
            writeKeytoFile("pub.key",pk);
            writeKeytoFile("pri.key",sk);

            Thread t = new Thread() {
                public void run() {
                    long end = start + (endTime * 1000); // 60 seconds * 1000 ms/sec
                    int i=0;
                    while (System.currentTimeMillis() < end)
                    {
                        //Busy wait
                    }
                    System.out.println(exitMessage);
                    try {
                        BulletinBoard.storeVotes();
                        System.out.println("The votes are stored safely to the storage device.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    private static void writeKeytoFile(String fileName,ArrayList<BigInteger> key) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(key);
        fos.close();
        oos.close();
    }
}