package io.github.vertxchina.vtalk;

import io.github.vertxchina.nodes.NavigatableScene;
import io.github.vertxchina.nodes.NumberTextField;
import io.github.vertxchina.nodes.PersistentPromptTextField;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static io.github.vertxchina.vtalk.Application.GLOBAL_FONT_FAMILY;

public class IndexPane extends VBox {
  public IndexPane() {
    this.setStyle(GLOBAL_FONT_FAMILY);
    this.setSpacing(20);
    this.setPadding(new Insets(10));
    var nicknameTextField = new PersistentPromptTextField("");
    nicknameTextField.setPromptText("请输入昵称");
    nicknameTextField.requestFocus();
    var serverTextField = new PersistentPromptTextField("localhost");
    serverTextField.setPromptText("服务器地址 例如：127.0.0.1, localhost");
    var portTextField = new NumberTextField(32167);
    portTextField.setPromptText("请输入端口 例如：8080");
    var hbox = new HBox();
    hbox.setSpacing(20);
    hbox.setPadding(new Insets(0,10,10,20));
    var connect = new Button("连接");
    var exit = new Button("退出");
    hbox.getChildren().addAll(connect,exit);
    this.getChildren().addAll(nicknameTextField,serverTextField,portTextField,hbox);

    exit.setOnAction(e -> System.exit(0));
    connect.setOnAction(e -> {
      var nickname = nicknameTextField.getText().trim();
      if(nickname.isEmpty()){
        nicknameTextField.requestFocus();
        return;
      }
      var server = serverTextField.getText().trim();
      if(server.isEmpty()){
        serverTextField.requestFocus();
        return;
      }
      var port = portTextField.getNumber();
      if(port < 0 || port > 65535){
        portTextField.clear();
        portTextField.requestFocus();
        return;
      }
      this.setDisable(true);
      var service = new ConnectionService((NavigatableScene)this.getScene(), nickname, server, port);
      service.setOnSucceeded(s -> ((NavigatableScene)this.getScene()).navigate("/"));
      service.setOnCancelled(service.getOnSucceeded());
      service.setOnFailed(service.getOnSucceeded());
      service.start();
    });
  }
}
