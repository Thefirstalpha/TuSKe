package com.github.tukenuke.tuske.extensions.citizens.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.DocumentationId;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

@Name("Citizen is")
@Description("Checks if a citizen has a certain ID.")
@RequiredPlugins("Citizens")
@DocumentationId("CondCitizenIs")
public class CondIsNpc extends Condition {
    static {
        Registry.newCondition(CondIsNpc.class, "%entity% is [a] (npc)");
    }
    // %entity% is [a] (npc|citizen)

    private Expression<Entity> ent;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exp, int arg1, Kleenean arg2, ParseResult arg3) {
        ent = (Expression<Entity>) exp[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        return null;
    }

    @Override
    public boolean check(Event evt) {
        return ent.getSingle(evt) != null && ent.getSingle(evt).hasMetadata("NPC");
    }
}
