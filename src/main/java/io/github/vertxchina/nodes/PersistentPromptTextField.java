package io.github.vertxchina.nodes;

import javafx.scene.control.TextField;

public class PersistentPromptTextField extends TextField {
  public PersistentPromptTextField() {
    super();
    this.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
  }

  public PersistentPromptTextField(String s) {
    super(s);
    this.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
  }
}
