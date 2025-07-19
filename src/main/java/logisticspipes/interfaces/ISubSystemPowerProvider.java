package logisticspipes.interfaces;

public interface ISubSystemPowerProvider {

    float getPowerLevel();

    void requestPower(int destination, long amount);

    String getBrand();
}
