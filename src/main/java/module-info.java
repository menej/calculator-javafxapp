module fri.uv.naloga.calculatorapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens fri.uv.naloga.calculatorapp to javafx.fxml;
    exports fri.uv.naloga.calculatorapp;
}