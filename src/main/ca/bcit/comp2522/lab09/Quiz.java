package ca.bcit.comp2522.lab09;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a quiz which can have multiple questions and can manage which questions have been asked and which still
 * need to be asked.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public final class Quiz {

    private final Set<QuizQuestion> questions;
    private final Map<QuizQuestion, String> questionAnswers;
    private final Map<QuizQuestion, Boolean> questionAnswerResults;

    private Iterator<QuizQuestion> questionIterator;

    /**
     * Creates a new quiz.
     *
     * @param questions the questions on the quiz
     */
    public Quiz(final Set<QuizQuestion> questions) {
        Quiz.validateQuestions(questions);

        this.questions = questions;
        this.questionAnswers = new HashMap<>();
        this.questionAnswerResults = new HashMap<>();
        this.questionIterator = questions.iterator();
    }

    /**
     * Creates a quiz containing all the questions from the given file.
     * <p>
     * The file is expected to have one encoded {@link QuizQuestion} per line.
     *
     * @param file         the file to get questions from
     * @param maxQuestions the maximum amount of questions to load from the file
     * @param shuffle      whether to shuffle the questions
     * @return the quiz with all the questions
     * @throws IOException if the provided file path cannot be open and read
     */
    public static Quiz fromQuestionsFile(final Path file, final int maxQuestions, boolean shuffle) throws IOException {
        final List<String> encodedQuestions;
        final Set<QuizQuestion> questions;

        encodedQuestions = Files.readAllLines(file);
        if (shuffle) Collections.shuffle(encodedQuestions);

        questions = encodedQuestions.stream().limit(maxQuestions).map(QuizQuestion::decode).collect(Collectors.toSet());

        return new Quiz(questions);
    }

    /**
     * Validates the given quiz questions to ensure they are within limits.
     *
     * @param questions the questions to check
     */
    private static void validateQuestions(final Set<QuizQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("A quiz must have at least one question.");
        }
    }

    /**
     * Advances the quiz questions, returning the next one in the series. If there is no next question, {@code null}
     * will be returned.
     *
     * @return the next quiz question
     */
    public QuizQuestion getNextQuestion() {
        if (this.questionIterator.hasNext()) {
            return this.questionIterator.next();
        }

        return null;
    }

    /**
     * Clears any answers and moves back to the first question of this quiz.
     */
    public void reset() {
        this.questionIterator = this.questions.iterator();
        this.questionAnswerResults.clear();
    }

    /**
     * Records the specified answer as the answer for the specified question.
     *
     * @param question the question to answer
     * @param answer   the answer for the question
     * @return whether the answer is accepted (whether it was right)
     */
    public boolean answerQuestion(final QuizQuestion question, final String answer) {
        if (!this.questions.contains(question)) {
            throw new IllegalArgumentException("The question \"" + question.getQuestionText() +
                                                       "\" is not on this quiz, so it cannot be answered.");
        }

        final boolean result;
        result = question.isAcceptedAnswer(answer);

        this.questionAnswers.put(question, answer);
        this.questionAnswerResults.put(question, result);

        return result;
    }

    /**
     * Determines the amount of questions on this quiz.
     *
     * @return the amount of questions on this quiz
     */
    public int getQuestionCount() {
        return this.questions.size();
    }

    /**
     * Determines the amount of questions that have been answered correctly.
     *
     * @return the amount of questions on this quiz that were correctly answered
     */
    public int getCorrectAnsweredCount() {
        return (int) this.questionAnswerResults.entrySet().stream().filter(Map.Entry::getValue).count();
    }

    /**
     * Applies the specified consumer for each question in the quiz.
     *
     * @param consumer the consumer to apply
     */
    public void forEachQuestion(final Consumer<QuizQuestion> consumer) {
        this.questions.forEach(consumer);
    }

    /**
     * Retrieves the recorded answer for the specified question.
     *
     * @param question the question to get the result for
     * @return the recorded answer, or null if there is no answer yet
     */
    public String getRecordedAnswerFor(final QuizQuestion question) {
        if (!this.questions.contains(question)) {
            throw new IllegalArgumentException("The question \"" + question.getQuestionText() +
                                                       "\" is not on this quiz, so it cannot have an answer.");
        }

        return this.questionAnswers.get(question);
    }

    /**
     * Retrieves the recorded result for the specified question.
     *
     * @param question the question to get the result for
     * @return the recorded answer, or false if there was no answer at all
     */
    public boolean getRecordedResultFor(final QuizQuestion question) {
        if (!this.questions.contains(question)) {
            throw new IllegalArgumentException("The question \"" + question.getQuestionText() +
                                                       "\" is not on this quiz, so it cannot have an answer.");
        }

        return this.questionAnswerResults.getOrDefault(question, false);
    }

}
