package com.netcracker.routebuilder.demo.visualizer;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to visualize potential maps
 * TODO: доделать, ЧЕРНОВОЙ ТЕСТОВЫЙ ВАРИАНТ
 *
 * @author Kirill.Vakhrushev
 */
@Slf4j
public class DrawMap {

    private class Grid extends JPanel {

        private List<Point> fillCells;
        private List<Color> ColorCells;

        Grid() {
            fillCells = new ArrayList<>();
            ColorCells = new ArrayList<>();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(new ImageIcon("auxiliary-images/background2.PNG").getImage(), 2, 2, 381 * 2, 401 * 2, new Color(0, 0, 0), null);

            for (int i = 0; i < fillCells.size(); i++) {
                int cellX = 2 + (fillCells.get(i).x * 2);
                int cellY = 2 + (fillCells.get(i).y * 2);
                g.setColor(ColorCells.get(i));
                g.fillRect(cellX, cellY, 2, 2);
            }
            g.setColor(Color.BLACK);
            g.drawRect(2, 2, 381 * 2, 401 * 2);
        }

        void fillCell(int x, int y, int value) {
            fillCells.add(new Point(y, x));
            ColorCells.add(new Color(value, 0, 0, 210));
        }

    }

    /**
     * Method to create a window with a drawn potential map
     *
     * @param map potential map
     */
    public void draw(int[][] map) {

        double maxValue = 0;
        for (int i = 0; i < 401; i++) {
            for (int j = 0; j < 381; j++) {
                maxValue = Math.max(maxValue, map[i][j]);
            }
        }

        final double biggestValue = maxValue;

        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignore) {
            }

            Grid grid = new Grid();
            JFrame window = new JFrame();
            window.setSize(840, 900);


            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.add(grid);
            window.setVisible(true);

            for (int i = 0; i < 401; i++) {
                for (int j = 0; j < 381; j++) {
                    grid.fillCell(i, j, (int) ((map[i][j] / biggestValue) * 255d));
                }
            }

            grid.repaint();
        });
    }
}
