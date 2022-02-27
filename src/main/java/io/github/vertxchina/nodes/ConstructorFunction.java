package io.github.vertxchina.nodes;

import javafx.scene.Parent;

import java.util.Map;

@FunctionalInterface
public interface ConstructorFunction {
  Parent construct(Map parameters);
}
