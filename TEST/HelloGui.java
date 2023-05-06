package TEST;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HelloGui extends JFrame {

    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    HelloGui frame = new HelloGui();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public HelloGui() throws Exception {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton btnTest = new JButton("Left");
        btnTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                RenderPanel.rotateleftright(1);
                repaint();
            }
        });
        btnTest.setBounds(10, 11, 89, 23);
        contentPane.add(btnTest);

        JButton btnRight = new JButton("Right");
        btnRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                RenderPanel.rotateleftright(-1);
                repaint();
            }
        });
        btnRight.setBounds(10, 45, 89, 23);
        contentPane.add(btnRight);

        JPanel panel = new JPanel();
        panel.setBounds(109, 11, 665, 540);
        contentPane.add(panel);
        panel.setLayout(new BorderLayout(0, 0));

        RenderPanel panel_1 = new RenderPanel();
        panel.add(panel_1, BorderLayout.CENTER);
    }
}