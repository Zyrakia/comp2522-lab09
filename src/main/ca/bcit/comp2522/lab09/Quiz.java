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
import java.util.stream.Collectors;

/**
 * Represents a quiz which can have multiple questions and can manage which questions have been asked and which still
 * need to be asked.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public class Quiz {

    private final Set<QuizQuestion> questions;
    private final Map<QuizQuestion, Boolean> answeredQuestions;

    private Iterator<QuizQuestion> questionIterator;

    /**
     * Creates a new quiz.
     *
     * @param questions the questions on the quiz
     */
    public Quiz(final Set<QuizQuestion> questions) {
        Quiz.validateQuestions(questions);

        this.questions = questions;
        this.answeredQuestions = new HashMap<>();
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
    public final void reset() {
        this.questionIterator = this.questions.iterator();
        this.answeredQuestions.clear();
    }

    /**
     * Records the specified answer as the answer for the specified question.
     *
     * @param question the question to answer
     * @param answer   the answer for the question
     * @return whether the answer is accepted (whether it was right)
     */
    public final boolean answerQuestion(final QuizQuestion question, final String answer) {
        if (!this.questions.contains(question)) {
            throw new IllegalArgumentException("The question \"" + question.getQuestionText() +
                                                       "\" is not on this quiz, so it cannot be answered.");
        }

        final boolean result;
        result = question.isAcceptedAnswer(answer);

        this.answeredQuestions.put(question, result);
        return result;
    }

    /**
     * Determines the amount of questions that have been answered.
     *
     * @return the amount of questions on this quiz that were answered
     */
    public final int getAnsweredCount() {
        return this.answeredQuestions.size();
    }

    /**
     * Determines the amount of questions on this quiz.
     *
     * @return the amount of questions on this quiz
     */
    public final int getQuestionCount() {
        return this.questions.size();
    }

    /**
     * Determines the amount of questions that have been answered correctly.
     *
     * @return the amount of questions on this quiz that were correctly answered
     */
    public final int getCorrectAnsweredCount() {
        return (int) this.answeredQuestions.entrySet().stream().filter(Map.Entry::getValue).count();
    }

}
