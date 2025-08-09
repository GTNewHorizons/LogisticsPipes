package logisticspipes.gui;

import logisticspipes.utils.Color;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import logisticspipes.blocks.powertile.LogisticsPowerProviderTileEntity;
import logisticspipes.utils.gui.LogisticsBaseGuiScreen;
import logisticspipes.utils.string.StringUtils;

import java.util.Arrays;

public class GuiPowerProvider extends LogisticsBaseGuiScreen {

    private static final String PREFIX = "gui.powerprovider.";

    private final LogisticsPowerProviderTileEntity junction;

    public GuiPowerProvider(EntityPlayer player, LogisticsPowerProviderTileEntity junction) {
        super(176, 166, 0, 0);

        this.junction = junction;
    }

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "logisticspipes",
            "textures/gui/power_junction.png");

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(GuiPowerProvider.TEXTURE);
        int j = guiLeft;
        int k = guiTop;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
        drawRect(guiLeft + 8, guiTop + 50, guiLeft + 8 + (xSize - 16) / 100 * junction.getChargeState(), guiTop + 60, Color.RED);

        String currentEnergyStringTemp = StringUtils.getStringWithSpacesFromDouble(junction.getCurrentEnergy()) + " " + junction.getBrand();
        String maxEnergyString = "/ " + StringUtils.getStringWithSpacesFromDouble(junction.getMaxEnergy()) + " " + junction.getBrand();
        String currentEnergyString = new String(new char[Math.max(0,maxEnergyString.length() - currentEnergyStringTemp.length()) + 1]).replace("\0", " ") + currentEnergyStringTemp;

        mc.fontRenderer.drawString(
                StringUtils.translate(GuiPowerProvider.PREFIX + "Logistics" + junction.getBrand() + "PowerProvider"),
                guiLeft + 8,
                guiTop + 8,
                0x404040);
        mc.fontRenderer.drawString(
                StringUtils.translate(GuiPowerProvider.PREFIX + "StoredEnergy") + ":",
                guiLeft + 8,
                guiTop + 20,
                0x404040);
        mc.fontRenderer.drawString(
                currentEnergyString,
                guiLeft + 8,
                guiTop + 30,
                0x404040);
        mc.fontRenderer.drawString(
                maxEnergyString,
                guiLeft + 8,
                guiTop + 40,
                0x404040);
        mc.fontRenderer.drawString(
            "Average IO: " + StringUtils.getStringWithSpacesFromDouble(junction.getAverageIO()) + " " + junction.getBrand() + "/t",
            guiLeft + 8,
            guiTop + 50,
            0x404040);

    }
}
