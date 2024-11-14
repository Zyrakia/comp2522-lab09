package ca.bcit.comp2522.lab09.scene;

import ca.bcit.comp2522.lab09.Quiz;
import javafx.scene.layout.VBox;

public class SummaryScene extends VBox {

    private final Quiz playedQuiz;
    private final Runnable onExit;

    public SummaryScene(final Quiz playedQuiz, final Runnable onExit) {
        super();

        this.playedQuiz = playedQuiz;
        this.onExit = onExit;
    }

}
