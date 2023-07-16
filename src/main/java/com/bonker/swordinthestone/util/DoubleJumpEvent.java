package com.bonker.swordinthestone.util;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired on the FORGE bus, only on the server side
 */
@Cancelable
public class DoubleJumpEvent extends PlayerEvent {
    public DoubleJumpEvent(Player player) {
        super(player);
    }
}
