package com.github.tukenuke.tuske.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class EffSaveInventory extends Effect {
    private static final Random random = new Random();

    static {
        Registry.newEffect(EffSaveInventory.class,
                "save %player% inventory in %string%");
    }

    private Expression<Player> playerExp;
    private Expression<String> nameExp;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] arg, int patter, Kleenean arg2, ParseResult arg3) {
        this.playerExp = (Expression<Player>) arg[0];
        this.nameExp = (Expression<String>) arg[1];
        return true;
    }

    @Override
    public String toString(Event e, boolean arg1) {
        return "save %player% inventory in %string%";
    }

    @Override
    protected void execute(Event e) {
        Player player = playerExp.getSingle(e);
        String name = nameExp.getSingle(e);

        if (player != null && name != null) {
            YamlConfiguration config = new YamlConfiguration();
            config.set("content", player.getInventory().getContents());
            config.set("armor", player.getInventory().getExtraContents());
            config.set("extra", player.getInventory().getContents());
            config.set("ender", player.getEnderChest().getContents());
            try {
                config.save(new File(name, player.getUniqueId() + ".yml"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
