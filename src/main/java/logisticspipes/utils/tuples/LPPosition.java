package logisticspipes.utils.tuples;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.network.abstractpackets.CoordinatesPacket;
import logisticspipes.pipes.basic.CoreUnroutedPipe;
import logisticspipes.routing.pathfinder.IPipeInformationProvider;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class LPPosition {

    private double xPos;
    private double yPos;
    private double zPos;

    public LPPosition(double xPos, double yPos, double zPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public LPPosition(int xPos, int yPos, int zPos) {
        this((double) xPos, (double) yPos, (double) zPos);
    }

    public LPPosition(TileEntity tile) {
        this((double) tile.xCoord, (double) tile.yCoord, (double) tile.zCoord);
    }

    public LPPosition(CoreUnroutedPipe pipe) {
        this((double) pipe.getX(), (double) pipe.getY(), (double) pipe.getZ());
    }

    public LPPosition(IPipeInformationProvider pipe) {
        this((double) pipe.getX(), (double) pipe.getY(), (double) pipe.getZ());
    }

    public LPPosition(CoordinatesPacket packet) {
        this((double) packet.getPosX(), (double) packet.getPosY(), (double) packet.getPosZ());
    }

    public LPPosition(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }

    public int getX() {
        return (int) xPos;
    }

    public int getY() {
        return (int) yPos;
    }

    public int getZ() {
        return (int) zPos;
    }

    public double getXD() {
        return xPos;
    }

    public double getYD() {
        return yPos;
    }

    public double getZD() {
        return zPos;
    }

    public TileEntity getTileEntity(World world) {
        return world.getTileEntity(getX(), getY(), getZ());
    }

    public LPPosition moveForward(ForgeDirection dir, double steps) {
        switch (dir) {
            case UP:
                yPos += steps;
                break;
            case DOWN:
                yPos -= steps;
                break;
            case NORTH:
                zPos -= steps;
                break;
            case SOUTH:
                zPos += steps;
                break;
            case EAST:
                xPos += steps;
                break;
            case WEST:
                xPos -= steps;
                break;
        }
        return this;
    }

    public LPPosition moveForward(ForgeDirection dir) {
        return moveForward(dir, 1);
    }

    @Override
    public String toString() {
        return "(" + xPos + ", " + yPos + ", " + zPos + ")";
    }

    public String toIntBasedString() {
        return "(" + xPos + ", " + yPos + ", " + zPos + ")";
    }

    public LPPosition copy() {
        return new LPPosition(xPos, yPos, zPos);
    }

    public Block getBlock(IBlockAccess world) {
        return world.getBlock(getX(), getY(), getZ());
    }

    public boolean blockExists(World world) {
        return world.blockExists(getX(), getY(), getZ());
    }

    public double distanceTo(LPPosition targetPos) {
        return Math.sqrt(
                Math.pow(targetPos.xPos - xPos, 2) + Math.pow(targetPos.yPos - yPos, 2)
                        + Math.pow(targetPos.zPos - zPos, 2));
    }

    public LPPosition center() {
        xPos += 0.5D;
        yPos += 0.5D;
        zPos += 0.5D;
        return this;
    }

    public void writeToNBT(String prefix, NBTTagCompound nbt) {
        nbt.setDouble(prefix + "xPos", xPos);
        nbt.setDouble(prefix + "yPos", yPos);
        nbt.setDouble(prefix + "zPos", zPos);
    }

    public static LPPosition readFromNBT(String prefix, NBTTagCompound nbt) {
        if (nbt.hasKey(prefix + "xPos") && nbt.hasKey(prefix + "yPos") && nbt.hasKey(prefix + "zPos")) {
            return new LPPosition(
                    nbt.getDouble(prefix + "xPos"),
                    nbt.getDouble(prefix + "yPos"),
                    nbt.getDouble(prefix + "zPos"));
        }
        return null;
    }
}
