package com.kneelawk.marionetteexample;

import com.kneelawk.marionette.gen.instance.MinecraftClientInstance;
import com.kneelawk.marionette.gen.instance.MinecraftClientInstanceBuilder;
import com.kneelawk.marionette.gen.instance.MinecraftServerInstance;
import com.kneelawk.marionette.gen.instance.MinecraftServerInstanceBuilder;
import com.kneelawk.marionette.rt.instance.InstanceException;
import com.kneelawk.marionette.rt.rmi.RMIConnectionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;

public class MarionetteExampleTest {
    static RMIConnectionManager manager;

    @BeforeAll
    static void startRMI() throws RemoteException {
        manager = new RMIConnectionManager();
    }

    @AfterAll
    static void stopRMI() {
        manager.shutdown();
    }

    @Test
    void startClient() throws IOException, InstanceException, InterruptedException {
        MinecraftClientInstanceBuilder builder = new MinecraftClientInstanceBuilder("client");

        System.out.println("#############################");
        System.out.println("# Starting Minecraft Client #");
        System.out.println("#############################");

        MinecraftClientInstance minecraft = builder.start(manager);

        System.out.println("Calling startMinecraft()");
        minecraft.startMinecraft();

        System.out.println("Calling finish()");
        minecraft.finish();

        System.out.println("#############################");
        System.out.println("# Minecraft Client finished #");
        System.out.println("#############################");
    }

    @Test
    void startServer() throws IOException, InterruptedException, InstanceException {
        MinecraftServerInstanceBuilder minecraftBuilder = new MinecraftServerInstanceBuilder("server");

        System.out.println("#############################");
        System.out.println("# Starting Minecraft Server #");
        System.out.println("#############################");
        MinecraftServerInstance minecraft = minecraftBuilder.start(manager);

        System.out.println("Calling startMinecraft()");
        minecraft.startMinecraft();

        // make sure the server stops
        System.out.println("Sending the server /stop command...");
        PrintStream serverInput = new PrintStream(minecraft.getProcess().getOutputStream());
        serverInput.println("/stop");
        serverInput.flush();

        System.out.println("Calling finish()");
        minecraft.finish();

        System.out.println("#############################");
        System.out.println("# Minecraft Server finished #");
        System.out.println("#############################");
    }
}
