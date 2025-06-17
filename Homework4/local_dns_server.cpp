#include <winsock2.h>
#include <fstream>
#include <iostream>
#include <string>
#pragma comment(lib, "ws2_32.lib")

std::string searchLocalDNS(const std::string& domain) {
    std::ifstream file("local_dns.txt");
    std::string record, ip;
    while (file >> record >> ip) {
        if (record == domain) {
            std::cout << "[Local DNS] Match found locally: " << domain << " -> " << ip << "\n";
            return ip;
        }
    }
    return "";
}

std::string queryGlobalDNS(const std::string& domain) {
    std::cout << "[Local DNS] Not found in local. Querying Global DNS...\n";
    SOCKET globalSocket = socket(AF_INET, SOCK_STREAM, 0);
    sockaddr_in globalAddr;
    globalAddr.sin_family = AF_INET;
    globalAddr.sin_port = htons(9000);
    globalAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    memset(&(globalAddr.sin_zero), 0, 8);

    if (connect(globalSocket, (sockaddr*)&globalAddr, sizeof(globalAddr)) == SOCKET_ERROR) {
        std::cerr << "[Local DNS ERROR] Failed to connect to Global DNS\n";
        closesocket(globalSocket);
        return "Not Found";
    }

    send(globalSocket, domain.c_str(), domain.size(), 0);
    char recvBuf[1024] = {};
    int recvLen = recv(globalSocket, recvBuf, sizeof(recvBuf) - 1, 0);
    closesocket(globalSocket);

    if (recvLen <= 0) return "Not Found";
    std::string result(recvBuf, recvLen);
    std::cout << "[Local DNS] Received from Global DNS: " << result << "\n";
    return result;
}

int main() {
    WSADATA wsaData; WSAStartup(MAKEWORD(2, 2), &wsaData);
    SOCKET udpSocket = socket(AF_INET, SOCK_DGRAM, 0);
    sockaddr_in localAddr;
    localAddr.sin_family = AF_INET;
    localAddr.sin_port = htons(8000);
    localAddr.sin_addr.s_addr = INADDR_ANY;
    bind(udpSocket, (sockaddr*)&localAddr, sizeof(localAddr));

    char recvBuffer[1024]; sockaddr_in clientAddr;
    int clientAddrLen = sizeof(clientAddr);

    std::cout << "[Local DNS Server] Ready on UDP port 8000...\n";

    while (true) {
        int recvLen = recvfrom(udpSocket, recvBuffer, sizeof(recvBuffer) - 1, 0, (sockaddr*)&clientAddr, &clientAddrLen);
        recvBuffer[recvLen] = '\0';
        std::string domain(recvBuffer);
        std::cout << "\n[Client Request] Domain: " << domain << "\n";

        std::string resolvedIP = searchLocalDNS(domain);
        if (resolvedIP.empty()) resolvedIP = queryGlobalDNS(domain);

        sendto(udpSocket, resolvedIP.c_str(), resolvedIP.size(), 0, (sockaddr*)&clientAddr, clientAddrLen);
    }
    closesocket(udpSocket);
    WSACleanup();
    return 0;
}