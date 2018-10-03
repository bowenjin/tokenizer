package com.bowenjin.tokenizer;

import com.bowenjin.regex.Matcher;
import com.bowenjin.regex.InvalidRegexException;

class TokenMatcher extends Matcher{
  String tokenType;
  public TokenMatcher(String tokenType, String regex) throws InvalidRegexException{
    super(regex);
    this.tokenType = tokenType;
    reset(); //add starting state
  }
  /**
   * Process a single character using the FSA
   * @param c a single input to the FSA
   * @return whether one of the current states of the FSA is the accept state
   * after processing the character.
   */
  boolean process(char c){
    lambdaClosure();
    transition(c);
    lambdaClosure();
    return hasReachedAcceptState();
  }

  /**
   * @return whether the current state of the Matcher can eventually
   * result in a match
   */ 
  boolean hasPossibleMatches(){
    return currentStates.size() != 0;
  }
  
  /**
   * @return the String representing the token type associated
   * with this TokenMatcher
   */
  String getTokenType(){
    return tokenType;
  }  
}
