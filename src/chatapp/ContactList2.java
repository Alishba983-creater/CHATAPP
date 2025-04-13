package chatapp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


public class ContactList2 {

    private JFrame f;

    public ContactList2(LinkedList<Chat> chats,ChatApp2 c) {
        // Create a list of contacts
        LinkedList<ContactNode> list = new LinkedList<>();
        list.add(new ContactNode("Aslab", 9999));
        list.add(new ContactNode("Ahmed", 1234));
        list.add(new ContactNode("Sara", 5678));
        list.add(new ContactNode("Imran", 2345));
        list.add(new ContactNode("Maryam", 6789));
        list.add(new ContactNode("Ali", 1122));
        list.add(new ContactNode("Noor", 3344));
        list.add(new ContactNode("Zain", 5566));
        list.add(new ContactNode("Ayesha", 7788));

        // Setup JFrame
        f = new JFrame("Chat Application");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setLayout(new BorderLayout());

        // Top panel with logo and app name
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(800, 50));
        topPanel.setBackground(Color.PINK);
        ImageIcon icon = createScaledImageIcon("contacts.png", 40, 40);
        topPanel.add(new JLabel(icon));
        JLabel txt = new JLabel("CONTACTS", SwingConstants.CENTER);
        Font font = new Font("Roboto", Font.BOLD, 35);
        txt.setFont(font);
        topPanel.add(txt);

        // Bottom panel with GridLayout for the buttons
        JPanel bottomPanel = new JPanel(new GridLayout(0, 1, 0, 0));

        // Add buttons for all contacts
        for (ContactNode contactNode : list) {
            JButton contactButton = new JButton(contactNode.name);
            ImageIcon icon1 = createScaledImageIcon("contact.png", 30, 30); // Adjust icon size
            contactButton.setIcon(icon1);
            contactButton.addActionListener(e -> {
                showContactPopup(contactNode, chats,c);
            });
            contactButton.setHorizontalAlignment(SwingConstants.LEFT);
            contactButton.setIconTextGap(10);
            contactButton.setBackground(Color.white);
            contactButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            contactButton.setFont(new Font("Arial", Font.BOLD, 30));
            contactButton.setFocusable(false);
            bottomPanel.add(contactButton);
        }

        // Main panel (wraps top and bottom panels)
        JPanel main = new JPanel(new BorderLayout());
        main.add(topPanel, BorderLayout.NORTH);
        main.add(bottomPanel, BorderLayout.CENTER);

        // JScrollPane to make the content scrollable
        JScrollPane scrollPane = new JScrollPane(main, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.GRAY);

        // Adding components to the frame
        f.add(scrollPane, BorderLayout.CENTER);

        // Display the frame
        f.setVisible(true);
    }

    private ImageIcon createScaledImageIcon(String path, int width, int height) {
        // Create a scaled image icon
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(path));
        Image scaledImage = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

   public void showContactPopup(ContactNode contact, LinkedList<Chat> chats,ChatApp2 c) {
    // Create a custom dialog
    JDialog dialog = new JDialog(f, "Contact Details", true);
    dialog.setSize(350, 200);
    dialog.setLayout(new BorderLayout());
    dialog.setLocationRelativeTo(f);

    // Set dialog background color
    dialog.getContentPane().setBackground(new Color(245, 245, 255)); // Light lavender

    // Contact info panel
    JPanel infoPanel = new JPanel(new GridLayout(2, 1));
    infoPanel.setBackground(Color.white); // Light blue
    infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the panel
    JLabel nameLabel = new JLabel("Name: " + contact.name);
    JLabel numberLabel = new JLabel("Number: " + contact.number);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
    nameLabel.setForeground(new Color(50, 50, 120)); // Deep blue
    numberLabel.setFont(new Font("Arial", Font.BOLD, 16));
    numberLabel.setForeground(new Color(80, 80, 150)); // Medium blue
    infoPanel.add(nameLabel);
    infoPanel.add(numberLabel);

    // Button panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.white); // Match dialog background
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the buttons

    JButton sendMessageButton = new JButton("Send Message");
    sendMessageButton.setFont(new Font("Arial", Font.BOLD, 14));
    sendMessageButton.setBackground(new Color(100, 200, 100)); // Light green
    sendMessageButton.setForeground(Color.WHITE); // White text
    sendMessageButton.setFocusPainted(false);
    sendMessageButton.setBorder(BorderFactory.createLineBorder(new Color(50, 150, 50), 2)); // Green border
    sendMessageButton.addActionListener(e -> {
        // Pass the contact to the ChatApp
        boolean contactExists = false;
        for (Chat chat : chats) {
            if (chat.c.name.equals(contact.name) && chat.c.number == contact.number) {
                contactExists = true;
                break;
            }
        }
        if (!contactExists) {
            chats.add(new Chat(contact));
        }

        dialog.dispose();
        f.dispose();
       try {
         
        c.createSocket();
        c.GUI();
        
        } catch (IOException ex) {
            Logger.getLogger(ContactList.class.getName()).log(Level.SEVERE, null, ex);
        }
    });

    buttonPanel.add(sendMessageButton);

    // Add panels to the dialog
    dialog.add(infoPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    // Display the dialog
    dialog.setVisible(true);
}

}