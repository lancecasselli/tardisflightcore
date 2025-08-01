package com.doc.tardisflightcore.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import whocraft.tardis_refined.registry.TRItemRegistry;

public class EngineCompartmentBlock extends BlockWithEntity {

    public EngineCompartmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EngineCompartmentBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                               PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof EngineCompartmentBlockEntity be) {
                ItemStack heldItem = player.getStackInHand(hand);

                // Check if holding screwdriver or mallet
                boolean isRepairTool = heldItem.isOf(TRItemRegistry.SCREWDRIVER.get()) ||
                                       heldItem.isOf(TRItemRegistry.MALLET.get());

                if (isRepairTool) {
                    if (!be.hasActiveRepairChallenge()) {
                        // Start minigame
                        be.triggerRepairChallengeFromPlayer(player);
                    } else {
                        // Attempt repair
                        be.tryRepair(player);
                    }
                    return ActionResult.SUCCESS;
                }

                // If a repair challenge is active, block GUI access
                if (be.hasActiveRepairChallenge()) {
                    player.sendMessage(Text.literal("Repair in progress! Complete it before accessing the compartment."), true);
                    return ActionResult.SUCCESS;
                }

                // Otherwise open the GUI
                if (blockEntity instanceof ExtendedScreenHandlerFactory screenHandlerFactory) {
                    player.openHandledScreen(screenHandlerFactory);
                } else {
                    System.err.println("[TardisFlightCore] ❌ EngineCompartmentBlockEntity is missing or invalid at " + pos);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos,
                                BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EngineCompartmentBlockEntity be) {
                be.clear(); // Drop inventory if needed
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public <T extends BlockEntity> net.minecraft.block.entity.BlockEntityTicker<T> getTicker(
            World world, BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type,
                com.doc.tardisflightcore.registry.ModBlockEntities.ENGINE_COMPARTMENT,
                EngineCompartmentBlockEntity::tick);
    }
}
