package logisticspipes.utils;

import java.util.Set;
import java.util.TreeSet;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import logisticspipes.utils.item.ItemIdentifier;

public class FuzzyInventoryUtil extends InventoryUtil {

    public FuzzyInventoryUtil(IInventory inventory) {
        super(inventory, false, false, 0, 0);
    }

    @Override
    public Set<ItemIdentifier> getItems() {
        Set<ItemIdentifier> items = new TreeSet<>();
        for (int i = 0; i < _inventory.getSizeInventory(); i++) {
            ItemStack stack = _inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            items.add(ItemIdentifier.get(stack).getIgnoringNBT());
        }
        return items;
    }
}
