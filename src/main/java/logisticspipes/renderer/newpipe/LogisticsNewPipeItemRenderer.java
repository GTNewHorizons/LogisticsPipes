package logisticspipes.renderer.newpipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.CapturingTessellator;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.vbo.VBOManager;
import com.gtnewhorizon.gtnhlib.client.renderer.vbo.VertexBuffer;
import com.gtnewhorizon.gtnhlib.client.renderer.vertex.DefaultVertexFormat;

import logisticspipes.LogisticsPipes;
import logisticspipes.items.ItemLogisticsPipe;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.object3d.interfaces.IIconTransformation;
import logisticspipes.proxy.object3d.interfaces.IModel3D;
import logisticspipes.renderer.newpipe.LogisticsNewRenderPipe.Corner;
import logisticspipes.renderer.newpipe.LogisticsNewRenderPipe.Edge;
import logisticspipes.renderer.newpipe.LogisticsNewSolidBlockWorldRenderer.BlockRotation;
import logisticspipes.renderer.newpipe.LogisticsNewSolidBlockWorldRenderer.CoverSides;
import logisticspipes.textures.Textures;
import logisticspipes.utils.tuples.Pair;

public class LogisticsNewPipeItemRenderer implements IItemRenderer {

    private final boolean renderAsBlock;

    public LogisticsNewPipeItemRenderer(boolean flag) {
        renderAsBlock = flag;
    }

    private void renderPipeItem(RenderBlocks render, ItemStack item, float translateX, float translateY,
            float translateZ) {
        GL11.glPushMatrix();
        // don't break other mods' guis when holding a pipe
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        // force transparency
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);

        // GL11.glBindTexture(GL11.GL_TEXTURE_2D, 10);
        GL11.glTranslatef(translateX, translateY, translateZ);
        Block block = LogisticsPipes.LogisticsPipeBlock;
        if (item.getItem() instanceof ItemLogisticsPipe) {
            ItemLogisticsPipe lItem = (ItemLogisticsPipe) item.getItem();
            int renderList = lItem.getNewPipeRenderList();

            if (renderList == -1) {
                if (LogisticsPipes.enableVBO) {
                    renderList = buildVBO(lItem);
                } else {
                    renderList = buildDisplayList(lItem);
                }
            }

            if (LogisticsPipes.enableVBO) {
                VBOManager.get(renderList).render();
            } else {
                GL11.glCallList(renderList);
            }
        }
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private int buildDisplayList(ItemLogisticsPipe lItem) {
        Tessellator tessellator = Tessellator.instance;
        lItem.setNewPipeRenderList(GL11.glGenLists(1));
        int listID = lItem.getNewPipeRenderList();
        GL11.glNewList(listID, GL11.GL_COMPILE);
        tessellator.startDrawingQuads();
        generatePipeRenderList(lItem.getNewPipeIconIndex());
        tessellator.draw();
        GL11.glEndList();
        return listID;
    }

    private int buildVBO(ItemLogisticsPipe lItem) {
        TessellatorManager.startCapturing();
        CapturingTessellator tess = (CapturingTessellator) TessellatorManager.get();

        tess.startDrawingQuads();
        generatePipeRenderList(lItem.getNewPipeIconIndex());
        tess.draw();

        VertexBuffer vbo = TessellatorManager.stopCapturingToVBO(DefaultVertexFormat.POSITION_TEXTURE_NORMAL);
        int vboID = VBOManager.generateDisplayLists(1);
        VBOManager.registerVBO(vboID, vbo);

        lItem.setNewPipeRenderList(vboID);
        return vboID;
    }

    private void generatePipeRenderList(int texture) {
        List<Pair<IModel3D, IIconTransformation>> objectsToRender = new ArrayList<>();

        for (Corner corner : Corner.values()) {
            for (IModel3D model : LogisticsNewRenderPipe.corners_M.get(corner)) {
                objectsToRender.add(new Pair<>(model, LogisticsNewRenderPipe.basicTexture));
            }
        }

        for (Edge edge : Edge.values()) {
            objectsToRender
                    .add(new Pair<>(LogisticsNewRenderPipe.edges.get(edge), LogisticsNewRenderPipe.basicTexture));
        }

        // ArrayList<Pair<CCModel, IconTransformation>> objectsToRender2 = new ArrayList<Pair<CCModel,
        // IconTransformation>>();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            for (IModel3D model : LogisticsNewRenderPipe.texturePlate_Outer.get(dir)) {
                IIconTransformation icon = Textures.LPnewPipeIconProvider.getIcon(texture);
                if (icon != null) {
                    objectsToRender.add(new Pair<>(model, icon));
                }
            }
        }

        for (Pair<IModel3D, IIconTransformation> part : objectsToRender) {
            part.getValue1().render(part.getValue2());
        }
    }

    private void renderBlockItem(RenderBlocks render, ItemStack item, float translateX, float translateY,
            float translateZ) {
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT); // don't break other mods' guis when holding a pipe
        // force transparency
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glTranslatef(translateX, translateY, translateZ);
        Block block = LogisticsPipes.LogisticsPipeBlock;
        Tessellator tess = Tessellator.instance;

        BlockRotation rotation = BlockRotation.ZERO;

        tess.startDrawingQuads();

        IIconTransformation icon = SimpleServiceLocator.cclProxy
                .createIconTransformer(Textures.LOGISTICS_REQUEST_TABLE_NEW);

        // Draw
        LogisticsNewSolidBlockWorldRenderer.block.get(rotation).render(icon);
        for (CoverSides side : CoverSides.values()) {
            LogisticsNewSolidBlockWorldRenderer.texturePlate_Outer.get(side).get(rotation).render(icon);
        }
        tess.draw();
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopAttrib(); // nicely leave the rendering how it was
    }

    private void renderItem(RenderBlocks render, ItemStack item, float translateX, float translateY, float translateZ) {
        if (renderAsBlock) {
            renderBlockItem(render, item, translateX, translateY, translateZ);
        } else {
            renderPipeItem(render, item, translateX, translateY, translateZ);
        }
    }

    /**
     * IItemRenderer implementation
     **/
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        switch (type) {
            case ENTITY:
            case INVENTORY:
            case EQUIPPED_FIRST_PERSON:
            case EQUIPPED:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
            case ENTITY:
            case INVENTORY:
                renderItem((RenderBlocks) data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            case EQUIPPED:
                if (renderAsBlock) {
                    renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f);
                } else {
                    renderItem((RenderBlocks) data[0], item, -0.4f, 0.50f, 0.35f);
                }
                break;
            case EQUIPPED_FIRST_PERSON:
                if (renderAsBlock) {
                    renderItem((RenderBlocks) data[0], item, 0f, 0.0f, 0.0f);
                } else {
                    renderItem((RenderBlocks) data[0], item, -0.4f, 0.50f, 0.35f);
                }
                break;
            default:
        }
    }
}
