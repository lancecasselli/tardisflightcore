package com.doc.tardisflightcore.screen;

import com.doc.tardisflightcore.block.EngineCompartmentBlockEntity;
import com.doc.tardisflightcore.registry.ModItemTags; // You need to define this
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EngineCompartmentScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public EngineCompartmentScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.ENGINE_COMPARTMENT, syncId);
        this.inventory = inventory;

        // ✅ Component slot that accepts only items in the tardisflightcore:components tag
        this.addSlot(new Slot(inventory, 0, 80, 18) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(ModItemTags.COMPONENTS);
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return true;
            }
        });

        // 🔲 Player inventory (3 rows)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }

        // 🔲 Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 109));
        }
    }

    public EngineCompartmentScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, getInventory(playerInventory, buf.readBlockPos()));
    }

    private static Inventory getInventory(PlayerInventory playerInventory, BlockPos pos) {
        World world = playerInventory.player.getWorld();
        if (world != null) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof Inventory inv) {
                return inv;
            }
        }
        return new SimpleInventory(EngineCompartmentBlockEntity.COMPONENT_SLOT_COUNT);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            int blockInventorySize = EngineCompartmentBlockEntity.COMPONENT_SLOT_COUNT;
            int playerInventoryStart = blockInventorySize;
            int playerInventoryEnd = playerInventoryStart + 36;

            if (index < blockInventorySize) {
                if (!this.insertItem(originalStack, playerInventoryStart, playerInventoryEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(originalStack, 0, blockInventorySize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }
}
