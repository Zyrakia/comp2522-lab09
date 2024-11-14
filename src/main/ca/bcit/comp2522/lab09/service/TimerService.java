package ca.bcit.comp2522.lab09.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.function.Consumer;

/**
 * Represents a service that starts a timer which will tick down and return when it is ended.
 * <p>
 * When terminated, either naturally or forcefully, the amount of milliseconds remaining is returned.
 *
 * @author Ole Lammers & Tianyou Xie
 * @version 1.0
 */
public final class TimerService extends Service<Long> {

    private static final long MIN_MILLISECONDS = 1;

    private final long milliseconds;
    private final Consumer<Long> onTick;

    /**
     * Creates a new timer that runs for the specified amount of milliseconds.
     *
     * @param milliseconds the milliseconds this timer will run for
     * @param onTick       the handler for each millisecond countdown tick, this is called with the milliseconds
     *                     remaining
     */
    public TimerService(final long milliseconds, final Consumer<Long> onTick) {
        super();

        TimerService.validateMilliseconds(milliseconds);

        this.milliseconds = milliseconds;
        this.onTick = onTick;
    }

    /**
     * Validates the milliseconds amount to ensure it is within limits.
     *
     * @param milliseconds the milliseconds amount to validate
     */
    private static void validateMilliseconds(final long milliseconds) {
        if (milliseconds < TimerService.MIN_MILLISECONDS) {
            throw new IllegalArgumentException(
                    "A timer must be started with at least " + TimerService.MIN_MILLISECONDS + " millisecond(s).");
        }
    }

    @Override
    protected Task<Long> createTask() {
        return new Task<>() {
            @Override
            protected Long call() {
                long millisRemaining = TimerService.this.milliseconds;

                while (millisRemaining > 0) {
                    try {
                        Thread.sleep(1);
                        TimerService.this.onTick.accept(millisRemaining);
                    } catch (final InterruptedException _) {
                        if (this.isCancelled()) {
                            break;
                        }
                    }

                    millisRemaining--;
                }

                TimerService.this.onTick.accept(millisRemaining);
                return millisRemaining;
            }
        };
    }
}
