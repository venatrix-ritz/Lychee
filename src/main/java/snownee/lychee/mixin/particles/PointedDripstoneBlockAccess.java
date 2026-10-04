package snownee.lychee.mixin.particles;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PointedDripstoneBlock.class)
public interface PointedDripstoneBlockAccess {

	@Invoker
	static boolean callCanDripThrough(BlockGetter level, BlockPos blockPos, BlockState blockState) {
		throw new IllegalStateException();
	}

	@Invoker
	static Optional<BlockPos> callFindRootBlock(Level level, BlockPos pos, BlockState state, int maxIterations) {
		throw new IllegalStateException();
	}
}
