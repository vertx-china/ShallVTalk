package io.github.vertxchina.vtalk;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionService extends Service<Void> {

  private final Scene scene;
  private final String server;
  private final int port;

  public ConnectionService(Scene scene, String server, int port) {
    this.scene = scene;
    this.server = server;
    this.port = port;
  }

  @Override
  protected Task<Void> createTask() {
    return new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        try(var socket = new Socket(server, port);
            var out = new PrintWriter(socket.getOutputStream(), true);
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

          final var simpleStringProperty = new SimpleStringProperty();

          Platform.runLater(()->{
            final var dialogPane = new DialogPane(socket);
            dialogPane.simpleStringProperty.bindBidirectional(simpleStringProperty);
            scene.setRoot(dialogPane);
            scene.getWindow().sizeToScene();
            scene.getWindow().centerOnScreen();
          });

          String inputLine;
          while ((inputLine = in.readLine()) != null) {
            simpleStringProperty.set(inputLine);
          }
        }catch (Exception exception){
          exception.printStackTrace();
        }
        return null;
      }
    };
  }
}
