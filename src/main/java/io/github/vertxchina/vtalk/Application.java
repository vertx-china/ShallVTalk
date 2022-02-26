package io.github.vertxchina.vtalk;

import javafx.css.Stylesheet;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
  public static Font GLOBAL_FONT;
  public static String GLOBAL_FONT_FAMILY;

  @Override
  public void start(Stage stage) throws IOException {
    var root = new IndexPane();
    root.setStyle(GLOBAL_FONT_FAMILY);
    Scene scene = new Scene(root);
    stage.setTitle("Shall we talk?");
    stage.setScene(scene);
    stage.show();
    stage.sizeToScene();
  }

  public static void main(String[] args) {
    GLOBAL_FONT = Font.loadFont(Application.class.getResourceAsStream("/font/zcool.ttf"), 12);
    GLOBAL_FONT_FAMILY = "-fx-font-family: "+GLOBAL_FONT.getFamily();
    launch(Application.class, args);
  }
}