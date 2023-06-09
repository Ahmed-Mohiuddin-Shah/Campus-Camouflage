import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class ServerCreationGUI implements ActionListener {
    private JFrame frame;

    private JButton createButton, cancelButton;

    JTextField ip;
    JTextField port;

    ServerCreationGUI() {
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

        createButton = new JButton("Create");
        cancelButton = new JButton("Cancel");

        createButton.addActionListener(this);
        cancelButton.addActionListener(this);

        JLabel l = new JLabel("IP Address", JLabel.CENTER);
        JLabel l2 = new JLabel("Port", JLabel.CENTER);

        ip = new JTextField();
        port = new JTextField();

        ip.setFont(helloHeadline);
        ip.setForeground(Color.BLACK);
        port.setFont(helloHeadline);
        port.setForeground(Color.BLACK);
        createButton.setFont(helloHeadline);
        createButton.setForeground(Color.BLACK);
        cancelButton.setFont(helloHeadline);
        cancelButton.setForeground(Color.BLACK);

        l.setFont(helloHeadline);
        l.setForeground(Color.BLACK);
        l2.setFont(helloHeadline);
        l2.setForeground(Color.BLACK);

        frame.setLayout(new GridLayout(3, 2));

        frame.add(l);
        frame.add(ip);
        frame.add(l2);
        frame.add(port);
        frame.add(cancelButton);
        frame.add(createButton);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Enter full-screen mode
        device.setFullScreenWindow(frame);
        frame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Create":
                if (ip.getText().equals("") && port.getText().equals("")) {
                    new Server();
                } else if (ip.getText().equals("") && !port.getText().equals("")) {
                    new Server(port.getText());
                } else {
                    new Server(ip.getText(), port.getText());
                }
                frame.dispose();
                break;
            case "Cancel":
                new Game();
                frame.dispose();
                break;
            default:
        }
    }
}
