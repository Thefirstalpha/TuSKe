package com.github.tukenuke.tuske.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

public class EffCopyBlock extends Effect {

    static {
        Registry.newEffect(EffCopyBlock.class,
                "copy block between %location% and %location% to %location%");
    }

    private Expression<Location> loc_1_exp;
    private Expression<Location> loc_2_exp;
    private Expression<Location> loc_dest_exp;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] arg, int patter, Kleenean arg2, ParseResult arg3) {
        this.loc_1_exp = (Expression<Location>) arg[0];
        this.loc_2_exp = (Expression<Location>) arg[1];
        this.loc_dest_exp = (Expression<Location>) arg[2];
        return true;
    }

    @Override
    public String toString(Event e, boolean arg1) {
        return "clone blocks";
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void execute(Event e) {
        Location l1 = loc_1_exp.getSingle(e);
        Location l2 = loc_2_exp.getSingle(e);
        Location dest = loc_dest_exp.getSingle(e);

        if (l1 != null && l2 != null && l1.getWorld().getUID().equals(l2.getWorld().getUID()) && dest != null) {
            double minX = Math.min(l1.getX(), l2.getX());
            double minY = Math.min(l1.getY(), l2.getY());
            double minZ = Math.min(l1.getZ(), l2.getZ());
            double maxX = Math.max(l1.getX(), l2.getX());
            double maxY = Math.max(l1.getY(), l2.getY());
            double maxZ = Math.max(l1.getZ(), l2.getZ());

            World world = l1.getWorld();

            for (double x = 0; x < maxX - minX + 1; x++) {
                for (double y = 0; y < maxY - minY + 1; y++) {
                    for (double z = 0; z < maxZ - minZ + 1; z++) {
                        Block block = new Location(world, x + minX, y + minY, z + minZ).getBlock();
                        Location f = dest.clone().add(x, y, z);
                        f.getBlock().setType(block.getType());
                    }
                }
            }
        }
    }

}
