/*
 * Copyright (c) Krapht, 2011 "LogisticsPipes" is distributed under the terms of the Minecraft Mod Public License 1.0,
 * or MMPL. Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package logisticspipes.utils.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import lombok.Setter;

public class DummySlot extends Slot {

    @Setter
    private boolean redirectCall = false;

    public DummySlot(IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }

    @Override
    public boolean isItemValid(ItemStack p_75214_1_) {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
        return false;
    }

    @Override
    public int getSlotStackLimit() {
        if (redirectCall) {
            return super.getSlotStackLimit();
        }
        return 0;
    }
}
