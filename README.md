# 📝 Computer Networks Homework Repository

This repository contains the implementation and documentation for the Computer Networks (SWE3022_41) course homework assignments.

---

## 📘 Homework 1: TCP & UDP-based Client-Server Calculator

### 🎯 Objective
Develop a simple client-server calculator using two different programming languages (e.g., Python for server, Java for client), with support for both **TCP** and **UDP** transport protocols.

### ✅ Requirements
- Implement both **TCP** and **UDP** versions.
- Use different programming languages for the client and server.
- Expression input: only integers, `+`, and `-`.
- Evaluate expressions manually (no `eval()` or built-in cal  c libraries).
- IP Address: `127.0.0.1`, Port: `12000`.
- The client saves the expression and result in `result.txt`.
- Code must be runnable using:
  ```bash
  python filename.py
  gcc filename.c -o filename.exe -lws2_32 && filename.exe
  g++ filename.cpp -o filename.exe -lws2_32 && filename.exe
  javac filename.java && java filename
  ```
### 📂 Folder Structure
```
├── TCP/
│   ├── tcp_client.cpp
│   └── tcp_server.py
│
├── UDP/
│   ├── udp_client.cpp
│   └── udp_server.py
└──
```


## 📘 Homework 2: Multi-Client Expression & HTTP Server

### 🎯 Objective








## 📘 Homework 5: Multi-client Video Streaming

### 🎯 Objective
Implement a real-time video streaming system using multi-client and multi-server architecture.

### ✅ Requirements
- Client1 streams cached video
- Client2 triggers global server → local server → client flow
- Frame-level streaming (e.g., JPEG)
- Real-time display and saving of streamed video

### 📂 File Structure
Homework5/
├── client1.cpp
├── client2.py
├── local_server.cpp
├── global_server.java
└── video_2025.mp4

### 💻 How to Run
```bash
g++ local_server.cpp -o local.exe -lws2_32 && local.exe
javac global_server.java && java global_server
python client2.py
