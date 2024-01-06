package xyz.immortius.advent2022.day9;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.util.ArrayList;
import java.util.List;

public class RopeSimulation {
    private List<Vector2i> knots = new ArrayList<>();

    public RopeSimulation(int numKnots) {
        for (int i = 0; i < numKnots; i++) {
            knots.add(new Vector2i(0, 0));
        }
    }

    public Vector2ic getHeadPos() {
        return new Vector2i(knots.get(0));
    }

    public Vector2ic getTailPos() {
        return new Vector2i(knots.get(knots.size() - 1));
    }

    public void moveHead(Direction dir) {
        Vector2i lastKnot = knots.get(0).add(dir.toVector());
        for (int i = 1; i < knots.size(); i++) {
            Vector2i currentKnot = knots.get(i);
            int distance = Math.max(Math.abs(lastKnot.x - currentKnot.x), Math.abs(lastKnot.y - currentKnot.y));
            if (distance > 1) {
                Vector2i diff = new Vector2i(Integer.compare(lastKnot.x, currentKnot.x), Integer.compare(lastKnot.y, currentKnot.y));
                currentKnot.add(diff);
                lastKnot = currentKnot;
            } else {
                break;
            }
        }
    }
}
