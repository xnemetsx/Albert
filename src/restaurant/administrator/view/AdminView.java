package restaurant.administrator.view;

import restaurant.administrator.AdminController;
import restaurant.administrator.AdminModel;
import restaurant.administrator.view.customcomponents.AddDishPanel;
import restaurant.administrator.view.customcomponents.ChangeDishStatusPanel;
import restaurant.administrator.view.customcomponents.MenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Аркадий on 31.03.2016.
 */
public class AdminView {
    private AdminModel model;
    private AdminController controller;

    private JFrame frame;

    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private AddDishPanel addDishPanel;
    private ChangeDishStatusPanel deleteDishPanel;
    private ChangeDishStatusPanel restoreDishPanel;
    private MenuPanel menuPanel;
    private JPanel statisticsPanel;

    public AdminView(AdminController controller, AdminModel model) {
        this.controller = controller;
        this.model = model;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void initView() {
        frame = new JFrame("Brutz");
        frame.setResizable(false);
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        addDishPanel = new AddDishPanel(this);
        deleteDishPanel = new ChangeDishStatusPanel("Enter name of dish you want to delete:",
                "DELETE DISH", createListenerForDeleteButton());
        restoreDishPanel = new ChangeDishStatusPanel("Enter name of dish you want to restore:",
                "RESTORE DISH" , createListenerForRestoreButton());
        menuPanel = new MenuPanel(model.getNewMenu(), createListenerForStartButton());
        statisticsPanel = new JPanel();
    }

    private ActionListener createListenerForStartButton() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int start = showConfirmDialog("Do you really want to start server?");
                if(start == 0) {
                    controller.startServer();
                    setButtonNotEnable(e);
                }
            }

            private void setButtonNotEnable(ActionEvent e) {
                JButton startButton = (JButton) e.getSource();
                startButton.setEnabled(false);
                deleteDishPanel.setButtonNotEnable();
                restoreDishPanel.setButtonNotEnable();
                addDishPanel.setButtonsNotEnable();
            }
        };
    }

    private int showConfirmDialog(String text) {
        return JOptionPane.showConfirmDialog(
                frame,
                text,
                "Brutz",
                JOptionPane.YES_NO_OPTION);
    }

    private ActionListener createListenerForDeleteButton() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Component component: deleteDishPanel.getComponents()) {
                    if(component instanceof JTextField) {
                        tryEditMenu((JTextField) component, "deleted");
                    }
                }
            }
        };
    }

    private ActionListener createListenerForRestoreButton() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Component component: restoreDishPanel.getComponents()) {
                    if(component instanceof JTextField) {
                        tryEditMenu((JTextField) component, "restored");
                    }
                }
            }
        };
    }

    private void tryEditMenu(JTextField textField, String operationName) {
        String dishName = textField.getText();
        dishName = dishName.trim();
        if(!"".equals(dishName)) {
            boolean statusChanged = false;
            switch(operationName) {
                case "restored":
                    statusChanged = model.changeDishStatus(dishName, false);
                    break;
                case "deleted":
                    statusChanged = model.changeDishStatus(dishName, true);
            }

            showStatusChangedResult(dishName, statusChanged, operationName);
            if(statusChanged) {
                textField.setText("");
                updateMenuPanel();
            }
        }
    }

    public void updateMenuPanel() {
        menuPanel.updateMenuTextArea(model.getNewMenu());
    }

    private void showStatusChangedResult(String dishName, boolean statusChaged, String operationName) {
        if (statusChaged) {
            showSuccessChangedStatusDialog(operationName, dishName);
        } else {
            showNotExistDishDialog();
        }
    }

    private void showSuccessChangedStatusDialog(String operationName, String dishName) {
        JOptionPane.showMessageDialog(
                frame,
                String.format("Dish \"%s\" was %s!", dishName, operationName),
                "Brutz",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showNotExistDishDialog() {
        JOptionPane.showMessageDialog(
                frame,
                "Dish with this name isn't in menu!",
                "Brutz",
                JOptionPane.ERROR_MESSAGE);
    }

    public boolean addOrEditDish(
            String type, String name, String shortDesc,
            String fullDesc, String imagePath, double price) {
        return model.addOrEditDish(type, name, shortDesc, fullDesc, imagePath, price);
    }

    public String askServerAddress() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter server address:",
                "Brutz",
                JOptionPane.QUESTION_MESSAGE);
    }

    public int askServerPort() {
        while(true) {
            String port = JOptionPane.showInputDialog(
                    frame,
                    "Enter server port:",
                    "Brutz",
                    JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            }catch (Exception e) {
                showWarningDialog("Incorrect port was entered, try again.");
            }
        }
    }

    public void showWarningDialog(String text) {
        JOptionPane.showMessageDialog(
                frame,
                text,
                "Brutz",
                JOptionPane.ERROR_MESSAGE);
    }
    public void updateConnectionsInfo(String text) {
        menuPanel.updateConnectionsTextArea(text);
    }
}
