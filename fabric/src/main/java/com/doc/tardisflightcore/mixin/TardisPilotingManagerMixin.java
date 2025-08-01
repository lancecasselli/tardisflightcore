package com.doc.tardisflightcore.mixin;

import com.doc.tardisflightcore.block.EngineCompartmentBlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import whocraft.tardis_refined.common.capability.tardis.TardisLevelOperator;
import whocraft.tardis_refined.common.tardis.manager.TardisPilotingManager;

@Mixin(value = TardisPilotingManager.class, remap = false)
public abstract class TardisPilotingManagerMixin {

    @Shadow private TardisLevelOperator operator;

    // Explicit descriptor to ensure the method match
    @Inject(method = "canBeginFlight()Z", at = @At("HEAD"), cancellable = true)
    private void flightcore_blockIfMissingComponents(CallbackInfoReturnable<Boolean> cir) {
        System.out.println("[TARDIS Flight Core] DEBUG: canBeginFlight mixin triggered");

        if (operator == null) {
            System.out.println("[TARDIS Flight Core] DEBUG: operator is NULL — cannot check components.");
            return;
        }

        World world = operator.getLevel();
        if (!(world instanceof ServerWorld level)) {
            System.out.println("[TARDIS Flight Core] DEBUG: World is NOT a ServerWorld (" + world.getClass().getName() + ")");
            return;
        }

        RegistryKey<World> dimKey = level.getRegistryKey();
        System.out.println("[TARDIS Flight Core] DEBUG: dimKey = " + dimKey);

        boolean allowed = EngineCompartmentBlockEntity.isFlightAllowed(dimKey);
        System.out.println("[TARDIS Flight Core] DEBUG: isFlightAllowed(dimKey) = " + allowed);

        if (!allowed) {
            cir.setReturnValue(false);
            cir.cancel();
            System.out.println("[TARDIS Flight Core] Flight blocked: Missing required components!");
        }
    }
}
