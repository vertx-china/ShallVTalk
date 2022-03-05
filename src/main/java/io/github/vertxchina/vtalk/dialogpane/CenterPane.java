package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.vertxchina.vtalk.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
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

    var wholeMessage = new VBox();
    wholeMessage.setPadding(new Insets(5));
    wholeMessage.setSpacing(3);

    var msgHead = new Text();
    var nn = nickname.isMissingNode() ? "我" : nickname.asText();
    var color = node.path("color").asText("#000");
    msgHead.setText(nn + " " + time);
    msgHead.setFill(Color.web(color));
    wholeMessage.getChildren().addAll(msgHead);

    var msg = node.path("message");
    placeNodesByJsonNode(msg, wholeMessage);

    if (nickname.isMissingNode())
      wholeMessage.setBackground(new Background(new BackgroundFill(Color.web("#b3e6b3"), new CornerRadii(5), Insets.EMPTY)));
    Platform.runLater(() -> chatHistory.getChildren().addAll(wholeMessage, new Text(System.lineSeparator()+System.lineSeparator())));
  }

  private Hyperlink generateHyperLink(String address){
    var hyperlink = new Hyperlink(address);
    hyperlink.setOnAction(e -> Application.hostServices.showDocument(address));
    return hyperlink;
  }

  private void placeNodesByJsonNode(JsonNode json, Pane wholeMessage){
    switch (json.getNodeType()){
      case STRING -> {
        var message = json.asText("").trim();
        if(message.startsWith("http")){
          if(message.endsWith("png")||message.endsWith("jpg")||message.endsWith("jpeg")||message.endsWith("gif")){
            var imageview = new ImageView(message);
            if(imageview.getImage().isError())
              wholeMessage.getChildren().add(generateHyperLink(message));
            else
              wholeMessage.getChildren().add(imageview);
          }else
            wholeMessage.getChildren().add(generateHyperLink(message));
        }else{
          wholeMessage.getChildren().add(new Text(message));
        }
      }
      case ARRAY -> {
        var flowPane = new FlowPane();
        flowPane.setRowValignment(VPos.BASELINE);
        for(int i=0;i<json.size();i++){
          var jsonNode = json.get(i).path("message");
          if(jsonNode.isMissingNode())
            placeNodesByJsonNode(json.get(i), flowPane);
          else
            placeNodesByJsonNode(json.get(i).path("message"), flowPane);
        }
        wholeMessage.getChildren().add(flowPane);
      }
      default -> wholeMessage.getChildren().add(new Text(json.asText()));
    };
  }
}
