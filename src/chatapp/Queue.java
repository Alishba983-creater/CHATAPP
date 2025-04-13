
package chatapp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


// Custom Queue class
public class Queue {
    private LinkedList<String> messages;

    public Queue() {
        this.messages = new LinkedList<>();
    }

    // Add a message to the queue
    public synchronized void enqueue(String message) {
        messages.add(message);
    }

    // Get and remove the first message in the queue
    public synchronized String dequeue() {
        return messages.isEmpty() ? null : messages.poll();
    }

    // Check if the queue is empty
    public synchronized boolean isEmpty() {
        return messages.isEmpty();
    }

    // Get all the messages in the queue
    public synchronized List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }
}
