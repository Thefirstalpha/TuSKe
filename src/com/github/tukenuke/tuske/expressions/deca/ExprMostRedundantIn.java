package com.github.tukenuke.tuske.expressions.deca;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;

import java.util.*;
import java.util.stream.Collectors;

public class ExprMostRedundantIn extends SimpleExpression<String> {
	static {
		Registry.newSimple(ExprMostRedundantIn.class, "most redundant in %strings%");
	}

	Random random = new Random();
	private Expression<String> data;

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
		data = (Expression<String>) arg[0];
		return true;
	}

	@Override
	public String toString(Event arg0, boolean arg1) {
		return "most redundant in";
	}

	@Override
	protected String[] get(Event e) {
		String[] datas = this.data.getAll(e);
		List<String> values = new ArrayList<>(Arrays.asList(datas));
		Map<String, Long> count = values.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));
		Optional<Map.Entry<String, Long>> max = count.entrySet().stream().max(Map.Entry.comparingByValue());
		List<String> selected = count.entrySet().stream().filter(v -> v.getValue().equals(max.get().getValue())).map(Map.Entry::getKey).collect(Collectors.toList());
		return new String[]{selected.get(random.nextInt(selected.size()))};
	}
}
