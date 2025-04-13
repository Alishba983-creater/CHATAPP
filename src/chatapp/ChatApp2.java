package chatapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.border.Border;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class ChatApp2 implements ActionListener {
    private static JPanel panel1;
    private static Box vertical;
    private static ChatApp instance;
    private JPanel currentPanel;
    private static JPanel txtPanel;
    private JButton fixedButton;
    private static JFrame f;
    private LinkedList<Chat> chats;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;
    private static final String CLIENT_NAME = "Alishba"; 

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public ChatApp2(LinkedList<Chat> chats) throws IOException {
        if (chats == null) {
        this.chats = new LinkedList<>();
    } else {
        this.chats = chats;
    }
}
public void GUI() throws IOException{
        f = new JFrame("Alishba");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setLayout(new BorderLayout());
 vertical = Box.createVerticalBox();
        Font fancyFont;
        try {
            fancyFont = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Users\\Family\\Documents\\NetBeansProjects\\ChatApp\\src\\chatapp\\Pacifico-Regular.ttf")).deriveFont(50f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(fancyFont);
        } catch (IOException | FontFormatException e) {
            fancyFont = new Font("Serif", Font.BOLD, 50); // Fallback font
        }

        panel1 = new JPanel(new BorderLayout());
        panel1.setBackground(Color.PINK);

        ImageIcon icon = createScaledImageIcon("chat.png", 100, 100);
        JLabel imgLabel = new JLabel(icon);
        JLabel txt = new JLabel("CHATTERLY", SwingConstants.CENTER);
        txt.setFont(fancyFont);

        JPanel mid = new JPanel(new GridBagLayout());
        mid.setBackground(Color.PINK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mid.add(imgLabel, gbc);
        gbc.gridy = 1;
        mid.add(txt, gbc);

        ImageIcon buttonIcon = createScaledImageIcon("addchat.png", 40, 40);
        fixedButton = new JButton(buttonIcon);
        fixedButton.setBorderPainted(false);
        fixedButton.setContentAreaFilled(false);
        fixedButton.setFocusPainted(false);
        fixedButton.setPreferredSize(new Dimension(100, 100));
        fixedButton.addActionListener(this);

        JPanel b = new JPanel(new BorderLayout());
        b.setBackground(Color.PINK);
        b.add(fixedButton, BorderLayout.LINE_END);

        panel1.add(mid, BorderLayout.CENTER);
        panel1.add(b, BorderLayout.SOUTH);

        showPanel(panel1);

      

        JButton btnPanel1 = createStyledButton("Home", panel1);
        JPanel buttonPanel = new JPanel(new MigLayout("wrap 1", "[]", "[][][]"));
        buttonPanel.setBackground(Color.cyan);
        buttonPanel.add(btnPanel1);

        for (Chat node : chats) {
            JPanel chatPanel = createChatPanel(node);
            JButton btnPanel = createStyledButton(node.c.name, chatPanel);
            buttonPanel.add(btnPanel);
        }

        f.add(buttonPanel, BorderLayout.WEST);
        JScrollPane scrollPane = new JScrollPane(buttonPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.GRAY);
        f.add(scrollPane, BorderLayout.WEST);
        f.setVisible(true);
            f.addWindowListener(new WindowAdapter() {
    public void windowClosing(WindowEvent e) {
        closeConnection(); // Close socket and streams when the window is closed
    }
});

    }
     void createSocket() throws IOException
    {
                // Network setup
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        // Send predefined client name to server
        output.writeUTF(CLIENT_NAME);

        // Start receiving messages in a separate thread
        new Thread(this::receiveMessages).start();
    }

private void closeConnection() {
    try {
        if (input != null) {
            input.close();
        }
        if (output != null) {
            output.close();
        }
        if (socket != null) {
            socket.close();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    private void receiveMessages() {
        try {
            while (true) {
                String msg= input.readUTF();

                JPanel p2 = formatLabel(msg);  
                JPanel right = new JPanel(new BorderLayout());
                right.setBackground(Color.WHITE);
                right.add(p2, BorderLayout.LINE_END);
                vertical.add(right);
                vertical.add(Box.createVerticalStrut(15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private JPanel createChatPanel(Chat node) throws IOException {
        JPanel chatPanel = new JPanel(new BorderLayout());
        output.writeUTF(node.c.name);
        txtPanel = new JPanel();
        txtPanel.setLayout(new BorderLayout());
        txtPanel.setBackground(Color.WHITE);
        vertical = Box.createVerticalBox();

        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(300, 30));

        JButton send = new JButton();
        ImageIcon buttonIcon = createScaledImageIcon("send.png", 20, 30);
        send.setIcon(buttonIcon);
        send.setBorderPainted(false);
        send.setContentAreaFilled(false);
        send.setFocusPainted(false);
        send.setPreferredSize(new Dimension(50, 50));
        send.addActionListener(e -> {
            
            String msgText = txt.getText();
            if (!msgText.isEmpty()) {


                JPanel p2 = formatLabel(msgText);  
                JPanel right = new JPanel(new BorderLayout());
                right.setBackground(Color.WHITE);
                right.add(p2, BorderLayout.LINE_END);
                vertical.add(right);
                vertical.add(Box.createVerticalStrut(15));

                try {
                    
                    output.writeUTF(msgText);  // Send encoded message
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                txtPanel.add(vertical, BorderLayout.PAGE_START);
                txtPanel.revalidate();
                txtPanel.repaint();
            }
            txt.setText("");  // Clear the input field
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomPanel.add(txt, BorderLayout.CENTER);
        bottomPanel.add(send, BorderLayout.EAST);
        bottomPanel.setBackground(Color.pink);

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton back = new JButton();
        ImageIcon backIcon = createScaledImageIcon("back.png", 40, 40);
        back.setIcon(backIcon);
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setFocusPainted(false);
        back.setPreferredSize(new Dimension(50, 50));
        back.addActionListener(e -> showPanel(panel1));
        topPanel.add(back, BorderLayout.WEST);
        topPanel.setBackground(Color.pink);

        JScrollPane scrollPane = new JScrollPane(txtPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(topPanel, BorderLayout.NORTH);
        chatPanel.add(bottomPanel, BorderLayout.SOUTH);

        return chatPanel;
    }

    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(37, 211, 102));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(0, 15, 0, 50));

        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));
        time.setBackground(new Color(37, 211, 102));
        time.setBorder(new EmptyBorder(0, 15, 0, 50));
        time.setOpaque(true);
        panel.add(time);

        return panel;
    }

    private ImageIcon createScaledImageIcon(String path, int width, int height) {
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(path));
        Image scaledImage = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private void showPanel(JPanel panel) {
        if (currentPanel != null) {
            f.remove(currentPanel);
        }
        currentPanel = panel;
        f.add(currentPanel, BorderLayout.CENTER);
        f.revalidate();
        f.repaint();
    }

    private JButton createStyledButton(String text, JPanel targetPanel) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        Color buttonColor = new Color(0, 128, 128);
        Color hoverColor = new Color(0, 180, 180);

        button.setBackground(buttonColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.PLAIN, 18));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setOpaque(true);

        button.addActionListener(e -> {
            showPanel(targetPanel);
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonColor);
            }
        });

        return button;
       
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fixedButton) {
            new ContactList2(chats,this);
            f.setVisible(false);
        }
    }

    public static void main(String[] args) throws IOException {
        LinkedList<Chat> chats = new LinkedList<>();
        ChatApp2 chatApp2 = new ChatApp2(chats);
        chatApp2.GUI();
    }
}


