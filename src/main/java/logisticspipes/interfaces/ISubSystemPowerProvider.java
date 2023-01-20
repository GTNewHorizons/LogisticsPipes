package logisticspipes.interfaces;

public interface ISubSystemPowerProvider {

    float getPowerLevel();

    void requestPower(int destination, float amount);

    boolean usePaused();

    String getBrand();
}
