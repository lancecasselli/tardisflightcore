package com.doc.tardisflightcore.registry;

import com.doc.tardisflightcore.TardisFlightCore;
import com.doc.tardisflightcore.item.DematCircuitItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item DEMAT_CIRCUIT = register("demat_circuit",
        new DematCircuitItem(new Item.Settings()
            .maxDamage(100) // 🔧 Set durability
        ));

    private static Item register(String name, Item item) {
        // ✅ FIXED: use Registries.ITEM instead of Registry.ITEM
        return Registry.register(Registries.ITEM, new Identifier(TardisFlightCore.MOD_ID, name), item);
    }

    public static void registerModItems() {
        // Called during mod init to ensure items are loaded
    }
}
