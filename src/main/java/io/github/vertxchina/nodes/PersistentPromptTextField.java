package io.github.vertxchina.nodes;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TextField;

public class PersistentPromptTextField extends TextField {
  public PersistentPromptTextField() {
    super();
    this.styleProperty().bind(Bindings.createStringBinding(()->"-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);"));
  }

  public PersistentPromptTextField(String s) {
    super(s);
    this.styleProperty().bind(Bindings.createStringBinding(()->"-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);"));
  }
}
