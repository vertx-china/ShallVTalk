package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.vertxchina.vtalk.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
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
    placeNodesOnPane(msg, wholeMessage);

    if (nickname.isMissingNode())
      wholeMessage.setBackground(new Background(new BackgroundFill(Color.web("#b3e6b3"), new CornerRadii(5), Insets.EMPTY)));
    Platform.runLater(() -> chatHistory.getChildren().addAll(wholeMessage, new Text(System.lineSeparator()+System.lineSeparator())));
  }

  private Hyperlink generateHyperLink(String address){
    var hyperlink = new Hyperlink(address);
    hyperlink.setOnAction(e -> Application.hostServices.showDocument(address));
    return hyperlink;
  }

  private Hyperlink generateHyperLink(String text, String address){
    var hyperlink = new Hyperlink(text);
    hyperlink.setOnAction(e -> Application.hostServices.showDocument(address));
    return hyperlink;
  }

  private void placeNodesOnPane(JsonNode json, Pane pane){
    switch (json.getNodeType()){
      case OBJECT -> {
        var type = json.path("type").asText();
        switch (type){
          case "1","img","image" -> {
            var url = json.path("url").asText("Image url is null.");
            var imageview = new ImageView(url);
            if(imageview.getImage().isError()){
              if(url.startsWith("http"))
                pane.getChildren().add(new Hyperlink(url));
              else
                pane.getChildren().add(new Text(url));
            }else{
              pane.getChildren().add(imageview);
            }
          }
          case "2","url","link","hyperlink" -> {
            var url = json.path("url").asText("URL is null.");
            var content = json.path("content").asText(url);
            pane.getChildren().add(generateHyperLink(content, url));
          }
          default -> {
            var content = json.path("content").asText("null");
            var color = json.path("color").asText("#000");
            var text = new Text(content);
            text.setFill(Color.web(color));
            pane.getChildren().add(text);
          }
        }
      }
      case STRING -> {
        var message = json.asText("");
        if(message.startsWith("http")){
          var msg = message.toLowerCase().trim();
          if(msg.endsWith("png")||msg.endsWith("jpg")||
              msg.endsWith("jpeg")||msg.endsWith("gif")){
            var imageview = new ImageView(message);
            if(imageview.getImage().isError())
              pane.getChildren().add(generateHyperLink(message));
            else {
              imageview.setPreserveRatio(true);
              if(this.getWidth() - 50 < imageview.getImage().getWidth())
                imageview.setFitWidth(this.getWidth() - 50);
              pane.getChildren().add(imageview);
            }
          }else
            pane.getChildren().add(generateHyperLink(message));
        }else{
          if((pane instanceof FlowPane flowPane) && message.contains("\n")){
            var msgs = message.split("\n");
            for(int i=0;i<msgs.length;i++){
              var msg = msgs[i];
              var text = new Text(msg);
              flowPane.getChildren().add(text);
              if(i<msgs.length-1 || message.endsWith("\n")){
                Region p = new Region();
                p.setPrefSize(this.getWidth()- text.getWrappingWidth() - 50, 0.0);
                flowPane.getChildren().add(p);
              }
            }
          }else{
            pane.getChildren().add(new Text(message));
          }
        }
      }
      case ARRAY -> {
        var flowPane = new FlowPane();
        flowPane.setRowValignment(VPos.BASELINE);
        for(int i=0;i<json.size();i++){
          var jsonNode = json.get(i).path("message");
          if(jsonNode.isMissingNode())
            placeNodesOnPane(json.get(i), flowPane);
          else
            placeNodesOnPane(json.get(i).path("message"), flowPane);
        }
        pane.getChildren().add(flowPane);
      }
      default -> pane.getChildren().add(new Text(json.asText()));
    };
  }
}
