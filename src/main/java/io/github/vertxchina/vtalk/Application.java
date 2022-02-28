package io.github.vertxchina.vtalk;

import io.github.vertxchina.nodes.NavigatableScene;
import io.github.vertxchina.vtalk.dialogpane.DialogPane;
import io.github.vertxchina.vtalk.indexpane.IndexPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class Application extends javafx.application.Application {
  public static Font GLOBAL_FONT;
  public static String GLOBAL_FONT_FAMILY;

  @Override
  public void start(Stage stage) throws IOException {
    stage.setTitle("Shall we talk?");
    var scene = new NavigatableScene((parameters) -> new IndexPane(), new HashMap<>());
    scene.route("/dialog", DialogPane::new);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    GLOBAL_FONT = Font.loadFont(Application.class.getResourceAsStream("/font/zcool.ttf"), 12);
    GLOBAL_FONT_FAMILY = "-fx-font-family: "+GLOBAL_FONT.getFamily();
    launch(Application.class, args);
  }
}