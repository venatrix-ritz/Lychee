package snownee.lychee.recipes;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.lychee.context.ActionContext;
import snownee.lychee.context.LootParamsContext;
import snownee.lychee.util.LycheeEntity;
import snownee.lychee.util.LycheeEntityType;
import snownee.lychee.util.context.LycheeContext;
import snownee.lychee.util.context.LycheeContextKey;
import snownee.lychee.util.recipe.LycheeRecipeType;


public class EntityTickingRecipeType extends LycheeRecipeType<EntityTickingRecipe> {
	private static final Set<LycheeContextKey<?>> PRESERVED_KEYS = Set.of(LycheeContextKey.LEVEL, LycheeContextKey.LOOT_PARAMS);

	public EntityTickingRecipeType(String name, Class<EntityTickingRecipe> clazz, @Nullable ContextKeySet contextParamSet) {
		super(name, clazz, contextParamSet);
	}

	@Override
	public void refreshCache(RecipeMap recipeMap) {
		super.refreshCache(recipeMap);
		ListMultimap<EntityType<?>, RecipeHolder<EntityTickingRecipe>> map = ArrayListMultimap.create();
		for (RecipeHolder<EntityTickingRecipe> recipe : recipes) {
			try {
				com.google.gson.JsonElement json = net.minecraft.advancements.predicates.entity.EntityPredicate.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, recipe.value().predicate()).getOrThrow();
				if (json.isJsonObject() && json.getAsJsonObject().has("type")) {
					com.google.gson.JsonElement typeJson = json.getAsJsonObject().get("type");
					if (typeJson.isJsonPrimitive()) {
						net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.parse(typeJson.getAsString().replace("#", ""));
						if (typeJson.getAsString().startsWith("#")) {
						    net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, id))
						        .ifPresent(tag -> tag.forEach(holder -> map.put(holder.value(), recipe)));
						} else {
						    net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getOptional(id).ifPresent(type -> map.put(type, recipe));
						}
					}
				}
			} catch (Exception e) {}
		}
		for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
			((LycheeEntityType) type).lychee$setTickingRecipes(ImmutableList.copyOf(map.get(type)));
		}
	}

	public void process(Entity entity, List<RecipeHolder<EntityTickingRecipe>> recipes) {
		if (recipes.isEmpty() || entity.level().isClientSide()) {
			return;
		}
		ServerLevel level = (ServerLevel) entity.level();
		LycheeEntity lycheeEntity = (LycheeEntity) entity;
		LycheeContext context = lycheeEntity.lychee$getContext();
		LootParamsContext lootParams;
		if (context == null) {
			context = new LycheeContext();
			context.put(LycheeContextKey.LEVEL, level);
			lycheeEntity.lychee$setContext(context);
			lootParams = context.initLootParams(this);
		} else {
			lootParams = context.get(LycheeContextKey.LOOT_PARAMS);
		}
		lootParams.set(LootContextParams.THIS_ENTITY, entity);
		lootParams.set(LootContextParams.ORIGIN, entity.position());
		lootParams.validate();
		for (RecipeHolder<EntityTickingRecipe> recipeHolder : recipes) {
			EntityTickingRecipe recipe = recipeHolder.value();
			if (recipe.interval() > 1 && (entity.tickCount + 1) % recipe.interval() != 0) {
				continue;
			}
			if (recipe.predicate().matches(level, entity.position(), entity) && recipe.test(context) && recipe.matches(
					context,
					level)) {
				context.put(recipeHolder);
				ActionContext actionContext = recipe.applyPostActions(context, 1);
				boolean avoidDefault = actionContext != null && actionContext.avoidDefault;
				context.removeAllExcept(PRESERVED_KEYS);
				if (avoidDefault) {
					break;
				}
			}
		}
	}
}
