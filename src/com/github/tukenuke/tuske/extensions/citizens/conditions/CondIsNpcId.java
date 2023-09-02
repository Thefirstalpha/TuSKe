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
import net.citizensnpcs.api.event.NPCEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

@Name("Citizen is")
@Description("Checks if a citizen has a certain ID.")
@RequiredPlugins("Citizens")
@DocumentationId("CondCitizenIs")
public class CondIsNpcId extends Condition {
    static {
        Registry.newCondition(CondIsNpcId.class, "npc is %number%");
    }

    private Expression<Number> id;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exp, int arg1, Kleenean arg2, ParseResult arg3) {
        id = (Expression<Number>) exp[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        return "is NPC";
    }

    @Override
    public boolean check(Event evt) {
        if (evt instanceof NPCEvent) {
            NPC testTarget = ((NPCEvent) evt).getNPC();
            if (testTarget == null) {
                return false;
            }
            return id.getSingle(evt).intValue() == testTarget.getId();
        } else {
            return false;
        }
    }

}
