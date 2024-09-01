/*
 * Copyright (c) Krapht, 2011 "LogisticsPipes" is distributed under the terms of the Minecraft Mod Public License 1.0,
 * or MMPL. Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package logisticspipes.utils.item;

import java.util.LinkedList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.interfaces.IStack;
import logisticspipes.logisticspipes.IRoutedItem;
import logisticspipes.pipes.basic.CoreRoutedPipe.ItemSendMode;
import logisticspipes.proxy.computers.interfaces.ILPCCTypeHolder;
import logisticspipes.utils.tuples.Triplet;
import lombok.Getter;
import lombok.Setter;

public final class ItemIdentifierStack implements IStack, ILPCCTypeHolder {

    private Object ccType;
    private final ItemIdentifier _item;

    @Getter
    @Setter
    private int stackSize;

    public static ItemIdentifierStack getFromStack(ItemStack stack) {
        return new ItemIdentifierStack(ItemIdentifier.get(stack), stack.stackSize);
    }

    public ItemIdentifierStack(ItemIdentifier item, int stackSize) {
        _item = item;
        setStackSize(stackSize);
    }

    public ItemIdentifier getItem() {
        return _item;
    }

    public void lowerStackSize(int stackSize) {
        this.stackSize -= stackSize;
    }

    public ItemStack unsafeMakeNormalStack() {
        return _item.unsafeMakeNormalStack(stackSize);
    }

    public ItemStack makeNormalStack() {
        return _item.makeNormalStack(stackSize);
    }

    public EntityItem makeEntityItem(World world, double x, double y, double z) {
        return _item.makeEntityItem(stackSize, world, x, y, z);
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof ItemIdentifierStack) {
            ItemIdentifierStack stack = (ItemIdentifierStack) that;
            return stack._item.equals(_item) && stack.getStackSize() == getStackSize();
        }
        if ((that instanceof ItemIdentifier)) {
            throw new IllegalStateException(
                    "Comparison between ItemIdentifierStack and ItemIdentifier -- did you forget a .getItem() in your code?");
        }

        return false;
    }

    @Override
    public int hashCode() {
        return _item.hashCode() ^ (1023 * getStackSize());
    }

    @Override
    public String toString() {
        return getStackSize() + "x " + _item.toString();
    }

    @Override
    public ItemIdentifierStack clone() {
        return new ItemIdentifierStack(_item, getStackSize());
    }

    public String getFriendlyName() {
        return getStackSize() + " " + _item.getFriendlyName();
    }

    public static LinkedList<ItemIdentifierStack> getListFromInventory(IInventory inv) {
        return ItemIdentifierStack.getListFromInventory(inv, false);
    }

    public static LinkedList<ItemIdentifierStack> getListFromInventory(IInventory inv, boolean removeNull) {
        LinkedList<ItemIdentifierStack> list = new LinkedList<>();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i) == null) {
                if (!removeNull) {
                    list.add(null);
                }
            } else {
                list.add(ItemIdentifierStack.getFromStack(inv.getStackInSlot(i)));
            }
        }
        return list;
    }

    public static LinkedList<ItemIdentifierStack> getListSendQueue(
            LinkedList<Triplet<IRoutedItem, ForgeDirection, ItemSendMode>> _sendQueue) {
        LinkedList<ItemIdentifierStack> list = new LinkedList<>();
        for (Triplet<IRoutedItem, ForgeDirection, ItemSendMode> part : _sendQueue) {
            if (part == null) {
                list.add(null);
            } else {
                boolean added = false;
                for (ItemIdentifierStack stack : list) {
                    if (stack.getItem().equals(part.getValue1().getItemIdentifierStack().getItem())) {
                        stack.setStackSize(stack.getStackSize() + part.getValue1().getItemIdentifierStack().stackSize);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    list.add(part.getValue1().getItemIdentifierStack().clone());
                }
            }
        }
        return list;
    }

    @Override
    public ItemIdentifier getItemIdentifier() {
        return _item;
    }

    @Override
    public void setCCType(Object type) {
        ccType = type;
    }

    @Override
    public Object getCCType() {
        return ccType;
    }
}
