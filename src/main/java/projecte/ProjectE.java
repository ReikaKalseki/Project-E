package projecte;

import java.util.EnumMap;
import java.util.logging.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import projecte.api.emc.EmcRegistry;
import projecte.crafting.PhilosopherStoneCraftingHandler;
import projecte.event.BucketFillEvent;
import projecte.event.CraftingEvent;
import projecte.fluid.PEFluids;
import projecte.gui.GuiHandler;
import projecte.handlers.FurnaceFuelHandler;
import projecte.handlers.TooltipHandler;
import projecte.items.PEItems;
import projecte.packet.ChannelHandler;
import projecte.proxy.CommonProxy;
import projecte.util.CreativeTab;
import projecte.util.OredictUtil;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, useMetadata = false)
public class ProjectE {

	public static CreativeTabs tab = new CreativeTab(ModInfo.MOD_ID);

	@Instance(ModInfo.MOD_ID)
	public static ProjectE inst;

	@SidedProxy(clientSide = "projecte.proxy.ClientProxy", serverSide = "projecte.proxy.CommonProxy", modId = ModInfo.MOD_ID)
	public static CommonProxy proxy;

	public EnumMap<Side, FMLEmbeddedChannel> channels;
	
	public static Logger log = Logger.getLogger(ModInfo.MOD_NAME);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		/* Register vanilla oredict names */
		OredictUtil.registerVanillaOredict();

		/* Register items and blocks and fluids */
		proxy.registerFluids();
		proxy.registerBlocks();
		proxy.registerItems();
		proxy.registerRenders();
		/* Register default EMC values */
		EmcRegistry.registerDefault();

		/* Register channels */
		channels = NetworkRegistry.INSTANCE.newChannel(ModInfo.MOD_ID, new ChannelHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		/* Register events */
		FMLCommonHandler.instance().bus().register(new CraftingEvent());

		/* Register handlers */
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			MinecraftForge.EVENT_BUS.register(new TooltipHandler());
		MinecraftForge.EVENT_BUS.register(new BucketFillEvent());
		GameRegistry.registerFuelHandler(new FurnaceFuelHandler());
		GameRegistry.addRecipe(PhilosopherStoneCraftingHandler.inst);
		
		/* Register recipes */
		proxy.addRecipes();

		/* Register GUI handler */
		NetworkRegistry.INSTANCE.registerGuiHandler(inst, new GuiHandler());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		EmcRegistry.onReachPostInit();
	}

	@EventHandler
	public void onFinishLoading(FMLLoadCompleteEvent event) {
		// FIXME generate recipes
	}

}
