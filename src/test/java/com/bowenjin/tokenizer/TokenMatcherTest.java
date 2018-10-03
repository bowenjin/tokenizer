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
