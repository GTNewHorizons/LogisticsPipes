package logisticspipes.interfaces;

public interface IPowerLevelDisplay {

    /**
     * @return how much charge in percent (0 - 100)
     */
    int getChargeState();

    /**
     * @return currently stored energy
     */
    double getCurrentEnergy();

    /**
     * @return the maximum stored energy
     */
    double getCurrentCapacity();

    boolean isHUDInvalid();

    /**
     * @return the kind of power this entity stores
     */
    String getBrand();
}
