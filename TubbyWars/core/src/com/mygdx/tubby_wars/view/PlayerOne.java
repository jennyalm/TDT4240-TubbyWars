package com.mygdx.tubby_wars.view;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.tubby_wars.TubbyWars;
import com.mygdx.tubby_wars.controller.PlayerSystem;
import com.mygdx.tubby_wars.model.ControllerLogic;
import com.mygdx.tubby_wars.model.PlayerModel;
import com.mygdx.tubby_wars.model.components.PlayerComponent;


public class PlayerOne extends PlayerModel {


    public TextureRegion region;

    public Weapon weapon;

    private Array<Bullet> bullets;


    public Healthbar healthbar;

    public boolean timeToRedefine;


    // ASHLEY
    private Entity playerEntity;
    private ComponentMapper<PlayerComponent> pm;

    /**
     * Creates an uninitialized sprite. The sprite will need a texture region and bounds set before it can be drawn.
     */
    public PlayerOne(World world, TubbyWars game, float posX, float posY, Entity playerEntity, Engine engine) {
        super(world, game, posX, posY, playerEntity, engine);


        bullets = new Array<>();

        definePlayer();

        timeToRedefine = false;
        Texture weaponTexture = engine.getSystem(PlayerSystem.class).getWeaponTexture(playerEntity);
        weapon = new Weapon(b2Body,-0.3f, 0.1f, weaponTexture);
        Texture texture = engine.getSystem(PlayerSystem.class).getTexture(playerEntity);
        region = new TextureRegion(texture, 0,0,texture.getWidth(),texture.getHeight());

        // width og height var 0.5f og 0.7f før
        setBounds(0, 0, 1f, 1.4f);
        setRegion(region);

        healthbar = new Healthbar(b2Body, playerEntity);



    }

    /***
     * Draws the Player with the superclass draw method
     * as well as drawing the bullet(s).
     * @param batch
     */
    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        for(Bullet bullet: bullets){
            if(showBullet){
                bullet.draw(game.batch);
            }
        }
        weapon.draw(game.batch);
        healthbar.draw(game.batch);

    }

    @Override
    public void update(float dt) {
        if(timeToRedefine){
            redefinePlayer();

        }
        if(bullets.isEmpty() && !super.isPlayersTurn()){
            addBullet();
        }
        for(Bullet b: bullets){

            b.update(dt);
            if(b.isDestroyed()){
                bullets.removeValue(b, true);

            }
        }
        setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
        weapon.update(dt);
        healthbar.update(dt);

    }

    @Override
    public void redefinePlayer(){
        System.out.println("player redefined");
        world.destroyBody(b2Body);
        definePlayer();
        timeToRedefine = false;
        addBullet();
        getBullet().destroyBullet();



    }

    @Override
    public void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(this.posX, this.posY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = this.world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();

        // var 0.25
        shape.setRadius(0.7f);
        fdef.shape = shape;
        fdef.friction = 0.8f;
        fdef.filter.categoryBits = ControllerLogic.BULLET_1 | ControllerLogic.PLAYER_1;
        fdef.filter.maskBits = ControllerLogic.BULLET_2 | ControllerLogic.GROUND_BIT;
        b2Body.createFixture(fdef).setUserData(this);
    }

    @Override
    public Bullet getBullet() {
        if(bullets.isEmpty()){
            return null;
        }
        return bullets.get(0);
    }

    @Override
    public void addBullet() {
        Bullet bullet = new Bullet(b2Body.getPosition().x, b2Body.getPosition().y, world, false);
        bullets.add(bullet);
        hideBullet();
    }

    @Override
    public void setRedefine() {
        timeToRedefine = true;

    }
}
