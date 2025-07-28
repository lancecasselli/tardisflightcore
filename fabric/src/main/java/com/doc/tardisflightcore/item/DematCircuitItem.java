package com.doc.tardisflightcore.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DematCircuitItem extends Item {

    public DematCircuitItem(Settings settings) {
        super(settings.maxDamage(64)); // 👈 durability + only one per stack
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false; // Optional: prevents anvil repair
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.tardisflightcore.demat_circuit.tooltip").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Durability: " + (stack.getMaxDamage() - stack.getDamage()) + " / " + stack.getMaxDamage())
                .formatted(Formatting.DARK_GREEN));
    }
}
