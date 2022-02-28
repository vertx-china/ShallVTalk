package io.github.vertxchina.vtalk;

import io.github.vertxchina.nodes.NavigatableScene;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionService extends Service<Void> {

  private final NavigatableScene scene;
  private String nickname;
  private final String server;
  private final int port;

  public ConnectionService(NavigatableScene scene, String nickname, String server, int port) {
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
            var parameters = new HashMap<>();
            parameters.put("socket", socket);
            DialogPane dialogPane = (DialogPane)(scene.navigate("/dialog", parameters));
            dialogPane.simpleStringProperty.bind(simpleStringProperty);
            dialogPane.sendSimpleMessage(socket, "nickname", nickname);
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
                  if (!string.trim().isEmpty()) {
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
