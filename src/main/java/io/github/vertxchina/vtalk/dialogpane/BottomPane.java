package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.PrintWriter;
import java.net.Socket;

public class BottomPane extends HBox {
  CenterPane centerPane;
  TextArea textarea = new TextArea();
  ColorPicker colorPicker = new ColorPicker(Color.BLACK);
  ObjectMapper mapper = new ObjectMapper();
  PrintWriter printWriter;

  public BottomPane(Socket socket, CenterPane centerPane) {
    this.centerPane = centerPane;
    try{
      var outputStream = socket.getOutputStream();
      printWriter = new PrintWriter(outputStream);
    }catch (Exception e){
      e.printStackTrace();
      try {
        socket.close();
        if(printWriter!=null) printWriter.close();
      }catch (Exception ee){
        ee.printStackTrace();
      }
    }

    textarea.addEventFilter(KeyEvent.KEY_PRESSED, v -> {
      if(v.getCode() == KeyCode.ENTER){
        if(v.isShiftDown()){
          textarea.insertText(textarea.getCaretPosition(),"\n");
        }else{
          if(colorPicker.getValue().equals(Color.BLACK)){
            var sentJson = sendSimpleMessage("message", textarea.getText());
            centerPane.appendChatHistory(sentJson.put("id","_"));
          }else{
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("message", textarea.getText());
            objectNode.put("color", toWebColorCode(colorPicker.getValue()));
            sendJson(objectNode);
            centerPane.appendChatHistory(objectNode.put("id","_"));
          }
          textarea.clear();
        }
        v.consume();
      }
    });

    this.setSpacing(10);

    textarea.prefWidthProperty().bind(this.widthProperty().subtract(colorPicker.widthProperty()).subtract(20));

    colorPicker.setOnAction(event -> textarea.setStyle("-fx-text-fill:"+ toWebColorCode(colorPicker.getValue())+";"));

    this.getChildren().addAll(textarea, colorPicker);
  }

  public ObjectNode sendSimpleMessage(String key, String value){
    ObjectNode message = mapper.createObjectNode();
    message.put(key, value);
    sendJson(message);
    return message;
  }

  public void sentDebugMessage(String rawString){
    printWriter.write(rawString + "\r\n");
    printWriter.flush();
  }

  public void sendJson(ObjectNode jsonNode){
    try {
      var json = mapper.writeValueAsString(jsonNode);
      printWriter.write(json + "\r\n");
      printWriter.flush();
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public String toWebColorCode(Color color )
  {
    return String.format( "#%02X%02X%02X",
        (int)( color.getRed() * 255 ),
        (int)( color.getGreen() * 255 ),
        (int)( color.getBlue() * 255 ) );
  }
}
