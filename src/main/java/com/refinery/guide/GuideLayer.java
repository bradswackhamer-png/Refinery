package com.refinery.guide;

import java.util.Map;

public record GuideLayer(
        int layerIndex,
        String layerName,
        String[] grid, // Each string is a row from front to back or back to front
        Map<Character, LayerSymbol> legend
) {
    public int getWidth() {
        return grid.length > 0 ? grid[0].length() : 0;
    }

    public int getDepth() {
        return grid.length;
    }

    public char getAt(int x, int z) {
        if (z < 0 || z >= grid.length) return ' ';
        String row = grid[z];
        if (x < 0 || x >= row.length()) return ' ';
        return row.charAt(x);
    }
}
