package com.doc.tardisflightcore.registry;

import com.doc.tardisflightcore.TardisFlightCore;
import com.doc.tardisflightcore.item.DematCircuitItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {

    // === Items ===
    public static final Item DEMAT_CIRCUIT = register("demat_circuit",
        new DematCircuitItem(new Item.Settings()
            .maxDamage(100) // Durability
        ));

    // === Creative Tab ===
    public static final ItemGroup TARDIS_GROUP = Registry.register(
        Registries.ITEM_GROUP,
        new Identifier(TardisFlightCore.MOD_ID, "tardis_tab"),
        FabricItemGroup.builder()
            .displayName(Text.translatable("itemgroup.tardis_tab"))
            .icon(() -> new ItemStack(DEMAT_CIRCUIT))
            .entries((displayContext, entries) -> {
                // Add mod items here
                entries.add(DEMAT_CIRCUIT);
                entries.add(ModBlocks.ENGINE_COMPARTMENT); // Requires ModBlocks
            })
            .build()
    );

    // === Registration Methods ===
    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(TardisFlightCore.MOD_ID, name), item);
    }

    public static void registerModItems() {
        // This ensures static init runs
        TardisFlightCore.LOGGER.info("Registering Mod Items for " + TardisFlightCore.MOD_ID);
    }
}
