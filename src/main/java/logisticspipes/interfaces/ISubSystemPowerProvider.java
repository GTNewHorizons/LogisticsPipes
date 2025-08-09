package logisticspipes.interfaces;

public interface ISubSystemPowerProvider {
    /**
     * Request Power from this PowerProvider
     * @param destination the requester
     * @param amount the amount of energy
     */
    void requestPower(int destination, double amount);

    /**
     * @return EU or RF
     */
    String getBrand();
}
