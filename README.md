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
- Evaluate expressions manually (no `eval()` or built-in calc libraries).
- IP Address: `127.0.0.1`, Port: `12000`.
- The client saves the expression and result in `result.txt`.
- Code must be runnable using:
  ```bash
  python filename.py
  gcc filename.c -o filename.exe -lws2_32 && filename.exe
  g++ filename.cpp -o filename.exe -lws2_32 && filename.exe
  javac filename.java && java filename
