package io.github.vertxchina.vtalk;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionService extends Service<Void> {

  private final String server;
  private final int port;

  public ConnectionService(String server, int port) {
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
          System.out.println(socket.isConnected());
          String inputLine;
          while ((inputLine = in.readLine()) != null) {
            if ("\r\n".equals(inputLine)) {
              out.println("good bye");
              break;
            }
            out.println(inputLine);
          }
        }catch (Exception exception){
          exception.printStackTrace();
        }
        return null;
      }
    };
  }
}
