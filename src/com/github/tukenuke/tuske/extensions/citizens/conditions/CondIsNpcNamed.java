package com.github.tukenuke.tuske.extensions.citizens.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import net.citizensnpcs.api.event.NPCEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

@Name("Citizen is named")
@Description({"Check citizen by:",
        "* Name",
        "A conditional check using citizens names over id numbers"})
@RequiredPlugins("Citizens")
public class CondIsNpcNamed extends Condition {
    static {
        Registry.newCondition(CondIsNpcNamed.class, "npc['s] [is] name[d] [is] %string%");
    }
    // npc/citizen['s] [is] name[d] [is] "%string%"

    private Expression<String> name;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exp, int arg1, Kleenean arg2, ParseResult arg3) {
        name = (Expression<String>) exp[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        return null;
    }

    @Override
    public boolean check(Event evt) {
        if (evt instanceof NPCEvent) {
            NPC testTarget = ((NPCEvent) evt).getNPC();
            if (testTarget == null) {
                return false;
            }
            return name.getSingle(evt).replace("\"", "").trim()
                    .equals(testTarget.getFullName());
        } else {
            return false;
        }
    }

}
