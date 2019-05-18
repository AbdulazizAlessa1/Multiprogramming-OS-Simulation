import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    private static GUI instance = new GUI();

    public static GUI getInstance() {
        return instance;
    }

    private JLabel timeLabel, memoryLabel, mainLabel;

    private GUI() {
        setSize(800, 400);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        timeLabel = new JLabel();
        memoryLabel = new JLabel();
        topPanel.add(timeLabel);
        topPanel.add(memoryLabel);
        add(topPanel, BorderLayout.NORTH);
        mainLabel = new JLabel();
        add(mainLabel, BorderLayout.CENTER);
    }

    public void updateUI(final String content, final int availableMemory) {
        EventQueue.invokeLater(() -> {
            timeLabel.setText("Time: " + Clock.getInstance().getTime());
            memoryLabel.setText("Free Memory: " + availableMemory);
            mainLabel.setText("<html><pre>"+content+"</pre></html>");
        });
    }

}
