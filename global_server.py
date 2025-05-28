import socket
import threading
import cv2
import struct
import os

def handle_client(conn, addr):
    print(f"[Global] Connected from: {addr}")
    
    try:
        filename = conn.recv(1024).decode().strip()
        print(f"[Global] Requested: {filename}")

        if filename.lower() == "live":
            cap = cv2.VideoCapture(0)  
            if not cap.isOpened():
                print("[Global] Failed to open live stream.")
                conn.close()
                return
            print("[Global] Live streaming started.")
        else:
            video_path = os.path.join("videos", filename)
            if not os.path.exists(video_path):
                print(f"[Global] File not found: {video_path}")
                conn.close()
                return

            print("[Global] Sending mp4 file ...")

            try:
                def preview():
                    cap = cv2.VideoCapture(video_path)
                    while cap.isOpened():
                        ret, frame = cap.read()
                        if not ret:
                            break
                        resized = cv2.resize(frame, (0, 0), fx=0.3, fy=0.5)
                        cv2.imshow(f"Global Server - {filename}", resized)
                        if cv2.waitKey(30) == 27:
                            break
                    cap.release()
                    cv2.destroyAllWindows()
                    print("[Global] Streaming finished.")

                def send_file():
                    with open(video_path, 'rb') as f:
                        while True:
                            chunk = f.read(4096)
                            if not chunk:
                                break
                            conn.sendall(chunk)
                    print("[Global] File transmission finished.")
                    conn.close()

                t1 = threading.Thread(target=send_file)
                t1.start()
                preview()
                t1.join()

            except Exception as e:
                print(f"[Global] Error: {e}")
                conn.close()
    except Exception as e:
        print(f"[Global] Error with client {addr}: {e}")
        conn.close()


def main():
    HOST = '0.0.0.0'
    PORT = 9000
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((HOST, PORT))
    s.listen(5)
    print(f"[Global] Listening on port {PORT}...")

    while True:
        conn, addr = s.accept()
        threading.Thread(target=handle_client, args=(conn, addr)).start()

if __name__ == "__main__":
    main()
