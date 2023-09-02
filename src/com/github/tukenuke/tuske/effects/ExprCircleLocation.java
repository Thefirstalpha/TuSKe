package com.github.tukenuke.tuske.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Location;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class ExprCircleLocation extends SimpleExpression<Location> {
    static {
        Registry.newSimple(ExprCircleLocation.class, "circle %number% location in radius %number% started at %number% centered at %location%");
    }

    private Expression<Number> amountExp;
    private Expression<Number> radiusExp;
    private Expression<Number> angleExp;
    private Expression<Location> centerExp;

    @Override
    public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
        amountExp = (Expression<Number>) arg[0];
        radiusExp = (Expression<Number>) arg[1];
        angleExp = (Expression<Number>) arg[2];
        centerExp = (Expression<Location>) arg[3];
        return true;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        return "circle %number% location in radius %number% started at %number% centered at %location%";
    }

    @Override
    @Nullable
    protected Location[] get(Event e) {
        int amount = amountExp.getSingle(e).intValue();
        int radius = radiusExp.getSingle(e).intValue();
        float angle = angleExp.getSingle(e).floatValue();
        float separation = 360.0f / amount;
        Location center = centerExp.getSingle(e);
        Location[] locations = new Location[amount];
        for (int i = 0; i < amount; i++) {
            locations[i] = getCirclePosition(radius, angle, center.clone());
            angle += separation;
        }
        return locations;
    }

    private Location getCirclePosition(double radius, float angle, Location center) {
        double x = Math.cos(Math.toRadians(angle)) * radius;
        double z = Math.sin(Math.toRadians(angle)) * radius;
        return center.add(Math.round(x * 10.0) / 10.0, 0, Math.round(z * 10.0) / 10.0);
    }

}
