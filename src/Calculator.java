import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.*;

public class Calculator implements ActionListener {

    // stack for storing values
    private Stack<Double> currentCalc;

    // string for storing current operation (+-/*)
    private String operator = null;

    // Display field
    private JTextField displayField;

    // Display filed default value
    private String displayFieldDefault = "0";

    // Default button size
    private int buttonSize = 40;

    // Calculator class constructor
    public Calculator() {
        currentCalc = new Stack<Double>();

        // some standard window init
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        frame.setLocationRelativeTo(null);

        // init number display field
        displayField = new JTextField(displayFieldDefault);
        displayField.setEditable(false);
        displayField.setHorizontalAlignment(JTextField.RIGHT);

        // add number display field to form
        // place it on th top (north)
        frame.add(displayField, BorderLayout.NORTH);
        // add panel with number buttons to form
        // place it on the left (west)
        frame.add(createNumbersPanel(), BorderLayout.WEST);
        // add panel with operation buttons to form
        // place it on the right (east)
        frame.add(createOperationPanel(), BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    // method for creation number buttons panel
    public JPanel createNumbersPanel() {

        JPanel numbersPanel = new JPanel();
        JPanel mainPanel = new JPanel(new GridLayout(4,3));
        JButton[] number_buttons = new JButton[10];

        for (int i = 0; i < 10; i++) {
            int n = (i + 1) % 10;
            number_buttons[i] = createJButton(String.valueOf(n),
                    String.valueOf(n), buttonSize);
        }

        for (JButton number_button : number_buttons) {
            mainPanel.add(number_button);
        }

        numbersPanel.add(mainPanel);
        numbersPanel.setOpaque(true);
        return numbersPanel;
    }

    // method for creation operation buttons panel
    public JPanel createOperationPanel() {

        JPanel operationPanel = new JPanel();
        char[] operations = { '+', '-', '*', '/' , 'C', '='};
        JButton[] operation_buttons = new JButton[operations.length];
        JPanel mainPanel = new JPanel(new GridLayout(2,5));

        for (int i = 0; i < operations.length; i++) {
            operation_buttons[i] = createJButton(String.valueOf(operations[i]),
                    String.valueOf(operations[i]), buttonSize);
        }

        for (JButton operation_button : operation_buttons) {
            mainPanel.add(operation_button);
        }

        operationPanel.add(mainPanel);
        operationPanel.setOpaque(true);
        return operationPanel;
    }

    // method for button creation
    // description - text in a button
    // action - name of command for a button
    // size - weight and height of a button
    private JButton createJButton(String description, String action, int size)
    {
        JButton tempButton = new JButton(description);
        tempButton.setPreferredSize(new Dimension(size, size));
        tempButton.setActionCommand(action);
        tempButton.addActionListener(this);
        return tempButton;
    }

    // listener of button actions
    // do some action depends on button been pressed
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        // if pressed one of the operation buttons, except 'C'
        if ("+-/*=".contains(command)) {

            //save value from display to stack
            try {
                Double num = Double.valueOf(displayField.getText());
                currentCalc.push(num);
                logInfo(num + " push to stack");
            } catch (Exception ex) {
                logError(ex.getMessage());
                displayField.setText("Error!");
            }

            // if operation '=' then calculate
            if (command.equals("=")) {
                try {
                    // calc and display result
                    Double result = calculate();
                    displayField.setText(result.toString());
                // if can't get variable from stack display "Can't calculate!"
                } catch(EmptyStackException ex) {
                    logError("Stack empty: can't get variable");
                    showErrorMessage("Can't calculate");
                    displayField.setText(displayFieldDefault);
                    clearStack();
                // if get wrong (illegal) argument display error msg
                } catch (IllegalArgumentException ex) {
                    String msg = ex.getMessage();
                    logError(msg);
                    showErrorMessage(msg);
                    displayField.setText(displayFieldDefault);
                    clearStack();
                // if catch another error display "Error!"
                } catch(Exception ex) {
                    logError(e.toString());
                    showErrorMessage("Error!");
                    displayField.setText(displayFieldDefault);
                    clearStack();
                }
            } else {
                // else set operator to this command
                operator = command;
                logInfo("operator set to " + operator);
                displayField.setText("");
            }
        // if pressed on of number buttons
        } else if ("1234567890".contains(command)) {
            // if display set to the default value '0'
            if (displayField.getText().equals(displayFieldDefault)) {
                // remove 0 from display
                displayField.setText("");
            }
            // append button number to the last entered value
            displayField.setText(displayField.getText() + command);
        // if command is 'C'
        } else if (command.equals("C")) {
            // clear stack
            clearStack();
            // set display to default variable
            displayField.setText(displayFieldDefault);
        }
    }

    // method for calculate and display result
    private Double calculate() {
        logInfo("--start calculate()--");

        // get last to numbers from stack
        Double number_2 = currentCalc.pop();
        logInfo("num_2: " + number_2);

        Double number_1 = currentCalc.pop();
        logInfo("num_1: " + number_1);

        // set result to null (undefined)
        Double calculate;
        logInfo("operator: " + operator);

        // depend on operator make an action
        if (operator.equals("+")) {
            calculate = number_1 + number_2;
        } else if (operator.equals("-")) {
            calculate = number_1 - number_2;
        } else if (operator.equals("*")) {
            calculate = number_1 * number_2;
        } else if (operator.equals("/")) {
            // check for division on by zero
            if (number_2 == 0) {
                // return error
                throw new IllegalArgumentException("Can't divide by zero");
            }
            calculate = number_1 / number_2;
        } else {
            throw new IllegalArgumentException("Unknown operation");
        }

        logInfo("result: " + calculate);

        // put result to stack
        currentCalc.push(calculate);
        logInfo(MessageFormat.format("{0} push to stack", calculate.toString()));
        logInfo("--end calculate()--");
        return calculate;
    }

    // method for cleaning stack
    private void clearStack() {
        while(!currentCalc.empty()) {
            currentCalc.pop();
        }
    }

    // log error message to console
    private void logError(String msg) {
        System.out.println("[ERROR] " + msg);
    }

    // log info message to console
    private void logInfo(String msg) {
        System.out.println("[INFO] " + msg);
    }

    // show message box with error
    private void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error!", JOptionPane.ERROR_MESSAGE);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Calculator();
            }
        });
    }
}