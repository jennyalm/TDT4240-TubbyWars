package com.mygdx.tubby_wars.model.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.tubby_wars.view.Bullet;

public class PlayerComponent implements Component{

    public String playerName;
    public int health;
    public int score;
    public Texture characterBody;

    public float weaponDamage = (float)2.6;
    public TextureRegion weaponTexture;

    public TextureRegion region;

    public Array<Bullet> bullets;

}
