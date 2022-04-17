package com.neo.tf2.gsp;

import com.neo.tf2.gsp.event.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static final String LOG_LOCATION = "D:\\Program Files (x86)\\Origin Games\\Titanfall2\\R2Northstar\\logs\\nslog2022-04-16 22-15-36.txt"; //nslog2022-04-12 21-59-41.txt

    private Map<String, Player> playerMap = new HashMap<>();
    private Map<UUID, List<JSONObject>> games = new HashMap<>();

    private GameState gameState = null;
    private List<JSONObject> gameStateList = new ArrayList<>();
    private UI ui;

    public static void main(String[] args) {
        new Main().run();
    }

    public Main() {
        ui = new UI(1000, 1000);
    }

    public void run() {
        parseData();
        ui.repaint(gameStateList);
    }

    private void parseData() {
        long start = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_LOCATION))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("[SERVER SCRIPT] |")) {
                    handleInputData(line);
                }
            }
        } catch (Exception ex) {

        }
        System.out.println(System.currentTimeMillis() - start);
    }

    private void setupNewGame(String[] content) {
        gameState = new GameState(UUID.randomUUID(),content[2], playerMap); //TODO GET UUID FROM SERVER
    }

    private void concludeGame() {
        ui.repaint(gameStateList);
        playerMap = new HashMap<>();
        gameStateList = new ArrayList<>();
        gameState = null;

    }

    private void handleInputData(String inputData) {
        String[] content = inputData.split("\\|");
        try {
            switch (Integer.parseInt(content[1])) {
                case 0:
                    int players = Integer.parseInt(content[2]);
                    for (int i = 0; i < players;i++) {
                        int startingIndex = 3 + i * 5;
                        updatePlayer(playerMap.get(content[startingIndex++]),content,startingIndex);
                    }
                    gameStateList.add(gameState.toJson());
                    gameState.setEventList(new ArrayList<>());
                    Thread.sleep(10);
                    ui.repaint(gameStateList);
                    //TODO SEND DATA TO WEBSERVER
                    break;
                case 1:
                    //Game started
                    setupNewGame(content);
                    break;
                case 2:
                    //Game Ended
                    concludeGame();
                    //TODO SEND GAME FINISH
                    break;
                case 3:
                    //Player connect
                    playerMap.put(content[2], new Player(content[2], Integer.parseInt(content[3])));
                    break;
                case 4:
                    playerMap.remove(content[2]);
                    break;
                case 5:
                    Event playerKilledEvent = new PlayerKilledEvent(content[2],content[3],content[4]);
                    gameState.getEventList().add(playerKilledEvent);
                    break;
                case 6:
                    Event respawnEvent = new BasicPlayerEvent(content[2], EventType.PLAYER_RESPAWNED);
                    gameState.getEventList().add(respawnEvent);
                    break;
                case 7:
                    Player case5 = playerMap.get(content[2]);
                    case5.setTitan(true);
                    case5.setTitanClass(content[3]);

                    Event playerBecomesTitanEvent = new PlayerBecomesTitan(content[2],content[3]);
                    gameState.getEventList().add(playerBecomesTitanEvent);
                    break;
                case 8:
                    Player case6 = playerMap.get(content[2]);
                    case6.setTitan(false);
                    case6.setTitanClass(null);

                    Event titansBecomesPlayer = new BasicPlayerEvent(content[2],EventType.TITAN_BECOMES_PLAYER);
                    gameState.getEventList().add(titansBecomesPlayer);
                    break;
                case 9:
                    Player case7 = playerMap.get(content[2]);
                    case7.setPrimary(content[2]);
                    case7.setPrimary(content[3]);
                    case7.setWeapon3(content[4]);
                    break;
                case 10:
                    Player case8 = playerMap.get(content[2]);
                    case8.setWallRunning(Boolean.parseBoolean(content[3]));
                    break;
                case 11:
                    Player case9 = playerMap.get(content[2]);
                    case9.setShooting(Boolean.parseBoolean(content[3]));
                    break;
            }
        } catch (Exception ex) {
            System.out.println(inputData);
            ex.printStackTrace();
        }
    }

    private void updatePlayer(Player player, String[] content, int startingIndex) {
        player.setPosition(parseToVector(content[startingIndex++]));
        player.setRotation(parseToVector(content[startingIndex++]));
        player.setVelocity(parseToVector(content[startingIndex++]));
        player.setHealth(content[startingIndex]);
    }

    private Vector parseToVector(String input) {
        input = input.replace("<","");
        input = input.replace(">","");
        input = input.replace(" ","");

        String[] positions = input.split(",");

        return new Vector(
                Float.parseFloat(positions[0]),
                Float.parseFloat(positions[1]),
                Float.parseFloat(positions[2]));
    }

    private Date parseToDate(String input) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return parser.parse(input);
    }
}