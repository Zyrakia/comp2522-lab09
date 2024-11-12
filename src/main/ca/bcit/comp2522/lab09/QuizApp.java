package ca.bcit.comp2522.lab09;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The driver class for COMP2522 Lab #9.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public final class QuizApp extends Application {

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
        primaryStage.setTitle("Hello World!");

        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction((_) -> {
            System.out.println("Hello World");
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

}
