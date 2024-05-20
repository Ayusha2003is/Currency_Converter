
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;

public class MoneyChanger {

    private JFrame mainWindow;
    private JLabel labelAmount, labelFrom, labelTo;
    private JTextField fieldAmount, fieldConverted;
    private JButton buttonConvert, buttonClose;
    private JComboBox<String> comboBoxSourceCurrency, comboBoxTargetCurrency;
    private Properties currencyRates;

    public MoneyChanger() {
        loadRates();

        mainWindow = new JFrame("Currency Converter");

        labelAmount = new JLabel("Amount:");
        labelAmount.setBounds(20, 40, 100, 30);
        labelFrom = new JLabel("From:");
        labelFrom.setBounds(170, 40, 100, 30);
        labelTo = new JLabel("To:");
        labelTo.setBounds(320, 40, 100, 30);

        fieldAmount = new JTextField("0");
        fieldAmount.setBounds(80, 40, 70, 30);
        fieldConverted = new JTextField("0");
        fieldConverted.setBounds(240, 40, 70, 30);

        buttonConvert = new JButton("Convert");
        buttonConvert.setBounds(150, 100, 150, 30);
        buttonClose = new JButton("Close");
        buttonClose.setBounds(320, 100, 90, 30);

        comboBoxSourceCurrency = new JComboBox<>(new String[]{"INR", "NPR", "USD", "GBP", "EUR", "AUD", "CAD", "SGD", "CHF", "MYR", "JPY", "CNY", "SAR", "QAR", "THB", "AED", "KRW", "SEK", "DKK", "HKD", "KWD", "BHD"});
        comboBoxSourceCurrency.setBounds(220, 40, 70, 30);
        comboBoxTargetCurrency = new JComboBox<>(new String[]{"INR", "NPR", "USD", "GBP", "EUR", "AUD", "CAD", "SGD", "CHF", "MYR", "JPY", "CNY", "SAR", "QAR", "THB", "AED", "KRW", "SEK", "DKK", "HKD", "KWD", "BHD"});
        comboBoxTargetCurrency.setBounds(370, 40, 70, 30);

        buttonConvert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BigDecimal amount;
                try {
                    amount = new BigDecimal(fieldAmount.getText());
                } catch (NumberFormatException ex) {
                    fieldConverted.setText("Invalid amount");
                    return;
                }
                String sourceCurrency = (String) comboBoxSourceCurrency.getSelectedItem();
                String targetCurrency = (String) comboBoxTargetCurrency.getSelectedItem();
                System.out.println("Converting " + amount + " from " + sourceCurrency + " to " + targetCurrency);
                BigDecimal convertedAmount = convertCurrency(amount, sourceCurrency, targetCurrency);
                System.out.println("Converted Amount: " + convertedAmount);
                fieldConverted.setText(convertedAmount.toPlainString());
            }
        });

        buttonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainWindow.dispose();
            }
        });

        mainWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        mainWindow.add(labelAmount);
        mainWindow.add(fieldAmount);
        mainWindow.add(labelFrom);
        mainWindow.add(labelTo);
        mainWindow.add(fieldConverted);
        mainWindow.add(buttonConvert);
        mainWindow.add(buttonClose);
        mainWindow.add(comboBoxSourceCurrency);
        mainWindow.add(comboBoxTargetCurrency);

        mainWindow.setLayout(null);
        mainWindow.setSize(500, 200);
        mainWindow.setVisible(true);
    }

    private void loadRates() {
        currencyRates = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("exchange_rates.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find exchange_rates.properties");
                return;
            }
            currencyRates.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public BigDecimal convertCurrency(BigDecimal amount, String sourceCurrency, String targetCurrency) {
        String rateKey = sourceCurrency + "_TO_" + targetCurrency;
        String rateValue = currencyRates.getProperty(rateKey);

        if (rateValue != null) {
            try {
                BigDecimal rate = new BigDecimal(rateValue);
                return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                System.out.println("Invalid rate format for: " + rateKey);
                return amount;
            }
        } else {
            System.out.println("Conversion rate not found for: " + rateKey);
            return amount;
        }
    }

    public static void main(String[] args) {
        new MoneyChanger();
    }
}
