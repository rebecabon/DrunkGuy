package plane;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.Scanner;

public class Main extends Application {

    /*
    PROBLEM
    A drunk guy decides to walk 10 blocks in order to below the effects of alcohol.
    Calculate the probability of him walking 2 blocks or further from the beginning point.
    SUCCESS = he walks 2 blocks or further from the beginning point.
    FAIL = he doesn't get to walk further more than 2 blocks.
    PROBABILITY = number of success / number of iterations

    PROGRAMMERS
    Bon Cabanillas Rebeca Sofía
    Cárdenas Pérez Mauricio
    Escobar Sánchez José Alejandro
    Portillo Morales Norma Esmeralda
    Valenzuela Ponce Kevin Jair
    Verduzco Raggio Daniel
     */

    private final Random R = new Random();
    private final Circle drunkGuy = new Circle();
    private final Pane root = new Pane(); // window's container
    private final Path path = new Path(); // creates a path to follow
    private final Path tail = new Path(); // footprints left from the path
    private final int windowSize = 600;
    private static int numSimulations;

    public static void main(String[] args) {
        Scanner R = new Scanner(System.in);
        System.out.println("How many simulations should I execute?");
        numSimulations = R.nextInt();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("DRUNK GUY");
            // draw cartesian plane
            CartesianPlane();

            // drunk guy's attributes
            drunkGuy.setRadius(10);
            drunkGuy.setFill(Color.RED);

            // prepare drunk guy's trip
            OnMyWay();

            // tail of path followed
            tail.setStrokeWidth(5.0);
            tail.setStroke(Color.rgb(0, 96, 255));
            root.getChildren().add(tail);

            // attributes of transitions' object
            PathTransition transition = new PathTransition();
            transition.setDuration(Duration.seconds(20 * numSimulations));
            transition.setPath(path); // camino a seguir
            transition.setNode(drunkGuy); // objeto a moverse
            transition.setCycleCount(1);
            transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            transition.play();

            Scene scene = new Scene(root, windowSize, windowSize, Color.WHITE);
            root.getChildren().add(drunkGuy);

            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            //
        }
    }

    public void OnMyWay() {
        int x, y, success = 0;

        System.out.println("\nN\t# Block\t# Gen\tLocalization(x,y)"+"\tSuccess?");
        // number of simulations to produce
        for (int i = 0; i < numSimulations; i++) {
            x = 0;
            y = 0;
            // to start the simulation, move our drunk guy and his tail to the center of the plane
            tail.getElements().add(new MoveTo(windowSize/2, windowSize/2));
            path.getElements().add(new MoveTo(windowSize/2, windowSize/2));
            System.out.print(i + 1);
            // cycle of the 10 blocks the drunk guy will walk
            for (int j = 0; j < 10; j++) {
                double actualIteration = R.nextDouble();
                System.out.print("\t" + (j + 1) + "\t\t" + roundDouble(actualIteration, 4) + "\t");
                // verify in which range the random number belongs
                // increase or decrease corresponding variable
                if (actualIteration >= 0 && actualIteration <= 0.25) {
                    y++;
                    if (j == 9) {
                        System.out.print("\t("+x+","+y+")\t");
                    } else {
                        System.out.print("\t("+x+","+y+")\n");
                    }
                } else if (actualIteration > 0.25 && actualIteration <= 0.50) {
                    y--;
                    if (j == 9) {
                        System.out.print("\t("+x+","+y+")\t");
                    } else {
                        System.out.print("\t("+x+","+y+")\n");
                    }
                } else if (actualIteration > 0.50 && actualIteration <= 0.75) {
                    x++;
                    if (j == 9) {
                        System.out.print("\t("+x+","+y+")\t");
                    } else {
                        System.out.print("\t("+x+","+y+")\n");
                    }
                } else if (actualIteration > 0.75 && actualIteration <= 1) {
                    x--;
                    if (j == 9) {
                        System.out.print("\t("+x+","+y+")\t");
                    } else {
                        System.out.print("\t("+x+","+y+")\n");
                    }
                }
                // add resulting vertex (x, y) to the path
                Steps(x, y);
            }
            // pinpoint final position of the iteration after walking 10 blocks
            FinalStep(x, y);
            // valuate if it was a success or a failure
            if (Math.abs(x) + Math.abs(y) >= 2) {
                success++;
                System.out.print("\tYes\n");
            } else {
                System.out.print("\tNo\n");
            }
        }
        System.out.println();
        System.out.println("The probability of success is " + (((double) success/numSimulations) * 100) + "%");
    }

    public void Steps(int x, int y) {
        CubicCurveTo cubicTo = new CubicCurveTo();
        // convert pair of coordenates to scale 1:50
        int newX = convertX(x);
        int newY = convertY(y);
        // tail of path
        LineTo line = new LineTo(newX, newY);
        tail.getElements().add(line);
        // get to the new vertex/position
        cubicTo.setX(newX);
        cubicTo.setY(newY);
        // control values of the curve
        // to simulate the unsteady steps of the druk guy
        cubicTo.setControlX1(newX - 100);
        cubicTo.setControlY1(newY + 50);
        cubicTo.setControlX2(newX + 50);
        cubicTo.setControlY2(newY - 75);
        // add pair of coordenates in scale 1:50
        path.getElements().add(cubicTo);
    }

    public void FinalStep(int x, int y){
        // convert pair of coordenates to scale 1:50
        int newX = convertX(x);
        int newY = convertY(y);
        Circle end = new Circle();
        end.setCenterX(newX);
        end.setCenterY(newY);
        end.setRadius(10);
        end.setFill(Color.rgb(0, 96, 255));
        // add final position of the trip to the map
        root.getChildren().add(end);
    }

    public void CartesianPlane(){
        // fill area where the drunk guys will not make a success (< 2 blocks)
        Rectangle twoBlocks = new Rectangle();
        twoBlocks.setWidth(200);
        twoBlocks.setHeight(200);
        twoBlocks.setX(200);
        twoBlocks.setY(200);
        twoBlocks.setFill(Color.rgb(249, 215, 28, 0.75));
        root.getChildren().add(twoBlocks);
        // draw axis of the plane
        int x1, x2, y1, y2;
        // columns
        x1 = 50;
        x2 = 50;
        y1 = 0;
        y2 = windowSize;
        Line [] columns = new Line [windowSize/50];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new Line(x1, y1, x2, y2);
            columns[i].setFill(Color.GRAY);
            if (columns[i].getStartX() == windowSize/2) {
                columns[i].setStrokeWidth(3);
            }
            x1 += 50;
            x2 += 50;
            root.getChildren().add(columns[i]);
        }
        // rows
        x1 = 0;
        x2 = windowSize;
        y1 = 50;
        y2 = 50;
        Line [] rows = new Line [windowSize/50];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new Line(x1, y1, x2, y2);
            rows[i].setFill(Color.GRAY);
            if (rows[i].getStartY() == windowSize/2) {
                rows[i].setStrokeWidth(3);
            }
            y1 += 50;
            y2 += 50;
            root.getChildren().add(rows[i]);
        }
    }

    public int convertX(int x){
        int newX = 0;
        // turn coordenate X into scale 1:50
        if (x != 0) {
            newX = (windowSize/2) + (x * 50);
        } else {
            newX = windowSize/2;
        }
        return newX;
    }

    public int convertY(int y){
        int newY = 0;
        // turn coordenate Y into scale 1:50
        if (y != 0) {
            newY = (windowSize/2) - (y * 50);
        } else {
            newY = windowSize/2;
        }
        return newY;
    }

    public static double roundDouble(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
