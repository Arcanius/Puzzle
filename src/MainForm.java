import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm extends JFrame {
    private JTextField pathField;
    private JButton cutButton;
    private JButton collectButton;

    public MainForm() {
        super("Меню");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout());
        setSize(300, 100);
        setResizable(false);
        setLocationRelativeTo(null);
        initComponents();
        addListeners();
        setVisible(true);
    }

    private void initComponents() {
        pathField = new JTextField(30);
        add(pathField);
        cutButton = new JButton("Створити");
        add(cutButton);
        collectButton = new JButton("Скласти");
        add(collectButton);
    }

    private void addListeners() {
        cutButton.addActionListener((e) -> {
            new CutForm(pathField.getText());
            dispose();
        });
        collectButton.addActionListener((e) -> {
            new CollectForm(pathField.getText());
            dispose();
        });
    }
}
