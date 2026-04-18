package com.mervyn.emicompat;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.spell_engine.api.item.trinket.SpellBookItem;
import net.spell_engine.api.item.trinket.SpellBooks;
import net.spell_engine.api.spell.SpellContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EmiEntrypoint
public class SpellEngineEmiCompat implements EmiPlugin {
	public static final String MOD_ID = "emicompat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceLocation SPELL_BINDING_ID = new ResourceLocation("emicompat", "spell_binding");
	public static final EmiRecipeCategory SPELL_BINDING_CATEGORY = new EmiRecipeCategory(SPELL_BINDING_ID,
			EmiStack.of(BuiltInRegistries.ITEM.get(new ResourceLocation("spell_engine", "spell_binding"))));

	@Override
	public void register(EmiRegistry registry) {
		LOGGER.info("Registering Spell Engine x EMI Compatibility");

		registry.addCategory(SPELL_BINDING_CATEGORY);
		registry.addWorkstation(SPELL_BINDING_CATEGORY, EmiStack.of(BuiltInRegistries.ITEM.get(new ResourceLocation("spell_engine", "spell_binding"))));

		// Register Book to Spellbook recipes
		for (SpellBookItem book : SpellBooks.sorted()) {
			if (book instanceof Item item) {
				registry.addRecipe(new SpellBookBindingEmiRecipe(item));
			}
		}

		// Iterate through all items to find those with spell pools
		BuiltInRegistries.ITEM.forEach(item -> {
			SpellContainer container = net.spell_engine.internals.SpellRegistry.containerForItem(BuiltInRegistries.ITEM.getKey(item));
			if (container != null && container.pool != null && !container.pool.isEmpty()) {
				net.spell_engine.api.spell.SpellPool pool = net.spell_engine.internals.SpellRegistry.spellPool(new ResourceLocation(container.pool));
				if (pool != null) {
					for (ResourceLocation spellId : pool.spellIds()) {
						registry.addRecipe(new SpellBindingEmiRecipe(item, spellId.toString()));
					}
				}
			}
		});
	}
}
