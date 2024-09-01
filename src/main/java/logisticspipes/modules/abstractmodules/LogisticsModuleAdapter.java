package logisticspipes.modules.abstractmodules;

public abstract class LogisticsModuleAdapter extends LogisticsModule {

    @Override
    public int getX() {
        return _service.getX();
    }

    @Override
    public int getY() {
        return _service.getY();
    }

    @Override
    public int getZ() {
        return _service.getZ();
    }
}
