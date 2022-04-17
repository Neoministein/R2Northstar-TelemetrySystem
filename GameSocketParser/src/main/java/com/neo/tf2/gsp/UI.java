package com.neo.tf2.gsp;


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
    private static final String KODAI_LOCATION = "F:\\00-Data\\04-Pictrues\\0x98f6cb7c629836b0.png";

    private UIBackGroundComponent uiBackGroundComponent = new UIBackGroundComponent();
    private UIDrawComponent uiDrawComponent = new UIDrawComponent();

    private final BufferedImage image;
    private final BufferedImage player;


    public UI(int mapSizeX, int maxSizeZ) {
        try {
            image = ImageIO.read(new File(KODAI_LOCATION));
            player = ImageIO.read(new File("F:\\00-Data\\04-Pictrues\\player.png"));
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

    public void repaint() {
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
            /*
            //setColor to  surface background
            //g.clipRect(0,0,1000,1000);
            uiBackGroundComponent.paint(uiBackGroundComponent.getGraphics());
            int i = 0;
            for (PlayerGameState positionData: positionDataList) {
                i++;
                g.setColor(Color.BLACK);
                if (positionData.getPlayer().getTeam().equals("1")) {
                    g.setColor(Color.CYAN);
                }
                if (positionData.getPlayer().getTeam().equals("2")) {
                    g.setColor(Color.RED);
                }
                if (positionData.getPlayer().getTeam().equals("3")) {
                    g.setColor(Color.GREEN);
                }
                if (i < 20) {
                    g.fillRect(
                            Math.round((positionData.getPosition().getX() + 5325) / 10.1f),
                            Math.round((positionData.getPosition().getY() * -1 + 5700) / 10.1f),
                            5,
                            5);
                }
/*


            }
            */
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
