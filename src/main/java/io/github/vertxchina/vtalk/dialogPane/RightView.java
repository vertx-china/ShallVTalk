package io.github.vertxchina.vtalk.dialogPane;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;

public class RightView extends ListView<String> {
  public RightView() {
    setPadding(new Insets(10));
  }

  public void updateList(JsonNode node){
    Platform.runLater(()->{
      getItems().clear();
      var nicknames = node.get("nicknames");
      for(int i=0;i<nicknames.size();i++){
        var nickname = nicknames.get(i).asText();
        getItems().add(nickname);
      }
    });
  }
}
