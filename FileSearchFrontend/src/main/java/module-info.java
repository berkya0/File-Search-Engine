module com.berkaykomur.filesearchfrontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires javafx.base;
    requires org.slf4j;
    requires javafx.graphics;

    opens com.berkaykomur.filesearchfrontend.view to javafx.fxml;
    opens com.berkaykomur.filesearchfrontend.dto to com.fasterxml.jackson.databind, javafx.base;

    opens com.berkaykomur.filesearchfrontend to javafx.fxml;
    exports com.berkaykomur.filesearchfrontend;
    exports com.berkaykomur.filesearchfrontend.view;
}