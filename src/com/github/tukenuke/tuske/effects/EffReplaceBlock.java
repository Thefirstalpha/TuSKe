package com.github.tukenuke.tuske.effects;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EffReplaceBlock extends Effect {
    private static Random random = new Random();

    static {
        Registry.newEffect(EffReplaceBlock.class,
                "replace block of %itemtypes% between %location% and %location% with %itemtypes%");
    }

    private Expression<ItemType> material_from_exp;
    private Expression<Location> loc_1_exp;
    private Expression<Location> loc_2_exp;
    private Expression<ItemType> material_to_exp;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] arg, int patter, Kleenean arg2, ParseResult arg3) {
        this.material_from_exp = (Expression<ItemType>) arg[0];
        this.loc_1_exp = (Expression<Location>) arg[1];
        this.loc_2_exp = (Expression<Location>) arg[2];
        this.material_to_exp = (Expression<ItemType>) arg[3];
        return true;
    }

    @Override
    public String toString(Event e, boolean arg1) {
        return "replace blocks";
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void execute(Event e) {
        Location l1 = loc_1_exp.getSingle(e);
        Location l2 = loc_2_exp.getSingle(e);
        ItemType[] material_from = material_from_exp.getAll(e);
        ItemType[] material_to = material_to_exp.getAll(e);

        if (l1 != null && l2 != null && l1.getWorld().getUID().equals(l2.getWorld().getUID())) {
            double minX = Math.min(l1.getX(), l2.getX());
            double minY = Math.min(l1.getY(), l2.getY());
            double minZ = Math.min(l1.getZ(), l2.getZ());
            double maxX = Math.max(l1.getX(), l2.getX());
            double maxY = Math.max(l1.getY(), l2.getY());
            double maxZ = Math.max(l1.getZ(), l2.getZ());

            int size = material_to.length;
            System.out.println(size);
            World world = l1.getWorld();
            List<Material> materials = Arrays.stream(material_from).map(ItemType::getMaterial).collect(Collectors.toList());

            for (double x = minX; x < maxX + 1; x++) {
                for (double y = minY; y < maxY + 1; y++) {
                    for (double z = minZ; z < maxZ + 1; z++) {
                        Block block = world.getBlockAt(new Location(world, x, y, z));
                        if (materials.contains(block.getType())) {
                            int i = random.nextInt(size);
                            block.setType(material_to[i].getMaterial());
                        }

                    }
                }
            }
        }
    }

}
