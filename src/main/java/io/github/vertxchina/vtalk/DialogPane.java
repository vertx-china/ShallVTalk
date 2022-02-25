package io.github.vertxchina.vtalk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;

import java.io.PrintWriter;
import java.net.Socket;

public class DialogPane extends BorderPane {
  final public SimpleStringProperty simpleStringProperty = new SimpleStringProperty();

  public DialogPane(Socket socket) {
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    this.setPrefSize(screenBounds.getWidth()*3/4, screenBounds.getHeight()*3/4);

    simpleStringProperty.addListener((o,oldValue, newValue)->{
      Platform.runLater(() ->{
        //update ui components here
        System.out.println(newValue);
      });
    });

    var textfield = new TextField();
    textfield.setOnKeyPressed(v ->{
      if(v.getCode()== KeyCode.ENTER){
        System.out.println("entered");
        ObjectMapper mapper = new ObjectMapper();
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
    this.setBottom(textfield);
  }
}
