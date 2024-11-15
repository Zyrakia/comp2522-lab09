package ca.bcit.comp2522.lab09;

import ca.bcit.comp2522.lab09.scene.GameScene;
import ca.bcit.comp2522.lab09.scene.HomeScene;
import ca.bcit.comp2522.lab09.scene.SummaryScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * The driver class for COMP2522 Lab #9.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public final class QuizApp extends Application {

    private static final Path GLOBAL_STYLES_PATH = Path.of("src", "resources", "style.css");

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

    /**
     * Starts a file watcher thread on the global styles file for changes to hot-reload during development.
     */
    private static void startStyleReloading() {
        final Path stylesheetPath;
        final Thread watcherThread;

        stylesheetPath = QuizApp.GLOBAL_STYLES_PATH.getParent();
        watcherThread = new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                stylesheetPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {
                    final WatchKey key;
                    key = watchService.take();

                    for (final WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY) {
                            continue;
                        }

                        if (!(event.context() instanceof Path modifiedPath)) {
                            continue;
                        }

                        if (!modifiedPath.endsWith(QuizApp.GLOBAL_STYLES_PATH.getFileName())) {
                            continue;
                        }

                        Platform.runLater(QuizApp::applyStyles);
                    }

                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    /**
     * Reapplies the global stylesheet to the currently viewed scene on the primary stage. This will also add an
     * empty stylesheet to cache-bust the scene styles, which means the new stylesheet will be applied instantly.
     */
    private static void applyStyles() {
        final Scene currentScene;
        currentScene = QuizApp.primaryStage.getScene();

        if (currentScene == null) return;

        currentScene.getStylesheets().clear();
        currentScene.getStylesheets().add("data:,");
        currentScene.getStylesheets().add(QuizApp.GLOBAL_STYLES_PATH.toUri().toString());
    }

    @Override
    public void start(final Stage primaryStage) {
        QuizApp.primaryStage = primaryStage;
        QuizApp.startStyleReloading();

        this.setToHomeScreen();

        primaryStage.setTitle("Quizzer!");
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.toFront();
    }

    /**
     * Transitions to the game summary screen with the given quiz being summarized.
     *
     * @param playedQuiz the quiz to summarize on the summary screen
     */
    private void summarizeGame(final Quiz playedQuiz) {
        this.loadAsScene(new SummaryScene(playedQuiz, this::setToHomeScreen));
    }

    /**
     * Transitions to the home screen.
     */
    private void setToHomeScreen() {
        this.loadAsScene(new HomeScene(this::startGame));
    }

    /**
     * Transitions to the game screen, which will start a new quiz game immediately.
     */
    private void startGame() {
        try {
            this.loadAsScene(new GameScene(this::summarizeGame));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the specified root node as the scene on the primary stage, it will have the global stylesheet applied.
     * <p>
     * If the previous scene root is {@link Destroyable}, it will be destroyed. Which means that if the new root is
     * {@link Destroyable}, it will be destroyed when transitioned away from.
     *
     * @param root the root node to load
     */
    private void loadAsScene(final Parent root) {
        final Scene currentScene;
        final Scene newScene;

        currentScene = QuizApp.primaryStage.getScene();
        newScene = new Scene(root, QuizApp.SCENE_WIDTH, QuizApp.SCENE_HEIGHT);

        if (currentScene != null && currentScene.getRoot() instanceof Destroyable destroyable) {
            destroyable.destroy();
        }

        QuizApp.primaryStage.setScene(newScene);
        QuizApp.applyStyles();
    }

}
