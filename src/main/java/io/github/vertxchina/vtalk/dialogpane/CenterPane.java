package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.vertxchina.vtalk.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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

  public void appendChatHistory(JsonNode node) {
    var nickname = node.path("nickname");
    var time = node.path("time").asText(ZonedDateTime.now().format(timeFormatter));
    var msgHead = new Text();
    var wholeMessage = new VBox();
    wholeMessage.setPadding(new Insets(5));
    wholeMessage.setSpacing(3);

    var nn = nickname.isMissingNode() ? "我" : nickname.asText();
    var color = node.path("color").asText("#000");
    msgHead.setText(nn + " " + time);
    msgHead.setFill(Color.web(color));

    var message = node.path("message").asText(" ").trim();
    if(message.startsWith("http")){
      if(message.endsWith("png")||message.endsWith("jpg")||message.endsWith("gif")){
        var imageview = new ImageView(message);
        if(imageview.getImage().isError())
          wholeMessage.getChildren().addAll(msgHead,generateHyperLink(message));
        else
          wholeMessage.getChildren().addAll(msgHead,imageview);
      }else
        wholeMessage.getChildren().addAll(msgHead,generateHyperLink(message));
    }else{
      var text = new Text(message);
      text.setFill(Color.web(color));
      wholeMessage.getChildren().addAll(msgHead,text);
    }

    if (nickname.isMissingNode())
      wholeMessage.setBackground(new Background(new BackgroundFill(Color.web("#b3e6b3"), new CornerRadii(5), Insets.EMPTY)));
    Platform.runLater(() -> chatHistory.getChildren().addAll(wholeMessage, new Text(System.lineSeparator()+System.lineSeparator())));
  }

  private Hyperlink generateHyperLink(String address){
    var hyperlink = new Hyperlink(address);
    hyperlink.setOnAction(e -> Application.hostServices.showDocument(address));
    return hyperlink;
  }
}
