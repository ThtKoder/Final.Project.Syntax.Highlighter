//Syntax Highlighter 1.0
//S D - ThtKoder
//Collaborators: Poe, Stackoverflow (users: queeg (mainly)) Ben (C.) and Ms. N's constant support
//Acknowledgements: RabaDabaDoba (keywords.txt)
//11-1-23

package com.thtkoder.dev;
//just having fun

import java.util.*;

import java.io.File;
import java.io.*;

import java.util.regex.Matcher;
//imports the matcher
import java.util.regex.Pattern;
//imports the pattern

public class main {
    public static final String ANSI_RED = "\u001b[0;31m";
    public static final String ANSI_BLUE = "\u001b[0;34m";
    public static final String ANSI_GREEN = "\u001b[0;32m";
    public static final String ANSI_YELLOW = "\033[0;33m";
    public static final String ANSI_PURPLE = "\u001b[0;35m";
    public static final String ANSI_RESET = "\u001b[0;37m";

    //all of the above are quick ANSI 'variables' (classes) that are refered to throughout the code in an attempt to
    //simplify the processes of the code.
    public static void main(String[] args) throws IOException {
        System.out.flush();
        System.out.println(ANSI_BLUE + "Welcome to the Syntax Highlighter!" + ANSI_RESET);
        while(true) {
            //title screen
            System.out.flush();
            System.out.println("Please choose which functionality you would like to use:");
            System.out.println("1: Syntax Highlighting Menu");
            System.out.println("2: Quit");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            //takes the users input for the main menu choice

            if (choice == 1) {
                syntaxHighlighterMenu();
            } else if (choice == 2) {
                System.out.println("User chose to end program. Goodbye.");
                System.exit(0);
                //ends program
            }
            else{
                System.out.println("Please choose a valid response");
                //deals with erroneous inputs
            }
        }
    }

    public static void syntaxHighlighterMenu() throws IOException {
        //method also finds instances of keywords
        //1 reads file -> turns into a string list array CHECK
        //2 goes through string array and detects where keywords exist and adds coloration
        //3 turns the final array into an output file
        System.out.flush();
        //clears screen, or is supposed to
        System.out.println("\nPlease enter your chosen code file to highlight:");
        System.out.println("\nTips: Please do NOT include the '.txt' at the end of the file name.");
        System.out.println("\nThis program only is able to read a .txt file, a non .txt file will return an error.");
        //prompts user for the chosen file + detailed instructions on limitations of the program

        Scanner scanner = new Scanner(System.in);
        String filename = scanner.nextLine();
        filename += ".txt";
        //adds .txt to the end of filename instead of having user do it

        String keywordColor = highlighting("keywords");
        String commentColor = highlighting("comment");
        String stringTextColor = highlighting("string");
        //all three of the above perform the same main task of asking the user
        //for coloring option info

        long startTime = System.nanoTime();
        readFile(filename, keywordColor, commentColor, stringTextColor);
        //reads, and then passes the file list onto the file parser, and then prints for the user
        long stopTime = System.nanoTime();
        //nanotime is a java class that measures compiler time in nano seconds, a very small fraction of even a
        //milisecond
        long totalTime = (stopTime-startTime)/10000000;
        //above: start and stop time for compiler clock, and parsing the actual code.

        System.out.println("\nThe program took: " + totalTime + " milliseconds.");
        System.out.println("\n\nWould you like to proceed with creating a highlighted file?");
        System.out.println("Press" + ANSI_PURPLE + " 1 " + ANSI_RESET + "to write out the file.");
        System.out.println("Press" + ANSI_PURPLE + " 2 " + ANSI_RESET + "to return to the main manu.");
        //prompting the user for what to do after the code has been displayed to terminal

        int choice = scanner.nextInt();
        if (choice == 1) {
            createFile(filename, keywordColor, commentColor, stringTextColor);
        } else if (choice == 2) {
            System.out.println("\nYou will now return to the main menu.");
        } else {
            System.out.println("\nPlease choose a valid response.");
        }
        //creates the fully highlighted code file
        //while loop used for purposes of the scanner.
    }

