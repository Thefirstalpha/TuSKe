package com.github.tukenuke.tuske.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class ExprGroupOfPlayer extends SimpleExpression<String> {
    static {
        Registry.newSimple(ExprGroupOfPlayer.class, "[luckperm] group of %player%");
    }

    private Expression<Player> player;

    @Override
    public boolean init(Expression<?>[] exp, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        player = (Expression<Player>) exp[0];
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
    @Nullable
    protected String[] get(Event event) {
        LuckPerms api = LuckPermsProvider.get();
        Player p = player.getSingle(event);
        if (p == null)
            return new String[]{};
        User user = api.getPlayerAdapter(Player.class).getUser(p);
        return new String[]{user.getPrimaryGroup()};
    }

    @Override
    public String toString(Event event, boolean b) {
        return "group of player";
    }
}
