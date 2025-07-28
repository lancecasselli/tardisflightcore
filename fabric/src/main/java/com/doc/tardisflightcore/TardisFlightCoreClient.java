package com.doc.tardisflightcore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import com.doc.tardisflightcore.screen.ModScreenHandlers;
import com.doc.tardisflightcore.screen.EngineCompartmentScreen;

public class TardisFlightCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(ModScreenHandlers.ENGINE_COMPARTMENT, EngineCompartmentScreen::new);
    }
}