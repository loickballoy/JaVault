package fr.epita.assistants.myide.domain.entity;

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
