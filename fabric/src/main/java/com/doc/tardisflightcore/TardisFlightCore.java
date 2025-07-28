package com.doc.tardisflightcore;

import net.fabricmc.api.ModInitializer;
import com.doc.tardisflightcore.registry.ModBlocks;
import com.doc.tardisflightcore.registry.ModBlockEntities;
import com.doc.tardisflightcore.screen.ModScreenHandlers;
import com.doc.tardisflightcore.registry.ModItems;

// New imports for event hook registration
import com.doc.tardisflightcore.integration.TakeOffHandler;
import com.doc.tardisflightcore.integration.LandHandler;

public class TardisFlightCore implements ModInitializer {
    public static final String MOD_ID = "tardisflightcore";

    @Override
    public void onInitialize() {
        ModScreenHandlers.register();
        ModBlocks.register();
        ModItems.registerModItems();
        ModBlockEntities.register();

        // 🔌 Register TARDIS: Refined hooks
        TardisAPIs.EVENTS.registerTakeOff(new TakeOffHandler());
        TardisAPIs.EVENTS.registerLand(new LandHandler());
    }
}
