package ca.bcit.comp2522.lab09.scene;

import ca.bcit.comp2522.lab09.Quiz;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;

/**
 * Represents the summary screen for a played quiz.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public final class SummaryScene extends HBox {

    private static final double ELEMENT_SPACING = 25.0;

    private final Quiz playedQuiz;
    private final Runnable onExit;

    private final Label gradeText;
    private final Label questionCountText;
    private final TextArea missedQuestionsText;
    private final Button exitButton;

    /**
     * Creates a new summary screen that displays information for the specified quiz.
     *
     * @param playedQuiz the quiz that was played
     * @param onExit     the action to perform when the summary screen is exited
     */
    public SummaryScene(final Quiz playedQuiz, final Runnable onExit) {
        super();

        this.playedQuiz = playedQuiz;
        this.onExit = onExit;

        this.gradeText = this.createGradeText();
        this.questionCountText = this.createQuestionCountText();
        this.missedQuestionsText = this.createMissedQuestionsText();
        this.exitButton = this.createExitButton();

        final VBox summaryBox;
        summaryBox = new VBox();

        summaryBox.setSpacing(SummaryScene.ELEMENT_SPACING);
        summaryBox.setAlignment(Pos.CENTER);

        summaryBox.getChildren().add(this.gradeText);
        summaryBox.getChildren().add(this.questionCountText);
        summaryBox.getChildren().add(this.exitButton);

        this.setAlignment(Pos.CENTER);
        this.setSpacing(SummaryScene.ELEMENT_SPACING);
        this.setPadding(new Insets(SummaryScene.ELEMENT_SPACING));
        HBox.setHgrow(summaryBox, Priority.ALWAYS);

        this.getChildren().add(summaryBox);
        this.getChildren().add(this.missedQuestionsText);
    }

    /**
     * Creates the grade text to display the percentage of questions answered correctly.
     *
     * @return the created element
     */
    private Label createGradeText() {
        final DecimalFormat percFmt;
        final Label label;
        final double correctPerc;

        percFmt = new DecimalFormat("#.##%");
        label = new Label();
        correctPerc = ((double) this.playedQuiz.getCorrectAnsweredCount()) / this.playedQuiz.getQuestionCount();

        label.getStyleClass().add("grade-text");
        label.setText(percFmt.format(correctPerc));

        return label;
    }

    /**
     * Creates the question count text to display how many questions were answered correctly out of the total amount.
     *
     * @return the created element
     */
    private Label createQuestionCountText() {
        final Label label;
        label = new Label();

        label.getStyleClass().add("question-count-text");
        label.setText(String.format("%d/%d Correctly Answered", this.playedQuiz.getCorrectAnsweredCount(),
                                    this.playedQuiz.getQuestionCount()));

        return label;
    }

    /**
     * Creates the readonly text area outlining all the questions that were unanswered, or answered incorrectly,
     * alongside the actual answer.
     *
     * @return the created element
     */
    private TextArea createMissedQuestionsText() {
        final TextArea area;
        area = new TextArea();

        area.setEditable(false);
        area.setFocusTraversable(false);
        area.appendText("Missed Questions:\n");

        this.playedQuiz.forEachQuestion((question) -> {
            final boolean result;
            final String answer;

            result = this.playedQuiz.getRecordedResultFor(question);
            if (result) return;

            answer = this.playedQuiz.getRecordedAnswerFor(question);

            area.appendText("\nQuestion: " + question.getQuestionText());
            if (answer != null) {
                area.appendText("\nYour Answer: " + answer);
            }

            area.appendText("\nCorrect Answer: " + question.getBestAnswer() + "\n");
        });

        return area;
    }

    /**
     * Creates the exit button to go back home.
     *
     * @return the created element
     */
    private Button createExitButton() {
        final Button button;
        button = new Button();

        button.setText("Exit");
        button.setOnAction(_ -> this.onExit.run());

        return button;
    }

}
