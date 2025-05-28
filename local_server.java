import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.highgui.HighGui;

public class local_server {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) throws Exception {
        int port = 8000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[Local] Listening on port " + port + "...");

        Socket clientSocket = serverSocket.accept();
        System.out.println("[Local] Client connected: " + clientSocket.getInetAddress());

        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String filename = reader.readLine().trim();
        String cachedPath = "cache/" + filename;
        BufferedOutputStream toClient = new BufferedOutputStream(clientSocket.getOutputStream());

        if (filename.equalsIgnoreCase("live")) {
            System.out.println("[Local] Live streaming request received.");

            Socket globalSocket = new Socket("127.0.0.1", 9000);
            PrintWriter gOut = new PrintWriter(globalSocket.getOutputStream(), true);
            InputStream gIn = globalSocket.getInputStream();
            gOut.println("live");

            while (true) {
                byte[] sizeBuf = gIn.readNBytes(4);
                if (sizeBuf.length < 4) break;

                int size = ByteBuffer.wrap(sizeBuf).getInt();
                byte[] frameBuf = gIn.readNBytes(size);
                if (frameBuf.length < size) break;

                Mat mat = Imgcodecs.imdecode(new MatOfByte(frameBuf), Imgcodecs.IMREAD_COLOR);
                if (mat.empty()) continue;

                toClient.write(sizeBuf);
                toClient.write(frameBuf);
                toClient.flush();

                HighGui.imshow("Local Server Live Streaming", mat);
                if (HighGui.waitKey(30) == 27) break;
            }

            globalSocket.close();
            clientSocket.close();
            serverSocket.close();
            HighGui.destroyAllWindows();
            System.out.println("[Local] Live streaming ended.");
            return;
        }
        File cachedFile = new File(cachedPath);
        if (!cachedFile.exists()) {
            System.out.println("[Local] Not in cache. Requesting to Global Server...");

            Socket globalSocket = new Socket("127.0.0.1", 9000);
            OutputStream gOut = globalSocket.getOutputStream();
            InputStream gIn = globalSocket.getInputStream();
            gOut.write((filename + "\n").getBytes());
            gOut.flush();

            FileOutputStream fos = new FileOutputStream(cachedPath);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = gIn.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            fos.close();
            globalSocket.close();

            System.out.println("[Local] File cached. Now streaming to client...");

            Thread sender = new Thread(() -> {
                try (
                    FileInputStream fis = new FileInputStream(cachedPath)
                ) {
                    byte[] sendBuffer = new byte[4096];
                    int len;
                    while ((len = fis.read(sendBuffer)) != -1) {
                        toClient.write(sendBuffer, 0, len);
                    }
                    toClient.flush();
                    toClient.close();
                    System.out.println("[Local] File transmission finished.");
                } catch (IOException e) {
                    System.err.println("[Local] Error while sending cached video.");
                    e.printStackTrace();
                }
            });

            Thread player = new Thread(() -> {
                VideoCapture cap = new VideoCapture(cachedPath);
                Mat frame = new Mat();
                while (cap.read(frame)) {
                    HighGui.imshow("Local Server Live Streaming (From Global)", frame);
                    if (HighGui.waitKey(30) == 27) break;
                }
                cap.release();
                HighGui.destroyAllWindows();
            });

            sender.start();
            player.start();
            sender.join();
            player.join();
        }
        else {
            System.out.println("[Local] Found in cache. Streaming...");

            Thread sender = new Thread(() -> {
                try (
                    FileInputStream fis = new FileInputStream(cachedPath)
                ) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        toClient.write(buffer, 0, len);
                    }
                    toClient.flush();
                    toClient.close();
                    System.out.println("[Local] File transmission finished.");
                } catch (IOException e) {
                    System.err.println("[Local] Error while sending cached video.");
                    e.printStackTrace();
                }
            });

            Thread player = new Thread(() -> {
                VideoCapture cap = new VideoCapture(cachedPath);
                Mat frame = new Mat();
                while (cap.read(frame)) {
                    HighGui.imshow("Local Server Streaming (From Cache)", frame);
                    if (HighGui.waitKey(30) == 27) break;
                }
                cap.release();
                HighGui.destroyAllWindows();
            });

            sender.start();
            player.start();
            sender.join();
            player.join();
        }

        clientSocket.close();
        serverSocket.close();
        System.out.println("[Local] Finished.");
    }
}
