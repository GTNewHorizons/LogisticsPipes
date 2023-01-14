package logisticspipes.interfaces;

import logisticspipes.utils.item.ItemIdentifier;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public interface IStringBasedModule {

    List<String> getStringList();

    String getStringForItem(ItemIdentifier ident);

    void listChanged();

    void readFromNBT(NBTTagCompound nbt);
}
