package fr.epita.assistants.utils;

/**
 * This is one of the Throwables (CF Exceptions).
 *
 * @author remy.decourcelle@epita.fr
 * @version 1.0
 */
@Given()
@FunctionalInterface
public interface ThrowingRunnable<THROWS_T extends Exception> {
    void run() throws THROWS_T;
}
