package logisticspipes.mixins.late.computercraft;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.core.lua.LuaJLuaMachine;
import logisticspipes.proxy.cc.LPASMHookCC;

@Mixin(LuaJLuaMachine.class)
public class MixinLuaJLuaMachine {

    @ModifyReturnValue(at = @At("TAIL"), method = "wrapLuaObject", remap = false)
    private LuaTable logisticspipes$onCCWrappedILuaObject(LuaTable table, ILuaObject object) {
        return LPASMHookCC.onCCWrappedILuaObject(table, object);
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "wrapLuaObject", remap = false)
    private void logisticspipes$returnCCWrappedILuaObject(ILuaObject object, CallbackInfoReturnable<LuaTable> cir) {
        if (LPASMHookCC.handleCCWrappedILuaObject(object)) {
            cir.setReturnValue(LPASMHookCC.returnCCWrappedILuaObject(object));
        }
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "toObject", remap = false)
    private void logisticspipes$returnCCToObject(LuaValue value, CallbackInfoReturnable<Object> cir) {
        if (LPASMHookCC.handleCCToObject(value)) {
            cir.setReturnValue(LPASMHookCC.returnCCToObject(value));
        }
    }
}
