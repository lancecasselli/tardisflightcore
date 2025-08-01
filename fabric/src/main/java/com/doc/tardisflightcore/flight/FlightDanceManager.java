package com.doc.tardisflightcore.flight;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import whocraft.tardis_refined.common.capability.tardis.TardisLevelOperator;
import whocraft.tardis_refined.api.event.EventResult;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FlightDanceManager {
    private static final Set<UUID> activeFlights = new HashSet<>();

    public static boolean isFlying(UUID tardisId) {
        return activeFlights.contains(tardisId);
    }

    public interface TakeOff {
        EventResult onTakeOff(TardisLevelOperator tardisLevelOperator, WorldAccess level, BlockPos pos);
    }

    /**
     * Stop the event using the oldest available API.
     */
    public static EventResult blockTakeOff() {
        // Old API method for "cancel"
        return EventResult.cancel(); // cancels the event and stops other listeners
    }
}
