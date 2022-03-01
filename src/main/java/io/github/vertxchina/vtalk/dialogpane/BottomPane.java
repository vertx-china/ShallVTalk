package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class BottomPane extends HBox {
  TextArea textarea = new TextArea();
  ObjectMapper mapper = new ObjectMapper();
  PrintWriter printWriter;

  public BottomPane(Socket socket) {
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

    textarea.addEventFilter(KeyEvent.KEY_PRESSED, v ->{
      if(v.getCode() == KeyCode.ENTER){
        if(v.isShiftDown()){
          textarea.insertText(textarea.getCaretPosition(),"\n");
        }else{
          sendSimpleMessage("message", textarea.getText());
          textarea.setText("");
        }
        v.consume();
      }
    });

    textarea.prefWidthProperty().bind(this.widthProperty());
    this.getChildren().add(textarea);
  }

  public void sendSimpleMessage(String key, String value){
    ObjectNode message = mapper.createObjectNode();
    message.put(key, value);
    try {
      var json = mapper.writeValueAsString(message);
      printWriter.write(json + "\r\n");
      printWriter.flush();
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
