package fr.epita.assistants.utils;

import static lombok.Lombok.sneakyThrow;

/**
 * This is the Exception class where we create throwables to simplify error handling when creating our features (Git, maven, Any).
 *
 * @author remy.decourcelle@epita.fr hamza.ouhmani@epita.fr
 * @version 1.0
 */
@Given()
public class Exceptions {
    public static <EXCEPTION_T extends Exception>
    void mayThrow(final ThrowingRunnable<EXCEPTION_T> runnable) {
        try {
            runnable.run();
        } catch (final Exception throwable) {
            sneakyThrow(throwable);
        }
    }

    public static <EXCEPTION_T extends Exception, VALUE_T>
    VALUE_T mayThrow(final ThrowingSupplier<VALUE_T, EXCEPTION_T> supplier) {
        try {
            return supplier.get();
        } catch (final Exception throwable) {
            sneakyThrow(throwable);
            return null; // never reached.
        }
    }
}
