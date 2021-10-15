package com.obama69.newz.player;

import com.obama69.newz.Newz;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerLoginEventListener {

	@SubscribeEvent
	public void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {		
		final Player player = event.getPlayer();
		
		if (!(player instanceof ServerPlayer)) {
			return;
		}
		
		Newz.session.showNews((ServerPlayer) player);
	}
}
