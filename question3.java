import java.io.*;
import java.util.*;


public final class CNF {

static char vee = 'v';
static char wedge = '^';
static char neg = '~';


public static Conjunction parse(String sentence){
   return parseConjunction(sentence);
}




public static Conjunction parse(File infile){
   try{/* read in the file */

      StringBuffer sb = new StringBuffer();
      Reader reader = new BufferedReader(new FileReader(infile));
      int c = -1;

      while((c = reader.read()) != -1)
       sb.append((char) c);
      return parse(sb.toString());
   }
   catch(IOException e){
      System.out.println("Error reading file: " + infile);
      return null;
   }
}


public static String pl2dimacs( File cnfFile ){
    Conjunction c = parse( cnfFile );
    return pl2dimacs( c, cnfFile.toString()+".dimacs" );
}

public static String pl2dimacs( File cnfFile, String dimacsFile ){
    Conjunction c = parse( cnfFile );
    return pl2dimacs( c, dimacsFile );
}

public static String pl2dimacs( Sentence cnfSentence, String dimacsFile ){
    String mapFile = dimacsFile+".map" ;
    if( cnfSentence instanceof Conjunction ){
      Conjunction cnf = (Conjunction)cnfSentence;
      Set vars = cnf.getVariables();
      List disjunctions = cnf.getClauses();
      int numVars = vars.size();
      int numDisjunctions = disjunctions.size();
      HashMap nameToInt = new HashMap();
    
      String valenceStr = "";
      String varName = "";
      int counter = 1;
    
      String dimacsString = "c original CNF sentence: \n";
      //dimacsString += "c "+cnf.toString()+"\nc\nc\n";
      dimacsString += "p cnf "+numVars+" "+numDisjunctions+" \n";
      String mapString = "";
    
      for( int i = 0; i<numDisjunctions; i++){
   List units = ( (Disjunction)disjunctions.get(i) ).getClauses();
   int numUnits = units.size();
   for( int j = 0; j< numUnits; j++ ){
      Sentence v = (Sentence)units.get(j);
      if( v instanceof Variable ){
        varName = ((Variable)v).toString();
        valenceStr = "";
      }
      else if( v instanceof Negation ){
        varName = ((((Negation)v).getVariables().toArray())[0]).toString();
        valenceStr = "-";
      }
      Object intName = nameToInt.get( varName );
      if( intName == null ){
        intName = new Integer( counter );
        nameToInt.put( varName, intName);
        mapString += varName + " " + counter+ "\n";
        counter++;
      }
      dimacsString += ""+ valenceStr + intName.toString() +" ";
   }
   dimacsString += "0 \n"; //end the clause
      }
    
    
      try{
   Writer writer = new BufferedWriter(new FileWriter(dimacsFile));
   writer.write(dimacsString);
   writer.close();
      }
      catch(IOException e){ System.out.println("IO Error writing file " + dimacsFile + ".");}
    
      try{
   Writer writer = new BufferedWriter(new FileWriter(mapFile));
   writer.write(mapString);
   writer.close();
      }
      catch(IOException e){ System.out.println("IO Error writing file " + mapFile + ".");}
    
    }
    else{
      System.out.println(" Didn't get a conjunction. Exiting. ");
      return null;
    }
    return mapFile;
}


public static Interpretation zchaffToInterpretation( String zchaffOutputFile, String mapFile ){

    BufferedReader in;      
    String line =null;
    int space = -1;

    HashMap names = new HashMap();
    Boolean assignment = null;
    Integer intName = null;
    String asmt = "";
    Interpretation interp= new Interpretation();
  
    // read in the map of the names
    try{
      in = new BufferedReader(new FileReader( mapFile ));
      line = in.readLine();
      while( line != null ){
   space = line.indexOf(' ');
   String name = line.substring(0,space);
   intName =new Integer( Integer.parseInt( line.substring( space+1 )));
   names.put( intName , name );
   line = in.readLine();
      }
    }catch(IOException e){
      System.out.println("Error reading file: " + mapFile);
      return null;
    }
  

    // read in the zchaff output
    try{
    in = new BufferedReader(new FileReader( zchaffOutputFile ));
    line = in.readLine();
    while( line != null ){
      if( line.startsWith( "Verify Solution successful" ) ){//next line contains the assignment
   line = in.readLine();
   space = line.indexOf(' ');
  
   asmt = line.substring( 0,space );
   intName = new Integer( Math.abs( Integer.parseInt( asmt )) );
   assignment = getAssignment( asmt, intName );
   interp.put( new Variable( (String)names.get( intName )), assignment );
  
   line = line.substring( space+1 );
   space = line.indexOf(' ');
   while( space != -1 ){

      asmt = line.substring( 0,space );
      intName = new Integer( Math.abs( Integer.parseInt( asmt )) );
      assignment = getAssignment( asmt, intName );
      interp.put( new Variable( (String)names.get( intName )), assignment );
      //System.out.println( " putting "+intName+" / "+assignment );
      line = line.substring( space+1 );//next iteration
      space = line.indexOf(' ');
   }
      }
    
      line = in.readLine();
    }
    }catch(IOException e){
      System.out.println("Error reading file: " + zchaffOutputFile);
      return null;
    }
    if( interp.isEmpty() )
      return null;
  
    return interp;
}
private static Boolean getAssignment( String asmt, Integer intNameOut ){
    int numValue = Integer.parseInt( asmt );
    Boolean assignment = null;
    if( numValue < 0 ){
      intNameOut = new Integer( Math.abs( numValue ));
      assignment = Boolean.FALSE;
    }
    else{
      intNameOut = new Integer( Math.abs( numValue ));
      assignment = Boolean.TRUE;
    }
    return assignment;
}

/* attempts to parse a conjunction. */
private static Conjunction parseConjunction(String sentence) {
   sentence = trimParens(sentence);
   List list = new ArrayList();
   int index = sentence.indexOf(wedge);
  
   if (index == -1){
      list.add(parseDisjunction(sentence));
      sentence = "";
   }

   while(index != -1){
      list.add(parseDisjunction(sentence.substring(0, index)));
      sentence = trimParens(sentence.substring(index+1));
      index = sentence.indexOf(wedge);
   }

   if (!(sentence.length() == 0))
      list.add(parseDisjunction(sentence));
  
   return new Conjunction(list);
}
  
/* attempts to parse a disjunction. */
private static Sentence parseDisjunction(String sentence) {
   sentence = trimParens(sentence);
   List list = new ArrayList();
   int index = sentence.indexOf(vee);
  
   if (index == -1){
      list.add(parseLiteral(sentence));
      sentence = "";
   }

   while(index != -1){
      list.add(parseLiteral(sentence.substring(0, index)));
      sentence = trimParens(sentence.substring(index+1));
      index = sentence.indexOf(vee);
   }

   if (!(sentence.length() == 0))
      list.add(parseLiteral(sentence));

   return new Disjunction(list);
}

/* attempts to parse a Literal. If none is present, causes a
   * runtime exception. */
private static Sentence parseLiteral(String sentence){
   sentence = trimParens(sentence);
   int index = sentence.indexOf(' ');

   /* error check */
   if(sentence.length() == 0)
      throw new RuntimeException("Parse Error: Missing Variable.");
  
   boolean negated = (sentence.charAt(0) == neg);

   if(negated)
      sentence = trimParens(sentence.substring(1));

   if(sentence.length() < 1 || !Character.isLetter(sentence.charAt(0)))
      throw new RuntimeException("Parse Error: unexpected literal "
                               + sentence);
   Variable v = new Variable(sentence);
   if(negated)
      return new Negation(v);
   else
      return v;
}

private static String trimParens(String input){
   input = input.trim();

   while(input.startsWith("(") && input.endsWith(")")){
      int depth = 1;
      boolean matched = true;
      for(int i = 1; i < input.length()-1; i++){
       char c = input.charAt(i);
       if(c == '(')
          depth++;
       else if (c == ')')
          depth--;
       if(depth == 0)
          matched = false;
      }
  
      if(depth == 1 && matched)
       input = input.substring(1, input.length()-1).trim();
      else if (depth == 1)
       break;
      else if (depth != 1)
       throw new RuntimeException("Parse Err: Parenthesis Mismatch.");
   }

   return input;
}

private static final int NUMVARS = 26;

public static void main(String[] args){
   if(args.length != 1 && args.length != 2){
      System.out.println("Usage:");
      System.out.print("\t java techniques.PL.CNF ");
      System.out.println("<clause/var ratio> " + "filename.cnf");
      return;
   }
   double ratio = Double.parseDouble(args[0]);
   int numClauses = Math.max(1, (int) Math.round(ratio * NUMVARS));
   String instance = randInstance(numClauses);

   if(args.length == 1){
      System.out.println(instance);
      return;
   }
   try{
      Writer writer = new BufferedWriter(new FileWriter(args[1]));
      writer.write(instance);
      writer.close();
   }
   catch(IOException e){
      System.out.println("IO Error writing file " + args[1] + ".");
   }
}



public static String randInstance(int numClauses){
   StringBuffer sb = new StringBuffer(randDisjunction());
   for(int i = 1; i < numClauses; i++)
      sb.append(" " + CNF.wedge + "\n" + randDisjunction());
   return sb.toString();
}


public static String randDisjunction(){
   return "(" + randLiteral()
      + " " + CNF.vee + " " + randLiteral()
      + " " + CNF.vee + " " + randLiteral() + ")";
}


public static String randLiteral(){
   return (coinFlip() ? "~" : "") + (char)('A' + randInt(0, 25));
}


public static boolean coinFlip(){
return (Math.random() < 0.5);
}

/** returns a random integer between a and b, inclusive. */
public static int randInt(int a, int b) {
   return((int)(Math.floor(Math.random()*(b-a+1))+a));
}

}