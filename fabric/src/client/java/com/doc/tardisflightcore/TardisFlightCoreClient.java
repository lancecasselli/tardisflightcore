package com.doc.tardisflightcore;

import com.doc.tardisflightcore.screen.EngineCoreScreen;
import com.doc.tardisflightcore.registry.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class TardisFlightCoreClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register screen handler <-> screen binding
        HandledScreens.register(ModScreenHandlers.ENGINE_CORE, EngineCoreScreen::new);

        System.out.println("✅ TardisFlightCoreClient initialized: EngineCoreScreen registered.");
    }
}
