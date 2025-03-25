package logisticspipes.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import logisticspipes.proxy.computers.interfaces.CCCommand;
import logisticspipes.proxy.computers.interfaces.SetSourceMod;
import logisticspipes.proxy.computers.wrapper.CCWrapperInformation.SourceMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import logisticspipes.interfaces.IQueueCCEvent;
import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.computers.objects.CCSinkResponder;
import logisticspipes.utils.OneList;
import logisticspipes.utils.SinkReply;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.item.ItemIdentifierStack;

public class ModuleCCBasedItemSink extends LogisticsModule {

    private IQueueCCEvent eventQueuer;

    private final List<CCSinkResponder> responses = new ArrayList<>();

    private SourceMod sourceMod;

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void registerCCEventQueuer(IQueueCCEvent eventQueuer) {
        this.eventQueuer = eventQueuer;
    }

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

    @Override
    public SinkReply sinksItem(ItemIdentifier stack, int bestPriority, int bestCustomPriority, boolean allowDefault,
            boolean includeInTransit) {
        return null;
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    @Override
    public void tick() {}

    @Override
    public boolean hasGenericInterests() {
        return true;
    }

    @Override
    public Collection<ItemIdentifier> getSpecificInterests() {
        return null;
    }

    @Override
    public boolean interestedInAttachedInventory() {
        return false;
    }

    @Override
    public boolean interestedInUndamagedID() {
        return false;
    }

    @Override
    public boolean recievePassive() {
        return false;
    }

    @Override
    public List<CCSinkResponder> queueCCSinkEvent(ItemIdentifierStack item) {
        CCSinkResponder response = new CCSinkResponder(item, _service.getSourceID(), eventQueuer);
        eventQueuer.queueEvent("ItemSink", new Object[] { nextResponseId(response) }); // Interactive objects cannot be transmitted in OC signal.
        return new OneList<>(response);
    }

    @SetSourceMod
    public void setSourceMod(SourceMod sourceMod) {
        this.sourceMod = sourceMod;
    }

    @CCCommand(description = "return response from CC Based Quick Sort by local response id")
    public Object getResponseCCBQ(Double id) {
        CCSinkResponder response = responses.get(id.intValue() - 1);
        Object wrapper = response;
        if (response.isDestroy()) return wrapper;
        switch (sourceMod) {
            case OPENCOMPUTERS:
                wrapper = SimpleServiceLocator.openComputersProxy.getWrappedObject(response);
                break;
            case COMPUTERCRAFT:
                wrapper = SimpleServiceLocator.ccProxy.getAnswer(response);
                break;
        }
        return wrapper;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconTexture(IIconRegister register) {
        return register.registerIcon("logisticspipes:itemModule/ModuleCCBasedItemSink");
    }

    private int nextResponseId(CCSinkResponder response) {
        boolean noSet = true;
        int id = responses.size();
        for (int i = 0; i < responses.size(); i++) {
            CCSinkResponder resp = responses.get(i);
            if (resp != null && resp.isDestroy())
                responses.set(i, null);
            if (resp == null && noSet) {
                responses.set(i, response);
                noSet = false;
                id = i;
            }
        }

        if (noSet) {
            responses.add(response);
        }

        return id + 1;
    }
}
