package com.github.tukenuke.tuske.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Random;

public class EffRestoreInventory extends Effect {
    private static final Random random = new Random();

    static {
        Registry.newEffect(EffRestoreInventory.class,
                "restore %player% inventory from %string%");
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
        return "restore %player% inventory from %string%";
    }

    @Override
    protected void execute(Event e) {
        Player player = playerExp.getSingle(e);
        String name = nameExp.getSingle(e);

        if (player != null && name != null) {
            File file = new File(name, player.getUniqueId() + ".yml");
            if (file.exists()) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                List<ItemStack> content = (List<ItemStack>) config.getList("content");
                List<ItemStack> armor = (List<ItemStack>) config.getList("armor");
                List<ItemStack> extra = (List<ItemStack>) config.getList("extra");
                List<ItemStack> ender = (List<ItemStack>) config.getList("ender");

                player.getInventory().setContents(content.toArray(new ItemStack[content.size()]));
                player.getInventory().setContents(armor.toArray(new ItemStack[armor.size()]));
                player.getInventory().setContents(extra.toArray(new ItemStack[extra.size()]));
                player.getInventory().setContents(ender.toArray(new ItemStack[ender.size()]));
            }

        }
    }

}
