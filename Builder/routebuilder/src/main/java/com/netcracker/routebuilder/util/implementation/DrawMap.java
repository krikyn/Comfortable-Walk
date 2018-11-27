package com.netcracker.routebuilder.util.implementation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DrawMap {
    public static class Grid extends JPanel {

        private List<Point> fillCells;

        public Grid() {
            fillCells = new ArrayList<>(25);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Point fillCell : fillCells) {
                int cellX = 10 + (fillCell.x * 10);
                int cellY = 10 + (fillCell.y * 10);
                g.setColor(Color.RED);
                g.fillRect(cellX, cellY, 5, 5);
            }
            g.setColor(Color.BLACK);
            g.drawRect(10, 10, 381*2, 401*2);

        }

        public void fillCell(int x, int y) {
            fillCells.add(new Point(x, y));
            repaint();
        }

    }

    public void draw() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                Grid grid = new Grid();
                JFrame window = new JFrame();
                window.setSize(840, 560);
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.add(grid);
                window.setVisible(true);
                grid.fillCell(0, 0);
                grid.fillCell(79, 0);
                grid.fillCell(0, 49);
                grid.fillCell(79, 49);
                grid.fillCell(39, 24);
            }
        });
    }
}
