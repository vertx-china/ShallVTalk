package io.github.vertxchina.nodes;

import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.HashMap;
import java.util.Map;

public class NavigatableScene extends Scene {
  public NavigatableScene(ConstructorFunction function, Map parameters){
    super(function.construct(parameters));
    routes.put("/",function);
  }

  public Map<String, ConstructorFunction> routes = new HashMap<>();

  public NavigatableScene route(String path, ConstructorFunction function){
    routes.put(path, function);
    return this;
  }

  public Parent navigate(String path){
    return navigate(path, new HashMap<>());
  }

  public Parent navigate(String path, Map parameters){
    var root = this.routes.get(path).construct(parameters);
    this.setRoot(root);
    this.getWindow().sizeToScene();
    this.getWindow().centerOnScreen();
    return root;
  }
}
