package com.github.tukenuke.tuske.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class EffPlayerShow extends Effect {

    static {
        Registry.newEffect(EffPlayerShow.class,
                "show %players% to %players%");
    }

    private Expression<Player> player_1;
    private Expression<Player> player_2;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] arg, int patter, Kleenean arg2, ParseResult arg3) {
        this.player_1 = (Expression<Player>) arg[0];
        this.player_2 = (Expression<Player>) arg[1];
        return true;
    }

    @Override
    public String toString(Event e, boolean arg1) {
        return "show players";
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void execute(Event e) {
        Player[] from = player_1.getAll(e);
        Player[] to = player_2.getAll(e);

        for (Player p2 : to) {
            for (Player p1 : from)
                p2.showPlayer(TuSKe.getInstance(), p1);
        }
    }

}
