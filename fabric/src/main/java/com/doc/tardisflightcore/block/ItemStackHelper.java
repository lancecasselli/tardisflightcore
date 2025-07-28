package com.doc.tardisflightcore.block;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class ItemStackHelper {
    public static void writeNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); i++) {
            NbtCompound stackNbt = new NbtCompound();
            stacks.get(i).writeNbt(stackNbt);
            nbt.put("Slot" + i, stackNbt);
        }
    }

    public static void readNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); i++) {
            if (nbt.contains("Slot" + i)) {
                stacks.set(i, ItemStack.fromNbt(nbt.getCompound("Slot" + i)));
            } else {
                stacks.set(i, ItemStack.EMPTY);
            }
        }
    }

    public static ItemStack splitStack(DefaultedList<ItemStack> stacks, int slot, int amount) {
        ItemStack stack = stacks.get(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (stack.getCount() <= amount) {
            stacks.set(slot, ItemStack.EMPTY);
            return stack;
        } else {
            ItemStack result = stack.split(amount);
            if (stack.isEmpty()) stacks.set(slot, ItemStack.EMPTY);
            return result;
        }
    }

    public static ItemStack removeStack(DefaultedList<ItemStack> stacks, int slot) {
        ItemStack stack = stacks.get(slot);
        stacks.set(slot, ItemStack.EMPTY);
        return stack;
    }
}
