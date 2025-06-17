import socket
d=open("domain.txt").readlines()
r=open("result.txt","a")
for i,l in enumerate(d):
 s=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
 s.sendto(l.encode(),("127.0.0.1",8000))
 m,_=s.recvfrom(1024)
 r.write(f"Scenario {i+3}\t{m.decode()}\n")