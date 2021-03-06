package edu.dirtybit.battlechat.model;

import java.awt.Point;
import java.util.ArrayList;

public class Ship {
    private int x;
    private int y;
    private Rotation rotation;
    private ShipType shiptype;
    private ArrayList<Point> cells;

    public Ship(ShipType shiptype) {
        this(0, 0, Rotation.HORIZONTAL, shiptype);
    }

    public Ship(int x, int y, Rotation rotation, ShipType shiptype) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.shiptype = shiptype;
        this.cells = new ArrayList<>();
        this.resetCells();
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
        this.resetCells();
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
        this.resetCells();
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
        this.resetCells();
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
        this.resetCells();
    }

    public ShipType getShiptype() {
        return shiptype;
    }

    /*public void setShiptype(ShipType shiptype) {
        this.shiptype = shiptype;
    }*/

    public boolean isAt(int x, int y) {
        boolean value = false;
        for (int i = 0; i < this.cells.size(); i++) {
            Point c = this.cells.get(i);
            if (c.getX() == x && c.getY() == y) {
                value = true;
                i = this.cells.size();
            }
        }
        return value;
    }

    public void removeCell(int x, int y)
    {
        for (int i = 0; i < this.cells.size(); i++) {
            Point c = this.cells.get(i);
            if (c.getX() == x && c.getY() == y) {
                this.cells.remove(i);
                i = this.cells.size();
            }
        }
    }

    public int getHealth() {
        return this.cells.size();
    }

    public void resetCells() {
        this.cells = new ArrayList<>();

        int x = this.x;
        int y = this.y;
        for (int i = 0; i < this.shiptype.getLength(); i++) {
            this.cells.add(new Point(x, y));
            if (this.rotation == Rotation.HORIZONTAL) {
                x++;
            } else {
                y++;
            }
        }
    }
}
