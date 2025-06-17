// 2020312429 Minjae Jeong

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class server_hw2 {
    private static final int PORT = 12000;
    private static final String HISTORY = "history.html";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService pool = Executors.newCachedThreadPool();
        System.out.println("[Java-based Server Started] Listening on port " + PORT + "...");

        while (true) {
            Socket client = serverSocket.accept();
            pool.execute(() -> handleClient(client));
        }
    }

    private static void handleClient(Socket socket) {
        String addr = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        System.out.println("[NEW CONNECTION] " + addr);

        try (
            InputStream inputStream = socket.getInputStream();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                String input = new String(buffer, 0, len).trim();
                if (input.isEmpty()) continue;

                System.out.println("[REQUEST] From " + addr + ": " + input);

                if (input.startsWith("GET")) {
                    String filename = input.split(" ")[1].replace("/", "");
                    File file = new File(filename);
                    if (file.exists()) {
                        StringBuilder content = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) content.append(line).append("\n");
                        }
                        out.println("HTTP/1.1 200 OK\r\n\r\n" + content);
                        System.out.println("[FILE FOUND] " + filename + " sent to " + addr);
                    } else {
                        out.println("HTTP/1.1 404 File Not Found\r\n\r\n");
                        System.out.println("[FILE MISSING] " + filename + " not found for " + addr);
                    }
                } else {
                    String result = evaluate(input);
                    out.println("Result = " + result);
                    synchronized (HISTORY) {
                        try (FileWriter fw = new FileWriter(HISTORY, true)) {
                            fw.write(input + " = " + result + "\n");
                        }
                    }
                    System.out.println("[RESULT] " + input + " = " + result);
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("[DISCONNECTED] " + addr);
        }
    }

    private static String evaluate(String expr) {
        try {
            expr = expr.toLowerCase().replace(" ", "");
            if (expr.startsWith("square")) {
                double val = Double.parseDouble(expr.substring(6));
                return String.valueOf(val * val);
            } else if (expr.startsWith("sqrtof") || expr.startsWith("sqrt")) {
                String numPart = expr.replace("sqrtof", "").replace("sqrt", "");
                double val = Double.parseDouble(numPart);
                return String.format("%.1f", Math.sqrt(val));
            }

            List<String> postfix = new ArrayList<>();
            Stack<String> opStack = new Stack<>();
            String num = "";
            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);
                if (Character.isDigit(c) || c == '.') {
                    num += c;
                } else {
                    if (!num.isEmpty()) {
                        postfix.add(num);
                        num = "";
                    }
                    String op = String.valueOf(c);
                    while (!opStack.isEmpty() && precedence(opStack.peek()) >= precedence(op)) {
                        postfix.add(opStack.pop());
                    }
                    opStack.push(op);
                }
            }
            if (!num.isEmpty()) postfix.add(num);
            while (!opStack.isEmpty()) postfix.add(opStack.pop());

            Stack<Double> stack = new Stack<>();
            for (String token : postfix) {
                if ("+-*/".contains(token)) {
                    double b = stack.pop(), a = stack.pop();
                    switch (token) {
                        case "+": stack.push(a + b); break;
                        case "-": stack.push(a - b); break;
                        case "*": stack.push(a * b); break;
                        case "/": stack.push(a / b); break;
                    }
                } else {
                    stack.push(Double.parseDouble(token));
                }
            }

            return String.format("%.1f", stack.pop());

        } catch (Exception e) {
            return "Invalid Expression";
        }
    }

    private static int precedence(String op) {
        if (op.equals("+") || op.equals("-")) return 1;
        if (op.equals("*") || op.equals("/")) return 2;
        return 0;
    }
}
