package snownee.lychee.mixin.particles;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Since 26.2 these helpers live on {@link SpeleothemBlock}, the superclass of PointedDripstoneBlock.
 * Mixin invokers only see methods declared on their target class, so they need their own interface.
 */
@Mixin(SpeleothemBlock.class)
public interface SpeleothemBlockAccess {

	@Invoker
	static boolean callIsStalactiteStartPos(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
		throw new IllegalStateException();
	}

	@Invoker
	static BlockPos callFindTip(BlockState state, LevelAccessor level, BlockPos pos, int maxIterations, boolean isTipMerge) {
		throw new IllegalStateException();
	}

	@Invoker
	static Optional<BlockPos> callFindBlockVertical(
			LevelAccessor level,
			BlockPos blockPos2,
			AxisDirection axisDirection,
			BiPredicate<BlockPos, BlockState> biPredicate,
			Predicate<BlockState> predicate,
			int i
	) {
		throw new IllegalStateException();
	}
}
