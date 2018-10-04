package com.bowenjin.tokenizer;

import org.junit.Test;
import static org.junit.Assert.*;
import com.bowenjin.regex.InvalidRegexException;

/**
 * Test space partitions:
 * regex with concatentation, regex with '*', regex with '|', regex with '+', regex with parenthesis 
 */
public class TokenMatcherTest{
  @Test
  public void simple() throws InvalidRegexException{
    TokenMatcher matcher = new TokenMatcher("aaa", "aaa");
    assertFalse(matcher.process('a'));
    assertFalse(matcher.process('a'));
    assertTrue(matcher.process('a'));
    assertFalse(matcher.process('a'));
  }

  @Test
  public void differentChars() throws InvalidRegexException{
    TokenMatcher matcher = new TokenMatcher("abc", "abc");
    assertFalse(matcher.process('a'));
    assertFalse(matcher.process('b'));
    assertTrue(matcher.process('c'));
    assertFalse(matcher.process('c'));
  }

  @Test
  public void multipleRegex() throws InvalidRegexException{
    TokenMatcher matcher = new TokenMatcher("a*|b*|c*", "a*|b*|c*");
    String [] trueInputs = {"a", "b", "c", "aa", "bb", "cc"};
    String [] falseInputs = {"ab", "bc", "ac", "da", "db", "dc"};
    for(String testCase: trueInputs){
      matcher.reset();
      for(int i = 0; i < testCase.length() - 1; i++){
        matcher.process(testCase.charAt(i));
      }
      assertTrue(matcher.process(testCase.charAt(testCase.length() - 1)));
    }
    for(String testCase: falseInputs){
      matcher.reset();
      for(int i = 0; i < testCase.length() - 1; i++){
        matcher.process(testCase.charAt(i));
      }
      assertFalse(matcher.process(testCase.charAt(testCase.length() - 1)));
    }
  }
   
  @Test
  public void multipleMatches() throws InvalidRegexException{
    TokenMatcher matcher = new TokenMatcher("a*", "a*");
    assertTrue(matcher.process('a'));
    assertTrue(matcher.process('a'));
    assertTrue(matcher.process('a'));
    assertFalse(matcher.process('b'));
    assertFalse(matcher.process('a'));
  }
 
  @Test
  public void noMatchAfterFirstMismatch() throws InvalidRegexException{
    TokenMatcher matcher = new TokenMatcher("a*", "a*");
    assertTrue(matcher.process('a'));
    assertFalse(matcher.process('b'));
    assertFalse(matcher.process('a'));
  } 
 
}
