import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends Frame {
    private static final String SERVER_ADDRESS = "192.168.110.124";
    private static final int SERVER_PORT = 8080;
    private TextArea receivedMessagesTextArea;
    private TextField sendMessageTextField;
    private PrintWriter out;

    public ClientGUI() {
        setTitle("Chat Client");
        setSize(400, 300);
        setLayout(new BorderLayout());

        receivedMessagesTextArea = new TextArea();
        receivedMessagesTextArea.setEditable(false);
        receivedMessagesTextArea.setBackground(Color.LIGHT_GRAY); 
        add(receivedMessagesTextArea, BorderLayout.CENTER);

        Panel bottomPanel = new Panel(new BorderLayout());
        sendMessageTextField = new TextField();
        bottomPanel.add(sendMessageTextField, BorderLayout.CENTER);
        Button sendButton = new Button("Send");
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

       
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to the chat server!");
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        receivedMessagesTextArea.append(serverResponse + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

       
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        
        sendMessageTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    out.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void sendMessage() {
        String message = sendMessageTextField.getText();
        out.println(message);
        sendMessageTextField.setText("");
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}
