import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Tokenizer{
  List<TokenMatcher> tokenMatchers = new ArrayList<>();
  BufferedReader inputReader;
  int row = 0;
  int col = 0;
  public Tokenizer(String fileName, InputStream inputStream) throws IOException{
    BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
    StringBuilder sb = new StringBuilder;     
    int lineNum = 1;
    int colNum = 1;
    String line;
    while((line = bufferedReader.readLine()) != null){
      int index = line.indexOf(':');
      if(index == -1){
        throw new RuntimeExeption("No ':' found on line " + lineCount);
      }
      String tokenType = line.substring(0, index);
      String regex = line.substring(index + 1);
      TokenMatcher matcher = new TokenMatcher(tokenType, regex);
      tokenMatchers.add(matcher);      
    }
    inputReader = new BufferedReader(InputStreamReader(inputStream));
  }

  /**
   * @return the next token tokenized from the input stream 
   */
  public Token nextToken(){ 
    StringBuilder tokenBuilder = new StringBuilder();
    while(true){
      char c = inputReader.read();
      tokenBuilder.append(c);
      if(c == '\n'){
        lineNum++;
        colNum = 1;
      }else{
        colNum++;
      } 
      int numPossibleMatches = 0;
      for(TokenMatcher tokenMatcher: tokenMatchers){
        if(tokenMatcher.process(inputReader.read())){
          String tokenType = tokenMatcher.getTokenType();
          String tokenValue = tokenBuilder.toString();
          resetAllTokenMatchers();
          return new Token(tokenType, tokenValue, lineNum, colNum);
        }
        if(tokenMatcher.hasPossibleMatches()){
          numPossibleMatches++;
        }
      }
      if(numPossibleMatches == 0){
        throw new RuntimeException("Could not find a matching token for " + tokenBuilder.toString());
      }
    }
  }

  private void resetAllTokenMatchers(){
    for(TokenMatcher tokenMatcher: tokenMatchers){
      tokenMatcher.reset();
    }
  }
  
  
}
