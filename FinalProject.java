import javafx.application.*;
import javafx.animation.*;
import javafx.stage.*;
import javafx.stage.FileChooser.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.beans.value.*;
import javafx.event.*; 
import javafx.animation.*;
import javafx.geometry.*;
import java.io.*;
import java.util.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;

public class FinalProject extends Application {
    //initialize class properties
    //ball list to hold basketball objects
    private ArrayList<basketball> ballList;
    //counter for hits and misses
    private int hits, misses;
    //time counter
    private double time; 
    //boolean value to check if game is running
    private boolean gameRunning;
    //labels
    private Label hitMissLabel, timerLabel;
    //animation timer
    private AnimationTimer animationTimer;
    //difficulty level
    private String difficultyLevel = "Easy";
    private Image background;
    //Slider for ball size
    private Slider BallSizeSlider;
    //Slider label
    private Label BallSizeLabel;
    
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    
    public void start(Stage mainStage) {
        mainStage.setTitle("Basketball Target Practice Game");
        mainStage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-size: 18;");

        VBox box = new VBox();
        box.setPadding(new Insets(16));
        box.setSpacing(16);
        box.setAlignment(Pos.CENTER);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");
        
        //Menu Items
        MenuItem about = new MenuItem("About");
        MenuItem quit = new MenuItem("Quit");
        MenuItem instruction = new MenuItem("Instruction");
        MenuItem difficulty = new MenuItem("Difficulty Info");
        //add menu items to menu
        fileMenu.getItems().addAll(about, quit);
        helpMenu.getItems().addAll(instruction, difficulty);
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        //about menu item
        about.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText("Developer Info");
            alert.setContentText(
                "Dashnyam Puntsagnorov\n"
                + "Adelphi University\n"
                +"Computer Science Major\n"
                +"CSC 233-Fall 24' Final Project\n"
            );
            alert.showAndWait();
        });
        about.setGraphic( new ImageView( new Image("icons/help.png") ) );
        about.setAccelerator(
            new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN) );
        
        //quit menu item
        quit.setOnAction(
            (ActionEvent event) ->
            {
                mainStage.close();
            }
        );
        quit.setGraphic( new ImageView( new Image("icons/door_out.png") ) );
        quit.setAccelerator(
            new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN) );
        
        //instruction menu item
        instruction.setGraphic( new ImageView( new Image("icons/help.png") ) );
        instruction.setAccelerator(
            new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));     
        instruction.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Game Instruction");
            alert.setHeaderText("Basketball Target Game Instruction");
            alert.setContentText(
                "Click on all red basketballs to win\n"
                + "Don't click on black ball!\n"+
                "Restart the game using 'Start New Game' button\n"
                +"Adjust difficulty level\n"
                +"You can change the size of balls"
            );
            alert.showAndWait();
        });
        //difficulty menu item
        difficulty.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Difficulty Level Info");
            alert.setHeaderText("There are 3 levels - Easy,Medium,Hard");
            alert.setContentText(
                "Easy: 10 good balls, 10 bad balls\n"
                +"Medium: 20 good balls,20 bad balls\n"+
                "Hard: 30 good balls, 30 bad balls"
            );
            alert.showAndWait();
        });
        difficulty.setGraphic( new ImageView( new Image("icons/exclamation.png") ) );
        difficulty.setAccelerator(
            new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        
        //setup canvas
        Canvas canvas = new Canvas(600, 600);
        GraphicsContext context = canvas.getGraphicsContext2D();
        root.setCenter(canvas);
        
        //Load background image
        background = new Image("court.jpg", 600, 600, false, true);
        
        // Top Controls
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));
        
        Button newGameButton = new Button("Start New Game");
        //Combobox for difficulty level
        ComboBox<String> difficultyDropdown = new ComboBox<>();
        difficultyDropdown.getItems().addAll("Easy", "Medium", "Hard");
        //set default to Easy
        difficultyDropdown.setValue("Easy");
        //set difficulty level using dropdown choice
        difficultyDropdown.setOnAction(event ->{
            difficultyLevel = difficultyDropdown.getValue();
        });
        
        //add controls to HBox
        controls.getChildren().addAll(newGameButton, difficultyDropdown);

        root.setTop(new VBox(menuBar, controls));

        // Bottom Controls
        HBox bottomControls = new HBox(10);
        bottomControls.setAlignment(Pos.CENTER);
        bottomControls.setPadding(new Insets(10));
        
        //Control for adjusting ball size -
        HBox sizeControls = new HBox(10);
        sizeControls.setAlignment(Pos.CENTER);
        sizeControls.setPadding(new Insets(10));
        
        
        // Labels to display the sizes
        BallSizeLabel = new Label("Ball Size: 75");
        
        // Sliders for ball size
        BallSizeSlider = new Slider();
        BallSizeSlider.setMin(25);
        BallSizeSlider.setMax(125);
        BallSizeSlider.setValue(75);
        BallSizeSlider.setShowTickLabels(true);
        BallSizeSlider.setShowTickMarks(true);
        
        // Add change listeners to sliders
        BallSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            BallSizeLabel.setText(String.format("Ball Size: %.0f", newValue.doubleValue()));
            for (basketball ball : ballList) {
                ball.width = newValue.doubleValue();
                ball.height = newValue.doubleValue();
            }
            });
        

        // Add sliders and labels to the sizeControls HBox
        sizeControls.getChildren().addAll(BallSizeLabel, BallSizeSlider);

        //Label for Hit/Miss Percentage
        hitMissLabel = new Label("Hit/Miss: 0%");
        //Label for timer
        timerLabel = new Label("Timer:0.00s");
        //add items to add to bottom of the window
        bottomControls.getChildren().addAll(hitMissLabel, timerLabel,sizeControls);
        root.setBottom(new VBox(bottomControls));
        
        

        //Initialize game variables
        ballList = new ArrayList<>();
        hits = 0;
        misses = 0;
        time = 0;
        gameRunning = false;
        
        //InitializeGame method called when New Game button is clicked
        newGameButton.setOnAction(event -> initializeGame(context));
        
        //initialize the game
        initializeGame(context);
    
        //calls method that uses animation timer
        startGameTimer(context); 
        
        //Mouse click handling
        canvas.setOnMouseClicked(event -> {
            //retrieve the mouse click location
            double mouseX = event.getX();
            double mouseY = event.getY();
            basketball clickedBall = null;
            
            //determine if the ball was clicked on canvas
            for (basketball ball : ballList) {
                if (mouseX >= ball.x && mouseX <= ball.x + ball.width &&
                    mouseY >= ball.y && mouseY <= ball.y + ball.height) {
                    clickedBall = ball;
                    break;
                }
            }
            
            //check if ball is clicked
            if (clickedBall != null) {
                //if clicked ball is 'good' ball, remove it and increase hit variable
                if (clickedBall.isGoodBall) {
                    ballList.remove(clickedBall);
                    hits++;
                    //if all good balls are clicked, end the Game 
                    if (ballList.stream().noneMatch(b -> b.isGoodBall)) {
                        //won the game
                        endGame(true);
                        startGameTimer(context); 
                    }
                //if clicked ball is 'bad' ball, end game
                } else {
                    endGame(false);
                    startGameTimer(context); 
                }
            //if didn't click on any ball, increase misses variable
            } else {
                misses++;
            }
            //update hit/miss percentage each time mouse is clicked
            updateHitMissPercentage();
        });

        mainStage.show();
        mainStage.sizeToScene();
    }
    
    //Animation Timer Method
    private void startGameTimer(GraphicsContext context) {
        //initialize animation timer
        animationTimer = new AnimationTimer() {
            
            public void handle(long now) {
                //check if game is running
                if (gameRunning) {
                    //increase timer 
                    time += 1.0 / 60.0;
                    timerLabel.setText(String.format("Timer: %.2f s", time));

                    context.setFill(Color.WHITE);
                    context.fillRect(0, 0, 600, 600);
                    //draw background
                    context.drawImage(background,0,0);    
                    // Update ball positions and draw
                    for (basketball ball : ballList) {
                        ball.move(ball.distanceX, ball.distanceY);
                        ball.wrap();
                        ball.draw(context);
                    }
                }
            }
        };
        animationTimer.start();
    }
    
    //Method to update Hit/Miss percentage and show it on window
    private void updateHitMissPercentage() {
        //calculate the total number of clicks
        int totalClicks = hits + misses;
        double percentage;
        if (totalClicks == 0) {
            percentage = 0;
        } else {
            percentage = (100.0 * hits / totalClicks);
        }
        //update label
        hitMissLabel.setText(String.format("Hit/Miss: %.2f%%", percentage));
    }
    
    //Method to initialize the game
    private void initializeGame(GraphicsContext context) {
        //clear the basketball list
        ballList.clear();
        //initialize game variables
        hits = 0;
        misses = 0;
        time = 0;
        gameRunning = true;
        timerLabel.setText("Timer: 0.00s");
        updateHitMissPercentage();
        
        BallSizeSlider.setValue(75);
        BallSizeLabel.setText("Ball Size: 75");
        
        //determine total number of balls using difficulty level
        int numBalls = switch (difficultyLevel) {
            case "Medium" -> 40;
            case "Hard" -> 60;
            default -> 20;
        };
        
        
        
        //Draw the good and bad balls - moving randomly on canvas
        for (int i = 0; i < numBalls / 2; i++) {
            double x = 400 * Math.random() + 100;
            double y = 400 * Math.random() + 100;
            
            //initialize basketball objects
            basketball goodBall = new basketball(x, y, 75, 75, true);
            basketball badBall = new basketball(x, y, 75, 75, false);
            
            //make sure the balls are moving randomly
            goodBall.distanceX = Math.random() + 0.5;
            goodBall.distanceY = Math.random() + 0.5;
            badBall.distanceX = Math.random() + 0.5;
            badBall.distanceY = Math.random() + 0.5;

            if (Math.random() > 0.5) {
                goodBall.distanceX *= -1;
                badBall.distanceX *= -1;
            }
            if (Math.random() > 0.5) {
                goodBall.distanceY *= -1;
                badBall.distanceY *= -1;
            }
            //add both types of balls to the list
            ballList.add(goodBall);
            ballList.add(badBall);
            
        }
        
           
    }
    //Method to end the game and show alert message
    private void endGame(boolean won) {
        //gamerunning set to False
        gameRunning = false;
        //stop the animation timer
        animationTimer.stop();
        //initialize end game message
        String message;
        if (won) {
            message = "You win! All good balls clicked!";
        } else {
            message = "Game over! You clicked a bad ball!";
        }
        //show alert with appropriate message
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
    
}
