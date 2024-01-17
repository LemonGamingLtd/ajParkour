package us.ajg0702.parkour.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import us.ajg0702.parkour.Main;

public class MaterialParser {

	@SuppressWarnings("deprecation")
	public static void placeBlock(Location loc, String blockname) {
		blockname = blockname.split(";")[0];
		Material mat;
		int data = -1;
		if(blockname.contains(":") && VersionSupport.getMinorVersion() <= 12) {
			String sd = blockname.split(":")[1];
			if(sd.equalsIgnoreCase("true")) {
				data = Main.random(0, 16);
			} else {
				data = Integer.parseInt(sd);
			}
		}
		try {
			mat = Material.valueOf(blockname.split(":")[0]);
		} catch(Exception e) {
			Bukkit.getLogger().warning("[ajParkour] Could not find block '"+blockname+"'!");
			loc.getBlock().setType(Material.STONE);
			return;
		}
		loc.getBlock().setType(mat);
//		if(data >= 0) {
//			loc.getBlock().setData((byte) data);
//		}
	}

}
