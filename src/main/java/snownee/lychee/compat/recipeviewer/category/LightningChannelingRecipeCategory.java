package snownee.lychee.compat.recipeviewer.category;

import net.minecraft.world.entity.EntityTypes;
import java.util.Objects;
import net.minecraft.world.entity.EntityTypes;
import java.util.function.Function;
import net.minecraft.world.entity.EntityTypes;
import java.util.function.Supplier;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.client.renderer.entity.state.LightningBoltRenderState;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityTypes;
import snownee.lychee.RecipeTypes;
import net.minecraft.world.entity.EntityTypes;
import snownee.lychee.client.gui.GuiGameElement;
import net.minecraft.world.entity.EntityTypes;
import snownee.lychee.client.gui.RenderElement;
import net.minecraft.world.entity.EntityTypes;
import snownee.lychee.compat.recipeviewer.element.ShadowElement;
import net.minecraft.world.entity.EntityTypes;
import snownee.lychee.recipes.LightningChannelingRecipe;

public class LightningChannelingRecipeCategory extends ItemShapelessRecipeCategory<LightningChannelingRecipe> {
	public static final int BLOCK_SIZE = 15;
	private final LightningBoltRenderState lightningBolt = new LightningBoltRenderState();
	private final ShadowElement shadowElement = new ShadowElement(BLOCK_SIZE, 11, 4);

	public LightningChannelingRecipeCategory() {
		super(RecipeTypes.LIGHTNING_CHANNELING);
		lightningBolt.entityType = EntityTypes.LIGHTNING_BOLT;
	}

	@Override
	public void setupDecorations(DecorationMapBuilder<LightningChannelingRecipe> mapBuilder) {
		super.setupDecorations(mapBuilder);
		mapBuilder.condition("icon", _ -> false);

		mapBuilder.put(
				"lightning_bolt", (builder, recipeHolder) -> {
					int width = builder.width();
					int height = builder.height();
					builder.addElement(RenderElement.create(
							GuiGameElement.of(lightningBolt)
									.scale(7)
									.atLocal(0, 1.5, 1)
									.withSize(width, height),
							$ -> {
								long time = Objects.requireNonNull(Minecraft.getInstance().level).getGameTime();
								if (time % 3 == 0) {
									lightningBolt.seed = time;
								}
								time = time % 80;
								$.visible = time <= 7;
							}
					));
				});

		mapBuilder.put(
				"lightning_rod", (builder, recipeHolder) -> {
					Supplier<BlockState> blockStateSupplier = () -> {
						int time = (int) lightningBolt.seed % 80;
						BlockState blockState = Blocks.LIGHTNING_ROD.weathering().unaffected().defaultBlockState();
						if (time <= 7) {
							blockState = blockState.setValue(LightningRodBlock.POWERED, true);
						}
						return blockState;
					};
					Function<BlockState, RenderElement> blockElement = blockState -> GuiGameElement.of(blockState)
							.scale(BLOCK_SIZE)
							.rotateBlock(20, 225, 0);
					builder.addElement(shadowElement
							.blockWithShadow(blockStateSupplier, blockElement)
							.at((float) builder.width() / 2 - 7.5F, 37));
				});
	}
}
