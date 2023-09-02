package com.github.tukenuke.tuske.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ExprRemoveDuplicateValues extends SimpleExpression<Object> {
    static {
        Registry.newSimple(ExprRemoveDuplicateValues.class, "remove duplicate values of %objects%");
    }

    private Expression<Object> objectsExp;

    @Override
    public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
        objectsExp = (Expression<Object>) arg[0];
        return true;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        return "remove duplicate values of %objects%";
    }

    @Override
    @Nullable
    protected Object[] get(Event e) {
        Object[] list = objectsExp.getAll(e);
        Set<Object> filteredList = new HashSet<Object>(Arrays.asList(list));
        Object[] o = new Object[filteredList.size()];

        int i = 0;
        for (Iterator<Object> it = filteredList.iterator(); it.hasNext(); ) {
            Object b = it.next();
            o[i] = b;
            i++;
        }
        return o;
    }

}
