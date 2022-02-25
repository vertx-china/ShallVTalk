package io.github.vertxchina.vtalk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;

import java.io.PrintWriter;
import java.net.Socket;

public class DialogPane extends BorderPane {
  final public SimpleStringProperty simpleStringProperty = new SimpleStringProperty();

  ObjectMapper mapper = new ObjectMapper();
  public DialogPane(Socket socket) {
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    this.setPrefSize(screenBounds.getWidth()*3/4, screenBounds.getHeight()*3/4);

    var textArea = new TextArea();

    simpleStringProperty.addListener((o,oldValue, newValue)->{
      try {
        JsonNode node = mapper.readTree(newValue);
        var message = node.get("message");
        var msg = message == null ? "": message.asText();
        Platform.runLater(() -> textArea.appendText(msg+"\r\n"));
      }catch (Exception e){
        e.printStackTrace();
      }
    });

    var textfield = new TextField();
    textfield.setOnKeyPressed(v ->{
      if(v.getCode()== KeyCode.ENTER){
        ObjectNode message = mapper.createObjectNode();
        message.put("message",textfield.getText());
        try{
          var outputStream = socket.getOutputStream();
          var outputWriter = new PrintWriter(outputStream);
          var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
          outputWriter.write(json+"\r\n");
          outputWriter.flush();
          textfield.setText("");
        }catch (Exception e){
          e.printStackTrace();
        }
      }
    });
    this.setCenter(textArea);
    this.setBottom(textfield);
  }
}
