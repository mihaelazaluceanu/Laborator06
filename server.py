import socket

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

server_address = ('0.0.0.0', 5000)
server_socket.bind(server_address)

server_socket.listen(1)

while True:
        connection, client_address = server_socket.accept()
        print(f'Connection from {client_address}')
        connection.sendall(b'hello\n')
