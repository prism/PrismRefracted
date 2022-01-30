package network.darkhelmet.prism.api.modifications;

public record ModificationQueueResult(int countSkipped, int countPlanned, int countApplied) {
}
