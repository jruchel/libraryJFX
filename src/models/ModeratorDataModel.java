package models;

import models.tableRepresentations.ModeratorRefundTableRepresentation;
import tasks.ModeratorRefundDataRetrievalTask;
import updating.Updater;
import web.TaskRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ModeratorDataModel {
    private List<ModeratorRefundTableRepresentation> refunds;

    private static ModeratorDataModel instance;

    public static ModeratorDataModel getInstance() {
        if (instance == null) instance = new ModeratorDataModel();
        return instance;
    }

    private ModeratorDataModel(List<ModeratorRefundTableRepresentation> refunds) {
        this.refunds = refunds;

    }

    private ModeratorDataModel() {
        this.refunds = new ArrayList<>();
    }

    public void updateData(Runnable onTaskComplete) {
        TaskRunner taskRunner = new TaskRunner(new ModeratorRefundDataRetrievalTask(), onTaskComplete);
        taskRunner.run();
    }

    public void updateData() {
        updateData(null);
    }

    public List<ModeratorRefundTableRepresentation> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<ModeratorRefundTableRepresentation> refunds) {
        this.refunds = refunds;
        try {
            Updater.update(this.getClass());
        } catch (ClassNotFoundException | IOException | URISyntaxException ignored) {
        }
    }
}
