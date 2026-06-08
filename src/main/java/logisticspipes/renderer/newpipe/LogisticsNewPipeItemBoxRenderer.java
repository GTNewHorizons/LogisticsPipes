package logisticspipes.renderer.newpipe;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.DirectTessellator;
import com.gtnewhorizon.gtnhlib.client.renderer.vao.IVertexArrayObject;
import com.gtnewhorizon.gtnhlib.client.renderer.vao.VertexBufferType;

import logisticspipes.items.LogisticsFluidContainer;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.renderer.CustomBlockRenderer;
import logisticspipes.renderer.CustomBlockRenderer.RenderInfo;
import logisticspipes.renderer.FluidContainerRenderer;
import logisticspipes.utils.FluidIdentifier;
import logisticspipes.utils.item.ItemIdentifierStack;

public class LogisticsNewPipeItemBoxRenderer {

    private static final int RENDER_SIZE = 40;

    private IVertexArrayObject renderVBO;
    private static final ResourceLocation BLOCKS = new ResourceLocation("textures/atlas/blocks.png");
    private static final Map<FluidIdentifier, IVertexArrayObject[]> renderLists = new HashMap<>();

    public void doRenderItem(ItemIdentifierStack itemIdentifierStack, double x, double y, double z, double boxScale) {
        if (LogisticsNewRenderPipe.innerTransportBox == null) return;
        GL11.glPushMatrix();

        if (renderVBO == null) {
            renderVBO = generateInnerBoxVBO();
        }

        GL11.glTranslated(x, y, z);
        Minecraft.getMinecraft().getTextureManager().bindTexture(LogisticsNewPipeItemBoxRenderer.BLOCKS);
        GL11.glScaled(boxScale, boxScale, boxScale);
        GL11.glTranslated(-0.5, -0.5, -0.5);

        renderVBO.render();

        GL11.glTranslated(0.5, 0.5, 0.5);
        GL11.glScaled(1 / boxScale, 1 / boxScale, 1 / boxScale);
        GL11.glTranslated(-0.5, -0.5, -0.5);

        if (itemIdentifierStack != null && itemIdentifierStack.getItem().item instanceof LogisticsFluidContainer) {
            FluidStack f = SimpleServiceLocator.logisticsFluidManager.getFluidFromContainer(itemIdentifierStack);
            if (f != null) {
                FluidContainerRenderer.skipNext = true;
                IVertexArrayObject vbo = getRendererForFluid(f);
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                vbo.render();

                GL11.glPopAttrib();
            }
        }

        GL11.glPopMatrix();
    }

    private IVertexArrayObject getRendererForFluid(FluidStack fluid) {
        IVertexArrayObject[] array = renderLists.computeIfAbsent(
                FluidIdentifier.get(fluid),
                k -> new IVertexArrayObject[LogisticsNewPipeItemBoxRenderer.RENDER_SIZE]);
        int pos = Math.min(
                (int) (((Math.min(fluid.amount, 5000) * 1.0F) * LogisticsNewPipeItemBoxRenderer.RENDER_SIZE) / 5000),
                LogisticsNewPipeItemBoxRenderer.RENDER_SIZE - 1);
        if (array[pos] != null) {
            return array[pos];
        }
        float ratio = pos * 1.0F / (LogisticsNewPipeItemBoxRenderer.RENDER_SIZE - 1);

        RenderInfo block = new RenderInfo();
        block.baseBlock = fluid.getFluid().getBlock();
        block.texture = fluid.getFluid().getStillIcon();

        block.minX = 0.32;
        block.maxX = 0.68;

        block.minY = 0.32;
        block.maxY = 0.32 + (0.68 - 0.32) * ratio;

        block.minZ = 0.32;
        block.maxZ = 0.68;

        array[pos] = generateVBO(block);

        return array[pos];
    }

    private IVertexArrayObject generateVBO(RenderInfo block) {
        DirectTessellator.startCapturing();

        CustomBlockRenderer.INSTANCE.renderBlock(block, Minecraft.getMinecraft().theWorld, 0, 0, 0, false, true);

        return DirectTessellator.stopCapturingToVBO(VertexBufferType.IMMUTABLE);
    }

    private IVertexArrayObject generateInnerBoxVBO() {
        final DirectTessellator tess = DirectTessellator.startCapturing();
        tess.startDrawingQuads();
        LogisticsNewRenderPipe.innerTransportBox.render(LogisticsNewRenderPipe.innerBoxTexture);
        tess.draw();

        return DirectTessellator.stopCapturingToVBO(VertexBufferType.IMMUTABLE);
    }

}
