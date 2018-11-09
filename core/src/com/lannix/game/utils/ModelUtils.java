package com.lannix.game.utils;

import com.badlogic.gdx.graphics.Color;

public class ModelUtils {

    //Atoms Colors
    public static final Color ATOM_COLOR = Color.WHITE;
    public static final Color ATOM_EVENT_COLOR = Color.YELLOW;
    public static final Color ANTIMATTER_DYNAMIC = new Color(0.4f, 0.15f,0.55f,1f);
    public static final Color ANTIMATTER_STATIC = Color.FOREST;
    public static final Color RADIATION = new Color(0.55f, 0.0f,0.18f,1f);

    //Player
    public static volatile float VICTORY_PLAYER_MASS = 5f;
    public static final float PLAYER_ADDITION_VELOCITY = 1f;
    public static final float PLAYER_ACCELERATION = 2f;
    public static final float PLAYER_MAX_VEL = 15f;

    //Utils for world bodies
    public static final float DENSITY = 1f;
    public static final float ATOM_RADIUS = 1f;
    public static final float ANTIMATTER_RADIUS = 0.8f;
    public static final float RADIATION_RADIUS = 4f;
    public static final float MOLE_HOLE_RADIUS = 15f;
    public static final float ATOM_EVENT_RADIUS = 2f;
    public static final float PLAYER_BOOM_VELOCITY = 25f;

    //DistanceJoint
    public static final float JOINT_LENGTH = 7f;
    public static final float DAMPING_RATIO = 0.5f;
    public static final float FREQUENCY_HZ = 5f;

    //Random constants
    public static final float TIME_RAND_STEP = 1.8f;
    public static final float MIN_RAND_RADIUS = 20f;
    public static final float MAX_RAND_RADIUS = 70f;
    public static final float MAX_RANDOM_VELOCITY = 15f;

    //Events constants
    public static final float SMALL_BLAST_POWER = 1000f;

    public static final int MAX_COLUMNS_ROWS = 15;
    public static final float DIST_BETWEEN_ANTIMATTERS = 8f;

    public static final float DISTRUCTION_BIG_BLAST_RADIUS = 10f;
    public static final float BIG_BLAST_RADIUS = 25f;
    public static final float BIG_BLAST_POWER = 20000f;

    public static final float CIRCLE_DEF_RADIUS = 30f;
    public static final float NUMBER_OF_ATOMS = 10f;
}
