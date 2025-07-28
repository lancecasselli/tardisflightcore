package com.doc.tardisflightcore.block;

import com.doc.tardisflightcore.block.ItemStackHelper;
import com.doc.tardisflightcore.flight.FlightDanceManager;
import com.doc.tardisflightcore.registry.ModBlockEntities;
import com.doc.tardisflightcore.screen.EngineCompartmentScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class EngineCompartmentBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Inventory {
    public static final int COMPONENT_SLOT_COUNT = 4;
    private final DefaultedList<ItemStack> components = DefaultedList.ofSize(COMPONENT_SLOT_COUNT, ItemStack.EMPTY);

    public EngineCompartmentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENGINE_COMPARTMENT, pos, state);
    }

    // ✅ Check if the TARDIS (by UUID) is flying using your custom manager
    public void checkIfFlying(ServerPlayerEntity player, UUID tardisId) {
        if (FlightDanceManager.isFlying(tardisId)) {
            player.sendMessage(Text.literal("🚀 The TARDIS is currently in flight!"), false);
        } else {
            player.sendMessage(Text.literal("🛑 The TARDIS is stationary."), false);
        }
    }

    // -------------------------
    // Inventory Implementation
    // -------------------------

    @Override
    public int size() {
        return components.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : components) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return components.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = ItemStackHelper.splitStack(components, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = ItemStackHelper.removeStack(components, slot);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        components.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public void clear() {
        components.clear();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    // -------------------------
    // NBT Serialization
    // -------------------------

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        ItemStackHelper.writeNbt(nbt, components);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        ItemStackHelper.readNbt(nbt, components);
    }

    // -------------------------
    // UI / Menu
    // -------------------------

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.getPos());
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.tardisflightcore.engine_compartment");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EngineCompartmentScreenHandler(syncId, inv, this);
    }
}
