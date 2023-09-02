package com.github.tukenuke.tuske.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Event;

public class EffLoadChunk extends Effect {
	static {
		Registry.newEffect(EffLoadChunk.class, "load chunk at %number%, %number% in world %string%");
	}

	private Expression<Number> xExp;
	private Expression<Number> zExp;
	private Expression<String> worldExp;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.xExp = (Expression<Number>) arg[0];
		this.zExp = (Expression<Number>) arg[1];
		this.worldExp = (Expression<String>) arg[2];
		return true;
	}

	@Override
	public String toString(Event e, boolean arg1) {
		return "load chunk";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Event e) {
		String w = worldExp.getSingle(e);
		World world = Bukkit.getWorld(w);
		if (world != null) {

			int x = xExp.getSingle(e).intValue();
			int z = zExp.getSingle(e).intValue();
			world.loadChunk(x, z);
			Chunk chunk = world.getChunkAt(x, z);
			System.out.println(chunk.getBlock(1, 1, 1));
		} else {
			TuSKe.getInstance().getLogger().severe("World " + w + " can't be found");
		}
	}

}