    private static void readFile(String inputFilename, String keywordColor, String commentColor, String stringColor) {

        try (FileReader fileReader = new FileReader(inputFilename);
             //File reader is the same as any other instance of a file reader
             BufferedReader reader = new BufferedReader(fileReader)) {
             //However, with BufferedReader, the compiler

            String line;
            System.out.println("\nThis is your final parsed code: \n");

            while ((line = reader.readLine()) != null) {
                String parsedLine = parseLine(line, keywordColor, commentColor, stringColor);
                System.out.println(parsedLine);
                //parses and prints by line being read from the buffered
                //reader
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseLine(String line, String keywordColor, String commentColor, String stringColor) throws FileNotFoundException {
        //turns the keywords file (editable in settings) into an array, parsed by spaces
        String[] keywords = keywords();
        Pattern patternMultiComment = Pattern.compile("/\\\\*\\\\S");
        // essentially looking for the start of a multi line comment: /*
        Pattern patternStringText = Pattern.compile("\"([^\"]*)\"");
        // looking for the end of a multi line comment: */
        Pattern patternMultiCommentEnd = Pattern.compile("(?<!\\\\S)\\\\*/(?!\\\\S)");
        // looking for a regular one line comment beginning
        Matcher matcherMultiComment = patternMultiComment.matcher(line);
        Matcher matcherMultiCommentEnd = patternMultiCommentEnd.matcher(line);
        Matcher matcherStringText = patternStringText.matcher(line);
        //patterns for the beginning and end of multi line comments
        //then passed into a 'matcher' that tries to match it to the given file, which then goes into the
        //find() method below
        //Credits to Poe for helping with Matcher, Pattern and Regex codes

        if(matcherMultiComment.find()){
            String multiComment = matcherMultiComment.group();
            String highlightedComment = "";
            if(commentColor.equals("Blue")){
                    highlightedComment = ANSI_BLUE + multiComment;
            }
            if(commentColor.equals("Green")){
                highlightedComment = ANSI_GREEN + multiComment;
            }
            if(commentColor.equals("Yellow")){
                highlightedComment = ANSI_YELLOW + multiComment;
            }
            if(commentColor.equals("Purple")){
                highlightedComment = ANSI_PURPLE + multiComment;
            }
            if(commentColor.equals("Red")){
                highlightedComment = ANSI_RED + multiComment;
            }
            if(commentColor.equals("Reset")){
                highlightedComment = ANSI_RESET + multiComment;
            }

            //changes the comment color to whichever color the user chose previously
            return line.replace(multiComment, highlightedComment);
            //replaces the previous line of comment with a new line with ansi escape codes
        }

        if(matcherMultiComment.find()) {
            String multiCommentEnd = matcherMultiCommentEnd.group();
            String highlightedComment = "";
            if(commentColor.equals("Blue")){
                highlightedComment = ANSI_BLUE + multiCommentEnd + ANSI_RESET;
            }
            if(commentColor.equals("Green")){
                highlightedComment = ANSI_GREEN + multiCommentEnd + ANSI_RESET;
            }
            if(commentColor.equals("Yellow")){
                highlightedComment = ANSI_YELLOW + multiCommentEnd + ANSI_RESET;
            }
            if(commentColor.equals("Purple")){
                highlightedComment = ANSI_PURPLE + multiCommentEnd + ANSI_RESET;
            }
            if(commentColor.equals("Red")){
                highlightedComment = ANSI_RED + multiCommentEnd + ANSI_RESET;
            }
            if(commentColor.equals("Reset")){
                highlightedComment = ANSI_RESET + multiCommentEnd + ANSI_RESET;
            }
            //same as start, however, this time the ANSI escape codes reset to normal text
            return line.replace(multiCommentEnd, highlightedComment);
        }

        if(matcherStringText.find()){
            //if quotation marks are detected:
            String stringLine = matcherStringText.group();
            String highlightedComment = "";
            if(stringColor.equals("Blue")){
                highlightedComment = ANSI_BLUE + stringLine + ANSI_RESET;
            }
            if(stringColor.equals("Green")){
                highlightedComment = ANSI_GREEN + stringLine + ANSI_RESET;
            }
            if(stringColor.equals("Yellow")){
                highlightedComment = ANSI_YELLOW + stringLine + ANSI_RESET;
            }
            if(stringColor.equals("Purple")){
                highlightedComment = ANSI_PURPLE + stringLine + ANSI_RESET;
            }
            if(stringColor.equals("Red")){
                highlightedComment = ANSI_RED + stringLine + ANSI_RESET;
            }
            if(stringColor.equals("Reset")){
                highlightedComment = ANSI_RESET + stringLine + ANSI_RESET;
            }

            //changes the comment color to whichever color the user chose previously
            return line.replace(stringLine, highlightedComment);
            //replaces the previous line of comment with a new line with ansi escape codes
        }

        //if keywords are found:
        if(keywordColor == "Blue") {
            for (String keyword : keywords) {
                line = line.replaceAll(keyword, ANSI_BLUE + keyword + ANSI_RESET);
                //adds ansi to the beginning, and puts it back to normal afterwards
            }
        }
        else if(keywordColor == "Purple"){
            for (String keyword : keywords) {
                line = line.replaceAll(keyword, ANSI_PURPLE + keyword + ANSI_RESET);
            }
        }
        else if(keywordColor == "Red"){
            for (String keyword : keywords) {
                line = line.replaceAll(keyword, ANSI_RED + keyword + ANSI_RESET);
            }
        }
        else if(keywordColor == "Yellow"){
            for (String keyword : keywords) {
                line = line.replaceAll(keyword, ANSI_YELLOW + keyword + ANSI_RESET);
            }
        }
        else if(keywordColor == "Green"){
            for (String keyword : keywords) {
                line = line.replaceAll(keyword, ANSI_GREEN + keyword + ANSI_RESET);
            }
        }
        else if(keywordColor == "Reset"){
            for(String keyword : keywords){
                line = line.replaceAll(keyword, ANSI_RESET + keyword + ANSI_RESET);
            }
        }

        Pattern patternComment = Pattern.compile("//.*");
        Matcher matcherComment = patternComment.matcher(line);
        if (matcherComment.find()) {
            String comment = matcherComment.group();
            String highlightedComment = ANSI_GREEN + comment + ANSI_RESET;
            return line.replace(comment, highlightedComment);
        }

        //Using the matcher and find groups with regex, to find the start and stop of multi comments. However, one flaw
        //is that if a keyword is picked up in between, the highlighting will stop
        //looked into how to fully solve this issue, however it required stringbuilder and i did want to explore just yet

        return line;
    }

    private static String[] keywords() throws FileNotFoundException{
        //method that references the keywords in a local file
        //and then passes them to the parseLine method in a String Array Format
        String keywordFilename = "keywords.txt";

        int linesInFile = countLinesInFile(keywordFilename);
        String[] keywordsArr = new String[linesInFile];
        //counts lines in file to make the array that many elements long

        try(FileReader fileReader = new FileReader(keywordFilename);
                BufferedReader bufferedReader = new BufferedReader(fileReader)){
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                keywordsArr[i] = line;
                i++;
                //reading into an array with buffered reader
                //until there is no more (null) elements left
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
            //in the case the file has been deleted
        }

        return keywordsArr;
    }

    private static int countLinesInFile(String inputFilename) throws FileNotFoundException {
        File file = new File(inputFilename);
        Scanner scanner = new Scanner(file);
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            lineCount++;
            scanner.nextLine();
            //using scanner, detects how many lines remain
            //but does not actually read anything from them
            //this is a method courtesy of Lab 6.1
        }
        scanner.close();
        return lineCount;
    }

    public static void createFile(String filename,String keywordColor, String commentColor, String stringColor) throws IOException {
        String newFilename = filename.substring(0,filename.length()-3)+"highlighted.txt";

        try (FileReader fileReader = new FileReader(filename);
             //File reader is the same as any other instance of a file reader
             BufferedReader reader = new BufferedReader(fileReader)) {
            //However, with BufferedReader, the compiler

            String line;

            try (FileWriter fileWriter = new FileWriter(newFilename)) {
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                while ((line = reader.readLine()) != null) {
                    String parsedLine = parseLine(line,keywordColor, commentColor, stringColor);
                    bufferedWriter.write(parsedLine);
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
                System.out.println("The file has been created with the name: " + filename);
            }
        }
    }

    public static String highlighting(String type) {
        //method that asks the user for their choice of highlighting on each run through
        //runs first for keywords, and then for comments, working the same way for each run,
        if(type.equals("keywords")){
            System.out.println("What color would you like to highlight the keywords?");
            System.out.println("Default intelliJ: Yellow");
            System.out.println("Please enter one of the following: Blue, Yellow, Purple, Red, Green");
        }
        if(type.equals("comment")){
            System.out.println("What color would you like to highlight the comments?");
            System.out.println("Default intelliJ: Purple (recommended, gray currently in app)");
            System.out.println("Please enter one of the following: Blue, Yellow, Purple, Red, Green");
        }
        //for comment color
        if(type.equals("string")){
            System.out.println("What color would you like to highlight the text in string (between quotations)?");
            System.out.println("Default intelliJ: Green");
            System.out.println("Please enter one of the following: Blue, Yellow, Purple, Red, Green");
        }
        //for string color

        Scanner scanner = new Scanner(System.in);
        String colorChoice = scanner.nextLine();

        String chosenColor = "Reset";

        if(colorChoice.equals("Blue") ){
            chosenColor = "Blue";
        }
        else if(colorChoice.equals("Yellow")){
            chosenColor = "Yellow";
        }
        else if(colorChoice.equals("Purple")){
            chosenColor = "Purple";
        }
        else if(colorChoice.equals("Red")){
            chosenColor = "Red";
        }
        else if(colorChoice.equals("Green")){
            chosenColor = "Green";
        }

        return chosenColor;
    }

}
