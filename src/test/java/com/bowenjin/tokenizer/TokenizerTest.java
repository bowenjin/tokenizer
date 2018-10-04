package com.bowenjin.tokenizer;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import com.bowenjin.regex.InvalidRegexException;
import java.util.Map;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TokenizerTest{
  @Test
  public void longestMatch() throws IOException, InvalidRegexException{
    Map<String, String> map = new HashMap<>();
    map.put("a", "a");
    map.put("aa", "aa");
    map.put("aaa", "aaa");
    String input = "aaaaaaaa";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    Tokenizer tokenizer = new Tokenizer(map, inputStream);
    
    assertEquals("aaa", tokenizer.nextToken().getTokenType());
    assertEquals("aaa", tokenizer.nextToken().getTokenType());
    assertEquals("aa", tokenizer.nextToken().getTokenType());
    assertNull(tokenizer.nextToken());
  }

  @Test
  public void whitespace() throws IOException, InvalidRegexException{
    Map<String, String> map = new HashMap<>();
    map.put("space", " ");
    map.put("tab", "\t");
    map.put("newline","\n");
    map.put("return", "\r");
    String input = " \t\n\r";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    Tokenizer tokenizer = new Tokenizer(map, inputStream);
   
    assertEquals("space", tokenizer.nextToken().getTokenType());   
    assertEquals("tab", tokenizer.nextToken().getTokenType());   
    assertEquals("newline", tokenizer.nextToken().getTokenType());   
    assertEquals("return", tokenizer.nextToken().getTokenType());   
  }
}
