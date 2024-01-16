package us.ajg0702.parkour;

import me.nahu.scheduler.wrapper.WrappedScheduler;
import me.nahu.scheduler.wrapper.WrappedSchedulerBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import us.ajg0702.parkour.game.JumpManager;
import us.ajg0702.parkour.game.Manager;
import us.ajg0702.parkour.top.TopManager;
import us.ajg0702.parkour.utils.Updater;
import us.ajg0702.utils.spigot.Config;

import java.util.*;
import java.util.Map.Entry;

public class Main extends JavaPlugin {
	
	public boolean papi = false;
	public Messages msgs;
	public Scores scores;
	public Manager man;
	
	public AreaStorage areaStorage;

	public Config config = null;
	
	public Commands cmds;
	
	public BlockSelector selector;
	
	public Rewards rewards;
	
	public Placeholders placeholders;
	
	Updater updater;

	public WrappedScheduler scheduler;
	
	@Override
	public void onEnable() {
		scheduler = WrappedSchedulerBuilder.builder().plugin(this).build();
		getLogger().info("Successfully initialized scheduler of type: " + scheduler.getImplementationType());

		try {
			Class.forName("net.md_5.bungee.api.ChatColor");
		} catch(ClassNotFoundException e) {
			getLogger().severe("Your server software is not supported!");
			getLogger().severe("ajParkour requires spigot or a fork of spigot. Disabling.");
			this.setEnabled(false);
			return;
		}
		
		config = new Config(this);
		
		/*String popSound = "ENTITY_CHICKEN_EGG";
		//System.out.println("Minor Version: 1."+VersionSupport.getMinorVersion());
		
		if(VersionSupport.getMinorVersion() <= 8) {
			popSound = "CHICKEN_EGG_POP";
		}
		
		config = new Config(this, "config.yml");
		config.addEntry("area-selection", "lowest", "The method to fill multiple multiple parkour areas.\nIf you only have one, this option is ignored.\n Default: lowest");
		config.addEntry("random-block-selection", "each", "Whether to pick a random block each jump, or a random block at the start.\n Options: 'each' or 'start'.\n Default: each");
		config.addEntry("random-item", "VINE", "This is the item to show in the selector GUI to represent the random block mode.\n Default: VINE");
		config.addEntry("jump-sound", popSound, "This is the sound to play when a player makes a jump.\n"
				+ "Here is a list for the latest spigot version: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html "
				+ "(the list starts below the orange box that says Enum Constants)\n"
				+ " Default: "+popSound);
		config.addEntry("top-shown", 10, "The amount of players to show in /ajParkour top\n Default: 10");
		config.addEntry("jumps-ahead", 1, "The number of extra blocks to place ahead of the next jump.\n Default: 1");
		config.addEntry("start-sound", "NONE", "The sound to play when a player starts parkour. See jump-sound for more info.\n Default: NONE");
		config.addEntry("end-sound", "NONE", "The sound to play when a player falls. See jump-sound for more info.\n Default: NONE");
		config.addEntry("new-block-particle", "CLOUD", "The particle to use when a new block is placed.\nSee the list of particles here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html\n Default: CLOUD");
		config.addEntry("particle-count", 25, "The number of particles to spawn when a new block is placed.\n Default: 25");
		config.addEntry("execute-reward-commands", "earned", "When to execute the reward commands.\n Options: 'earned', 'after'\n Default: 'earned'");
		config.addEntry("parkour-inventory", false, "If this is true, the player's inventory will be cleared while on the parkour, and reset after.\nNOTICE: If one of your reward commands gives items, you need to set execute-reward-commands to 'after' or else they will lose the items.\n Default: false");
		config.addEntry("start-disabled-worlds", "disabledworld1,disabledworld2", "If a world is listed here, the /ajParkour start command will not be usable from that world.\nWorld names are seperated by commas (without spaces) and are case-sensitive!\n Example: 'disabledworld1,disabledworld2'");
		config.addEntry("kick-time", 60, "How long, in seconds, after a player doesnt move should we kick them from the parkour?\nSet to -1 to disable\n Default: 60");
		config.addEntry("notify-update", true, "Should we notify people with the permission ajparkour.update that an update is available?\nThey will then be able to download it using /ajParkour update\n Default: true");
		config.addEntry("begin-score-per-area", false, "Should the score we tell the player to beat be per-area or global?\nFor example, if this is true and the player got 30 on another area but only 10 on this one, they will be told to beat their record of 10.\n Default: false");
		config.addEntry("enable-portals", true, "Should the portals be disabled?\nIf your server is lagging from this plugin without many people on parkour, try disabling this.\nREQUIRES SERVER RESTART (not just config reload)\n Default: true");
		config.addEntry("faster-portals", false, "Shoud we use a more optimized method to look if players are at a portal?\nIt may require the player to be in the block for a little longer\nEnable this if you have a lot of people on your server and are experiencing lag.\n Default: false");
		config.addEntry("enable-updater", true, "Should the updater be enabled?\nIf this is disabled, the plugin will not attempt to check for updates, and you will have to download new updates manually\nRequires a restart\n Default: true");
		config.addEntry("faster-afk-detection", false, "Should we apply faster-portals to the afk detections?\n Default: false");
		config.setEntries();*/
		
		
		msgs = new Messages(this);
		scores = new Scores(this);
		
		man = new Manager(this);
		
		selector = new BlockSelector(this);
		
		areaStorage = new AreaStorage(this);
		
		rewards = new Rewards(this);

		TopManager.getInstance(this);

		JumpManager.getInstance(this);
		
		
		scheduler.runTaskLaterAsynchronously(() -> {
			areaStorage.getAreas();
			areaStorage.getPortals();
		}, 10);
		
		getCommand("ajParkour").setTabCompleter(new CommandComplete(this));
		
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			placeholders = new Placeholders(this);
            placeholders.register();
            this.papi = true;
		}
		
