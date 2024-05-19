package logisticspipes.proxy.buildcraft.subproxies;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.Test;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.pluggable.IPipePluggableRenderer;
import buildcraft.api.transport.pluggable.PipePluggable;
import io.netty.buffer.ByteBuf;

class LPBCPluggableStateTest {

    private static class FakePipePluggable extends PipePluggable {

        /** The data we'll write to the buffer so we can have different pluggables */
        private final long data;

        FakePipePluggable(long data) {
            this.data = data;
        }

        @Override
        public ItemStack[] getDropItems(IPipeTile pipe) {
            return new ItemStack[0];
        }

        @Override
        public boolean isBlocking(IPipeTile pipe, ForgeDirection direction) {
            return false;
        }

        @Override
        public AxisAlignedBB getBoundingBox(ForgeDirection side) {
            return null;
        }

        @Override
        public IPipePluggableRenderer getRenderer() {
            return null;
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {}

        @Override
        public void writeToNBT(NBTTagCompound tag) {}

        @Override
        public void writeData(ByteBuf data) {
            data.writeLong(this.data);
        }

        @Override
        public void readData(ByteBuf data) {}
    }

    @Test
    void isDirty() {
        LPBCPluggableState state = new LPBCPluggableState();
        assertTrue(state.isDirty());
        assertFalse(state.isDirty());

        PipePluggable[] pluggables = new PipePluggable[6];
        state.setPluggables(pluggables);
        assertFalse(state.isDirty());

        pluggables[0] = new FakePipePluggable(1);
        pluggables[1] = new FakePipePluggable(2);
        assertTrue(state.isDirty());
        assertFalse(state.isDirty());

        pluggables[4] = new FakePipePluggable(4);
        assertTrue(state.isDirty());
        assertFalse(state.isDirty());

        // Assign an ew pluggable but with the same data.
        pluggables[4] = new FakePipePluggable(4);
        assertFalse(state.isDirty());
    }
}
