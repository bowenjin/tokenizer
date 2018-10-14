package com.bowenjin.tokenizer;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.bowenjin.regex.InvalidRegexException;

/**
 * Creates tokens of different types from a character stream
 * based on regex descriptions of those tokens
 */
public class Tokenizer{
  List<TokenMatcher> tokenMatchers = new ArrayList<>();
  Queue<Character> backtrackBuffer = new LinkedList<>();
  List<Character> inputBuffer = new ArrayList<>();
  BufferedReader inputReader;
  int lineNum = 0;
  int colNum = 0;
  
  private static final String IGNORE_TOKEN_TYPE = "_ignore";
  
  /**
   * @param tokenTypeToRegexMap a LinkedHashMap of token types to regex patterns that describe
   * those token types, order of insertion matters for resolving tokens when the regex patterns match
   * character sequences of the same length, in which case the token that appears first in the Map will
   * be the one resolved
   * @param ignoreRegexSet a Set of regex patterns describing tokens to ignore (not return)
   * @param inputStream a stream of characters to tokenize
   */
  public Tokenizer(LinkedHashMap<String, String> tokenTypeToRegexMap, InputStream inputStream) throws IOException, InvalidRegexException{
    int lineNum = 1;
    int colNum = 1;
    for(Map.Entry<String, String> entry: tokenTypeToRegexMap.entrySet()){
      String tokenType = entry.getKey();
      String regex = entry.getValue();
      TokenMatcher matcher = new TokenMatcher(tokenType, regex);
      tokenMatchers.add(matcher);      
    }
    Collections.reverse(tokenMatchers); //reverse order of insertion, initially first inserted element has highest priority,
                                        //now the first element has the lowest priority.                      
    inputReader = new BufferedReader(new InputStreamReader(inputStream));
  }

  public Tokenizer(LinkedHashMap<String, String> tokenTypeToRegexMap, Set<String> ignoreRegexSet, InputStream inputStream) throws IOException, InvalidRegexException{
    this(tokenTypeToRegexMap, inputStream);
    for(String ignoreRegex: ignoreRegexSet){
      TokenMatcher matcher = new TokenMatcher(IGNORE_TOKEN_TYPE, ignoreRegex);
      tokenMatchers.add(matcher);
    }
  } 

  /**
   * @return the next non-ignore token tokenized from the input stream
   * or null if end of input is reached, always returns the longest possible
   * match for a token
   */
  public Token nextToken() throws IOException, TokenizerException{ 
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
      boolean hasPossibleMatches = false; //have all NFAs reached dead ends? 
      for(TokenMatcher tokenMatcher: tokenMatchers){
        if(tokenMatcher.process(c)){
          String tokenType = tokenMatcher.getTokenType();
          String tokenValue = tokenBuilder.toString();
          inputBuffer.clear();
          lastMatchingToken = new Token(tokenType, tokenValue, lineNum, colNum);
        }
        if(tokenMatcher.hasPossibleMatches()){
          hasPossibleMatches = true; 
        }
      }
      if(!hasPossibleMatches){
        if(lastMatchingToken == null){ 
          throw new TokenizerException("Could not find a matching token for " + tokenBuilder.toString());
        }else{
          backtrack(lastMatchingToken.getLineNumber(), lastMatchingToken.getColumnNumber());
          return lastMatchingToken.getTokenType().equals(IGNORE_TOKEN_TYPE) ? nextToken() : lastMatchingToken;
        }
      }
    }
    if(lastMatchingToken != null){
      backtrack(lastMatchingToken.getLineNumber(), lastMatchingToken.getColumnNumber());
      return lastMatchingToken.getTokenType().equals(IGNORE_TOKEN_TYPE) ? nextToken() : lastMatchingToken;
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
