package com.github.tukenuke.tuske.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.UUID;

public class ExprPlayerUniqueId extends SimpleExpression<String> {
    static {
        Registry.newSimple(ExprPlayerUniqueId.class, "bukkit uuid of %string%");
    }

    private Expression<String> player;

    @Override
    public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
        player = (Expression<String>) arg[0];
        return true;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        return "uuid of %string%" + player.toString(arg0, arg1);
    }

    @Override
    @Nullable
    protected String[] get(Event e) {
        String name = player.getSingle(e);
        UUID uuid = Bukkit.getPlayerUniqueId(name);
        if (uuid != null)
            return new String[]{uuid.toString()};
        return new String[]{null};
    }

}
