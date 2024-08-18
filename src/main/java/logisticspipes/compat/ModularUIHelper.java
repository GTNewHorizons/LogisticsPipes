package logisticspipes.compat;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.TileEntityGuiFactory;

import logisticspipes.LogisticsPipes;
import logisticspipes.pipes.basic.CoreUnroutedPipe;

public class ModularUIHelper {

    public static final UITexture BACKGROUND_TEXTURE = UITexture.builder()
            .location(LogisticsPipes.rl("textures/gui/GuiBackground.png")).imageSize(45, 45).adaptable(15).build();

    public static void openPipeUI(EntityPlayer player, CoreUnroutedPipe pipe) {
        World world = pipe.getWorld();
        if (world == null || world.isRemote) return;

        TileEntityGuiFactory.open(player, pipe.getX(), pipe.getY(), pipe.getZ());
    }

    public static IKey reallyDynamicKey(Supplier<IKey> keySupplier) {
        return IKey.dynamic(() -> keySupplier.get().get());
    }
}