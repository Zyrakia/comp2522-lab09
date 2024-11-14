package ca.bcit.comp2522.lab09.scene;

import ca.bcit.comp2522.lab09.Destroyable;
import ca.bcit.comp2522.lab09.Quiz;
import ca.bcit.comp2522.lab09.QuizQuestion;
import ca.bcit.comp2522.lab09.service.TimerService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Represents the game screen for Quizzer and the logic behind it.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public final class GameScene extends VBox implements Destroyable {

    private static final Path QUESTIONS_FILE = Path.of("quiz.txt");
    private static final int QUESTIONS_PER_GAME = 10;
    private static final long MILLIS_PER_QUESTION = TimeUnit.SECONDS.toMillis(10);

    private static final double ELEMENT_SPACING = 25.0;

    private final Consumer<Quiz> onComplete;

    private final Quiz quiz;
    private final TimerService questionTimer;

    private final Label questionText;
    private final Label timerText;
    private final TextField answerInput;
    private final Button submitButton;
    private final Button finishButton;
    private final Label runningScoreText;

    private QuizQuestion currentQuestion;

    /**
     * Creates a new game scene. This will immediately display the first question and begin the timer.
     *
     * @param onComplete the complete observer that can processes the game that this scene played
     * @throws IOException if the quiz questions cannot be loaded for the game
     */
    public GameScene(final Consumer<Quiz> onComplete) throws IOException {
        this.onComplete = onComplete;

        this.quiz = Quiz.fromQuestionsFile(GameScene.QUESTIONS_FILE, GameScene.QUESTIONS_PER_GAME, true);

        this.questionTimer = new TimerService(GameScene.MILLIS_PER_QUESTION, this::setMillisRemaining);
        this.questionTimer.setOnSucceeded((_) -> this.lockInAnswer());

        this.questionText = this.createQuestionText();
        this.timerText = this.createTimerText();
        this.answerInput = this.createAnswerInput();
        this.submitButton = this.createSubmitButton();
        this.finishButton = this.createFinishButton();
        this.runningScoreText = this.createRunningScoreText();

        final HBox answerArea;
        final HBox statusArea;

        answerArea = this.createAnswerArea();
        statusArea = this.createStatusArea();

        this.setAlignment(Pos.CENTER);
        this.setSpacing(GameScene.ELEMENT_SPACING);
        this.setPadding(new Insets(GameScene.ELEMENT_SPACING));

        this.getChildren().add(this.questionText);
        this.getChildren().add(statusArea);
        this.getChildren().add(answerArea);
        this.getChildren().add(this.finishButton);

        this.updateRunningScore();
        this.nextQuestion();
    }

    /**
     * Creates the text element for the current question text.
     *
     * @return the created element
     */
    private Label createQuestionText() {
        final Label text;
        text = new Label();

        text.getStyleClass().add("question-text");

        return text;
    }

    /**
     * Creates the text element for the time remaining.
     *
     * @return the created element
     */
    private Label createTimerText() {
        final Label text;
        text = new Label();

        text.getStyleClass().add("timer-text");

        return text;
    }

    /**
     * Creates the text field for the answer input.
     *
     * @return the created element
     */
    private TextField createAnswerInput() {
        final TextField field;
        field = new TextField();

        field.getStyleClass().add("answer-input");
        field.setPromptText("Enter your answer");
        field.setOnKeyReleased((event) -> {
            if (event.getCode() != KeyCode.ENTER) return;
            this.submitAnswer();
            this.nextQuestion();
        });

        return field;
    }

    /**
     * Creates the button that is used to manually submit an answer.
     *
     * @return the created element
     */
    private Button createSubmitButton() {
        final Button button;
        button = new Button();

        button.setText("Submit");
        button.setOnAction(_ -> {
            this.submitAnswer();
            this.nextQuestion();
        });

        return button;
    }

    /**
     * Creates the button that is used to finish a game early.
     *
     * @return the created element
     */
    private Button createFinishButton() {
        final Button button;
        button = new Button();

        button.setText("End Game");
        button.setOnAction(_ -> this.finishGame());

        return button;
    }

    /**
     * Creates the text that is used to display the running score.
     *
     * @return the created element
     */
    private Label createRunningScoreText() {
        final Label label;
        label = new Label();

        label.getStyleClass().add("running-score-text");

        return label;
    }

    /**
     * Creates the area that holds the score text and the timer text.
     *
     * @return the created area, with the required elements added
     */
    private HBox createStatusArea() {
        final HBox statusArea;
        statusArea = new HBox();

        statusArea.setAlignment(Pos.CENTER);
        statusArea.setSpacing(GameScene.ELEMENT_SPACING);
        statusArea.getChildren().add(this.runningScoreText);
        statusArea.getChildren().add(this.timerText);
        return statusArea;
    }

    /**
     * Creates the area that holds the answer input and submit button.
     *
     * @return the created area, with the required elements added
     */
    private HBox createAnswerArea() {
        final HBox answerArea;
        answerArea = new HBox();

        answerArea.getStyleClass().add("answer-area");
        answerArea.setAlignment(Pos.CENTER);
        answerArea.getChildren().add(this.answerInput);
        answerArea.getChildren().add(this.submitButton);
        HBox.setHgrow(this.answerInput, Priority.ALWAYS);
        return answerArea;
    }

    /**
     * Cancels the timer if it is currently active.
     */
    private void stopTimer() {
        this.questionTimer.cancel();
    }

    /**
     * Starts a new timer, even if one is currently active.
     */
    private void startTimer() {
        this.questionTimer.restart();
    }

    /**
     * Advances the question this game is on, and starts a new timer.
     * <p>
     * If there are no more questions left, the game will be finished.
     */
    private void nextQuestion() {
        this.toggleInputs(false);

        final QuizQuestion nextQuestion;
        nextQuestion = this.quiz.getNextQuestion();

        if (nextQuestion == null) {
            this.finishGame();
        } else {
            this.currentQuestion = nextQuestion;
            this.questionText.setText(this.currentQuestion.getQuestionText());

            this.toggleInputs(true);
            this.answerInput.requestFocus();
            this.startTimer();
        }
    }

    /**
     * Submits the current text within the answer input into the current question.
     * <p>
     * This will stop the question timer and disable inputs, it does not, however, advance to the next question.
     */
    private void submitAnswer() {
        this.stopTimer();
        this.toggleInputs(false);

        final String answer;
        answer = this.answerInput.getText();
        this.answerInput.clear();

        this.quiz.answerQuestion(this.currentQuestion, answer);
        this.updateRunningScore();
    }

    private void lockInAnswer() {
        this.answerInput.setDisable(true);
    }

    /**
     * Updates the timer text to display the specified amount of milliseconds remaining.
     *
     * @param millis the amount of milliseconds remaining
     */
    private void setMillisRemaining(final long millis) {
        final Color safeColor = Color.LIGHTGREEN;
        final Color dangerColor = Color.RED;
        final double millisPerSeconds = 1000;

        final double seconds;
        final double elapsedPerc;
        final Color indicatedColor;

        seconds = millis / millisPerSeconds;
        elapsedPerc = 1 - ((double) millis) / MILLIS_PER_QUESTION;
        indicatedColor = safeColor.interpolate(dangerColor, elapsedPerc);

        Platform.runLater(() -> {
            this.timerText.setTextFill(indicatedColor);
            this.timerText.setText(String.format("Remaining time: %.2fs", seconds));
        });
    }

    /**
     * Updates the running score to align with the amount of questions correctly answered in this quiz.
     */
    private void updateRunningScore() {
        this.runningScoreText.setText("Current score: " + this.quiz.getCorrectAnsweredCount());
    }

    /**
     * Ends the game, disabling all inputs.
     */
    private void finishGame() {
        this.stopTimer();
        this.toggleInputs(false);

        this.onComplete.accept(this.quiz);
    }

    /**
     * Toggles all inputs on the scene.
     *
     * @param enabled the enabled status to toggle to
     */
    private void toggleInputs(final boolean enabled) {
        final boolean disabled;
        disabled = !enabled;

        this.answerInput.setDisable(disabled);
        this.submitButton.setDisable(disabled);
        this.finishButton.setDisable(disabled);
    }

    @Override
    public void destroy() {
        this.stopTimer();
    }
}
