package fr.epita.assistants.myide.domain.entity;

/**
 * This is the AspectEntity class where we implement the given Aspect interface.
 *
 * @author loick.balloy@epita.fr hamza.ouhmani@epita.fr
 * @version 1.0
 */
public class ExecutionReportEntity implements Feature.ExecutionReport {

    private boolean bool;

    public ExecutionReportEntity(boolean bool) {
        this.bool = bool;
    }

    @Override
    public boolean isSuccess() {
        return bool;
    }
}
