package com.kneelawk.marionetteexample;

import com.kneelawk.marionette.gen.mod.server.ServerGlobalQueues;
import com.kneelawk.marionette.gen.mod.server.ServerGlobalSignals;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class MarionetteExampleTestServerMod implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> ServerGlobalSignals.signalGameStarted());
        ServerTickEvents.END_SERVER_TICK
                .register(ms -> ServerGlobalQueues.callGameTickCallbacks(ms, Thread.currentThread().getName()));
    }
}
