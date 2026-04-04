module com.example.trabajos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.naming;

    // Hibernate y JPA
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.mail;

    // Abrir paquetes para Hibernate
    opens com.example.trabajos.models to org.hibernate.orm.core, javafx.base;
    opens com.example.trabajos to javafx.fxml;

    exports com.example.trabajos;
    exports com.example.trabajos.models;
}