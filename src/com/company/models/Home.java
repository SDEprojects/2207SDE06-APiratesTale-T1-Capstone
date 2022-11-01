package com.company.models;

import com.apps.util.Prompter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.*;

public class Home {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    private FileGetter file = new FileGetter();
    Prompter prompter = new Prompter(new Scanner(System.in));



    //Methods
    public void buildHome() {
        banner();
        gameInfo();
        startGame();
    }

    // welcome screen
    // Changed file to be read from resources root vs a named path
    private void banner() {
        Scanner myReader = new Scanner(file.fileGetter("welcome.text"));
        while (myReader.hasNextLine()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String data = myReader.nextLine();
            System.out.println(ANSI_YELLOW + data + ANSI_RESET);
        }
        myReader.close();
    }

    private void gameInfo()  {

        System.out.println(ANSI_BLUE +"This is a terminal based role playing game. Seek adventure, by solving the mystery of the " +
                            "secret treasure of Skull Island. Please select a MENU option to continue.\n" + ANSI_RESET);
    }

    private void startGame() {
        while (true) {
            String menuSelection = prompter.prompt("MENU:   New Game  |  Exit \n");
            if (menuSelection.toLowerCase().equals("new game")) {
                Player player = new Player();
                player.newPlayer();
                Game newGame = new Game(player);
                newGame.playGame();
                break;
            }
            if (menuSelection.toLowerCase().equals("exit")) {
                System.out.println("\nThanks For Playing... Good Bye!");
                System.exit(0);
            }
            else {
                System.out.println("Invalid Command");
            }
        }
    }
}
