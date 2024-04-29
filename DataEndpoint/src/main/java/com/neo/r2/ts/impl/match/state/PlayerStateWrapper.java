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

        ObjectNode equipment = rawData.withObject("/equipment");
        equipment.put("weapon3", CustomConstants.UNKNOWN);
        equipment.put("special", CustomConstants.UNKNOWN);
    }

    public void killed() {
        rawData.put("isTitan", false);
        rawData.put("isWallRunning", false);
        rawData.put("isShooting", false);
        rawData.put("isGrounded", false);
        rawData.put("isHanging", false);
        rawData.put("isCrouching", false);
        rawData.put("isAlive", false);
        rawData.put("isRodeoing", false);
        rawData.put("titanClass", CustomConstants.UNKNOWN);
    }

    public void setIsTitan(boolean isTitan) {
        rawData.put("isTitan", isTitan);
    }

    public void setIsWallRunning(boolean isWallRunning) {
        rawData.put("isWallRunning", isWallRunning);
    }

    public void setIsShooting(boolean isShooting) {
        rawData.put("isShooting", isShooting);
    }

    public void setIsGrounded(boolean isGrounded) {
        rawData.put("isGrounded", isGrounded);
    }

    public void setIsHanging(boolean isHanging) {
        rawData.put("isHanging", isHanging);
    }

    public void setIsCrouching(boolean isCrouching) {
        rawData.put("isCrouching", isCrouching);
    }

    public void setIsRodeoing(boolean isRodeoing) {
        rawData.put("isRodeoing", isRodeoing);
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
