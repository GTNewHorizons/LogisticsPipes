package logisticspipes.gui.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import logisticspipes.modules.ModuleApiaristSink;
import logisticspipes.modules.ModuleApiaristSink.FilterType;
import logisticspipes.modules.ModuleApiaristSink.SinkSetting;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.packets.module.BeeModuleSetBeePacket;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.utils.gui.DummyContainer;
import logisticspipes.utils.gui.IItemTextureRenderSlot;
import logisticspipes.utils.gui.ISmallColorRenderSlot;
import logisticspipes.utils.item.ItemIdentifierInventory;

public class GuiApiaristSink extends ModuleBaseGui {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "logisticspipes",
            "textures/gui/apiarist_sink.png");

    private final ModuleApiaristSink module;

    public GuiApiaristSink(ModuleApiaristSink module, EntityPlayer player) {
        super(null, module);

        this.module = module;

        DummyContainer dummy = new DummyContainer(player.inventory, this.module.getInventoryBee());
        dummy.addNormalSlotsForHotbar(8, 157);
        dummy.addDummySlot(0, 110, 121);

        for (int i = 0; i < 6; i++) {
            SinkSetting filter = module.filter[i];
            addRenderSlot(new TypeSlot(24 + (i * 22), 20, filter, i));
            addRenderSlot(new GroupSlot(guiLeft + 29 + (i * 22), guiTop + 45, filter, i));
            addRenderSlot(new BeeSlot(24 + (i * 22), 60, filter, 0, i));
            addRenderSlot(new BeeSlot(24 + (i * 22), 60 + 18, filter, 1, i));
        }

        inventorySlots = dummy;

        xSize = 175;
        ySize = 180;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(GuiApiaristSink.TEXTURE);
        int j = guiLeft;
        int k = guiTop;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        mc.fontRenderer.drawString(module.getInventoryBee().getInventoryName(), 35, 125, 0x404040);
    }

    private class TypeSlot extends IItemTextureRenderSlot {

        private final int xPos;
        private final int yPos;
        private final SinkSetting setting;
        private final int row;

        private TypeSlot(int xPos, int yPos, SinkSetting setting, int row) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.setting = setting;
            this.row = row;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getTextureIcon() {
            if (setting.filterType == null) {
                return null;
            }
            return SimpleServiceLocator.forestryProxy.getIconFromTextureManager("analyzer/" + setting.filterType.icon);
        }

        @Override
        public void mouseClicked(int button) {
            if (button == 2) {
                setting.FilterTypeReset();
            }
            if (button == 0) {
                setting.FilterTypeUp();
            }
            if (button == 1) {
                setting.FilterTypeDown();
            }
            MainProxy.sendPacketToServer(
                    PacketHandler.getPacket(BeeModuleSetBeePacket.class).setInteger2(row).setInteger3(3)
                            .setInteger4(setting.filterType.ordinal()).setModulePos(module));
        }

        @Override
        public boolean drawSlotBackground() {
            return true;
        }

        @Override
        public int getXPos() {
            return xPos;
        }

        @Override
        public int getYPos() {
            return yPos;
        }

        @Override
        public boolean drawSlotIcon() {
            return true;
        }

        @Override
        public String getToolTipText() {
            if (setting.filterType == null) {
                return "";
            }
            return SimpleServiceLocator.forestryProxy.getForestryTranslation(setting.filterType.path);
        }

        @Override
        public boolean displayToolTip() {
            return setting.filterType != FilterType.Null;
        }

        @Override
        public boolean customRender(Minecraft mc, float zLevel) {
            return false;
        }
    }

    private class GroupSlot extends ISmallColorRenderSlot {

        private final int xPos;
        private final int yPos;
        private final SinkSetting setting;
        private final int row;

        private GroupSlot(int xPos, int yPos, SinkSetting setting, int row) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.setting = setting;
            this.row = row;
        }

        @Override
        public void mouseClicked(int button) {
            if (button == 2) {
                setting.filterGroupReset();
            }
            if (button == 0) {
                setting.filterGroupUp();
            }
            if (button == 1) {
                setting.filterGroupDown();
            }
            MainProxy.sendPacketToServer(
                    PacketHandler.getPacket(BeeModuleSetBeePacket.class).setInteger2(row).setInteger3(2)
                            .setInteger4(setting.filterGroup).setModulePos(module));
        }

        @Override
        public boolean drawSlotBackground() {
            return setting.filterType != FilterType.Null;
        }

        @Override
        public int getXPos() {
            return xPos;
        }

        @Override
        public int getYPos() {
            return yPos;
        }

        @Override
        public String getToolTipText() {
            switch (setting.filterGroup) {
                case 1:
                    return "GroupColor: RED";
                case 2:
                    return "GroupColor: Green";
                case 3:
                    return "GroupColor: Blue";
                case 4:
                    return "GroupColor: Yellow";
                case 5:
                    return "GroupColor: Cyan";
                case 6:
                    return "GroupColor: Purple";
                default:
                    return "No Group";
            }
        }

        @Override
        public boolean displayToolTip() {
            return drawSlotBackground();
        }

        @Override
        public int getColor() {
            switch (setting.filterGroup) {
                case 1:
                    return 0xFFFF0000;
                case 2:
                    return 0xFF00FF00;
                case 3:
                    return 0xFF0000FF;
                case 4:
                    return 0xFFFFFF00;
                case 5:
                    return 0xFF00FFFF;
                case 6:
                    return 0xFFFF00FF;
                default:
                    return 0;
            }
        }

        @Override
        public boolean drawColor() {
            return drawSlotBackground();
        }
    }

    private class BeeSlot extends IItemTextureRenderSlot {

        private final int xPos;
        private final int yPos;
        private final SinkSetting setting;
        private final int slotNumber;
        private final int row;

        private BeeSlot(int xPos, int yPos, SinkSetting setting, int slotNumber, int row) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.setting = setting;
            this.slotNumber = slotNumber;
            this.row = row;
        }

        private void renderForestryBeeAt(Minecraft mc, int x, int y, float zLevel, String id) {
            GL11.glDisable(GL11.GL_LIGHTING);
            mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);

            for (int i = 0; i < SimpleServiceLocator.forestryProxy.getRenderPassesForAlleleId(id); i++) {
                IIcon icon = SimpleServiceLocator.forestryProxy.getIconIndexForAlleleId(id, i);
                if (icon == null) {
                    continue;
                }
                int color = SimpleServiceLocator.forestryProxy.getColorForAlleleId(id, i);
                float colorR = (color >> 16 & 0xFF) / 255.0F;
                float colorG = (color >> 8 & 0xFF) / 255.0F;
                float colorB = (color & 0xFF) / 255.0F;

                GL11.glColor4f(colorR, colorG, colorB, 1.0F);

                // Render icon
                Tessellator var9 = Tessellator.instance;
                var9.startDrawingQuads();
                var9.addVertexWithUV(x, y + 16, zLevel, icon.getMinU(), icon.getMaxV());
                var9.addVertexWithUV(x + 16, y + 16, zLevel, icon.getMaxU(), icon.getMaxV());
                var9.addVertexWithUV(x + 16, y, zLevel, icon.getMaxU(), icon.getMinV());
                var9.addVertexWithUV(x, y, zLevel, icon.getMinU(), icon.getMinV());
                var9.draw();
            }
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        @Override
        public void mouseClicked(int button) {

            if (button == 2) {
                if (slotNumber == 0) {
                    setting.firstBeeReset();
                } else {
                    setting.secondBeeReset();
                }
            }

            if (button == 0) {
                ItemIdentifierInventory inventoryBee = module.getInventoryBee();
                if (slotNumber == 0) {
                    if (!inventoryBee.isEmpty() && SinkSetting.isBee(inventoryBee, 0)) {
                        setting.firstBeeSetFirstAlleleID(inventoryBee, 0);
                    } else {
                        setting.firstBeeUp();
                    }
                } else {
                    if (!inventoryBee.isEmpty() && SinkSetting.isBee(inventoryBee, 0)) {
                        setting.secondBeeSetFirstAlleleID(module.getInventoryBee(), 0);
                    } else {
                        setting.secondBeeUp();
                    }
                }
            }
            if (button == 1) {
                ItemIdentifierInventory inventoryBee = module.getInventoryBee();
                if (slotNumber == 0) {
                    if (!inventoryBee.isEmpty() && SinkSetting.isBee(inventoryBee, 0)) {
                        if (SinkSetting.isAnalyzedBee(inventoryBee, 0)) {
                            setting.firstBeeSetSecondAlleleID(inventoryBee, 0);
                        }
                    } else {
                        setting.firstBeeDown();
                    }
                } else {
                    if (!inventoryBee.isEmpty() && SinkSetting.isBee(inventoryBee, 0)) {
                        if (SinkSetting.isAnalyzedBee(inventoryBee, 0)) {
                            setting.secondBeeSetSecondAlleleID(inventoryBee, 0);
                        }
                    } else {
                        setting.secondBeeDown();
                    }
                }
            }
            MainProxy.sendPacketToServer(
                    PacketHandler.getPacket(BeeModuleSetBeePacket.class).setInteger2(row).setInteger3(slotNumber)
                            .setString1(slotNumber == 0 ? setting.firstBee : setting.secondBee).setModulePos(module));
        }

        @Override
        public boolean drawSlotBackground() {
            return setting.filterType.secondSlots > slotNumber;
        }

        @Override
        public int getXPos() {
            return xPos;
        }

        @Override
        public int getYPos() {
            return yPos;
        }

        @Override
        public boolean drawSlotIcon() {
            return drawSlotBackground() && !(slotNumber == 0 ? setting.firstBee : setting.secondBee).isEmpty();
        }

        @Override
        public String getToolTipText() {
            return SimpleServiceLocator.forestryProxy
                    .getAlleleName(slotNumber == 0 ? setting.firstBee : setting.secondBee);
        }

        @Override
        public boolean displayToolTip() {
            if (slotNumber == 0) {
                return !setting.firstBee.isEmpty() && drawSlotBackground();
            } else {
                return !setting.secondBee.isEmpty() && drawSlotBackground();
            }
        }

        @Override
        public boolean customRender(Minecraft mc, float zLevel) {
            if (slotNumber == 0) {
                renderForestryBeeAt(mc, xPos + 1, yPos + 1, zLevel, setting.firstBee);
            } else {
                renderForestryBeeAt(mc, xPos + 1, yPos + 1, zLevel, setting.secondBee);
            }
            return true;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getTextureIcon() {
            return null;
        }
    }
}
