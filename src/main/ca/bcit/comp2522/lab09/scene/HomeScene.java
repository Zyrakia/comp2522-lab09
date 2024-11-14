package ca.bcit.comp2522.lab09.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Represents the home screen for the Quizzer game.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public final class HomeScene extends VBox {

    private static final double ELEMENT_SPACING = 25.0;

    /** This is the action that is run when the start button is pressed. */
    private final Runnable startAction;

    /**
     * Creates a new home screen with the given start action.
     *
     * @param startAction the action executed when the start button is pressed
     */
    public HomeScene(final Runnable startAction) {
        this.startAction = startAction;

        this.setAlignment(Pos.CENTER);
        this.setSpacing(HomeScene.ELEMENT_SPACING);
        this.setPadding(new Insets(HomeScene.ELEMENT_SPACING));

        this.getChildren().add(this.createGameTitle());
        this.getChildren().add(this.createStartButton());
    }

    /**
     * Creates a new label for the game title.
     *
     * @return the game title label
     */
    private Text createGameTitle() {
        final Text text;
        text = new Text("Quizzer!");

        text.getStyleClass().add("title");

        return text;
    }

    /**
     * Creates a new button to start the game.
     *
     * @return the start button
     */
    private Button createStartButton() {
        final Button button;
        button = new Button();

        button.setText("Start Quiz");
        button.setOnAction(_ -> this.startAction.run());

        return button;
    }

}
