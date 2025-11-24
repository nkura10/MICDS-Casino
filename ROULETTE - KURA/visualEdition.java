//credits an citations
//Thank you to Lewis and Abhinav for playtesting my game.
//All methods have been tested for incorrect/impossible inputs, as well as cheats like inputting negative bets


//imports
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//to create a frame and allow a popup class
class AnimationWindow {
    //making the frame+canvas
    protected static JFrame frame;
    protected static spinningBall canvas;

    //pull frame up method - includes a bunch of stuff off of the youtube vid
    public static void show() {
        if (frame == null) {
            frame = new JFrame("Roulette Wheel Spinning - Nathan's Casino");
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            canvas = new spinningBall();
            frame.add(canvas, BorderLayout.CENTER);
            frame.setVisible(true);
            frame.setBackground(Color.GREEN);
        }
    }

    //a function to return the canvas
    public static spinningBall getCanvas() {
        return canvas;
    }
}

//the main class
public class visualEdition {

    //when bettype is 1, getting a color red-17 black-18
    public static int colorPick() {
        //initialize and declare
        Scanner scan = new Scanner(System.in);
        int color = 0;
        //making sure the color is valid
        while (color != 1 && color != 2) {
            System.out.print("Pick red(1) or black(2): ");
            try {
                color = scan.nextInt();
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid input.");
                scan.next();
            }
            //output + augmentation for later (red-17 black-18)
            if (color == 1 || color == 2) return (color + 16);
            else System.out.println("Needs to be 1 or 2.");
        }
        return color;
    }

    //when the bettype is 2, getting a number
    public static int numberChooser(){
        Scanner sc = new Scanner(System.in);
        int number = 0;
        //making sure the number is 1-16
        while (number > 16 || number < 1) {
            System.out.print("Pick a number 1-16: ");
            try {
                number = sc.nextInt();
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid input.");
                sc.next();
            }
            if (number > 16 || number < 1) System.out.println("Needs to be a number between 1 and 16.");
            //output
            else return number;
        }
        return number;
    }

    //getting the bet amount
    public static int betAmount(int limit) {
        //initialize and declare
        Scanner sc = new Scanner(System.in);
        int bet = 0;
        //getting a proper bet
        while (bet==0) {
            System.out.print("How much would you like to bet?: ");
            try {
                bet = sc.nextInt();
            }
            catch(Exception e) {
                System.out.println("Invalid input.");
                sc.next();
                continue;
            }
            if (bet > limit || bet<=0) {
                System.out.println("Bet amount not within range.\n---\n");
                bet = 0;
            }
            //output
            else return bet;
        }
        return bet;
    }

    //getting the number from the angle - trig
    public static int getWinningNumberFromAngle(double angle) {
        int totalNumbers = 16;
        //segmenting the whole circle
        double segmentAngle = 360.0 / totalNumbers;
        double positiveAngle = (angle % 360 + 360) % 360;
        //finding the number by detecting which segment the ball in is
        int winningIndex = (int) Math.floor(positiveAngle / segmentAngle);
        //making an int out of an index
        int winningNumber = winningIndex + 1;
        //output
        return winningNumber;
    }

    //wheel will spin on the canvas
    public static int spinFeature() {
        System.out.println("Spinning Wheel...");

        //create object canvas
        spinningBall canvas = AnimationWindow.getCanvas();
        //initialize winning number
        int winningNumber = 0;

        if (canvas != null) {
            // Start the spin of the canvas without a target number
            canvas.startSpinning();

            try {
                // Wait for animation of the canvas to end
                canvas.waitForSpinToFinish();

                //finding the angle of the canvas
                double finalAngle = canvas.getFinalAngle();
                winningNumber = getWinningNumberFromAngle(finalAngle);

                //assign the winning number
                final int finalWinNum = winningNumber;
                SwingUtilities.invokeLater(() -> {
                    canvas.updateDisplayNumber(finalWinNum);
                });

                //if the spinning breaks (if they close the tab or something)
            } catch (InterruptedException e) {
                System.out.println("Spinning interrupted.");
            }
        }

        //output
        System.out.print("The ball has landed on " + winningNumber);
        if (winningNumber % 2 != 0) System.out.println(" (red).");
        else System.out.println(" (black).");
        return winningNumber;
    }

    //find out how much you won
    public static int winValue(int winNumber, int betNumber, int betType, int chipsIn) {
        if (betType==1) { // Color bet
            //did you win the color?
            int winColor = 18 - (winNumber % 2);
            if (winColor == betNumber) {
                System.out.println("You won! x2 chips");
                return chipsIn*2;
            } else {
                System.out.println("You lost!");
                return 0;
            }
            //did you win the number bet?
        } else if (betType==2) {
            if (winNumber == betNumber) {
                System.out.println("JACKPOT!!! x300 CHIPS!!");
                return chipsIn*300;
            } else {
                System.out.println("You lost!");
                return 0;
            }
        } else {
            return 0;
        }
    }

    //Main argument
    public static void main(String[] args) {
        //initializing global variables
        Scanner scan = new Scanner(System.in);
        boolean grandLoop=true;
        int chips=100;
        int grandLoopActive = 1;

        //appear the window if not already there
        AnimationWindow.show();

        //introduction
        System.out.println("\nWelcome to Nathan's Casino!\n---");

        //the grand loop
        while (grandLoop) {
            System.out.print("You have "+chips+" chips. \nWould you like to bet on \ncolor(1) or number(2)?: ");
            int betType = 0;

            //making sure the bet is actually possible to assign
            try {
                betType =  scan.nextInt();
            } catch (Exception e) { //restarts the loop if invalid input
                System.out.println("Invalid input, please enter 1 or 2.");
                scan.next();
                continue;
            }


            //making sure the bet is 1 or 2 for color or number - and then assigning it
            int betOn = 0;
            if (betType==1){
                betOn = colorPick();
            } else if (betType==2){
                betOn = numberChooser();
            } else { //restarts the loop if invalid
                System.out.println("Invalid input, please enter 1 or 2.");
                continue;
            }

            //the bet amount is found
            int betChips = betAmount(chips);
            //bet is deducted from total
            chips = chips - betChips;

            //get the winning number
            int winningNumber = spinFeature();
            //find the amount winnings
            int winnings = winValue(winningNumber, betOn, betType, betChips);
            //add money if they won
            chips = chips + winnings;

            //get out of the casino if broke
            if (chips==0) {
                System.out.println("You don't have any more chips!");
                break;
            }

            //restart the grandloop
            System.out.print("Would you like to play again? Yes(1) for No(2): ");
            try {
                grandLoopActive = scan.nextInt();
            }
            catch(Exception e) {
                System.out.println("Invalid input, please enter 1 or 2.\nRestarting game...");
                scan.next();
                continue;
            }
            if (grandLoopActive==1) grandLoop = true;
            else break;
        }

        //final chips amount
        System.out.println("Thanks for playing at my casino! You finished with "+chips+" chips.");
        //close roulette window
        if (AnimationWindow.frame != null) AnimationWindow.frame.dispose();
    }
}
