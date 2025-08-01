package com.doc.tardisflightcore.block;

import com.doc.tardisflightcore.registry.ModBlockEntities;
import com.doc.tardisflightcore.registry.ModItems;
import com.doc.tardisflightcore.screen.EngineCompartmentScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import whocraft.tardis_refined.registry.TRItemRegistry;

import java.util.*;

public class EngineCompartmentBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Inventory {

    public static final int COMPONENT_SLOT_COUNT = 4;
    private final DefaultedList<ItemStack> components = DefaultedList.ofSize(COMPONENT_SLOT_COUNT, ItemStack.EMPTY);
    private static final Set<EngineCompartmentBlockEntity> LOADED_COMPARTMENTS = Collections.synchronizedSet(new HashSet<>());
    private static final Set<Item> REQUIRED_PRIMARY_COMPONENTS = Collections.synchronizedSet(new HashSet<>());

    private Item pendingRepairTool = null;
    private Item pendingRepairMaterial = null;
    private long repairDeadline = 0;

    private static final List<Item> POSSIBLE_REPAIR_MATERIALS = List.of(
            Items.IRON_INGOT,
            Items.REDSTONE,
            Items.COPPER_INGOT,
            Items.AMETHYST_SHARD
    );

    public EngineCompartmentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENGINE_COMPARTMENT, pos, state);
    }

    public static void registerPrimaryComponent(Item item) {
        REQUIRED_PRIMARY_COMPONENTS.add(item);
    }

    public static void registerPrimaryComponents(Collection<Item> items) {
        REQUIRED_PRIMARY_COMPONENTS.addAll(items);
    }

    public static Set<Item> getRequiredPrimaryComponents() {
        return Collections.unmodifiableSet(REQUIRED_PRIMARY_COMPONENTS);
    }

    private boolean hasAllPrimaryComponents() {
        if (REQUIRED_PRIMARY_COMPONENTS.isEmpty()) return true;
        for (Item required : REQUIRED_PRIMARY_COMPONENTS) {
            boolean found = components.stream().anyMatch(stack -> !stack.isEmpty() && stack.isOf(required));
            if (!found) return false;
        }
        return true;
    }

    public static Set<EngineCompartmentBlockEntity> getLoadedCompartments() {
        return Collections.unmodifiableSet(LOADED_COMPARTMENTS);
    }

    public static boolean hasAnyTardisAllRequiredComponents() {
        synchronized (LOADED_COMPARTMENTS) {
            return LOADED_COMPARTMENTS.stream().anyMatch(EngineCompartmentBlockEntity::hasAllPrimaryComponents);
        }
    }

    public static boolean isFlightAllowed(RegistryKey<World> tardisDim) {
        synchronized (LOADED_COMPARTMENTS) {
            return LOADED_COMPARTMENTS.stream()
                    .filter(comp -> comp.getWorld() != null && comp.getWorld().getRegistryKey().equals(tardisDim))
                    .anyMatch(EngineCompartmentBlockEntity::hasAllPrimaryComponents);
        }
    }

    public boolean hasActiveRepairChallenge() {
        return pendingRepairTool != null && pendingRepairMaterial != null;
    }

    public void triggerRepairChallengeFromPlayer(PlayerEntity player) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        pendingRepairTool = TRItemRegistry.SCREWDRIVER.get();
        pendingRepairMaterial = POSSIBLE_REPAIR_MATERIALS.get(serverWorld.getRandom().nextInt(POSSIBLE_REPAIR_MATERIALS.size()));
        repairDeadline = serverWorld.getTime() + (20 * 10);
        String toolName = Registries.ITEM.getId(pendingRepairTool).getPath();
        String materialName = Registries.ITEM.getId(pendingRepairMaterial).getPath();
        player.sendMessage(Text.literal("MANUAL REPAIR: Use " + toolName + " + " + materialName + " within 10s!"), true);
    }

    /** Damages the Dematerialization Circuit and removes it if broken */
    public void damageDematerializationCircuit() {
        for (int i = 0; i < components.size(); i++) {
            ItemStack stack = components.get(i);
            if (!stack.isEmpty() && stack.isOf(ModItems.DEMAT_CIRCUIT)) {
                int oldDamage = stack.getDamage();
                int maxDamage = stack.getMaxDamage();

                stack.setDamage(oldDamage + 1);
                System.out.println("[TARDIS Flight Core] Damaged Dematerialization Circuit: " + (oldDamage + 1) + "/" + maxDamage);

                if (stack.getDamage() >= maxDamage) {
                    components.set(i, ItemStack.EMPTY);
                    if (world != null && !world.isClient) {
                        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                    System.out.println("[TARDIS Flight Core] Dematerialization Circuit broke and was removed!");
                }

                markDirty();
                break;
            }
        }
    }

    /** Checks all slots for broken circuits and removes them */
    private void checkAndBreakDeadCircuits() {
        for (int i = 0; i < components.size(); i++) {
            ItemStack stack = components.get(i);
            if (!stack.isEmpty() && stack.isOf(ModItems.DEMAT_CIRCUIT)) {
                if (stack.getDamage() >= stack.getMaxDamage()) {
                    components.set(i, ItemStack.EMPTY);
                    if (world != null && !world.isClient) {
                        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                    System.out.println("[TARDIS Flight Core] Dematerialization Circuit reached 0 health and was removed!");
                    markDirty();
                    if (world != null) {
                        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
                    }
                }
            }
        }
    }

    public void triggerRepairChallenge() {
        if (!(world instanceof ServerWorld serverWorld)) return;
        List<Item> tools = List.of(TRItemRegistry.MALLET.get(), TRItemRegistry.SCREWDRIVER.get());
        net.minecraft.util.math.random.Random random = serverWorld.getRandom();
        pendingRepairTool = tools.get(random.nextInt(tools.size()));
        pendingRepairMaterial = POSSIBLE_REPAIR_MATERIALS.get(random.nextInt(POSSIBLE_REPAIR_MATERIALS.size()));
        repairDeadline = serverWorld.getTime() + (20 * 10);
        String toolName = Registries.ITEM.getId(pendingRepairTool).getPath();
        String materialName = Registries.ITEM.getId(pendingRepairMaterial).getPath();
        serverWorld.getPlayers(p -> p.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < 100)
                .forEach(p -> p.sendMessage(Text.literal("REPAIR REQUIRED: Use " + toolName + " + " + materialName + " within 10s!"), true));
    }

    public void tryRepair(PlayerEntity player) {
        if (pendingRepairTool == null || pendingRepairMaterial == null) return;
        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        boolean correctTool = main.isOf(pendingRepairTool) || off.isOf(pendingRepairTool);
        boolean correctMaterial = main.isOf(pendingRepairMaterial) || off.isOf(pendingRepairMaterial);
        if (correctTool && correctMaterial) {
            if (main.isOf(pendingRepairMaterial)) {
                main.decrement(1);
            } else if (off.isOf(pendingRepairMaterial)) {
                off.decrement(1);
            }
            for (ItemStack stack : components) {
                if (!stack.isEmpty() && stack.isOf(ModItems.DEMAT_CIRCUIT)) {
                    stack.setDamage(Math.max(0, stack.getDamage() - 10));
                    markDirty();
                    if (world != null) {
                        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
                    }
                    break;
                }
            }
            player.sendMessage(Text.literal("Circuit successfully repaired!"), true);
        } else {
            for (ItemStack stack : components) {
                if (!stack.isEmpty() && stack.isOf(ModItems.DEMAT_CIRCUIT)) {
                    stack.setDamage(stack.getDamage() + 2);
                    markDirty();
                    if (world != null) {
                        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
                    }
                    break;
                }
            }
            player.sendMessage(Text.literal("Incorrect repair! The circuit is now more damaged."), true);
        }
        resetRepairState();
    }

    private void resetRepairState() {
        pendingRepairTool = null;
        pendingRepairMaterial = null;
        repairDeadline = 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, EngineCompartmentBlockEntity be) {
        if (!world.isClient) {
            // Repair time expired
            if (be.repairDeadline > 0 && world.getTime() > be.repairDeadline) {
                be.resetRepairState();
                be.damageDematerializationCircuit();
            }
            // Always check for broken circuits
            be.checkAndBreakDeadCircuits();
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world != null && !world.isClient) {
            LOADED_COMPARTMENTS.add(this);
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        LOADED_COMPARTMENTS.remove(this);
    }

    @Override
    public int size() {
        return components.size();
    }

    @Override
    public boolean isEmpty() {
        return components.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return components.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(components, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(components, slot);
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

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, components);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, components);
    }

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
