#include <stdio.h>
#include <stdlib.h>
#include <string.h>
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
    FILE *fp;

    if (WSAStartup(MAKEWORD(2,2), &wsaData) != 0) {
        printf("WSAStartup failed.\n");
        return 1;
    }

    sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCKET) {
        printf("Could not create socket.\n");
        return 1;
    }

    server.sin_addr.s_addr = inet_addr(SERVER_IP);
    server.sin_family = AF_INET;
    server.sin_port = htons(SERVER_PORT);

    if (connect(sock, (struct sockaddr *)&server, sizeof(server)) < 0) {
        printf("Connection failed.\n");
        return 1;
    }
    printf("[Client 1] Connected to Local Server.\n");

    char base_name[256];
    printf("Enter video name (ex: Video_2025) : ");
    fgets(base_name, sizeof(base_name), stdin);
    base_name[strcspn(base_name, "\n")] = 0;

    char video_name[300];
    sprintf(video_name, "%s.mp4", base_name);

    char full_request[300];
    sprintf(full_request, "%s\n", video_name);  
    send(sock, full_request, strlen(full_request), 0);

    char filename[300];
    sprintf(filename, "%s_client1.mp4", base_name); 
    fp = fopen(filename, "wb");
    if (fp == NULL) {
        printf("Cannot open file to write.\n");
        return 1;
    }
    printf("[Client 1] Receiving and saving to %s...\n", filename);
    long total = 0;

    while ((bytesReceived = recv(sock, buffer, BUFFER_SIZE, 0)) > 0) {
        fwrite(buffer, 1, bytesReceived, fp);
        total += bytesReceived;
        printf("\r[Receiving] %ld KB", total / 1024);
        fflush(stdout);
    }

    fclose(fp);
    closesocket(sock);
    WSACleanup();

    printf("\n[Client 1] Done.\n");

    ShellExecute(NULL, "open", filename, NULL, NULL, SW_SHOWNORMAL);

    return 0;
}
