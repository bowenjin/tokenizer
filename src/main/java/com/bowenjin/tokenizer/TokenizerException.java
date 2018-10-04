package com.bowenjin.tokenizer;

public class TokenizerException extends Exception{
  public TokenizerException(){
    super();
  }
  public TokenizerException(String str){
    super(str);
  }
  public TokenizerException(Exception e){
    super(e);
  }
}
