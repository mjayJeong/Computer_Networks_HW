import java.io.*;
import java.net.*;

public class global_dns_server {
    static int PORT = 9000;
    static String EDU = "global_edu_dns.txt";
    static String COM = "global_com_dns.txt";

    public static void main(String[] args) {
        try (ServerSocket servSock = new ServerSocket(PORT)) {
            System.out.println("[Global DNS Server] Listening on port " + PORT + "...");
            while (true) {
                Socket sock = servSock.accept();
                new Thread(() -> handle(sock)).start();
            }
        } catch (IOException e) {
            System.err.println("[Global DNS Server Error] " + e.getMessage());
        }
    }

    static void handle(Socket sock) {
        try (
            InputStream is = sock.getInputStream();
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true)
        ) {
            byte[] buffer = new byte[1024];
            int readLen = is.read(buffer);
            if (readLen <= 0) return;
            String domain = new String(buffer, 0, readLen).trim();
            System.out.println("[Global DNS Server] Request: " + domain);

            String file = domain.endsWith(".edu") ? EDU : domain.endsWith(".com") ? COM : null, res = "Not Found";
            if (file != null) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.trim().split(" ");
                    if (p[0].equalsIgnoreCase(domain)) { res = p[1]; break; }
                }
                br.close();
            }
            pw.println(res);
        } catch (IOException e) {
            System.err.println("[Global DNS Server Error] " + e.getMessage());
        } finally {
            try { sock.close(); } catch (IOException ignored) {}
        }
    }
}
