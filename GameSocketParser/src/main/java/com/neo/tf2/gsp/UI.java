package com.neo.tf2.gsp;


import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class UI {

    //private static final String KODAI_LOCATION = "F:\\00-Data\\04-Pictrues\\Kodai2.0.png";
    private static final String KODAI_LOCATION = System.getenv("user.dir") + "\\0x98f6cb7c629836b0.png";

    private UIBackGroundComponent uiBackGroundComponent = new UIBackGroundComponent();
    private UIDrawComponent uiDrawComponent = new UIDrawComponent();

    private final BufferedImage image;

    private List<JSONObject> gameStateList = Collections.emptyList();

    public UI(int mapSizeX, int maxSizeZ) {
        try {
            image = ImageIO.read(new File(KODAI_LOCATION));
        } catch (IOException e) {
            throw new RuntimeException();
        }


        JFrame jFrame = new JFrame();
        JLayeredPane forLayers = new JLayeredPane();
        forLayers.setSize(2000, 2000);
        //jFrame.getContentPane().add(uiBackGroundComponent);
        //jFrame.getContentPane().add(uiDrawComponent);


        jFrame.getContentPane().add(forLayers);
        uiDrawComponent.setBackground(new Color(0,0,0,0));
        uiDrawComponent.setOpaque(false);
        uiBackGroundComponent.setBackground(new Color(0,0,0,0));
        uiBackGroundComponent.setOpaque(false);
        forLayers.setBackground(new Color(0,0,0,0));
        forLayers.setOpaque(false);
        forLayers.add(uiBackGroundComponent, JLayeredPane.PALETTE_LAYER);
        forLayers.add(uiDrawComponent, JLayeredPane.POPUP_LAYER);



        uiBackGroundComponent.setSize(2000,2000);
        uiDrawComponent.setSize(2000,2000);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(mapSizeX, maxSizeZ);
        jFrame.setVisible(true);

        uiBackGroundComponent.paint(uiBackGroundComponent.getGraphics());





    }

    public void repaint(List<JSONObject> gameStateList) {
        this.gameStateList = gameStateList;
        uiDrawComponent.paint(uiDrawComponent.getGraphics());
    }

    class UIBackGroundComponent extends JPanel {

        @Override
        public void paint(Graphics g) {
            g.drawImage(image, 0, 0, null);
        }

    }

    class UIDrawComponent extends JPanel {

        @Override
        public void paint(Graphics g) {
            g.clipRect(0,0,1000,1000);
            int startingPoint = Math.max(gameStateList.size() - 20, 0);
            System.out.println(startingPoint);
            if (!gameStateList.isEmpty()) {
               List<JSONObject> a = gameStateList.subList(startingPoint , gameStateList.size());
                for (JSONObject gameState : a) {

                    for (int i = 0; i < gameState.getJSONArray("players").length(); i++) {
                        JSONObject player =  gameState.getJSONArray("players").getJSONObject(i);
                        if (player.getInt("team") == 2) {
                            g.setColor(Color.RED);
                        }
                        if (player.getInt("team") == 3) {
                            g.setColor(Color.GREEN);
                        }
                        int x = player.getJSONObject("position").getInt("x");
                        int y = player.getJSONObject("position").getInt("y");


                        g.fillRect(
                                Math.round((x + 5325) / 10.1f),
                                Math.round((y * -1 + 5700) / 10.1f),
                                5,
                                5);
                    }
                }
                System.out.print("");
            }
        }
    }
}

                    /*g.drawImage(
                            player,
                            Math.round((positionData.getPosition().getX() + 5325) / 10.1f),
                            Math.round((positionData.getPosition().getY() * -1 + 5700) / 10.1f),
                            10,
                            10,
                            null);

                     */
