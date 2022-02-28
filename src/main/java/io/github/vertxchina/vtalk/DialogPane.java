package io.github.vertxchina.vtalk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
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

    var chatHistory = new TextArea();
    chatHistory.setEditable(false);

    simpleStringProperty.addListener((o,oldValue, newValue)->{
      try {
        JsonNode node = mapper.readTree(newValue);
        if(node.has("nickname"))
          Platform.runLater(() -> chatHistory.appendText(node.get("nickname").asText("") +" "));
        if(node.has("time"))
          Platform.runLater(() -> chatHistory.appendText(node.get("time").asText("") + "\r\n"));
        if(node.has("message"))
          Platform.runLater(() -> chatHistory.appendText(node.get("message").asText("")+"\r\n\r\n"));
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

    var textfield = new TextField();
    textfield.setOnKeyPressed(v ->{
      if(v.getCode()== KeyCode.ENTER){
        sendSimpleMessage(socket, "message", textfield.getText());
        textfield.setText("");
      }
    });

    this.setRight(userlist);
    this.setCenter(chatHistory);
    this.setBottom(textfield);

    BorderPane.setMargin(chatHistory,new Insets(10,5,5,10));
    BorderPane.setMargin(userlist,new Insets(10,10,5,5));
    BorderPane.setMargin(textfield,new Insets(5,10,10,10));
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
