package com.github.tukenuke.tuske.effects;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class EffRandomPlace extends SimpleExpression<ItemType> {
    private static final Random random = new Random();

    static {
        Registry.newSimple(EffRandomPlace.class,
                "place random block between %location% and %location% with %itemtypes%");
    }

    private Expression<Location> loc_1_exp;
    private Expression<Location> loc_2_exp;
    private Expression<ItemType> material_exp;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] arg, int patter, Kleenean arg2, ParseResult arg3) {
        this.loc_1_exp = (Expression<Location>) arg[0];
        this.loc_2_exp = (Expression<Location>) arg[1];
        this.material_exp = (Expression<ItemType>) arg[2];
        return true;
    }

    @Override
    public String toString(Event e, boolean arg1) {
        return "place random blocks";
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    @Nullable
    protected ItemType[] get(Event e) {
        Location l1 = loc_1_exp.getSingle(e);
        Location l2 = loc_2_exp.getSingle(e);
        ItemType[] materials = material_exp.getAll(e);

        Set<Material> output = new HashSet<>();
        if (l1 != null && l2 != null && l1.getWorld().getUID().equals(l2.getWorld().getUID())) {
            double minX = Math.min(l1.getX(), l2.getX());
            double minY = Math.min(l1.getY(), l2.getY());
            double minZ = Math.min(l1.getZ(), l2.getZ());
            double maxX = Math.max(l1.getX(), l2.getX());
            double maxY = Math.max(l1.getY(), l2.getY());
            double maxZ = Math.max(l1.getZ(), l2.getZ());

            int size = materials.length;
            World world = l1.getWorld();

            for (double x = minX; x < maxX + 1; x++) {
                for (double y = minY; y < maxY + 1; y++) {
                    for (double z = minZ; z < maxZ + 1; z++) {
                        int i = random.nextInt(size);
                        output.add(materials[i].getMaterial());
                        world.getBlockAt(new Location(world, x, y, z)).setType(materials[i].getMaterial());
                    }
                }
            }
        }
        ItemType[] mat = new ItemType[output.size()];
        List<ItemType> it = output.stream().map(ItemType::new).collect(Collectors.toList());
        it.toArray(mat);
        return mat;
    }
}
