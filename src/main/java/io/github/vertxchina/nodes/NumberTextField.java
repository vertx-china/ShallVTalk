package io.github.vertxchina.nodes;

import javafx.scene.control.TextField;

public class NumberTextField extends TextField {
  public NumberTextField(int port) {
    this.setText(""+port);
  }

  @Override
  public void replaceText(int start, int end, String text)
  {
    if (validate(text))
    {
      super.replaceText(start, end, text);
    }
  }

  @Override
  public void replaceSelection(String text)
  {
    if (validate(text))
    {
      super.replaceSelection(text);
    }
  }

  private boolean validate(String text)
  {
    return text.matches("[0-9]*");
  }

  public int getNumber(){
    return Integer.parseInt(this.getText());
  }
}
