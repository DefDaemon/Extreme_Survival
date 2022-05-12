package com.defdaemon.extreme_survival.block.custom;

import com.defdaemon.extreme_survival.Extreme_Survival;
import com.defdaemon.extreme_survival.block.entity.ModBlockEntities;
import com.defdaemon.extreme_survival.block.entity.custom.AlloyMixerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AlloyMixerBlock extends BaseEntityBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public AlloyMixerBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(LIT, Boolean.valueOf(false)));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit)
    {
        if (!pLevel.isClientSide())
        {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof AlloyMixerBlockEntity) {
                NetworkHooks.openGui(((ServerPlayer)pPlayer), (AlloyMixerBlockEntity)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext)
    {
        return this.defaultBlockState().setValue(POWERED, Boolean.valueOf(pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()))).setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(LIT, Boolean.valueOf(false));
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation)
    {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror)
    {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder)
    {
        pBuilder.add(FACING, POWERED, LIT);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState)
    {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving)
    {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof AlloyMixerBlockEntity) {
                ((AlloyMixerBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new AlloyMixerBlockEntity(pPos, pState);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving)
    {
        ShowCurrentState("Before NeighborChanged:", pLevel, pState, pPos);
        if (!pLevel.isClientSide) {
            boolean flag = pState.getValue(POWERED);
            if (flag != pLevel.hasNeighborSignal(pPos)) {
                if (flag) {
                    Extreme_Survival.LOGGER.info("Flag != hasNeighborSignal and Flag = true");
                    pLevel.scheduleTick(pPos, this, 4);
                } else {
                    Extreme_Survival.LOGGER.info("Flag != hasNeighborSignal and Flag = false");
                    pLevel.setBlock(pPos, pState.cycle(POWERED), 2);
                }
            }
            else
            {
                Extreme_Survival.LOGGER.info("Flag equals pState");
            }
        }
        else

        {
            Extreme_Survival.LOGGER.info("Clientside!!!");
        }
        ShowCurrentState("After NeighborChanged:", pLevel, pState, pPos);
    }

    private void ShowCurrentState(String title, Level pLevel, BlockState pState, BlockPos pPos)
    {
        Extreme_Survival.LOGGER.info(title);
        Extreme_Survival.LOGGER.info("======================================");
        Extreme_Survival.LOGGER.info("hasSignal: " + pLevel.hasNeighborSignal(pPos));
        Extreme_Survival.LOGGER.info("POWERED  : " + pState.getValue(POWERED));
        Extreme_Survival.LOGGER.info("LIT      : " + pState.getValue(LIT));
        Extreme_Survival.LOGGER.info("--------------------------------------");
        if (!pState.getValue(POWERED)) {
            pLevel.setBlock(pPos, pState.setValue(LIT, Boolean.valueOf(false)), 2);
        }
        Extreme_Survival.LOGGER.info("hasSignal: " + pLevel.hasNeighborSignal(pPos));
        Extreme_Survival.LOGGER.info("POWERED  : " + pState.getValue(POWERED));
        Extreme_Survival.LOGGER.info("LIT      : " + pState.getValue(LIT));
        Extreme_Survival.LOGGER.info("======================================");
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand)
    {
        if (pState.getValue(POWERED) && !pLevel.hasNeighborSignal(pPos)) {
            pLevel.setBlock(pPos, pState.cycle(POWERED), 2);
        }
    }
}