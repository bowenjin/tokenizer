package com.bowenjin.tokenizer;

import org.junit.Test;
import static org.junit.Assert.*;
import com.bowenjin.regex.InvalidRegexException;

public class TokenMatcherTest{
  @Test
  public void simple() throws InvalidRegexException{
    TokenMatcher matcher = new TokenMatcher("aaa", "aaa");
    assertFalse(matcher.process('a'));
    assertFalse(matcher.process('a'));
    assertTrue(matcher.process('a'));
  } 
}
