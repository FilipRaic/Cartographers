module hr.tvz.cartographers {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;
    requires java.rmi;
    requires java.naming;
    requires org.slf4j;

    opens hr.tvz.cartographers to javafx.fxml;
    exports hr.tvz.cartographers;
    exports hr.tvz.cartographers.controllers;
    exports hr.tvz.cartographers.shared.enums;
    exports hr.tvz.cartographers.shared.chat;
    opens hr.tvz.cartographers.controllers to javafx.fxml;
}
