package fr.epita.assistants.utils;

/**
 * This is one of the Throwables (CF Exceptions).
 *
 * @author remy.decourcelle@epita.fr
 * @version 1.0
 */
@Given()
@FunctionalInterface
public interface ThrowingSupplier<SUPPLY_T, THROWS_T extends Exception> {
    SUPPLY_T get() throws THROWS_T;
}