		getServer().getPluginManager().registerEvents(man, this);
		getServer().getPluginManager().registerEvents(selector, this);
		
		cmds = new Commands(this);
		
		
//		new Metrics(this);
		
//		updater = Updater.getInstance(this);
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', 
				"&aajParkour &2v"+this.getDescription().getVersion()+" by ajgeiss0702 has been &aenabled!"));
	}

	public Config getAConfig() {
		return this.config;
	}
	
	
	
	
	public LinkedHashMap<String, Double> sortByValue(HashMap<String, Double> passedMap) {
	    List<String> mapKeys = new ArrayList<>(passedMap.keySet());
	    List<Double> mapValues = new ArrayList<>(passedMap.values());
	    Collections.sort(mapValues);
	    //Collections.sort(mapKeys);

	    LinkedHashMap<String, Double> sortedMap =
	        new LinkedHashMap<>();

		for (Double val : mapValues) {
			Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				String key = keyIt.next();
				Double comp1 = passedMap.get(key);

				if (comp1.equals(val)) {
					keyIt.remove();
					sortedMap.put(key, val);
					break;
				}
			}
		}
	    LinkedHashMap<String, Double> reverseMap = new LinkedHashMap<>();
	    List<Entry<String,Double>> list = new ArrayList<>(sortedMap.entrySet());

	    for( int i = list.size() -1; i >= 0 ; i --){
	        Entry<String,Double> e = list.get(i);
	        reverseMap.put(e.getKey(), e.getValue());
	    }
	    return reverseMap;
	}
	public LinkedHashMap<Object, Double> sortByValueWithObjectKey(HashMap<Object, Double> passedMap) {
		return sortByValueWithObjectKey(passedMap, true);
	}
	public LinkedHashMap<Object, Double> sortByValueWithObjectKey(HashMap<Object, Double> passedMap, boolean reverse) {
	    List<Object> mapKeys = new ArrayList<>(passedMap.keySet());
	    List<Double> mapValues = new ArrayList<>(passedMap.values());
	    Collections.sort(mapValues);

	    LinkedHashMap<Object, Double> sortedMap =
	        new LinkedHashMap<>();

		for (Double val : mapValues) {
			Iterator<Object> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				Double comp1 = passedMap.get(key);

				if (comp1.equals(val)) {
					keyIt.remove();
					sortedMap.put(key, val);
					break;
				}
			}
		}
	    if(reverse) {
	    	LinkedHashMap<Object, Double> reverseMap = new LinkedHashMap<>();
		    List<Entry<Object,Double>> list = new ArrayList<>(sortedMap.entrySet());

		    for( int i = list.size() -1; i >= 0 ; i --){
		        Entry<Object,Double> e = list.get(i);
		        reverseMap.put(e.getKey(), e.getValue());
		    }
		    return reverseMap;
	    } else {
	    	return sortedMap;
	    }
	}
	
	@Override
	public void onDisable() {
		man.disable();
		scores.disable();
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&cajParkour &4v" + this.getDescription().getVersion() + " by ajgeiss0702 has been &cdisabled!"));
	}
	
	
	final private List<String> reloadable = new LinkedList<>(Arrays.asList("config", "areas", "messages", "blocks", "rewards", "jumps"));
	public List<String> getReloadable() {
		return new ArrayList<>(reloadable);
	}
	public void reload(String key, CommandSender sender) {
		if(sender == null) {
			sender = Bukkit.getConsoleSender();
		}
		switch(key) {
		case "config":
			getAConfig().reload();
			break;
		case "areas":
			areaStorage.reload();
			break;
		case "messages":
			msgs.reload();
			break;
		case "blocks":
			selector.reloadTypes();
			break;
		case "rewards":
			rewards.reload();
			break;
		case "jumps":
			JumpManager.getInstance().reload();
			break;
		default:
			sender.sendMessage("&cCould not find file for "+key+"!");
			return;
		}
		
		sender.sendMessage(msgs.color("&aReloaded "+key+"!"));
	}
	
	
	
	public static int random(int min, int max) {


		if (min > max) {
			throw new IllegalArgumentException("max must be greater than min: "+min+"-"+max);
		} else if(min == max) {
			return min;
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

}
