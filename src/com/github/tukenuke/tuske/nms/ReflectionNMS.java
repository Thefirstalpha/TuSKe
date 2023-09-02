package com.github.tukenuke.tuske.nms;

import java.util.logging.Level;

import com.github.tukenuke.tuske.util.ReflectionUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.tukenuke.tuske.TuSKe;

public class ReflectionNMS implements NMS {

	private final String version = ReflectionUtils.packageVersion;
	public ReflectionNMS(){
	}

	@Override
	public void makeDrop(Player p, ItemStack i) {
		if (p != null && i != null){
			try {
				Class<?> craftItemClz = Class.forName("org.bukkit.craftbukkit.v"+version+".inventory.CraftItemStack");
				Object nmsItem = craftItemClz.getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, i);
				Class<?> craftPlayerClz = Class.forName("org.bukkit.craftbukkit.v"+version+".entity.CraftPlayer");
				Object entity = craftPlayerClz.getDeclaredMethod("getHandle").invoke(p);
				Class.forName("net.minecraft.server.v"+version+".EntityHuman").getDeclaredMethod("drop", nmsItem.getClass(), boolean.class).invoke(entity, nmsItem, true);
	
			} catch (Exception e){
				TuSKe.log(Level.WARNING,
					"An error occured with effect to force a player to drop a item. It is because your server version isn't supported yet.",
					"So, report it somewhere, in Spigot or GitHub, to the developer with following details:",
					"Running version: v" + version,
					"Error details:");
				e.printStackTrace();
				
			}
		}
		
	}

	@Override
	public void setFastBlock(World world, int x, int y, int z, int blockId, byte data) {		
	}

	@Override
	public void updateChunk(Chunk c) {
	}

}
