package com.obama69.newz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.obama69.newz.commands.RegisterCommandsEventListener;
import com.obama69.newz.player.PlayerLoginEventListener;
import com.obama69.newz.session.WorldNewsSession;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("newz")
public class Newz {
	public static final String MODID = "newz";
	public static final Logger LOGGER = LogManager.getLogger();
    
    public static WorldNewsSession session;

    public Newz() {        
        try {
			session = new WorldNewsSession();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
        
        MinecraftForge.EVENT_BUS.register(new RegisterCommandsEventListener());
        MinecraftForge.EVENT_BUS.register(new PlayerLoginEventListener());
    }
}
