module org.example.bankserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires com.google.gson;


    opens org.example.bank to javafx.fxml;
    exports org.example.bank;
}