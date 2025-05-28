#include <iostream>
#include <fstream>
#include <winsock2.h>
#include <windows.h>

#pragma comment(lib, "ws2_32.lib")

#define SERVER_IP "127.0.0.1"
#define SERVER_PORT 8000
#define BUFFER_SIZE 4096

int main() {
    WSADATA wsaData;
    SOCKET sock;
    struct sockaddr_in server;
    char buffer[BUFFER_SIZE];
    int bytesReceived;

    WSAStartup(MAKEWORD(2,2), &wsaData);
    sock = socket(AF_INET, SOCK_STREAM, 0);
    server.sin_family = AF_INET;
    server.sin_port = htons(SERVER_PORT);
    server.sin_addr.s_addr = inet_addr(SERVER_IP);

    if (connect(sock, (struct sockaddr*)&server, sizeof(server)) != 0) {
        std::cerr << "[Client 2] Connection failed.\n";
        return 1;
    }

    std::string videoName;
    std::cout << "Enter video name (ex: Video_2024): ";
    std::getline(std::cin, videoName);
    videoName += ".mp4";

    std::string localSave = videoName.substr(0, videoName.find(".")) + "_client2.mp4";

    std::string request = videoName + "\n";
    send(sock, request.c_str(), request.length(), 0);

    FILE* fp = fopen(localSave.c_str(), "wb");
    if (!fp) {
        std::cerr << "[Client 2] Cannot open file to save.\n";
        return 1;
    }

    std::cout << "[Client 2] Receiving and saving to " << localSave << "...\n";

    long total = 0;
    while ((bytesReceived = recv(sock, buffer, BUFFER_SIZE, 0)) > 0) {
        fwrite(buffer, 1, bytesReceived, fp);
        total += bytesReceived;
        std::cout << "\r[Receiving] " << total / 1024 << " KB";
        fflush(stdout);
    }

    fclose(fp);
    closesocket(sock);
    WSACleanup();

    std::cout << "\n[Client 2] Done.\n";
    ShellExecute(NULL, "open", localSave.c_str(), NULL, NULL, SW_SHOWNORMAL);

    return 0;
}
