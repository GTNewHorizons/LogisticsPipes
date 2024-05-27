package logisticspipes.renderer;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import logisticspipes.LogisticsPipes;
import logisticspipes.utils.item.ItemIdentifier;

/**
 * This class renders the actual items that travel through the pipes. Since we (ab)use EntityItem's for this, this class
 * also has a cache to not allocate a fresh EntityItem for each individual travelling box.
 */
public class TravelingItemRenderer {

    private final Map<ItemIdentifier, EntityItem> entityCache = new HashMap<>();
    private final RenderItem renderItem = new RenderItem();

    public TravelingItemRenderer() {
        renderItem.setRenderManager(RenderManager.instance);
    }

    public void renderInWorld(World world, ItemIdentifier itemIdentifier, float partialTickTime) {
        EntityItem entityItem = entityCache.get(itemIdentifier);
        if (entityItem == null) {
            entityItem = new EntityItem(world, 0, 0, 0, itemIdentifier.makeNormalStack(1));
            entityItem.hoverStart = 0;
            entityCache.put(itemIdentifier, entityItem);
        }

        Item item = itemIdentifier.item;
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).field_150939_a;
            if (block instanceof BlockPane) {
                GL11.glScalef(0.5F, 0.5F, 0.5F);
            }
        } else if (item == LogisticsPipes.logisticsRequestTable) {
            GL11.glScalef(0.5F, 0.5F, 0.5F);
        }

        renderItem.doRender(entityItem, 0.0d, 0.0d, 0.0d, 0.0F, partialTickTime);
    }
}
