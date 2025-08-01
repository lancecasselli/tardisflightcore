package com.doc.tardisflightcore;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doc.tardisflightcore.registry.ModBlocks;
import com.doc.tardisflightcore.registry.ModBlockEntities;
import com.doc.tardisflightcore.registry.ModItems;
import com.doc.tardisflightcore.screen.ModScreenHandlers;
import com.doc.tardisflightcore.block.EngineCompartmentBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;
import whocraft.tardis_refined.api.event.TardisCommonEvents;
import whocraft.tardis_refined.common.capability.tardis.TardisLevelOperator;
import whocraft.tardis_refined.common.tardis.manager.TardisInteriorManager;
import com.doc.tardisflightcore.mixin.TardisInteriorManagerAccessor;

import java.util.Set;

public class TardisFlightCore implements ModInitializer {
    public static final String MOD_ID = "tardisflightcore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // -------------------------
        // Register all game content
        // -------------------------
        ModScreenHandlers.register();
        ModBlocks.register();
        ModItems.registerModItems(); // Also sets up creative tab
        ModBlockEntities.register();

        // -------------------------
        // Register required TARDIS flight components
        // -------------------------
        EngineCompartmentBlockEntity.registerPrimaryComponent(ModItems.DEMAT_CIRCUIT);
        LOGGER.info("Required flight components registered: {}", EngineCompartmentBlockEntity.getRequiredPrimaryComponents());

        // -------------------------
        // LAND event listener
        // -------------------------
        TardisCommonEvents.LAND.register((tardisLevelOperator, level, pos) -> {
            // 1️⃣ Get the TARDIS Interior Manager
            TardisInteriorManager interior = tardisLevelOperator.getInteriorManager();
            if (interior == null) {
                LOGGER.warn("No interior manager found!");
                return;
            }

            // 2️⃣ Get the interior's TardisLevelOperator via mixin accessor
            TardisLevelOperator interiorOperator = ((TardisInteriorManagerAccessor) interior).getOperator();
            if (interiorOperator == null) {
                LOGGER.warn("No interior operator found!");
                return;
            }

            // 3️⃣ Get the interior ServerWorld
            if (!(interiorOperator.getLevel() instanceof ServerWorld interiorWorld)) {
                LOGGER.warn("Interior world is not a ServerWorld!");
                return;
            }

            RegistryKey<World> interiorDim = interiorWorld.getRegistryKey();
            LOGGER.info("LAND event — Interior dimension: {}", interiorDim.getValue());

            // 4️⃣ Damage the Dematerialization Circuit in matching compartments
            Set<EngineCompartmentBlockEntity> compartments = EngineCompartmentBlockEntity.getLoadedCompartments();
            for (EngineCompartmentBlockEntity compartment : compartments) {
                World compWorld = compartment.getWorld();
                if (compWorld != null && compWorld.getRegistryKey().equals(interiorDim)) {
                    LOGGER.info("Damaging Dematerialization Circuit in compartment at {}", compartment.getPos());
                    compartment.damageDematerializationCircuit();
                }
            }
        });
    }
}
