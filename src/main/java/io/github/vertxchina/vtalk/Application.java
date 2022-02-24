package io.github.vertxchina.vtalk;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
  @Override
  public void start(Stage stage) throws IOException {
    Scene scene = new Scene(new IndexPane());
    stage.setTitle("Shall we talk?");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}