package fri.uv.naloga.calculatorapp;

import fri.uv.naloga.calculatorapp.external.Calculator;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.*;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class CalculatorController implements Initializable {
    public TitledPane calculatorTiltedPane;
    public TextArea eventlog;
    public TextArea calculatorOutput;
    public TitledPane logTiltedPane;
    public TextArea calculationsDiary;
    public Label statusLabel;
    public Accordion accordion;

    public void openDocument(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Izberi datoteko");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            StringBuilder sb = new StringBuilder();

            try (BufferedReader br = new BufferedReader((new FileReader(file)))) {
                String line;
                while ((line = br.readLine()) != null)
                    sb.append(line).append("\n");

                calculationsDiary.setText(sb.toString());
                statusLabel.setText(String.format("Prebrana datoteka: %s (%d B)", file.getName(), file.length()));
                eventlog.appendText(String.format("Prebrana datoteka: %s (%d B)\n", file, file.length()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Operacija \"Odpri\" preklicana");
            eventlog.appendText(statusLabel.getText() + "\n");
        }
    }

    public void saveToDocument(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Izberi datoteko za shranjevanje");
        File file = fc.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(calculationsDiary.getText());
                statusLabel.setText("Shranjena datoteka: " + file.getName());
                eventlog.appendText("Shranjena datoteka: " + file + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Operacija \"Shrani\" preklicana");
            eventlog.appendText(statusLabel.getText() + "\n");
        }
    }

    public void calculatorOperation(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        String buttonText = button.getText();

        if (calculatorOutput.getStyle().contains("red") || calculatorOutput.getStyle().contains("green"))
            calculatorOutput.setStyle("-fx-background-color: lightgray");

        // Pressed button is mathematical operation or a digit
        if (buttonText.matches("[\\d-+*/().]")) {
            calculatorOutput.appendText(buttonText);
        }
        // Pressed button is a backwards symbol (delete last element from output)
        else if (buttonText.equals("\u232b")) {
            int size = calculatorOutput.getLength();
            if (size > 0)
                calculatorOutput.deleteText(size - 1, size);
        }
        // Pressed button is clear operation
        else if (buttonText.equals("C")) {
            calculatorOutput.setText("");
        }
        // Pressed button is equals operation
        else if (buttonText.equals("=")) {
            calculate();
        }
    }

    private void calculate() {
        double calculationResult;
        try {
            calculationResult = Calculator.calculateDouble(calculatorOutput.getText());
        } catch (Exception e) {
            statusLabel.setText("Napaka: prosimo preverite podano operacijo");
            eventlog.appendText(statusLabel.getText() + "\n");
            calculatorOutput.setStyle("-fx-background-color: red");
            return;
        }

        String calculationString = getFormattedCalculationResult(calculationResult);


        calculationsDiary.appendText(calculatorOutput.getText() + " = " + calculationString + "\n");
        statusLabel.setText("Rezultat izračuna: " + calculationString);
        eventlog.appendText(statusLabel.getText() + "\n");

        calculatorOutput.setText(calculationString);
        calculatorOutput.setStyle("-fx-background-color: green");
    }

    private String getFormattedCalculationResult(double calculationResult) {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        // Check if the decimal is .0 then format it to an integer form, else round it to three decimals
        String calculationString;
        if (calculationResult % 1 == 0) {
            calculationString = String.valueOf(calculationResult);
            calculationString = calculationString.substring(0, calculationString.length() - 2);
        } else {
            calculationString = df.format(calculationResult);
        }

        return calculationString;
    }

    public void clear(ActionEvent actionEvent) {
        calculatorOutput.clear();
        calculationsDiary.clear();

        if (calculatorOutput.getStyle().contains("red") || calculatorOutput.getStyle().contains("green"))
            calculatorOutput.setStyle("-fx-background-color: lightgray");

        statusLabel.setText("Brisanje vsebine kalkulatorja in dnevnika izračunov");
        eventlog.appendText(statusLabel.getText() + "\n");
    }

    public void authorsStatement(ActionEvent actionEvent) {
        statusLabel.setText("Avtor: Enej Mastnak (študent)");
        eventlog.appendText(statusLabel.getText() + "\n");
    }

    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accordion.setExpandedPane(calculatorTiltedPane);
    }


}