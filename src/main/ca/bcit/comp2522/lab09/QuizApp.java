package ca.bcit.comp2522.lab09;

import ca.bcit.comp2522.lab09.scene.GameScene;
import ca.bcit.comp2522.lab09.scene.HomeScene;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The driver class for COMP2522 Lab #9.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public final class QuizApp extends Application {

    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 400;

    private static Stage primaryStage;

    /**
     * Entry point for the Lab #9 driver class.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        QuizApp.primaryStage = primaryStage;
        this.setToHomeScreen();

        primaryStage.setTitle("Quizzer!");
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.toFront();
    }

    private void setToHomeScreen() {
        this.loadAsScene(new HomeScene(this::startGame));
    }

    private void startGame() {
        try {
            this.loadAsScene(new GameScene());
            //            this.loadAsScene(new GameScene(this::summarizeGame));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //    private void summarizeGame(final Quiz playedQuiz) {
    //        this.loadAsScene(new SummaryScene(playedQuiz, this::setToHomeScreen));
    //    }

    private void loadAsScene(final Parent root) {
        final Scene currentScene;
        final Scene newScene;

        currentScene = QuizApp.primaryStage.getScene();
        newScene = new Scene(root, QuizApp.SCENE_WIDTH, QuizApp.SCENE_HEIGHT);

        if (currentScene instanceof Destroyable destroyable) {
            destroyable.destroy();
        }

        newScene.getStylesheets().add("style.css");
        QuizApp.primaryStage.setScene(newScene);
    }

}
