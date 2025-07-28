package com.doc.tardisflightcore.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

public class ModScreenHandlers {
    public static ExtendedScreenHandlerType<EngineCompartmentScreenHandler> ENGINE_COMPARTMENT;

    public static void register() {
        ENGINE_COMPARTMENT = Registry.register(
            Registries.SCREEN_HANDLER,
            new Identifier("tardisflightcore", "engine_compartment"),
            new ExtendedScreenHandlerType<>(EngineCompartmentScreenHandler::new)
        );
    }
}