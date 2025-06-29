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
- The client saves the expression and result in `result.txt`.


## 📘 Homework 2: Multi-Client Expression & HTTP Server

### 🎯 Objective
Extend Homework 1 to support:
- Multi-threaded TCP server
- Two different types of clients
  - Client 1: sends expressions
  - Client 2: sends HTTP GET requests

### ✅ Requirements
- Use three different languages for server, Client 1, and Client 2.
- Client 2 code size must be under 500 bytes.
- Server supports +, -, *, /, square, sqrt operations.
- Save results to history.html.
- If a requested file does not exist, server must return a proper 404 Not Found response.


## 📘 Homework 4: DNS Simulation (Client, Local DNS, Global DNS)

### 🎯 Objective
Implement a simulation of the DNS resolution process with:
- Client
- Local DNS Server
- Global DNS Server

### ✅ Requirements
- Client: Python
- Local DNS Server: C++
- Global DNS Server: Java
- Use UDP between Client ↔ Local DNS
- Use TCP between Local DNS ↔ Global DNS
- Cache resolved domains in local DNS


## 📘 Homework 5: Multi-client Video Streaming

### 🎯 Objective
Implement a real-time video streaming system using multi-client and multi-server architecture.

### ✅ Requirements
- Client1 streams cached video
- Client2 triggers global server → local server → client flow
- Frame-level streaming (e.g., JPEG)
- Real-time display and saving of streamed video
