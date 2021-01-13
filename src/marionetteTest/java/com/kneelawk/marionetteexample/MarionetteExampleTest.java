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
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    void startClient() throws IOException, InstanceException, InterruptedException, ExecutionException {
        MinecraftClientInstanceBuilder builder = new MinecraftClientInstanceBuilder("client");

        System.out.println("#############################");
        System.out.println("# Starting Minecraft Client #");
        System.out.println("#############################");

        MinecraftClientInstance minecraft = builder.start(manager);

        System.out.println("Calling startMinecraft()");
        minecraft.startMinecraft();

        System.out.println("Waiting for minecraft to start up...");
        minecraft.createGameStartedFuture().get();

        System.out.println("Waiting for gameTick callback...");
        minecraft.addGameTickCallback((thread, p0, p1) -> {
            System.out.println("Current Thread: " + p1);
            p0.scheduleStop(thread);
        }).get();

        System.out.println("Calling finish()");
        minecraft.finish();

        System.out.println("#############################");
        System.out.println("# Minecraft Client finished #");
        System.out.println("#############################");
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    void startServer() throws IOException, InterruptedException, InstanceException, ExecutionException {
        MinecraftServerInstanceBuilder minecraftBuilder = new MinecraftServerInstanceBuilder("server");

        System.out.println("#############################");
        System.out.println("# Starting Minecraft Server #");
        System.out.println("#############################");
        MinecraftServerInstance minecraft = minecraftBuilder.start(manager);

        System.out.println("Calling startMinecraft()");
        minecraft.startMinecraft();

        System.out.println("Waiting for minecraft to start up...");
        minecraft.createGameStartedFuture().get();

        System.out.println("Waiting for gameTick callback...");
        minecraft.addGameTickCallback((thread, p0, p1) -> System.out.println("Current Thread: " + p1)).get();

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

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    void startBoth() throws IOException, ExecutionException, InterruptedException, InstanceException {
        MinecraftServerInstanceBuilder serverBuilder = new MinecraftServerInstanceBuilder("server1");
        MinecraftClientInstanceBuilder clientBuilder = new MinecraftClientInstanceBuilder("client1");

        serverBuilder.setGamemode("creative");

        System.out.println("########################################");
        System.out.println("# Starting Minecraft Server And Client #");
        System.out.println("########################################");

        MinecraftServerInstance serverInstance = serverBuilder.start(manager);
        MinecraftClientInstance clientInstance = clientBuilder.start(manager);

        System.out.println("Calling startMinecraft()");
        serverInstance.startMinecraft();
        clientInstance.startMinecraft();

        System.out.println("Sending /op command...");
        PrintStream ps = new PrintStream(serverInstance.getProcess().getOutputStream());
        ps.println("/op client1");
        ps.flush();

        System.out.println("Waiting for client to start up...");
        clientInstance.createGameStartedFuture().get();

        System.out.println("Waiting for server to start up...");
        serverInstance.createGameStartedFuture().get();

        System.out.println("Conning the client to the server...");
        clientInstance.addGameTickCallback((thread, p0, p1) -> p0.openScreen(thread, clientInstance
                .newConnectScreen(thread, clientInstance.newTitleScreen(thread), p0, "localhost", 25565))).get();

        System.out.println("Calling finish()");
        serverInstance.finish();
        clientInstance.finish();

        System.out.println("#######################################");
        System.out.println("# Minecrft Server and Client finished #");
        System.out.println("#######################################");
    }
}
