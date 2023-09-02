package com.github.tukenuke.tuske;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.util.StringUtils;
import com.github.tukenuke.tuske.documentation.*;
import com.github.tukenuke.tuske.listeners.OnlineStatusCheck;
import com.github.tukenuke.tuske.manager.customenchantment.CustomEnchantment;
import com.github.tukenuke.tuske.manager.customenchantment.EnchantConfig;
import com.github.tukenuke.tuske.manager.customenchantment.EnchantManager;
import com.github.tukenuke.tuske.manager.gui.GUIManager;
import com.github.tukenuke.tuske.manager.gui.v2.SkriptGUIEvent;
import com.github.tukenuke.tuske.manager.recipe.RecipeManager;
import com.github.tukenuke.tuske.nms.NMS;
import com.github.tukenuke.tuske.nms.ReflectionNMS;
import com.github.tukenuke.tuske.util.ReflectionUtils;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class TuSKe extends JavaPlugin {
	private static NMS nms;
	private static TuSKe plugin;
	private static GUIManager gui;
	private static RecipeManager recipes;

	public TuSKe() {
		if (plugin != null) //Unnecessary, just to look cool.
			throw new IllegalStateException("TuSKe can't have two instances.");
		plugin = this;
	}

	@Override
	public void onEnable() {
		// --------- Safe check if everything is ok to load ---------
		Boolean hasSkript = hasPlugin("Skript");
		if (!hasSkript || !Skript.isAcceptRegistrations()) {
			if (!hasSkript)
				log("Error 404 - Skript not found.", Level.SEVERE);
			else
				log("TuSKe can't be loaded when the server is already loaded.", Level.SEVERE);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// ----------------------------------------------------------
		// ------------------ Initiate some stuffs ------------------
		EnchantConfig.loadEnchants();
		// ----------------------------------------------------------
		// ------------------------ Listener ------------------------
		// TODO temporary: make them auto-enable
		Bukkit.getServer().getPluginManager().registerEvents(new OnlineStatusCheck(this), this);
		// ----------------------------------------------------------
		// ------- Some stuffs like Metrics, docs and updater -------
		if (getConfig().getBoolean("use_metrics")) {
			new Metrics(this);
			log("Enabling Metrics... Done!");
		}
		// ----------------------------------------------------------
		// ---------------- Some thanks for donators ----------------
		//log(" ");
		//log(" A special thanks for donators:");
		//log(" @X0Freak - 50$");
		//log(" @RepublicanSensei - 10$");
		//log(" ");
		// ----------------------------------------------------------
		// ------------- Start to register all syntaxes -------------
		SkriptAddon tuske = Skript.registerAddon(this).setLanguageFileDirectory("lang");
		try {
			//                 It will return as "me.tuske.sktuke"
			tuske.loadClasses(getClass().getPackage().getName(), "register", "events", "conditions", "effects", "sections", "expressions");
			info("Loaded %d events, %d conditions, %d effects, %d expressions and %d types. Have fun!", (Object[]) Registry.getResults());
		} catch (Exception e) {
			info("Error while registering stuffs. Please, report it at %s", getDescription().getWebsite() + "/issues");
			e.printStackTrace();
		}
		try {
			tuske.loadClasses(getClass().getPackage().getName(), "extensions.deca");
			if (hasPlugin("WorldEdit"))
				tuske.loadClasses(getClass().getPackage().getName(), "extensions.we");
			if (hasPlugin("GPS"))
				tuske.loadClasses(getClass().getPackage().getName(), "extensions.gps");
			if (hasPlugin("Citizens"))
				tuske.loadClasses(getClass().getPackage().getName(), "extensions.citizens");
		} catch (Exception e) {
			info("Error while registering stuffs. Please, report it at %s", getDescription().getWebsite() + "/issues");
			e.printStackTrace();
		}
		// ----------------------------------------------------------
	}

	@Override
	public void onDisable() {
		SkriptGUIEvent.getInstance().unregisterAll();
		if (gui != null)
			gui.clearAll();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}
	public static TuSKe getInstance(){
		return plugin;
	}
	public boolean hasPlugin(String str) {
		return plugin.getServer().getPluginManager().isPluginEnabled(str);
	}

	public void info(String msg, Object... values) {
		log(String.format(msg, values), Level.INFO);
	}

	public void generateDocumentation() {
		FileType type;
		String config = getConfig().getString("documentation.file_type");
		switch (config.toLowerCase()) {
			case "yaml":
			case "yml":
				type = new YamlFile();
				break;
			case "json":
				type = new JsonFile(false);
				break;
			case "raw_json":
			case "raw json":
				type = new JsonFile(true);
				break;
			case "markdown":
				type = new MarkdownFile();
				break;
			case "default":
			case "skript":
			case "script":
			case "sk":
				type = new DefaultFile();
				break;
			default:
				log("Unknown value for 'documentation.file_type': " + config + ".");
				return;
		}
		new Documentation(this, type).load();
	}
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] arg){//TODO Remake this
		if (cmd.getName().equalsIgnoreCase("tuske")){
			if (arg.length > 0 && arg[0].equalsIgnoreCase("reload")) {
				if (arg.length > 1 && arg[1].equalsIgnoreCase("config")) {
					reloadConfig();
					sender.sendMessage("§e[§cTuSKe§e] §3Config reloaded!");
				} else if (arg.length > 1 && arg[1].equalsIgnoreCase("enchantments")) {
					EnchantConfig.reload();
					if (CustomEnchantment.getEnchantments().size() == 0)
						sender.sendMessage("§e[§cTuSKe§e] §3No enchantments were loaded. :(");
					else
						sender.sendMessage("§e[§cTuSKe§e] §3A total of §c" + CustomEnchantment.getEnchantments().size() + "§3custom enchantments were loaded succesfully.");
				} else if (arg.length > 1 && arg[1].equalsIgnoreCase("docs")){
					sender.sendMessage("§e[§cTuSKe§e] §3Regenerating documentation files using §c" + getConfig().getString("documentation.file_type") + " §3format.");
					generateDocumentation();
				} else {
					sender.sendMessage("§e[§cTuSKe§e] §3Main commands of §c"+ arg[0]+"§3:",
							"§4/§c" + label + " " + arg[0] + " config §e> §3Reload the config.",
							"§4/§c" + label + " " + arg[0] + " enchantments §e> §3Reload the enchantments' file.",
							"§4/§c" + label + " " + arg[0] + " docs §e> §Regenerate new documentation files.");
					
				}	

			} else if (arg.length > 0 && arg[0].matches("ench(antment)?")){
				if (arg.length > 1 && arg[1].equalsIgnoreCase("list")){
					sender.sendMessage("§e[§cTuSKe§e] §3All registred enchantments:", "      §eName       §c-§e ML §c-§e R §c-§e Enabled?");
					
					for (CustomEnchantment c : CustomEnchantment.getEnchantments()){
						sender.sendMessage("§c" + left(c.getId(), 15) + " §4-§c  " + c.getMaxLevel() + "  §4-§c " + c.getRarity() + " §4- " + (c.isEnabledOnAnvil() ? "§a" : "§c") + (c.isEnabledOnTable()));
					}
					
				} else if (arg.length > 1 && arg[1].equalsIgnoreCase("toggle")){
					String ench = getEnchantment(arg, 2);
					if (arg.length > 2 && EnchantManager.isCustomByID(ench)){
						CustomEnchantment ce = CustomEnchantment.getByID(ench);
						ce.setEnabledOnTable(!ce.isEnabledOnTable());
						sender.sendMessage("§e[§cTuSKe§e] §3The enchantment §c" + ce.getId() + "§3 was " + (ce.isEnabledOnTable() ? "§aenabled" : "§cdisabled") + "!");
					} else if (arg.length > 2 && !EnchantManager.isCustomByID(ench))
						sender.sendMessage("§e[§cTuSKe§e] §3There isn't any registred enchantment with ID §c" + ench + "§3.");
					else
						sender.sendMessage("§e[§cTuSKe§e] §3Use this command to enable/disable a enchantment.",
								"§4/§c" + label + " "+arg[0]+" toggle §4<§cID§4> §e> §3Enable/disable a enchantment.");
				} else if (arg.length > 1 && arg[1].equalsIgnoreCase("give")){			
					if (sender instanceof Player){
						String ench = getEnchantment(arg, 2);
						if (arg.length > 2 && EnchantManager.isCustomByID(ench)){			
							Integer lvl = (arg.length > 3 && isInteger(arg[arg.length - 1])) ? Integer.valueOf(arg[arg.length - 1]) : 1;
							CustomEnchantment ce = CustomEnchantment.getByID(ench);
							Player p = (Player)sender;
							ItemStack i = p.getInventory().getItem(p.getInventory().getHeldItemSlot());
							if (i != null && !i.getType().equals(Material.AIR)){
								if (ce.isCompatible(i)){
									if(!EnchantManager.addToItem(p.getInventory().getItem(p.getInventory().getHeldItemSlot()), ce, lvl, true)){
										sender.sendMessage("§e[§cTuSKe§e] §3The enchantment §c" + ce.getId() + "§3 couldn't be added to your held item.");
									} else
										sender.sendMessage("§e[§cTuSKe§e] §3The enchantment §c" + ce.getId() + "§3 was added to your held item.");
								} else
									sender.sendMessage("§e[§cTuSKe§e] §3The enchantment §c" + ce.getId() + "§3 doesn't accept this item.");
							} else
								sender.sendMessage("§e[§cTuSKe§e] §3You have to hold a item first.");
						} else if (arg.length > 2 && !EnchantManager.isCustomByID(ench)){	
							sender.sendMessage("§e[§cTuSKe§e] §3There isn't any registred enchantment with ID §c" + ench + "§3.");
						} else
							sender.sendMessage("§e[§cTuSKe§e] §3Use this command to enchant your held item.",
									"§4/§c" + label + " " + arg[0] + " give §4<§cID§4> §c[§4<§cLevel§4>§c] §e> §3Add a enchantment to your held item.");
					} else
						sender.sendMessage("§e[§cTuSKe§e] §3This command is only for players.");
				} else
					sender.sendMessage("§e[§cTuSKe§e] §3Main commands of §c"+ arg[0]+"§3:",
							"§4/§c" + label + " " + arg[0] + " list §e> §3Shows a list of registered enchantment.",
							"§4/§c" + label + " " + arg[0] + " toggle §e> §3Enable/disable a enchantment.",
							"§4/§c" + label + " " + arg[0] + " give §e> §3Add a enchantment to your held item.");
			} else {
				sender.sendMessage("§e[§cTuSKe§e] §3Main commands:",
						"§4/§c" + label + " reload §e> §3Reload config/enchantments.",
						"§4/§c" + label + " update §e> §3Check for latest update.",
						"§4/§c" + label + " ench §e> §3Manage the enchantments.");
				
			}
		}
		return true;
		
	}
	private String getEnchantment(String[] str, int id){
		StringBuilder sb = new StringBuilder();
		for (int x = id; x < str.length; x++){
			if (!(x == str.length - 1 && isInteger(str[x]))){
				sb.append(str[x]);
				if (x < str.length - 2)
					sb.append(" ");
			}
		}
		if (sb.toString().equals("") && str.length > id)
			sb.append(str[id]);
		return sb.toString();
	}
	private boolean isInteger(String arg){
		return arg.matches("\\d+");
	}
	private String left(String s, int d){
		StringBuilder sb = new StringBuilder(d);
		sb.append(s);
		while (sb.length() < d)
			sb.append(" ");
		return sb.toString();
	}

	public static RecipeManager getRecipeManager(){
		if (recipes == null)
			recipes = new RecipeManager();
		return recipes;
	}
	
	public static GUIManager getGUIManager(){
		if (gui == null)
			 gui = new GUIManager(getInstance());
	    return gui;
	}
	public static void log(String msg){
	    log(msg, Level.INFO);
	}
	public static void log(String msg, Level lvl){
	    plugin.getLogger().log(lvl, msg);
	}
	public static void log(Level lvl, String... msgs){
		for (String msg : msgs)
			log(msg, lvl);
	}
	public static boolean debug(){
		return plugin.getConfig().getBoolean("debug_mode");
	}
	public static void debug(Object... objects){
		if (!debug())
			return;
		StackTraceElement caller = new Exception().getStackTrace()[1];
		log(String.format("[Debug] [%s, line %s] %s", caller.getFileName(), caller.getLineNumber(), StringUtils.join(objects, " || ")));
	}
	public static NMS getNMS(){		
		if (nms == null){
			nms = (NMS) ReflectionUtils.newInstance(ReflectionUtils.getClass("me.tuke.sktuke.nms.M_" + ReflectionUtils.packageVersion));
			if (nms == null) {// Didn't find any interface avaliable for that version.
                nms = new ReflectionNMS(); //An default NMS class using reflection, in case it couldn't find it.
                log("Couldn't find support for the Bukkit version '" +ReflectionUtils.packageVersion+ "'. Some expressions, such as \"player data of %offline player%\", may or may not work fine, so it's better to ask the developer about it." , Level.WARNING);
            }
		}
		return nms;
	}

	public static boolean isSpigot(){
		return ReflectionUtils.hasMethod(Player.class, "spigot");
	}
}