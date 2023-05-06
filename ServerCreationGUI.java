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

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Create":
                Server s = null;
                if (ip.getText().equals("") && port.getText().equals("")) {
                    s = new Server();
                } else if (ip.getText().equals("") && !port.getText().equals("")) {
                    s = new Server(port.getText());
                } else {
                    s = new Server(ip.getText(), port.getText());
                }

                frame.dispose();
                
                s.loop();
                break;
            case "Cancel":
                new Game();
                frame.dispose();
                break;
            default:
        }
    }
}
