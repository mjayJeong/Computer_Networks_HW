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

def start_udp_server():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server_address = ('127.0.0.1', 12000)
    server_socket.bind(server_address)

    print("The server is ready to receive.")
    print(f"UDP Server is running on {server_address[0]}:{server_address[1]}...")

    try:
        while True:
            message, client_address = server_socket.recvfrom(1024)
            expression = message.decode()
            print(f"Received expression from {client_address}: {expression}")

            result = evaluate_expression(expression)
            print(f"Calculated result: {result}")

            server_socket.sendto(result.encode(), client_address)
            print(f"Result sent to {client_address}")

    except KeyboardInterrupt:
        print("\nServer shutting down...")
    finally:
        server_socket.close()
        print("Server socket closed.")

if __name__ == "__main__":
    start_udp_server()
