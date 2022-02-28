package io.github.vertxchina.vtalk.dialogPane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;

import java.io.PrintWriter;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static io.github.vertxchina.vtalk.Application.GLOBAL_FONT_FAMILY;

public class DialogPane extends BorderPane {
  DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
  final public SimpleStringProperty simpleStringProperty = new SimpleStringProperty();
  ObjectMapper mapper = new ObjectMapper();

  public DialogPane(Map parameters){
    Socket socket = (Socket) parameters.get("socket");
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    this.setPrefSize(screenBounds.getWidth()*3/4, screenBounds.getHeight()*3/4);
    this.setStyle(GLOBAL_FONT_FAMILY);

    var userlist = new ListView<String>();
    userlist.setPadding(new Insets(10));

    var chatHistory = new TextFlow();
    var scrollPane = new ScrollPane(chatHistory);
    scrollPane.setPadding(new Insets(10));
    scrollPane.setStyle("-fx-background: #FFFFFF");
    scrollPane.vvalueProperty().bind(chatHistory.heightProperty());

    simpleStringProperty.addListener((o,oldValue, newValue)->{
      try {
        JsonNode node = mapper.readTree(newValue);
        if(node.has("nickname"))
          appendChatHistory(chatHistory, node.get("nickname").asText("") +" ");
        if(node.has("time"))
          appendChatHistory(chatHistory,node.get("time").asText("") + "\r\n");
        if(node.has("message"))
          appendChatHistory(chatHistory,node.get("message").asText("")+"\r\n\r\n");
        if(node.has("nicknames")){
          Platform.runLater(()->{
            userlist.getItems().clear();
            var nicknames = node.get("nicknames");
            for(int i=0;i<nicknames.size();i++){
              var nickname = nicknames.get(i).asText();
              userlist.getItems().add(nickname);
            }
          });
        }
      }catch (Exception e){
        e.printStackTrace();
      }
    });

    var textarea = new TextArea();
    textarea.setPrefHeight(screenBounds.getHeight()/6);
    textarea.addEventFilter(KeyEvent.KEY_PRESSED, v ->{
      if(v.getCode() == KeyCode.ENTER){
        if(v.isShiftDown()){
          textarea.insertText(textarea.getCaretPosition(),"\n");
        }else{
          sendSimpleMessage(socket, "message", textarea.getText());
          textarea.setText("");
        }
        v.consume();
      }
    });

    this.setRight(userlist);
    this.setCenter(scrollPane);
    this.setBottom(textarea);

    BorderPane.setMargin(scrollPane,new Insets(10,5,5,10));
    BorderPane.setMargin(userlist,new Insets(10,10,5,5));
    BorderPane.setMargin(textarea,new Insets(5,10,10,10));
  }

  public void appendChatHistory(TextFlow chatHistory, String message){
    Platform.runLater(() -> {
      var text = new Text(message);
      chatHistory.getChildren().add(text);
    });
  }

  public void sendSimpleMessage(Socket socket, String key, String value){
    try {
      var outputStream = socket.getOutputStream();
      var outputWriter = new PrintWriter(outputStream);
      ObjectNode message = mapper.createObjectNode();
      message.put(key, value);
      var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
      outputWriter.write(json + "\r\n");
      outputWriter.flush();
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
