package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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
    var nickname = node.path("nickname").asText("我");
    var time = node.path("time").asText(ZonedDateTime.now().format(timeFormatter));
    var color = node.path("color").asText("#000");
    var label = new Label();
    label.setText(nickname + " " + time + System.lineSeparator() + node.path("message").asText(""));
    label.setTextFill(Color.web(color));
    label.setPadding(new Insets(5));
    label.setLineSpacing(3);
    if(nickname.equals("我"))
      label.setBackground(new Background(new BackgroundFill(Color.web("#b3e6b3"), new CornerRadii(5) , Insets.EMPTY)));
    Platform.runLater(()->
      chatHistory.getChildren().addAll(label, new Text(System.lineSeparator()+System.lineSeparator())));//nameText, new Text(System.lineSeparator()), msgLabel
  }
}
