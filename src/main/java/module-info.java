module hr.tvz.cartographers {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens hr.tvz.cartographers to javafx.fxml;
    exports hr.tvz.cartographers;
}