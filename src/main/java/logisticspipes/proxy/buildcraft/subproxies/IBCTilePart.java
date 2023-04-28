package logisticspipes.proxy.buildcraft.subproxies;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.asm.IgnoreDisabledProxy;

public interface IBCTilePart {

    boolean hasBlockingPluggable(ForgeDirection side);

    @IgnoreDisabledProxy
    void writeToNBT_LP(NBTTagCompound nbt);

    @IgnoreDisabledProxy
    void readFromNBT_LP(NBTTagCompound nbt);

    boolean isSolidOnSide(ForgeDirection side);

    void invalidate_LP();

    void validate_LP();

    void updateEntity_LP();

    void scheduleNeighborChange();

    boolean hasGate(ForgeDirection orientation);

    IBCRenderState getBCRenderState();

    IBCPipePart getBCPipePart();

    IBCPluggableState getBCPlugableState();

    boolean hasEnabledFacade(ForgeDirection dir);

    IBCPipePluggable getBCPipePluggable(ForgeDirection sideHit);

    void readOldRedStone(NBTTagCompound nbt);

    void afterStateUpdated();

    Object getOriginal();

    boolean hasPipePluggable(ForgeDirection dir);

    void setWorldObj_LP(World world);
}
