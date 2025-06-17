// 2020312429 Minjae Jeong

#include <winsock2.h>
#include <iostream>
#include <string>
#pragma comment(lib, "ws2_32.lib")

int main() {
    WSADATA wsa; WSAStartup(MAKEWORD(2,2), &wsa);
    SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
    sockaddr_in server;
    server.sin_family = AF_INET;
    server.sin_port = htons(12000);
    server.sin_addr.s_addr = inet_addr("127.0.0.1");

    connect(sock, (sockaddr*)&server, sizeof(server));

    char buf[4096];
    int len;

    std::string input;
    while (true) {
        std::cout << "Enter expression or HTTP GET request (type 'exit' to quit): ";
        std::getline(std::cin, input);
        if (input == "exit") break;

        input += '\n';  
        send(sock, input.c_str(), input.length(), 0);

        len = recv(sock, buf, sizeof(buf) - 1, 0);
        if (len > 0) {
            buf[len] = '\0';
            std::cout << "Server response:\n" << buf << "\n";
        }
    }

    closesocket(sock); WSACleanup();
    return 0;
}
