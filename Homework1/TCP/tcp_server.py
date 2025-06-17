# 2020312429 Minjae Jeong

import socket

def evaluate_expression(expression):
    expression = "".join(expression.split())
    total = 0
    num = 0
    operator = '+'
    for char in expression:
        if char.isdigit():
            num = num * 10 + int(char)
        else:
            if operator == '+':
                total += num
            elif operator == '-':
                total -= num
            num = 0
            operator = char
    if operator == '+':
        total += num
    elif operator == '-':
        total -= num
    return str(total)

def start_tcp_server():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('127.0.0.1', 12000))
    server_socket.listen(1)
    
    print("The server is ready to receive.")
    print("TCP Server is running on 127.0.0.1:12000...")

    try:
        while True:
            conn, addr = server_socket.accept()
            print(f"Connected by {addr}")

            expression = conn.recv(1024).decode()
            print(f"Received expression: {expression}")

            result = evaluate_expression(expression)
            print(f"Calculated result: {result}")

            conn.sendall(result.encode())
            conn.close()
    except KeyboardInterrupt:
        print("\nServer shutting down...")
    finally:
        server_socket.close()
        print("Server socket closed.")

if __name__ == "__main__":
    start_tcp_server()
