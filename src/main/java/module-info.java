module hr.tvz.cartographers {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;

    opens hr.tvz.cartographers to javafx.fxml;
    exports hr.tvz.cartographers;
    exports hr.tvz.cartographers.controllers;
    opens hr.tvz.cartographers.controllers to javafx.fxml;
}