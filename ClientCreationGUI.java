import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class ClientCreationGUI implements ActionListener {
    private JFrame frame;

    private JButton connectButton, cancelButton;

    GraphicsDevice device;

    JTextField ip;
    JTextField port;
    JTextField name;

    ClientCreationGUI() {
        Font helloHeadline = new Font("", Font.PLAIN, 0);
        try {
            helloHeadline = Font.createFont(Font.TRUETYPE_FONT, new File("resources/HelloHeadline.ttf"))
                    .deriveFont(36f);
        } catch (Exception e) {
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(helloHeadline);

        frame = new JFrame("Create Server");

        frame.setUndecorated(true);
        frame.setResizable(false);

        connectButton = new JButton("Connect");
        cancelButton = new JButton("Cancel");

        connectButton.addActionListener(this);
        cancelButton.addActionListener(this);

        JLabel l = new JLabel("IP Address", JLabel.CENTER);
        JLabel l2 = new JLabel("Port", JLabel.CENTER);
        JLabel l3 = new JLabel("Your Name:", JLabel.CENTER);

        ip = new JTextField();
        port = new JTextField();
        if (!Functions.serverIP.equals("") && !Functions.serverPort.equals("")) {
            ip.setText(Functions.serverIP);
            port.setText(Functions.serverPort);
        }
        name = new JTextField();

        ip.setFont(helloHeadline);
        ip.setForeground(Color.BLACK);
        port.setFont(helloHeadline);
        port.setForeground(Color.BLACK);
        name.setFont(helloHeadline);
        name.setForeground(Color.BLACK);
        connectButton.setFont(helloHeadline);
        connectButton.setForeground(Color.BLACK);
        cancelButton.setFont(helloHeadline);
        cancelButton.setForeground(Color.BLACK);

        l.setFont(helloHeadline);
        l.setForeground(Color.BLACK);
        l2.setFont(helloHeadline);
        l2.setForeground(Color.BLACK);
        l3.setFont(helloHeadline);
        l3.setForeground(Color.BLACK);

        frame.setLayout(new GridLayout(4, 2));

        frame.add(l);
        frame.add(ip);
        frame.add(l2);
        frame.add(port);
        frame.add(l3);
        frame.add(name);
        frame.add(cancelButton);
        frame.add(connectButton);

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // // Enter full-screen mode
        device.setFullScreenWindow(frame);
        frame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Connect":
                if (!ip.getText().equals("") && !port.getText().equals("") && !name.getText().equals("")) {
                    new GameClient(ip.getText(), port.getText(), name.getText());
                    frame.dispose();
                } else {
                    connectButton.setText("Please Fill Fields!");
                    int delay = 0750; // 3 seconds
                    Timer timer = new Timer(delay, ae -> {
                        connectButton.setText("Connect");
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
                break;
            case "Cancel":
                new Game();
                frame.dispose();
                break;
            default:
        }
    }
}
