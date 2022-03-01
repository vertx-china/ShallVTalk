package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CenterPane extends ScrollPane {
  DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
  TextFlow chatHistory = new TextFlow();

  public CenterPane() {
    this.setContent(chatHistory);
    this.setPadding(new Insets(10));
    this.setStyle("-fx-background: #FFFFFF");
    this.vvalueProperty().bind(chatHistory.heightProperty());
  }

  public void appendChatHistory(JsonNode node){
    var nickname = node.path("nickname").asText("未知用户");
    var time = node.path("time").asText(ZonedDateTime.now().format(timeFormatter));
    var message = node.path("message").asText("");
    var msgText = new Text(message);
    var color = node.path("color").asText("#000");
    msgText.setFill(Color.web(color));
    Platform.runLater(()->
      chatHistory.getChildren().addAll(new Text(nickname + " " + time + "\r\n"),
          msgText, new Text("\r\n\r\n")
      ));
  }
}
