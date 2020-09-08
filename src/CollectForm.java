import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectForm extends JFrame {
    private JTable table;
    private JButton collectButton;
    private BufferedImage[][] images;
    private BufferedImage[][] result;

    private final int SIZE = 200;

    public CollectForm(String path) {
        super("Збирання пазла");
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
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();
            List<BufferedImage> imageList = new ArrayList<>();
            for (int i = 0; i < 9; ++i) {
                for (int j = 0; j < listOfFiles.length; ++j) {
                    if (listOfFiles[j].getName().equals(i + ".png")) {
                        BufferedImage image = ImageIO.read(new File(path + "\\" + i + ".png"));
                        if (image.getWidth() != SIZE || image.getHeight() != SIZE) {
                            throw new WrongImageSizeException();
                        }
                        imageList.add(image);
                    }
                }
            }
            Collections.shuffle(imageList);
            images = new BufferedImage[3][3];
            int k = 0;
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    images[i][j] = imageList.get(k);
                    ++k;
                }
            }
            table = new JTable(3, 3) {
                public Class getColumnClass(int column) {
                    return ImageIcon.class;
                }
            };
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    table.setValueAt(new ImageIcon(images[i][j]), i, j);
                }
            }
            table.setRowHeight(200);
            for (int i = 0; i < table.getColumnCount(); ++i) {
                TableColumn column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(200);
            }
            table.setEnabled(false);
            add(table);
            collectButton = new JButton("Зібрати пазл");
            add(collectButton);
            solvePuzzle();
        } catch (WrongImageSizeException e) {
            JOptionPane.showMessageDialog(this, "Неправильний розмір зображення",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Неправильний шлях до папки",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void addListeners() {
        collectButton.addActionListener((e) -> {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    table.setValueAt(new ImageIcon(result[i][j]), i, j);
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                String[] options = {"[0, 0]", "[0, 1]", "[0, 2]", "[1, 0]", "[1, 1]", "[1, 2]", "[2, 0]", "[2, 1]",
                        "[2, 2]"};
                String answer = (String)JOptionPane.showInputDialog(null, "Виберіть координати", "Вибір",
                        JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                switch (answer) {
                    case "[0, 0]":
                        swapImages(row, col, 0, 0);
                        break;
                    case "[0, 1]":
                        swapImages(row, col, 0, 1);
                        break;
                    case "[0, 2]":
                        swapImages(row, col, 0, 2);
                        break;
                    case "[1, 0]":
                        swapImages(row, col, 1, 0);
                        break;
                    case "[1, 1]":
                        swapImages(row, col, 1, 1);
                        break;
                    case "[1, 2]":
                        swapImages(row, col, 1, 2);
                        break;
                    case "[2, 0]":
                        swapImages(row, col, 2, 0);
                        break;
                    case "[2, 1]":
                        swapImages(row, col, 2, 1);
                        break;
                    case "[2, 2]":
                        swapImages(row, col, 2, 2);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Неправильний вибір", "Помилка",
                                JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void swapImages(int row1, int col1, int row2, int col2) {
        BufferedImage temp = images[row1][col1];
        images[row1][col1] = images[row2][col2];
        images[row2][col2] = temp;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                table.setValueAt(new ImageIcon(images[i][j]), i, j);
            }
        }
        checkPuzzle();
    }

    private boolean isSameColor(Color c1, Color c2) {
        if (Math.abs(c1.getBlue() - c2.getBlue()) < 10 && Math.abs(c1.getRed() - c2.getRed()) < 10
                && Math.abs(c1.getGreen() - c2.getGreen()) < 10) {
            return true;
        }
        else
            return false;
    }

    private void checkPuzzle() {
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                if (images[i][j] != result[i][j]) {
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Пазл складено", "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    private void solvePuzzle() {
        result = new BufferedImage[3][3];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                result[i][j] = new BufferedImage(images[0][0].getWidth(), images[0][0].getHeight(), images[0][0].getType());
            }
        }
        result[1][1] = findCenterImage();
        result[0][1] = findTopImage(result[1][1]);
        result[2][1] = findBottomImage(result[1][1]);
        result[1][0] = findLeftImage(result[1][1]);
        result[1][2] = findRightImage(result[1][1]);
        result[0][0] = findTopLeftImage(result[1][0]);
        result[0][2] = findTopRightImage(result[1][2]);
        result[2][0] = findBottomLeftImage(result[2][1]);
        result[2][2] = findBottomRightImage(result[2][1]);
    }

    private BufferedImage findCenterImage() {
        BufferedImage centerImage = new BufferedImage(images[0][0].getWidth(), images[0][0].getHeight(), images[0][0].getType());
        int centerCount = Integer.MAX_VALUE;
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                BufferedImage tested = images[i][j];
                int count = 0;
                for (int k = 0; k < images.length; ++k) {
                    for (int l = 0; l < images[k].length; ++l) {
                        if (k == i && l == j) continue;
                        BufferedImage temp = images[k][l];
                        for (int x = 0; x < temp.getWidth(); ++x) {
                            Color testedColor = new Color(tested.getRGB(x, 0));
                            Color tempColor = new Color(temp.getRGB(x, temp.getHeight() - 1));
                            if (isSameColor(testedColor, tempColor)) ++count;
                        }
                        for (int x = 0; x < temp.getWidth(); ++x) {
                            Color testedColor = new Color(tested.getRGB(x, tested.getHeight() - 1));
                            Color tempColor = new Color(temp.getRGB(x, 0));
                            if (isSameColor(testedColor, tempColor)) ++count;
                        }
                        for (int y = 0; y < temp.getHeight(); ++y) {
                            Color testedColor = new Color(tested.getRGB(0, y));
                            Color tempColor = new Color(temp.getRGB(temp.getWidth() - 1, y));
                            if (isSameColor(testedColor, tempColor)) ++count;
                        }
                        for (int y = 0; y < temp.getHeight(); ++y) {
                            Color testedColor = new Color(tested.getRGB(tested.getWidth() - 1, y));
                            Color tempColor = new Color(temp.getRGB(0, y));
                            if (isSameColor(testedColor, tempColor)) ++count;
                        }
                    }
                }
                if (count < centerCount) {
                    centerImage = tested;
                    centerCount = count;
                }
            }
        }
        return centerImage;
    }

    private BufferedImage findTopImage(BufferedImage center) {
        BufferedImage topImage = new BufferedImage(center.getWidth(), center.getHeight(), center.getType());
        int topCount = 0;
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                BufferedImage temp = images[i][j];
                int count = 0;
                for (int x = 0; x < temp.getWidth(); ++x) {
                    Color centerColor = new Color(center.getRGB(x, 0));
                    Color tempColor = new Color(temp.getRGB(x, temp.getWidth() - 1));
                    if (isSameColor(centerColor, tempColor)) ++count;
                }
                if (count > topCount) {
                    topImage = temp;
                    topCount = count;
                }
            }
        }
        return topImage;
    }

    private BufferedImage findBottomImage(BufferedImage center) {
        BufferedImage bottomImage = new BufferedImage(center.getWidth(), center.getHeight(), center.getType());
        int bottomCount = 0;
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                BufferedImage temp = images[i][j];
                int count = 0;
                for (int x = 0; x < temp.getWidth(); ++x) {
                    Color centerColor = new Color(center.getRGB(x, center.getWidth() - 1));
                    Color tempColor = new Color(temp.getRGB(x, 0));
                    if (isSameColor(centerColor, tempColor)) ++count;
                }
                if (count > bottomCount) {
                    bottomImage = temp;
                    bottomCount = count;
                }
            }
        }
        return bottomImage;
    }

    private BufferedImage findLeftImage(BufferedImage center) {
        BufferedImage leftImage = new BufferedImage(center.getWidth(), center.getHeight(), center.getType());
        int leftCount = 0;
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                BufferedImage temp = images[i][j];
                int count = 0;
                for (int y = 0; y < temp.getHeight(); ++y) {
                    Color centerColor = new Color(center.getRGB(0, y));
                    Color tempColor = new Color(temp.getRGB(temp.getHeight() - 1, y));
                    if (isSameColor(centerColor, tempColor)) ++count;
                }
                if (count > leftCount) {
                    leftImage = temp;
                    leftCount = count;
                }
            }
        }
        return leftImage;
    }

    private BufferedImage findRightImage(BufferedImage center) {
        BufferedImage rightImage = new BufferedImage(center.getWidth(), center.getHeight(), center.getType());
        int rightCount = 0;
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                BufferedImage temp = images[i][j];
                int count = 0;
                for (int y = 0; y < temp.getHeight(); ++y) {
                    Color centerColor = new Color(center.getRGB(center.getHeight() - 1, y));
                    Color tempColor = new Color(temp.getRGB(0, y));
                    if (isSameColor(centerColor, tempColor)) ++count;
                }
                if (count > rightCount) {
                    rightImage = temp;
                    rightCount = count;
                }
            }
        }
        return rightImage;
    }

    private BufferedImage findTopLeftImage(BufferedImage left) {
        BufferedImage topLeftImage = new BufferedImage(left.getWidth(), left.getHeight(), left.getType());
        int topLeftCount = 0;
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                BufferedImage temp = images[i][j];
                int count = 0;
                for (int x = 0; x < temp.getWidth(); ++x) {
                    Color leftColor = new Color(left.getRGB(x, 0));
                    Color tempColor = new Color(temp.getRGB(x, temp.getHeight() - 1));
                    if (isSameColor(leftColor, tempColor)) ++count;
                }
                if (count > topLeftCount) {
                    topLeftImage = temp;
                    topLeftCount = count;
                }
            }
        }
        return topLeftImage;
    }

    private BufferedImage findTopRightImage(BufferedImage right) {
        BufferedImage topRightImage = new BufferedImage(right.getWidth(), right.getHeight(), right.getType());
        int topRightCount = 0;
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                BufferedImage temp = images[i][j];
                int count = 0;
                for (int x = 0; x < temp.getWidth(); ++x) {
                    Color rightColor = new Color(right.getRGB(x, 0));
                    Color tempColor = new Color(temp.getRGB(x, temp.getHeight() - 1));
                    if (isSameColor(rightColor, tempColor)) ++count;
                }
                if (count > topRightCount) {
                    topRightImage = temp;
                    topRightCount = count;
                }
            }
        }
        return topRightImage;
    }

    private BufferedImage findBottomLeftImage(BufferedImage bottom) {
        BufferedImage bottomLeftImage = new BufferedImage(bottom.getWidth(), bottom.getHeight(), bottom.getType());
        int bottomLeftCount = 0;
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                BufferedImage temp = images[i][j];
                int count = 0;
                for (int y = 0; y < temp.getHeight(); ++y) {
                    Color centerColor = new Color(bottom.getRGB(0, y));
                    Color tempColor = new Color(temp.getRGB(temp.getHeight() - 1, y));
                    if (isSameColor(centerColor, tempColor)) ++count;
                }
                if (count > bottomLeftCount) {
                    bottomLeftImage = temp;
                    bottomLeftCount = count;
                }
            }
        }
        return bottomLeftImage;
    }

    private BufferedImage findBottomRightImage(BufferedImage bottom) {
        BufferedImage bottomRightImage = new BufferedImage(bottom.getWidth(), bottom.getHeight(), bottom.getType());
        int bottomRightCount = 0;
        for (int i = 0; i < images.length; ++i) {
            for (int j = 0; j < images[i].length; ++j) {
                BufferedImage temp = images[i][j];
                int count = 0;
                for (int y = 0; y < temp.getHeight(); ++y) {
                    Color centerColor = new Color(bottom.getRGB(bottom.getHeight() - 1, y));
                    Color tempColor = new Color(temp.getRGB(0, y));
                    if (isSameColor(centerColor, tempColor)) ++count;
                }
                if (count > bottomRightCount) {
                    bottomRightImage = temp;
                    bottomRightCount = count;
                }
            }
        }
        return bottomRightImage;
    }
}
