package ca.bcit.comp2522.lab09;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a quiz question with one or multiple valid answers.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public final class QuizQuestion {

    private static final String ENCODED_ANSWER_SEP = "|";
    private static final String ENCODED_ANSWER_SEP_REGEX = "\\" + ENCODED_ANSWER_SEP;

    private final String question;
    private final List<String> answers;

    /**
     * Creates a new quiz question with the given question text and valid answers.
     *
     * @param question the question text
     * @param answers  the list of valid answers for the question
     */
    public QuizQuestion(final String question, final List<String> answers) {
        QuizQuestion.validateQuestion(question);
        QuizQuestion.validateAnswers(answers);

        this.question = question;
        this.answers = answers;
    }

    /**
     * Decodes a quiz question from its encoded format.
     * <p>
     * Then encoded format is the question text followed by answers, each separated by the
     * {@link QuizQuestion#ENCODED_ANSWER_SEP}. The first answer is always required, and it must be separated from
     * the question text by the {@link QuizQuestion#ENCODED_ANSWER_SEP}.
     *
     * @param encodedQuestion the encoded version of a quiz question
     * @return the decoded question representing the encoded input
     */
    public static QuizQuestion decode(final String encodedQuestion) {
        final int minimumParts = 2;

        final String[] questionParts;
        questionParts = encodedQuestion.split(QuizQuestion.ENCODED_ANSWER_SEP_REGEX);

        if (questionParts.length < minimumParts) {
            throw new IllegalArgumentException(
                    "Invalid encoded question: \"" + encodedQuestion + "\" (does not contain any answers).");
        }

        final String decodedQuestion;
        final List<String> decodedAnswers;

        decodedQuestion = questionParts[0];
        decodedAnswers = new ArrayList<>();

        for (int i = 1; i < questionParts.length; i++) {
            decodedAnswers.add(questionParts[i]);
        }

        return new QuizQuestion(decodedQuestion, decodedAnswers);
    }

    /**
     * Validates the given question to ensure it is within limits.
     *
     * @param question the question to validate
     */
    private static void validateQuestion(final String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be null or blank.");
        }

        if (question.contains(QuizQuestion.ENCODED_ANSWER_SEP)) {
            throw new IllegalArgumentException(
                    "A question cannot contain the answer separator (\"" + QuizQuestion.ENCODED_ANSWER_SEP + "\").");
        }
    }

    /**
     * Validates the given answers to ensure they are within limits.
     *
     * @param answers the answer to validate
     */
    private static void validateAnswers(final List<String> answers) {
        if (answers.isEmpty()) {
            throw new IllegalArgumentException("A question must have at least one answer.");
        }

        if (answers.stream().anyMatch((s) -> s.contains(QuizQuestion.ENCODED_ANSWER_SEP))) {
            throw new IllegalArgumentException(
                    "An answer cannot contain the answer separator (\"" + QuizQuestion.ENCODED_ANSWER_SEP + "\").");
        }
    }

    /**
     * Normalizes the specified answer for comparison against another answer.
     *
     * @param answer the answer to normalize
     * @return the normalized answer
     */
    public static String normalizeAnswer(final String answer) {
        return answer.toLowerCase().trim();
    }

    /**
     * Determines whether a given answer matches a known answer, after normalization.
     * <p>
     * Normalization consists of lowercasing both answers, and trimming any excess whitespace.
     * <p>
     * An answer matches if it has a substring of the known answer, and the length of that substring is greater than or
     * equal to half of the length of the total given answer (floored). This ensures that "the skin" matches the
     * answer "skin" but
     * also has a pitfall where "not skin" would match the answer "skin".
     *
     * @param rawKnownAnswer the known correct answer
     * @param rawGivenAnswer the given, potentially correct, answer
     * @return whether the given answer is considered a match after normalization
     */
    public static boolean doAnswersMatch(final String rawKnownAnswer, final String rawGivenAnswer) {
        final String knownAnswer;
        final String givenAnswer;
        final int knownAnswerLength;
        final int givenAnswerLength;

        knownAnswer = QuizQuestion.normalizeAnswer(rawKnownAnswer);
        givenAnswer = QuizQuestion.normalizeAnswer(rawGivenAnswer);
        knownAnswerLength = knownAnswer.length();
        givenAnswerLength = givenAnswer.length();

        if (!givenAnswer.contains(knownAnswer)) {
            return false;
        }

        return knownAnswerLength >= (givenAnswerLength / 2);
    }

    /**
     * Validates whether the given answer matches one of the accepted answers of a questions.
     * <p>
     * An answer matches if it has a substring of an accepted answer, and the length of that substring is greater or
     * equal to half of the length of the total answer. This ensures that "the skin" matches the answer "skin", but
     * also has a pitfall where "not skin" would match the answer "skin".
     * <p>
     * Any comparison is also done with case insensitivity.
     *
     * @param givenAnswer the answer to check
     * @return whether the given answer matches at least one accepted answer
     */
    public boolean isAcceptedAnswer(final String givenAnswer) {
        return this.answers.stream().anyMatch((knownAnswer) -> QuizQuestion.doAnswersMatch(knownAnswer, givenAnswer));
    }

    /**
     * Returns the question text of this quiz question.
     *
     * @return the question text
     */
    public String getQuestionText() {
        return this.question;
    }

    /**
     * Returns the first, usually the best, answer for this quiz question.
     *
     * @return the "best" answer
     */
    public String getBestAnswer() {
        return this.answers.getFirst();
    }

}
