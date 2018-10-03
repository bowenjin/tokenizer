package com.bowenjin.tokenizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import com.bowenjin.regex.InvalidRegexException;

public class Tokenizer{
  List<TokenMatcher> tokenMatchers = new ArrayList<>();
  Queue<Character> backtrackBuffer = new LinkedList<>();
  List<Character> inputBuffer = new ArrayList<>();
  BufferedReader inputReader;
  int lineNum = 0;
  int colNum = 0;
  public Tokenizer(Map<String, String> tokenTypeToRegexMap, InputStream inputStream) throws IOException, InvalidRegexException{
    int lineNum = 1;
    int colNum = 1;
    for(Map.Entry<String, String> entry: tokenTypeToRegexMap.entrySet()){
      String tokenType = entry.getKey();
      String regex = entry.getValue();
      TokenMatcher matcher = new TokenMatcher(tokenType, regex);
      tokenMatchers.add(matcher);      
    }
    inputReader = new BufferedReader(new InputStreamReader(inputStream));
  }

  /**
   * @return the next token tokenized from the input stream
   * or null if end of input is reached
   */
  public Token nextToken() throws IOException{ 
    StringBuilder tokenBuilder = new StringBuilder();
    Token lastMatchingToken = null;
    int ci = 0;
    while(backtrackBuffer.size() > 0 || (ci = inputReader.read()) != -1){
      char c = backtrackBuffer.size() > 0 ? backtrackBuffer.remove() : (char)ci;
      tokenBuilder.append(c);
      inputBuffer.add(c);
      if(c == '\n'){
        lineNum++;
        colNum = 1;
      }else{
        colNum++;
      } 
      int numPossibleMatches = 0;
      for(TokenMatcher tokenMatcher: tokenMatchers){
        if(tokenMatcher.process(c)){
          String tokenType = tokenMatcher.getTokenType();
          String tokenValue = tokenBuilder.toString();
          inputBuffer.clear();
          lastMatchingToken = new Token(tokenType, tokenValue, lineNum, colNum);
        }
        if(tokenMatcher.hasPossibleMatches()){
          numPossibleMatches++;
        }
      }
      if(numPossibleMatches == 0){
        if(lastMatchingToken == null){ 
          throw new RuntimeException("Could not find a matching token for " + tokenBuilder.toString());
        }else{
          backtrack(lastMatchingToken.getLineNumber(), lastMatchingToken.getColumnNumber());
          return lastMatchingToken;
        }
      }
    }
    if(lastMatchingToken != null){
      backtrack(lastMatchingToken.getLineNumber(), lastMatchingToken.getColumnNumber());
      return lastMatchingToken;
    }else{
      return null;
    }
  }

  /**
   * Backtrack to a previous point in the stream (using the backtrackBuffer)
   * @param lineNum line number to backtrack to
   * @param colNum column number to backtrack to
   */
  private void backtrack(int lineNum, int colNum){
    backtrackBuffer.addAll(inputBuffer);
    inputBuffer.clear();
    resetAllTokenMatchers();
    this.lineNum = lineNum;
    this.colNum = colNum;
  }
  
  /**
   * Helper method to reset all the TokenMatchers used by this Tokenizer
   */
  private void resetAllTokenMatchers(){
    for(TokenMatcher tokenMatcher: tokenMatchers){
      tokenMatcher.reset();
    }
  }
  
  
}
