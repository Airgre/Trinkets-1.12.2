package xzeroair.trinkets.util.eventhandlers;

import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import xzeroair.trinkets.attributes.RaceAttribute.RaceAttribute;
import xzeroair.trinkets.capabilities.sizeCap.ISizeCap;
import xzeroair.trinkets.capabilities.sizeCap.SizeCapPro;
import xzeroair.trinkets.network.BlocklistSyncPacket;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.PacketConfigSync;
import xzeroair.trinkets.util.TrinketsConfig;

public class OnWorldJoinHandler {

	@SubscribeEvent
	public void attachAttributes(EntityEvent.EntityConstructing event)
	{
		if(event.getEntity() instanceof EntityPlayer)
		{
			final EntityPlayer player = (EntityPlayer) event.getEntity();
			final AbstractAttributeMap map = player.getAttributeMap();

			map.registerAttribute(RaceAttribute.ENTITY_RACE);
		}
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		if((event.player.world != null)) {
			final EntityPlayer player = event.player;
			final boolean client = player.world.isRemote;
			//config Sync
			if(!client) {
				NetworkHandler.INSTANCE.sendTo(new PacketConfigSync(player,
						false,
						TrinketsConfig.SERVER.FAIRY_RING.creative_flight,
						TrinketsConfig.SERVER.DRAGON_EYE.oreFinder,
						TrinketsConfig.SERVER.GUI.guiEnabled,
						TrinketsConfig.SERVER.FAIRY_RING.creative_flight_speed,
						TrinketsConfig.SERVER.FAIRY_RING.flight_speed,
						TrinketsConfig.SERVER.GUI.guiEnabled,
						TrinketsConfig.SERVER.GUI.guiSlotsRows,
						TrinketsConfig.SERVER.GUI.guiSlotsRowLength,
						TrinketsConfig.compat.artemislib,
						TrinketsConfig.compat.baubles,
						TrinketsConfig.compat.enhancedvisuals,
						TrinketsConfig.compat.morph,
						TrinketsConfig.compat.toughasnails,
						TrinketsConfig.compat.betterdiving
						) ,(EntityPlayerMP) player);

				final String[] configArray = TrinketsConfig.getBlockListArray(false);
				String combinedArray = "";
				for(int i = configArray.length-1;i>=0;--i) {
					combinedArray = configArray[i] + ", " + combinedArray;
				}
				if(!combinedArray.isEmpty()) {
					final int hd = TrinketsConfig.SERVER.DRAGON_EYE.BLOCKS.DR.C001_HD;
					final int vd = TrinketsConfig.SERVER.DRAGON_EYE.BLOCKS.DR.C00_VD;
					NetworkHandler.INSTANCE.sendTo(new BlocklistSyncPacket(player, false, combinedArray, 0, hd, vd), (EntityPlayerMP) player);
				}
			}
			// end config Sync

		}
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		if((event.player.world != null) && !event.player.world.isRemote) {
			final EntityPlayer player = event.player;
			if((player.getCapability(SizeCapPro.sizeCapability, null) != null)) {
				final ISizeCap cap = player.getCapability(SizeCapPro.sizeCapability, null);
				if((player.getRidingEntity() instanceof EntityMinecart) && (cap.getTrans() == true)) {
					player.dismountRidingEntity();
				}
			}
		}
	}

	@SubscribeEvent
	public void entityJoinWorld(EntityJoinWorldEvent event) {
		if((event.getEntity() instanceof EntityPlayer)) {
			final EntityPlayer player = (EntityPlayer) event.getEntity();
			final ISizeCap cap = player.getCapability(SizeCapPro.sizeCapability, null);
			final Boolean client = player.world.isRemote;
			if(!client) {
				NetworkHandler.sendPlayerDataTo(player, cap, (EntityPlayerMP) player);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		final EntityPlayer player = event.player;
		final Boolean client = player.world.isRemote;
		if(player instanceof EntityPlayerMP) {
			final ISizeCap cap = player.getCapability(SizeCapPro.sizeCapability, null);
			if(cap != null) {
				NetworkHandler.sendPlayerDataAll(player, cap);
			}
		}
	}

}
