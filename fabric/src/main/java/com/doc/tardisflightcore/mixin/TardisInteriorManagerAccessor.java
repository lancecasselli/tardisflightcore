package com.doc.tardisflightcore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import whocraft.tardis_refined.common.capability.tardis.TardisLevelOperator;
import whocraft.tardis_refined.common.tardis.manager.TardisInteriorManager;

@Mixin(value = TardisInteriorManager.class, remap = false)
public interface TardisInteriorManagerAccessor {

    /**
     * Exposes the private 'operator' field from TardisInteriorManager.
     * This lets us get the TardisLevelOperator, which can then be used
     * to retrieve the interior's ServerWorld.
     */
    @Accessor("operator")
    TardisLevelOperator getOperator();
}
