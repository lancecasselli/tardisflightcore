package com.doc.tardisflightcore.registry;

import com.doc.tardisflightcore.block.EngineCompartmentBlock;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

public class ModBlocks {
    public static final Block ENGINE_COMPARTMENT = new EngineCompartmentBlock(
        net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.create().strength(4.0f)
    );

    public static void register() {
        Registry.register(Registries.BLOCK, new Identifier("tardisflightcore", "engine_compartment"), ENGINE_COMPARTMENT);
        Registry.register(Registries.ITEM, new Identifier("tardisflightcore", "engine_compartment"),
            new BlockItem(ENGINE_COMPARTMENT, new Item.Settings()));
    }
}