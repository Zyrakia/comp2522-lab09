package ca.bcit.comp2522.lab09.scene;

import ca.bcit.comp2522.lab09.Destroyable;
import ca.bcit.comp2522.lab09.Quiz;
import ca.bcit.comp2522.lab09.QuizQuestion;
import javafx.concurrent.Service;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.nio.file.Path;

public final class GameScene extends VBox implements Destroyable {

    private static final double ELEMENT_SPACING = 25.0;
    private static final Path QUESTIONS_FILE = Path.of("quiz.txt");
    private static final int QUESTIONS_PER_GAME = 10;

    private final Quiz quiz;
    private final Service<Void> questionTimerService;
    private final Text questionText;
    private final Text timerText;
    private final TextField answerInput;
    private final Button submitButton;
    private final Button finishButton;
    private QuizQuestion currentQuestion;


    public GameScene() throws IOException {
        this.quiz = Quiz.fromQuestionsFile(GameScene.QUESTIONS_FILE, GameScene.QUESTIONS_PER_GAME, true);

        this.questionText = this.createQuestionText();
        this.timerText = this.createTimerText();
        this.answerInput = this.createAnswerInput();
        this.submitButton = this.createSubmitButton();
        this.finishButton = this.createFinishButton();

        this.setAlignment(Pos.CENTER);
        this.setSpacing(GameScene.ELEMENT_SPACING);
        this.setPadding(new Insets(GameScene.ELEMENT_SPACING));

        final HBox answerArea;
        answerArea = new HBox();

        answerArea.getChildren().add(this.answerInput);
        answerArea.getChildren().add(this.submitButton);


        this.getChildren().add(this.questionText);
        this.getChildren().add(this.timerText);
        this.getChildren().add(answerArea);
        this.getChildren().add(this.finishButton);

        this.nextQuestion();
    }

    private void stopTimer() {
        if (this.questionTimerService == null) {
            return;
        }

        this.questionTimerService.cancel();
    }

    private void startTimer() {
        this.questionTimerService.restart();
    }

    private void nextQuestion() {

    }

    private Text createQuestionText() {
        final Text text;
        text = new Text();

        text.getStyleClass().add("question-text");
        return text;
    }

    private Text createTimerText() {
        final Text text;
        text = new Text();

        text.getStyleClass().add("timer-text");

        return text;
    }

    private TextField createAnswerInput() {
        final TextField field;
        field = new TextField();

        field.getStyleClass().add("answer-input");
        field.setPromptText("Enter your answer");
        field.setOnKeyReleased((event) -> {
            if (event.getCode() != KeyCode.ENTER) return;
            this.submitAnswer();
        });

        return field;
    }

    private Button createSubmitButton() {
        final Button button;
        button = new Button();

        button.setText("Submit");
        button.getStyleClass().add("submit-button");
        button.setOnAction(_ -> this.submitAnswer());

        return button;
    }

    private Button createFinishButton() {
        final Button button;
        button = new Button();

        button.setText("Finish");
        button.getStyleClass().add("finish-button");
        button.setOnAction(_ -> {
            // TODO finish and exit
        });

        return button;
    }

    private void submitAnswer() {
        this.submitButton.setDisable(true);
        this.answerInput.setDisable(true);

        final String answer;
        answer = this.answerInput.getText();
        answerInput.clear();

        // TODO submit answer into quiz
        // TODO set to next question
    }

    private void setSecondsRemaining(double seconds) {
        seconds = Math.max(seconds, 0);
        if (seconds == 0) {
            // TODO lock submit button and cancel timer
        }

        this.timerText.setText(String.format("Remaining time: %.2fs", seconds));
    }

    @Override
    public void destroy() {
        this.stopTimer();
    }
}
