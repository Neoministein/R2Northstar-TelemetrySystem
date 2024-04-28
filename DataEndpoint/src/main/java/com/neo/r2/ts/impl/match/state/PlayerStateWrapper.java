package com.neo.r2.ts.impl.match.state;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.CustomConstants;

public class PlayerStateWrapper extends EntityStateWrapper {

    public PlayerStateWrapper(JsonNode creationEvent) {
        super("player" ,creationEvent);
        rawData.put("isTitan", false);
        rawData.put("isWallRunning", false);
        rawData.put("isShooting", false);
        rawData.put("isGrounded", false);
        rawData.put("isHanging", false);
        rawData.put("isCrouching", false);
        rawData.put("isAlive", false);
        rawData.put("isRodeoing", false);

        rawData.put("distance", 0);

        ObjectNode equipment = rawData.withObject("/equipment");
        equipment.put("weapon3", CustomConstants.UNKNOWN);
        equipment.put("special", CustomConstants.UNKNOWN);
    }

    public void setIsTitan(boolean isTitan) {
        rawData.put("isTitan", isTitan);
    }

    public void setIsAlive(boolean isAlive) {
        rawData.put("isAlive", isAlive);
    }

    public void setWeapon3(String weapon3) {
        rawData.withObject("/equipment").put("weapon3", weapon3);
    }

    public void setSpecial(String special) {
        rawData.withObject("/equipment").put("special", special);
    }
}
