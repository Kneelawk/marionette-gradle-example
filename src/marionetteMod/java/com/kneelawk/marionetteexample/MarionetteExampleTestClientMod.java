package com.kneelawk.marionetteexample;

import com.kneelawk.marionette.gen.mod.client.ClientGlobalQueues;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MarionetteExampleTestClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> ClientGlobalQueues.pollGameTickCallbacks(
                (callback, currentThread) -> callback.callback(currentThread, Thread.currentThread().getName())));
    }
}
