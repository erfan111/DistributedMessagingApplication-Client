package dev2dev.textclient;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextClient
        extends JFrame
        implements MessageProcessor {
    private SipLayer sipLayer;

    private JTextField fromAddress;
    private JLabel receivedLbl;
    private JTextArea receivedMessages;
    private JTextField authoritativeServer;
    private JLabel authoritativeServerLbl;
    private JScrollPane receivedScrollPane;
    private JButton sendBtn;
    private JButton deRegisterBtn;
    private JLabel sendLbl;
    private JTextField sendMessages;
    private JTextField toAddress;
    private JLabel toLbl;

    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
            System.exit(-1);
        }

        try {
            String username = args[0];
            int port = Integer.parseInt(args[1]);
            String ip = InetAddress.getLocalHost().getHostAddress();

            SipLayer sipLayer = new SipLayer(username, ip, port);
            TextClient tc = new TextClient(sipLayer);
            sipLayer.setMessageProcessor(tc);

            tc.setVisible(true);
        } catch (Throwable e) {
            System.out.println("Problem initializing the SIP stack.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void printUsage() {
        System.out.println("Syntax:");
        System.out.println("  java -jar textclient.jar <username> <port>");
        System.out.println("where <username> is the nickname of this user");
        System.out.println("and <port> is the port number to use. Usually 5060 if not used by another process.");
        System.out.println("Example:");
        System.out.println("  java -jar textclient.jar snoopy71 5061");
    }

    public TextClient(SipLayer sip) {
        super();
        sipLayer = sip;
        initWindow();
        String from = "sip:" + sip.getUsername() + "@" + sip.getHost() + ":" + sip.getPort();
        this.fromAddress.setText(from);
    }

    private void initWindow() {
        receivedLbl = new JLabel();
        sendLbl = new JLabel();
        sendMessages = new JTextField();
        receivedScrollPane = new JScrollPane();
        receivedMessages = new JTextArea();
        authoritativeServer = new JTextField();
        authoritativeServerLbl = new JLabel();
        JLabel fromLbl = new JLabel();
        fromAddress = new JTextField();
        toLbl = new JLabel();
        toAddress = new JTextField();
        sendBtn = new JButton();
        deRegisterBtn = new JButton();

        getContentPane().setLayout(null);

        setTitle(sipLayer.getUsername());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });

        receivedLbl.setText("Received Messages:");
        receivedLbl.setAlignmentY(0.0F);
        receivedLbl.setPreferredSize(new java.awt.Dimension(25, 100));
        getContentPane().add(receivedLbl);
        receivedLbl.setBounds(5, 0, 150, 20);
        authoritativeServerLbl.setBounds(5,150,85,20);
        getContentPane().add(authoritativeServerLbl);
        authoritativeServerLbl.setText("My Server:");
        authoritativeServer.setBounds(95,150,170,20);
        getContentPane().add(authoritativeServer);
        authoritativeServer.setText("Not Connected");
        authoritativeServer.setEditable(false);
        sendLbl.setText("Send Message:");
        getContentPane().add(sendLbl);
        sendLbl.setBounds(5, 170, 120, 20);

        getContentPane().add(sendMessages);
        sendMessages.setBounds(5, 190, 270, 20);

        receivedMessages.setAlignmentX(0.0F);
        receivedMessages.setEditable(false);
        receivedMessages.setLineWrap(true);
        receivedMessages.setWrapStyleWord(true);
        receivedScrollPane.setViewportView(receivedMessages);
        receivedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        getContentPane().add(receivedScrollPane);
        receivedScrollPane.setBounds(5, 20, 270, 130);

        fromLbl.setText("From:");
        getContentPane().add(fromLbl);
        fromLbl.setBounds(5, 220, 55, 15);

        getContentPane().add(fromAddress);
        fromAddress.setBounds(60, 220, 235, 20);
        fromAddress.setEditable(false);

        toLbl.setText("To:");
        getContentPane().add(toLbl);
        toLbl.setBounds(5, 245, 55, 15);

        getContentPane().add(toAddress);
        toAddress.setBounds(60, 245, 235, 21);

        sendBtn.addActionListener(evt -> {
            if (sipLayer.getIsRegistered()){
                sendBtnActionPerformed();
            }else{
                registerBtnActionPerformed();
            }
        });

        getContentPane().add(sendBtn);
        sendBtn.setBounds(190, 275, 100, 25);

        deRegisterBtn.addActionListener(evt -> {
            deRegisterBtnActionPerformed();
        });

        getContentPane().add(deRegisterBtn);
        deRegisterBtn.setBounds(100, 275, 85, 25);
        deRegisterBtn.setBackground(Color.RED);

        if (sipLayer.getIsRegistered()){
            setStateNotRegistered();
        }else{
            setStateNotRegistered();
        }

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 300) / 2, (screenSize.height - 340) / 2, 300, 340);

    }

    private void sendBtnActionPerformed() {

        try {
            String to = this.toAddress.getText();
            String message = this.sendMessages.getText();
            if(!message.equals(""))
            {
                if (!to.contains("@"))
                    to = "sip:" + to + "@" + sipLayer.serverRegistered.toString();
                sipLayer.sendMessage(to, message);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            this.receivedMessages.append("ERROR sending message: " + e.getMessage() + "\n");
        }

    }

    private void registerBtnActionPerformed() {

        try {
            String serverAddress = this.toAddress.getText();
            sipLayer.CallregisterRequest(serverAddress);
        } catch (Throwable e) {
            e.printStackTrace();
            this.receivedMessages.append("ERROR register" + e.getMessage() + "\n");
        }

    }

    private void deRegisterBtnActionPerformed() {

        try {
            sipLayer.CallDeRegisterRequest();
        } catch (Throwable e) {
            e.printStackTrace();
            this.receivedMessages.append("ERROR deregister" + e.getMessage() + "\n");
        }

    }

    public void processMessage(String sender, String message) {
        this.receivedMessages.append("From " +
                sender + ": " + message + "\n");
    }

    public void processError(String errorMessage) {
        this.receivedMessages.append("ERROR: " +
                errorMessage + "\n");
    }

    public void processInfo(String infoMessage) {
        this.receivedMessages.append(
                infoMessage + "\n");
    }

    public void processClientRegistration(boolean status){
        if(status)
            setStateRegistered();
        else
            setStateNotRegistered();
    }

    private void setStateRegistered(){
        sendBtn.setText("Send");
        deRegisterBtn.setText("deReg");
        deRegisterBtn.setVisible(true);
        toLbl.setText("To:");
        sendMessages.setVisible(true);
        sendMessages.setText(sipLayer.getUsername());
        sendLbl.setVisible(true);
        receivedLbl.setVisible(true);
        receivedScrollPane.setVisible(true);
        toAddress.setText("");
        authoritativeServer.setText(sipLayer.serverRegistered.toString());
    }

    private void setStateNotRegistered(){
        sendBtn.setText("Register");
        toLbl.setText("Server:");
        sendMessages.setVisible(false);
        sendLbl.setVisible(false);
        receivedLbl.setVisible(false);
        receivedScrollPane.setVisible(false);
        toAddress.setText("IP:PORT");
        deRegisterBtn.setVisible(false);
        authoritativeServer.setText("Not Connected");
    }

}
