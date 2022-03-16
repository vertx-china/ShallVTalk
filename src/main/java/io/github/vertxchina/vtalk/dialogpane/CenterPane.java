package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.vertxchina.util.Common;
import io.github.vertxchina.vtalk.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class CenterPane extends ScrollPane {
  TextFlow chatHistory = new TextFlow();

  public CenterPane() {
    this.setContent(chatHistory);
    this.setPadding(new Insets(10));
    this.setStyle("-fx-background: #FFFFFF");
    this.vvalueProperty().bind(chatHistory.heightProperty());
  }

  public void appendChatHistory(JsonNode node) {
    var nickname = node.path("nickname");

    var time = node.path("time").asText();
    time = Common.HandlerDate(time);

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
            if(json.has("base64")){
              var base64String = json.path("base64").asText("");
              if(base64String.contains(",")){
                var commaIndex = base64String.indexOf(",");
                base64String = base64String.substring(commaIndex+1);
              }
              byte[] base64 = Base64.getMimeDecoder().decode(base64String);
              var image = new Image(new ByteArrayInputStream(base64));
              if(image.isError()){
                image = new Image(new ByteArrayInputStream(Base64.getMimeDecoder().decode(ERROR_IMAGE)));
              }
              pane.getChildren().add(new ImageView(image));
            }else{
              var url = json.path("url").asText("Image url is null.").trim();
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
          }
          case "2","url","link","hyperlink" -> {
            var url = json.path("url").asText("URL is null.").trim();
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
            var imageview = new ImageView(message.trim());
            if(imageview.getImage().isError())
              pane.getChildren().add(generateHyperLink(message.trim()));
            else {
              imageview.setPreserveRatio(true);
              if(this.getWidth() - 50 < imageview.getImage().getWidth())
                imageview.setFitWidth(this.getWidth() - 50);
              pane.getChildren().add(imageview);
            }
          }else
            pane.getChildren().add(generateHyperLink(message.trim()));
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
    }
  }

  private static final String ERROR_IMAGE = "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";
}
