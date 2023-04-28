package logisticspipes.pipes.unrouted;

import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.pipes.basic.CoreUnroutedPipe;
import logisticspipes.textures.Textures;
import logisticspipes.transport.PipeTransportLogistics;

public class PipeItemsBasicTransport extends CoreUnroutedPipe {

    public PipeItemsBasicTransport(Item item) {
        super(new PipeTransportLogistics(false), item);
    }

    @Override
    public void writeData(LPDataOutputStream data) {}

    @Override
    public void readData(LPDataInputStream data) {}

    @Override
    public int getIconIndex(ForgeDirection direction) {
        return Textures.LOGISTICSPIPE_BASIC_TRANSPORT_TEXTURE.normal;
    }

    @Override
    public int getTextureIndex() {
        return Textures.LOGISTICSPIPE_BASIC_TRANSPORT_TEXTURE.newTexture;
    }
}
