package io.github.vertxchina.vtalk.dialogPane;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class CenterPane extends ScrollPane {
  TextFlow chatHistory = new TextFlow();

  public CenterPane() {
    this.setContent(chatHistory);
    this.setPadding(new Insets(10));
    this.setStyle("-fx-background: #FFFFFF");
    this.vvalueProperty().bind(chatHistory.heightProperty());
  }

  public void appendChatHistory(String message){
    Platform.runLater(() -> {
      var text = new Text(message);
      chatHistory.getChildren().add(text);
    });
  }
}
