package com.mervyn.emicompat;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EmiEntrypoint
public class EmiCompatPlugin implements EmiPlugin {
	public static final String MOD_ID = "emicompat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void register(EmiRegistry registry) {
		LOGGER.info("Initializing EMI Compat multi-mod plugin");

		if (FabricLoader.getInstance().isModLoaded("ad_astra")) {
			LOGGER.info("Loading Ad Astra EMI compatibility...");
			new AdAstraEmiCompat().register(registry);
		}
		if (FabricLoader.getInstance().isModLoaded("spell_engine")) {
			LOGGER.info("Loading Spell Engine EMI compatibility...");
			new SpellEngineEmiCompat().register(registry);
		}

		if (FabricLoader.getInstance().isModLoaded("chipped")) {
			LOGGER.info("Loading Chipped EMI compatibility...");
			new ChippedEmiCompat().register(registry);
		}
		if (FabricLoader.getInstance().isModLoaded("things")) {
			LOGGER.info("Loading Things EMI compatibility...");
			new ThingsEmiCompat().register(registry);
		}
		if (FabricLoader.getInstance().isModLoaded("enchanted")) {
			LOGGER.info("Loading Enchanted EMI compatibility...");
			new EnchantedEmiCompat().register(registry);
		}
	}
}
