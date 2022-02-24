package io.github.vertxchina.vtalk;

import io.github.vertxchina.components.NumberTextField;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IndexPane extends FlowPane {
  public IndexPane() {
    this.setOrientation(Orientation.VERTICAL);
    this.setVgap(10);
    this.setPadding(new Insets(10));
    var serverTextField = new TextField("localhost");
    serverTextField.setPromptText("Server e.g. 127.0.0.1, localhost");
    var portTextField = new NumberTextField(32167);
    portTextField.setPromptText("Port e.g. 8080");
    var hbox = new HBox();
    hbox.setSpacing(20);
    hbox.setPadding(new Insets(10,10,10,20));
    var connect = new Button("连接");
    var exit = new Button("退出");
    hbox.getChildren().addAll(connect,exit);
    this.getChildren().addAll(serverTextField,portTextField,hbox);

    exit.setOnAction(e -> System.exit(0));
    connect.setOnAction(e -> {
      var server = serverTextField.getText();
      var port = portTextField.getNumber();
      this.setDisable(true);
      var service = new ConnectionService(server, port);
      service.setOnSucceeded(s -> this.setDisable(false));
      service.setOnCancelled(s -> this.setDisable(false));
      service.setOnFailed(s -> this.setDisable(false));
      service.start();
    });
  }
}
