package io.github.vertxchina.vtalk;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;

import java.io.*;
import java.net.Socket;

public class ConnectionService extends Service<Void> {

  private final Scene scene;
  private String nickname;
  private final String server;
  private final int port;

  public ConnectionService(Scene scene, String nickname, String server, int port) {
    this.scene = scene;
    this.nickname = nickname;
    this.server = server;
    this.port = port;
  }

  @Override
  protected Task<Void> createTask() {
    return new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        try (var socket = new Socket(server, port);
             var isr = new InputStreamReader(socket.getInputStream());
             var in = new BufferedReader(isr);
             var charArrayWriter = new CharArrayWriter()) {

          final var simpleStringProperty = new SimpleStringProperty();

          Platform.runLater(() -> {
            final var dialogPane = new DialogPane(socket);
            dialogPane.simpleStringProperty.bindBidirectional(simpleStringProperty);
            scene.setRoot(dialogPane);
            scene.getWindow().sizeToScene();
            scene.getWindow().centerOnScreen();
          });

          char[] buffer = new char[1024 * 64];
          int length;
          while ((length = in.read(buffer)) > -1) {
            charArrayWriter.write(buffer, 0, length);
            if (charArrayWriter.size() > 2) {
              var receivedString = charArrayWriter.toString();
              if (receivedString.endsWith("\r\n")) {
                var strings = receivedString.split("\r\n");
                for (var string : strings) {
                  if (!string.trim().equals("")) {
                    simpleStringProperty.set(string);
                  }
                }
                charArrayWriter.reset();
              }
            }
          }

        } catch (Exception exception) {
          exception.printStackTrace();
        }
        return null;
      }
    };
  }
}
