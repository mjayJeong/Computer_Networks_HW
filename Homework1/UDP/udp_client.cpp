// 2020312429 Minjae Jeong

#include <iostream>
#include <fstream>
#include <cstring>
#include <winsock2.h>

#pragma comment(lib, "ws2_32.lib") 

#define SERVER_IP "127.0.0.1" 
#define SERVER_PORT 12000   
#define BUFFER_SIZE 1024  

using namespace std;

int main() 
{
    WSADATA wsaData;
    SOCKET sock;
    sockaddr_in serverAddress;
    char response[BUFFER_SIZE];
    string inputExpression;

    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        cerr << "Error: WSAStartup failed. Code: " << WSAGetLastError() << endl;
        return 1;
    }

    sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sock == INVALID_SOCKET) {
        cerr << "Error: Socket creation failed. Code: " << WSAGetLastError() << endl;
        WSACleanup();
        return 1;
    }

    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(SERVER_PORT);
    serverAddress.sin_addr.s_addr = inet_addr(SERVER_IP);

    cout << "Type the expression here: ";
    getline(cin, inputExpression);

    int sendResult = sendto(sock, inputExpression.c_str(), inputExpression.length(), 0,
                            (struct sockaddr*)&serverAddress, sizeof(serverAddress));
    if (sendResult == SOCKET_ERROR) {
        cerr << "Error: Sending data failed. Code: " << WSAGetLastError() << endl;
        closesocket(sock);
        WSACleanup();
        return 1;
    }

    int serverAddressSize = sizeof(serverAddress);
    int recvResult = recvfrom(sock, response, BUFFER_SIZE, 0,
                              (struct sockaddr*)&serverAddress, &serverAddressSize);
    if (recvResult == SOCKET_ERROR) {
        cerr << "Error: Receiving data failed. Code: " << WSAGetLastError() << endl;
        closesocket(sock);
        WSACleanup();
        return 1;
    }
    response[recvResult] = '\0';

    cout << "Server response: " << response << endl;

    ofstream resultFile("result.txt");
    if (resultFile.is_open()) {
        resultFile << "Expression: " << inputExpression << "\n";
        resultFile << "Result: " << response << "\n";
        resultFile.close();
        cout << "Result saved to result.txt" << endl;
    } else {
        cerr << "Error: Unable to open result.txt" << endl;
    }

    closesocket(sock);
    WSACleanup();
    return 0;
}
