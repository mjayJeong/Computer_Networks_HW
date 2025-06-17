import socket

HOST = '127.0.0.1'
PORT = 12000
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    while True:
        msg = input("Enter expression or HTTP GET request (type 'exit' to quit): ")
        if msg.lower() == 'exit':
            break
        s.sendall(msg.encode())
        res = s.recv(4096).decode()
        print("Server response:\n" + res)
