package io.github.vertxchina.vtalk;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    Label text = new Label("hello world");
    Scene scene = new Scene(new Pane(text), 320, 240);
    stage.setTitle("Hello!");
    stage.setScene(scene);
    stage.show();

    var animationTimer = new AnimationTimer(){
      @Override
      public void handle(long now) {
        text.setLayoutX(text.getLayoutX()+1);
        text.setLayoutY(text.getLayoutY()+1);
      }
    };
    animationTimer.start();

  }

  public static void main(String[] args) {
    launch();
  }
}