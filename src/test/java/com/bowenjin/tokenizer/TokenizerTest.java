package com.bowenjin.tokenizer;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import com.bowenjin.regex.InvalidRegexException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TokenizerTest{
  @Test
  public void longestMatch() throws IOException, InvalidRegexException, TokenizerException{
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
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
  public void whitespace() throws IOException, InvalidRegexException, TokenizerException{
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
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

  @Test
  public void ignoreTokens() throws IOException, InvalidRegexException, TokenizerException{ 
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    Set<String> ignoreSet = new HashSet<>();
    map.put("a", "a");
    map.put("aa", "aa");
    ignoreSet.add("aaa");
    String input = "aaaaaaaa";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    Tokenizer tokenizer = new Tokenizer(map, ignoreSet, inputStream);    
    assertEquals("aa", tokenizer.nextToken().getTokenType());   
    assertNull(tokenizer.nextToken());
  }

  @Test
  public void words() throws IOException, InvalidRegexException, TokenizerException{
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    Set<String> ignoreSet = new HashSet<>();
    map.put("apple", "apple");
    map.put("banana", "banana");
    map.put("carrot", "carrot");
    String input = "apple banana\tcarrot\napple";
    ignoreSet.add("\t");
    ignoreSet.add(" ");
    ignoreSet.add("\n");
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    Tokenizer tokenizer = new Tokenizer(map, ignoreSet, inputStream);
    
    assertEquals("apple", tokenizer.nextToken().getTokenType());
    assertEquals("banana", tokenizer.nextToken().getTokenType());
    assertEquals("carrot", tokenizer.nextToken().getTokenType());
    assertEquals("apple", tokenizer.nextToken().getTokenType());
  }


  @Test(expected=TokenizerException.class)
  public void doesThrowTokenizerException() throws IOException, InvalidRegexException, TokenizerException{
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    Set<String> ignoreSet = new HashSet<>();
    map.put("a", "a");
    map.put("b", "b");
    map.put("c", "c");
    ignoreSet.add(" ");
    String input = " a b c \n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    Tokenizer tokenizer = new Tokenizer(map, ignoreSet, inputStream); 
    assertEquals("a", tokenizer.nextToken().getTokenType());
    assertEquals("b", tokenizer.nextToken().getTokenType());
    assertEquals("c", tokenizer.nextToken().getTokenType());
    tokenizer.nextToken(); //should throw exception
  }

  @Test
  public void matchMultipleRegex() throws IOException, InvalidRegexException, TokenizerException{
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    Set<String> ignoreSet = new HashSet<>();
    map.put("a", "a*");
    map.put("b", "b*");
    map.put("c", "c*");
    ignoreSet.add("( )*");
    ignoreSet.add("(\t)*");
    ignoreSet.add("(\n)*");
    ignoreSet.add("(\r)*");
    String input = "abc \t\taa\r\rbb\n\ncc  ";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    Tokenizer tokenizer = new Tokenizer(map, ignoreSet, inputStream); 
    assertEquals("a", tokenizer.nextToken().getTokenType());
    assertEquals("b", tokenizer.nextToken().getTokenType());
    assertEquals("c", tokenizer.nextToken().getTokenType());
    assertEquals("a", tokenizer.nextToken().getTokenType());
    assertEquals("b", tokenizer.nextToken().getTokenType());
    assertEquals("c", tokenizer.nextToken().getTokenType());
    assertNull(tokenizer.nextToken()); //should throw exception
  }

  @Test
  public void sameLengthMatchPriority() throws IOException, InvalidRegexException, TokenizerException{
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    Set<String> ignoreSet = new HashSet<>();
    map.put("PRINT", "print");
    map.put("IDENTIFIER", "[a-zA-Z_][a-zA-Z0-9_]*");
    String input = "print";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    Tokenizer tokenizer = new Tokenizer(map, ignoreSet, inputStream);
    assertEquals("PRINT", tokenizer.nextToken().getTokenType()); 
 
     
    map = new LinkedHashMap<>();
    ignoreSet = new HashSet<>();
    map.put("IDENTIFIER", "[a-zA-Z_][a-zA-Z0-9_]*");
    map.put("PRINT", "print");
    input = "print";
    inputStream = new ByteArrayInputStream(input.getBytes());
    tokenizer = new Tokenizer(map, ignoreSet, inputStream);
    assertEquals("IDENTIFIER", tokenizer.nextToken().getTokenType()); 
  }
}
