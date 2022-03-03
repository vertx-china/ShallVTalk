package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
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

  public void appendChatHistory(JsonNode node) {
    var nickname = node.path("nickname");
    var time = node.path("time").asText(ZonedDateTime.now().format(timeFormatter));
    var msgHead = new Label();
    var msgBody = new Label();
    var nn = nickname.isMissingNode() ? "我" : nickname.asText();
    msgHead.setText(nn + " " + time);
    var message = node.path("message").asText(" ").trim();
    if(message.startsWith("http")&&(message.endsWith("png")||message.endsWith("jpg")||message.endsWith("gif"))){
      msgBody.setGraphic(new ImageView(message));
    }else{
      msgBody.setText(message);
      var color = node.path("color").asText("#000");
      msgBody.setTextFill(Color.web(color));
    }
    msgBody.setPadding(new Insets(5));
    msgBody.setLineSpacing(3);
    if (nickname.isMissingNode())
      msgBody.setBackground(new Background(new BackgroundFill(Color.web("#b3e6b3"), new CornerRadii(5), Insets.EMPTY)));
    Platform.runLater(() ->
        chatHistory.getChildren().addAll(msgHead, new Text(System.lineSeparator()), msgBody, new Text(System.lineSeparator() + System.lineSeparator())));//nameText, new Text(System.lineSeparator()), msgLabel
  }
}
