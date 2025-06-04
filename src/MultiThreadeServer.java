import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Multi threader server reads from
 * port 80 and processes. Uses ExecutorService
 * fixed thread pool
 */
public class MultiThreadeServer {


    public static void main(String[] args) {

    }
}

class ServerExec {


    public static void startProcessing() {

        ExecutorService executor = Executors.newFixedThreadPool(10);

        ServerSocket socket= null;

        try {
            socket = new ServerSocket(80);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        while(true) {

            try {
                final Socket conn = socket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                RequestProcessor requestProcessor = new RequestProcessor();

                requestProcessor.setReader(reader);

                executor.submit(requestProcessor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

class RequestProcessor implements Runnable {

    private BufferedReader reader;

    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        while(true) {

        }
    }
}