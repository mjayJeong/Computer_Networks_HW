import socket
import struct
import cv2
import numpy as np

SERVER_IP = '127.0.0.1'
SERVER_PORT = 8000

def recv_all(sock, size):
    data = b''
    while len(data) < size:
        packet = sock.recv(size - len(data))
        if not packet:
            return None
        data += packet
    return data

def main():
    sock = socket.socket()
    sock.connect((SERVER_IP, SERVER_PORT))
    print("[Client 3] Connected to Local Server")

    filename = input("Enter video name (ex: live): ").strip()
    sock.sendall((filename + "\n").encode())

    first_frame = None
    writer = None
    save_name = f"client3_{filename}.avi"

    while True:
        size_data = recv_all(sock, 4)
        if not size_data:
            break
        frame_size = struct.unpack('>I', size_data)[0]

        frame_data = recv_all(sock, frame_size)
        if not frame_data:
            break

        frame_array = bytearray(frame_data)
        frame = cv2.imdecode(np.frombuffer(frame_array, dtype=np.uint8), cv2.IMREAD_COLOR)
        if frame is None:
            continue

        if writer is None:
            h, w = frame.shape[:2]
            writer = cv2.VideoWriter(save_name, cv2.VideoWriter_fourcc(*'MJPG'), 30, (w, h))

        writer.write(frame)
        cv2.imshow(f"Client 3 Streaming: {filename}", frame)
        if cv2.waitKey(1) == 27:
            break

    if writer:
        writer.release()
    sock.close()
    cv2.destroyAllWindows()
    print(f"[Client 3] Stream saved as {save_name}")

if __name__ == "__main__":
    main()
