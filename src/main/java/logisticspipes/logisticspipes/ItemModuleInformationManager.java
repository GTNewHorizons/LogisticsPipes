package logisticspipes.logisticspipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import logisticspipes.interfaces.IClientInformationProvider;
import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.proxy.MainProxy;

public class ItemModuleInformationManager {

    private static final List<String> Filter = new ArrayList<>();

    static {
        ItemModuleInformationManager.Filter.add("moduleInformation");
        ItemModuleInformationManager.Filter.add("informationList");
        ItemModuleInformationManager.Filter.add("Random-Stack-Prevent");
    }

    public static void saveInfotmation(ItemStack itemStack, LogisticsModule module) {
        if (module == null) {
            return;
        }
        NBTTagCompound nbt = new NBTTagCompound();
        module.writeToNBT(nbt);
        if (nbt.equals(new NBTTagCompound())) {
            return;
        }
        if (MainProxy.isClient()) {
            NBTTagList list = new NBTTagList();
            String info1 = "Please reopen the window";
            String info2 = "to see the information.";
            list.appendTag(new NBTTagString(info1));
            list.appendTag(new NBTTagString(info2));
            if (!itemStack.hasTagCompound()) {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound stacktag = itemStack.getTagCompound();
            stacktag.setTag("informationList", list);
            stacktag.setDouble("Random-Stack-Prevent", new Random().nextDouble());
            return;
        }
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound stacktag = itemStack.getTagCompound();
        stacktag.setTag("moduleInformation", nbt);
        if (module instanceof IClientInformationProvider) {
            List<String> information = ((IClientInformationProvider) module).getClientInformation();
            if (information.size() > 0) {
                NBTTagList list = new NBTTagList();
                for (String info : information) {
                    list.appendTag(new NBTTagString(info));
                }
                stacktag.setTag("informationList", list);
            }
        }
        stacktag.setDouble("Random-Stack-Prevent", new Random().nextDouble());
    }

    public static void readInformation(ItemStack itemStack, LogisticsModule module) {
        if (module == null) {
            return;
        }
        if (itemStack.hasTagCompound()) {
            NBTTagCompound nbt = itemStack.getTagCompound();
            if (nbt.hasKey("moduleInformation")) {
                NBTTagCompound moduleInformation = nbt.getCompoundTag("moduleInformation");
                module.readFromNBT(moduleInformation);
            }
        }
    }


    /**
     * Remove mod-specific NBT tags from the given ItemStack.
     */
    public static void removeInformation(final ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasTagCompound()) {
            return;
        }

        final NBTTagCompound nbtCopy = new NBTTagCompound();
        for (Object e : itemStack.getTagCompound().tagMap.entrySet()) {
            @SuppressWarnings("unchecked")
            final Map.Entry<String, NBTBase> entry = (Map.Entry<String, NBTBase>) e;
            if (!ItemModuleInformationManager.Filter.contains(entry.getKey())) {
                nbtCopy.setTag(entry.getKey(), entry.getValue());
            }
        }
        itemStack.setTagCompound(nbtCopy);
    }
}
