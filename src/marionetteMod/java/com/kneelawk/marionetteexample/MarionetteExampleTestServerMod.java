package com.kneelawk.marionetteexample;

import com.kneelawk.marionette.gen.mod.server.ServerGlobalSignals;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class MarionetteExampleTestServerMod implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> ServerGlobalSignals.signalGameStarted());
    }
}
