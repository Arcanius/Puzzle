import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CutForm extends JFrame {
    private JLabel pictureLabel;
    private JTextField destinationField;
    private JButton cutAndSaveButton;
    private BufferedImage picture;

    private final int SIZE = 600;

    public CutForm(String path) {
        super("Створення пазла");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout());
        setSize(650, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        initComponents(path);
        addListeners();
    }

    private void initComponents(String path) {
        try {
            picture = ImageIO.read(new File(path));
            if (picture.getWidth() != SIZE || picture.getHeight() != SIZE) {
                throw new WrongImageSizeException();
            }
            pictureLabel = new JLabel(new ImageIcon(picture));
            add(pictureLabel);
            destinationField = new JTextField(70);
            add(destinationField);
            cutAndSaveButton = new JButton("Розбити і зберегти");
            add(cutAndSaveButton);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Неправильний шлях до файлу",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            dispose();
        } catch (WrongImageSizeException e) {
            JOptionPane.showMessageDialog(this, "Неправильний розмір зображення",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void addListeners() {
        cutAndSaveButton.addActionListener((e) -> {
            try {
                int id = 0;
                for (int i = 0; i < picture.getWidth(); i += 200) {
                    for (int j = 0; j < picture.getHeight(); j += 200) {
                        ImageIO.write(picture.getSubimage(i, j, 200, 200), "png",
                                new File(destinationField.getText() + "\\" + id + ".png"));
                        ++id;
                    }
                }
                JOptionPane.showMessageDialog(this, "Пазл збережено",
                        "OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException exc) {
                JOptionPane.showMessageDialog(this, "Неправильна назва папки",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
