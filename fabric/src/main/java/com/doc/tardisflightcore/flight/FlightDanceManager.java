package com.doc.tardisflightcore.flight;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FlightDanceManager {
    private static final Set<UUID> activeFlights = new HashSet<>();

    public static void startFlight(UUID tardisId) {
        activeFlights.add(tardisId);
    }

    public static void endFlight(UUID tardisId) {
        activeFlights.remove(tardisId);
    }

    public static boolean isFlying(UUID tardisId) {
        return activeFlights.contains(tardisId);
    }

    public static Set<UUID> getAllFlyingTardises() {
        return Set.copyOf(activeFlights);
    }

    public static void reset() {
        activeFlights.clear();
    }
}
