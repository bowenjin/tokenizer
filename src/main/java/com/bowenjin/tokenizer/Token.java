package com.bowenjin.tokenizer;

class Token{
  private String tokenType;
  private String tokenValue;
  private int lineNum;
  private int colNum;
  public Token(String tokenType, String tokenValue, int lineNum, int colNum){
    this.tokenType = tokenType;
    this.tokenValue = tokenValue;
    this.lineNum = lineNum;
    this.colNum = colNum;
  }
  public String getTokenType(){
    return tokenType;
  }
  public String getTokenValue(){
    return tokenValue;
  }
  public int getLineNumber(){
    return lineNum;
  }
  public int getColumnNumber(){
    return colNum;
  }
}
