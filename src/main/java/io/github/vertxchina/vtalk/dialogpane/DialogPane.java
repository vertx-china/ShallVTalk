package io.github.vertxchina.vtalk.dialogpane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;

import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static io.github.vertxchina.vtalk.Application.GLOBAL_FONT_FAMILY;

public class DialogPane extends BorderPane {
  final public SimpleStringProperty simpleStringProperty = new SimpleStringProperty();
  ObjectMapper mapper = new ObjectMapper();

  public DialogPane(Map parameters) {
    Socket socket = (Socket) parameters.get("socket");
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    this.setPrefSize(screenBounds.getWidth() * 3 / 4, screenBounds.getHeight() * 3 / 4);
    this.setStyle(GLOBAL_FONT_FAMILY);

    var rightUserList = new RightView();
    var centerPane = new CenterPane();
    var bottomPane = new BottomPane(socket, centerPane);

    this.setRight(rightUserList);
    this.setCenter(centerPane);
    this.setBottom(bottomPane);

    BorderPane.setMargin(centerPane, new Insets(10, 5, 5, 10));
    BorderPane.setMargin(rightUserList, new Insets(10, 10, 5, 5));
    BorderPane.setMargin(bottomPane, new Insets(5, 10, 10, 10));

    simpleStringProperty.addListener((o, oldValue, newValue) -> {
      try {
        JsonNode node = mapper.readTree(newValue);
        if (node.has("message")) centerPane.appendChatHistory(node);
        if (node.has("nicknames")) rightUserList.updateList(node);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    bottomPane.sendSimpleMessage("nickname", parameters.get("nickname").toString());
  }
}
