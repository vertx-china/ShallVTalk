package io.github.vertxchina.nodes;

public class NumberTextField extends PersistentPromptTextField {
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
    if(this.getText().trim().isEmpty()) return -1;
    return Integer.parseInt(this.getText());
  }
}
