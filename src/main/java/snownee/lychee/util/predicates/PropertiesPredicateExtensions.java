package snownee.lychee.util.predicates;

import java.util.Optional;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;

public class PropertiesPredicateExtensions {
	public static Optional<Object> findMatcher(StatePropertiesPredicate predicate, String name) {
		try {
			for (Object matcher : predicate.properties()) {
				String matcherName = (String) matcher.getClass().getMethod("name").invoke(matcher);
				if (matcherName.equals(name)) {
					return Optional.of(matcher);
				}
			}
		} catch (Exception e) {}
		return Optional.empty();
	}
	
	public static boolean match(Object matcher, net.minecraft.world.level.block.state.StateDefinition<?, ?> definition, net.minecraft.world.level.block.state.BlockState state) {
	    try {
	        return (Boolean) matcher.getClass().getMethod("match", net.minecraft.world.level.block.state.StateDefinition.class, net.minecraft.world.level.block.state.StateHolder.class).invoke(matcher, definition, state);
	    } catch (Exception e) {
			return false;
		}
	}
}
