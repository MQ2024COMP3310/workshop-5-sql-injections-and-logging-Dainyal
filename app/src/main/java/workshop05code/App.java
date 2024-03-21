package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            logger.log(Level.WARNING,"SecurityException or IO Exception", e1);
            e1.printStackTrace();
        }
    }

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (line.matches("^[a-z]{4}$")) {
                    // System.out.println(line);
                    wordleDatabaseConnection.addValidWord(i, line);
                    logger.log(Level.INFO,"valid word " + line + " added from data.txt");
                    i++;
                }
                else {
                    System.out.println("Ignored unacceptable input");
                    logger.log(Level.SEVERE,"Ignored unacceptable input");
                }
            }

        } catch (IOException e) {
            logger.log(Level.WARNING,"Exception", e);
            System.out.println("Not able to load . Sorry!");
            System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");

            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                
                System.out.println("You've guessed '" + guess+"'.");
             
                if (guess.matches("^[a-z]{4}$")) {
                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                        System.out.println("Success! It is in the the list.\n");
                        break;
                    }else{
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                    }
                }else{
                    logger.log(Level.INFO,"User guessed invalid input: " + guess);
                    System.out.println("Invalid input. Guess again");
                }
                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING,"NoSuchElementException or IllegalStateException", e);
            e.printStackTrace();
        }

    }
}