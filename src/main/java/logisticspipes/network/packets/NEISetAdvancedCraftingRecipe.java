package logisticspipes.network.packets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import logisticspipes.modules.ModuleCrafter;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.network.abstractpackets.ModuleCoordinatesPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class NEISetAdvancedCraftingRecipe extends ModuleCoordinatesPacket {

    @Getter
    @Setter
    private List<ItemStack> inputs = new ArrayList<>();

    @Getter
    @Setter
    private List<ItemStack> outputs = new ArrayList<>();

    @Getter
    @Setter
    private List<FluidStack> fluidInputs = new ArrayList<>();

    public NEISetAdvancedCraftingRecipe(int id) {
        super(id);
    }

    @Override
    public void processPacket(EntityPlayer player) {
        ModuleCrafter module = getLogisticsModule(player, ModuleCrafter.class);
        if (module != null) {
            module.handleAdvancedNEIRecipePacket(inputs, outputs, fluidInputs, player);
        }
    }

    @Override
    public ModernPacket template() {
        return new NEISetAdvancedCraftingRecipe(getId());
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        super.writeData(data);
        data.writeInt(inputs.size());
        for (ItemStack stack : inputs) {
            data.writeItemStack(stack);
        }
        data.writeInt(outputs.size());
        for (ItemStack stack : outputs) {
            data.writeItemStack(stack);
        }
        data.writeInt(fluidInputs.size());
        for (FluidStack stack : fluidInputs) {
            data.writeFluidStack(stack);
        }
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        super.readData(data);
        int inputSize = data.readInt();
        inputs = new ArrayList<>(inputSize);
        for (int i = 0; i < inputSize; i++) {
            inputs.add(data.readItemStack());
        }
        int outputSize = data.readInt();
        outputs = new ArrayList<>(outputSize);
        for (int i = 0; i < outputSize; i++) {
            outputs.add(data.readItemStack());
        }
        int fluidSize = data.readInt();
        fluidInputs = new ArrayList<>(fluidSize);
        for (int i = 0; i < fluidSize; i++) {
            fluidInputs.add(data.readFluidStack());
        }
    }
}
