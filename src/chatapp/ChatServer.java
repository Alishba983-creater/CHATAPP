package chatapp;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static AVLTree clients = new AVLTree(); // AVL Tree for client management
    private static Map<String, Queue> messageQueues = new HashMap<>(); // Map to hold message queues for offline clients

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server started at port 5000.");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    // Register a client
    public static synchronized void registerClient(String name, ClientHandler handler) {
        clients.insertClient(name, handler);

        // Check if there are queued messages for the client
        Queue clientQueue = messageQueues.get(name);
        if (clientQueue != null) {
            while (!clientQueue.isEmpty()) {
                handler.sendMessage(clientQueue.dequeue()); // Send all messages in the queue
            }
            messageQueues.remove(name); // Remove the queue after sending messages
        }
    }

    // Remove a client
    public static synchronized void removeClient(String name) {
        clients.deleteClient(name);
    }

    // Get a client by name
    public static synchronized ClientHandler getClient(String name) {
        ClientNode node = clients.searchClient(name);
        return node != null ? node.handler : null;
    }

    // Queue a message for a recipient if they're offline
    public static synchronized void queueMessage(String recipientName, String message) {
        Queue recipientQueue = messageQueues.getOrDefault(recipientName, new Queue());
        recipientQueue.enqueue(message);
        messageQueues.put(recipientName, recipientQueue);
    }

    // Dequeue all messages for a specific recipient
    public static synchronized List<String> dequeueAllMessages(String recipientName) {
        List<String> messages = new ArrayList<>();
        Queue queue = messageQueues.get(recipientName);
        if (queue != null) {
            while (!queue.isEmpty()) {
                messages.add(queue.dequeue());
            }
            messageQueues.remove(recipientName); // Remove the queue after processing
        }
        return messages;
    }
}

// ClientHandler class remains unchanged, except for minor adjustments in `run()` to integrate with the Queue.
class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String clientName;
    private String recipientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error initializing client handler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Step 1: Receive client's name
            clientName = input.readUTF();
            ChatServer.registerClient(clientName, this);
            System.out.println(clientName + " has joined the chat.");

            // Step 2: Receive recipient's name
            recipientName = input.readUTF();

            // Step 3: Communication loop
            while (true) {
                String message = input.readUTF();

                if (message.equalsIgnoreCase("exit")) {
                    output.writeUTF("Chat ended.");
                    ChatServer.removeClient(clientName);
                    break;
                }

                ClientHandler recipientHandler = ChatServer.getClient(recipientName);
                if (recipientHandler != null) {
                    recipientHandler.sendMessage(clientName + ": " + message);
                } else {
                    ChatServer.queueMessage(recipientName, clientName + ": " + message);
                    output.writeUTF("Message queued for " + recipientName);
                }
            }
        } catch (IOException e) {
            System.out.println("Connection closed for " + clientName);
            ChatServer.removeClient(clientName);
        }
    }

    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Error sending message to " + clientName);
        }
    }
}
