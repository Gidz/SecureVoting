import java.io.IOException;
import java.net.InetAddress;

public class Client {
    static final int MAX_THREADS = 40;
    public static void main(String[] args) throws IOException, InterruptedException {
        InetAddress addr = InetAddress.getByName(null);

            new ClientThread(addr);
            Thread.currentThread().sleep(100);
        }
}