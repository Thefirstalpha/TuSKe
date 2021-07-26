package com.github.tukenuke.tuske.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.tukenuke.tuske.documentation.Dependency;
import com.github.tukenuke.tuske.util.Registry;
import com.viaversion.viaversion.ViaVersionPlugin;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;



@Name("Minecraft Version")
@Description("Returns the minecraft version of {{types|Player|player}}.")
@Examples({
		"on join:",
		"if minecraft version of player is \"1.9\":",
		"send \"You're joining with version %mc version of player%!\""})
@Since("1.0 (ProtocolSupport), 1.0.5 (ViaVersion)")
@Dependency("ProtocolSupport or ViaVersion")
public class ExprPlayerVersion extends SimplePropertyExpression<Player, String>{
	private static boolean hasViaVersion = false;
	private static boolean hasProtocolSupport = Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolSupport");
	private static ViaAPI<Player> api; //ViaVersion API instance

	static {
		Plugin viaVersionPlugin = Bukkit.getServer().getPluginManager().getPlugin("ViaVersion");
		if (viaVersionPlugin != null && viaVersionPlugin.isEnabled()) {
			hasViaVersion = true;
			api = ((ViaVersionPlugin) viaVersionPlugin).getApi();
		}
		if (hasViaVersion || hasProtocolSupport)
			Registry.newProperty(ExprPlayerVersion.class, "(mc|minecraft) version", "player");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override

	public String convert(Player p) {
		if (hasViaVersion){
			int i = api.getPlayerVersion(p.getUniqueId());
			return ProtocolVersion.getProtocol(i).getName().replace(".x", "");
		} else if (hasProtocolSupport)
			return protocolsupport.api.ProtocolSupportAPI.getProtocolVersion(p).getName();
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "minecraft version";
	}

}
