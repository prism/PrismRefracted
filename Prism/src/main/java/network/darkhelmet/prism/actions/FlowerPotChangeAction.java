package network.darkhelmet.prism.actions;

public class FlowerPotChangeAction extends BlockChangeAction {

    @Override
    public String getNiceName() {
        return getOldMaterial().name().toLowerCase().replace('_', ' ')
                + " to "
                + getMaterial().name().toLowerCase().replace('_', ' ');
    }

}
