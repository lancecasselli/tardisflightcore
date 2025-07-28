package com.doc.tardisflightcore.registry;

import com.doc.tardisflightcore.block.EngineCompartmentBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

public class ModBlockEntities {
    public static BlockEntityType<EngineCompartmentBlockEntity> ENGINE_COMPARTMENT;

    public static void register() {
        ENGINE_COMPARTMENT = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("tardisflightcore", "engine_compartment"),
            BlockEntityType.Builder.create(EngineCompartmentBlockEntity::new, ModBlocks.ENGINE_COMPARTMENT).build(null)
        );
    }
}