package restaurant.administrator.view.customcomponents;

import restaurant.SwingHelper;
import restaurant.kitchen.Dish;
import restaurant.kitchen.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.List;

import static restaurant.SwingHelper.setPrefMaxMinSizes;

/**
 * Created by Аркадий on 01.04.2016.
 */
public class MenuPanel extends JPanel {
    private JTextArea menuTextArea;
    private JTextArea connectionsTextArea;

    public MenuPanel(Menu menu, ActionListener startButtonListener) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        menuTextArea = createTextArea(Color.decode("0x2798AA"));
        JScrollPane scrollPane = new JScrollPane(menuTextArea);
        setPrefMaxMinSizes(scrollPane, new Dimension(700, 400));
        updateMenuTextArea(menu);
        JPanel southPanel = createSouthPanel(startButtonListener);
        JLabel label = createLabel("Connections:");

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(scrollPane);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(label);
        add(southPanel);
    }

    private JTextArea createTextArea(Color textColor) {
        JTextArea textArea = new JTextArea();
        textArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        textArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setForeground(textColor);
        textArea.setFont(new Font("Dialog", Font.BOLD, 14));
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    public void updateMenuTextArea(Menu menu) {
        menuTextArea.setText("");

        StringBuilder resultText = new StringBuilder();
        for(Map.Entry<String, List<Dish>> pair: menu.getStore().entrySet()) {
            resultText.append(pair.getKey().toUpperCase());
            resultText.append(":\n");

            for(Dish dish: pair.getValue()) {
                if(!dish.isDeleted()) {
                    resultText.append("\t");
                    resultText.append(dish.getName());
                    resultText.append("\n");
                }
            }
            for(Dish dish: pair.getValue()) {
                if(dish.isDeleted()) {
                    resultText.append("\t");
                    resultText.append("deleted\t");
                    resultText.append(dish.getName());
                    resultText.append("\n");
                }
            }

            resultText.append("\n");
        }

        menuTextArea.setText(resultText.toString());
    }

    private JPanel createSouthPanel(ActionListener startButtonListener) {
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.LINE_AXIS));
        setPrefMaxMinSizes(resultPanel, new Dimension(700, 100));
        resultPanel.setOpaque(false);

        connectionsTextArea = createTextArea(Color.decode("0x187DAA"));
        JScrollPane scrollPane = new JScrollPane(connectionsTextArea);
        setPrefMaxMinSizes(scrollPane, new Dimension(350, 100));
        JButton startButton = SwingHelper.createSimpleButton(
                "START SERVER", startButtonListener, new Dimension(300, 80));

        resultPanel.add(scrollPane);
        resultPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        resultPanel.add(startButton);

        return resultPanel;
    }

    public void updateConnectionsTextArea(String text) {
        connectionsTextArea.append(text + "\n");
    }

    private JLabel createLabel(String text) {
        JLabel resultLabel = new JLabel(text);
        resultLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        setPrefMaxMinSizes(resultLabel, new Dimension(700, 30));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return resultLabel;
    }
}
