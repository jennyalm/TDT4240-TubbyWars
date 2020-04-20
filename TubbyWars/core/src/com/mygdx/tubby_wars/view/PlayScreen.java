package com.mygdx.tubby_wars.view;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.tubby_wars.TubbyWars;
import com.mygdx.tubby_wars.controller.CourseSystem;
import com.mygdx.tubby_wars.controller.InputProcessor;
import com.mygdx.tubby_wars.controller.Physics;
import com.mygdx.tubby_wars.controller.PlayerSystem;
import com.mygdx.tubby_wars.model.Assets;
import com.mygdx.tubby_wars.model.B2WorldCreator;
import com.mygdx.tubby_wars.model.CollisionListener;
import com.mygdx.tubby_wars.model.ControllerLogic;
import com.mygdx.tubby_wars.model.PlayerModel;

import java.util.List;


public class PlayScreen implements Screen {


    public OrthographicCamera gameCam;
    public Viewport viewPort;
    public TubbyWars game;
    public World world;
    public PlayerModel player1, player2;

    //MAP
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Box2DDebugRenderer b2dr;

    // MAP PROPERTIES
    private int mapPixelWidth;
    private int mapWidth;
    private int tilePixelWidth;

    private TrajectoryActor trajectoryActor;
    public Physics physics;
    private Stage stage;

    // HUD
    private Hud hud;

    private InputMultiplexer inputMultiplexer;
    public static TextureAtlas atlas;

    public float position_player1, position_player2;


    private Texture settingsB;

    // ASHLEY
    private Engine engine;
    private List<Entity> players;


    public PlayScreen(TubbyWars game, Engine engine, List<Entity> players) {
        this.game = game;

        this.engine = engine;
        this.players = players;

        gameCam = new OrthographicCamera(TubbyWars.V_WIDTH, TubbyWars.V_HEIGHT);
        viewPort = new StretchViewport(TubbyWars.V_WIDTH, TubbyWars.V_HEIGHT, gameCam);
        viewPort.apply();
        gameCam.position.set(viewPort.getWorldWidth() / 2, viewPort.getWorldHeight() / 2, 0);
        //gameCam.position.set(viewPort.getWorldWidth() / 2, viewPort.getWorldHeight() / 2, 0);

        gameCam.update();

        // INITIALIZES NEW WORLD AND STAGE
        world = new World(new Vector2(0, -9.81f), true);
        stage = new Stage();

        // INITIALIZES PHYSICS AND THE TRAJECTORYACTOR IS ADDED TO THE STAGE.
        physics = new Physics();
        trajectoryActor = new TrajectoryActor(game, physics);
        stage.addActor(trajectoryActor);

        // LOADS THE MAP
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map3.tmx");
        mapRenderer =  new OrthogonalTiledMapRenderer(map, 0.01f);
        b2dr = new Box2DDebugRenderer();

        // MAP PROPERTIES
        MapProperties properties = map.getProperties();
        mapWidth = properties.get("width", Integer.class);
        tilePixelWidth = properties.get("tilewidth", Integer.class);
        mapPixelWidth = mapWidth * tilePixelWidth;

        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        // ADDS THE PLAYERS
        player1 = new PlayerOne(world, game,viewPort.getWorldWidth() / 2  , 0.64f, players.get(0), engine);
        player2 = new PlayerTwo(world, game, viewPort.getWorldWidth() / 2 + 3f , 0.64f, players.get(1), engine);
        // player2 = new PlayerTwo(world, game, mapPixelWidth/100f - viewPort.getWorldWidth() / 2 , 0.64f, players.get(1), engine);

        physics.setPlayer(player1);
        // LOADS THE PACK FILE WITH INTO AN ATLAS WHERE ALL THE CHARACTER SPRITES ARE

        hud = new Hud(game.batch, players);


        // contact listener
        world.setContactListener(new CollisionListener());


        // TODO DENNE FIKSER SETTINGSKNAPPEN, HUK AV DENNE NÅR DEN ER KLAR
        // createSettingsButton();


        ControllerLogic.currentGame = this;
    }



    public void setGameCamPosition(){
        if(ControllerLogic.isPlayersTurn){
            gameCam.position.x = player2.b2Body.getPosition().x;
        }
        else{
            gameCam.position.x = player1.b2Body.getPosition().x;
        }
    }
    public TextureAtlas getAtlas() {
        return atlas;
    }

    public void changeTurn(){
        if(ControllerLogic.isPlayersTurn){
            physics.setPlayer(player2);
        }
        else{
            physics.setPlayer(player1);
        }
    }

    /**
     * Called when this screen becomes the current screen for a
     */
    @Override
    public void show() {
        // TODO ADD B2WORLDCREATOR


        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new InputProcessor(physics));
        // TODO ADD THE STAGES TO THE MULTIPLEXER
        new B2WorldCreator(world, map);
        Gdx.input.setInputProcessor(inputMultiplexer);

        // SET PLAYER1's TURN.

        position_player2 = player2.b2Body.getPosition().x;
        position_player1 = player1.b2Body.getPosition().x;
        System.out.println(player2.b2Body.getPosition());
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        update(delta);
        //MAP RENDERING
        mapRenderer.render();
        mapRenderer.setView(gameCam);

       // b2dr.render(world, gameCam.combined);

        // PLAYER RENDERING
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player1.draw(game.batch);
        player2.draw(game.batch);
        game.batch.end();

        // STAGE RENDERING
        game.batch.setProjectionMatrix(stage.getCamera().combined);
        stage.draw();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }

    public void update(float dt) {
        world.step(1 / 60f, 6, 2);
        gameCam.update();
        player1.update(dt);
        player2.update(dt);
        hud.update(dt);
        if(ControllerLogic.isPlayersTurn){
            physics.setPlayer(player2);
            //System.out.println( player2.b2Body.getPosition());
        }
        else{
            physics.setPlayer(player1);
        }

        //TODO FIX CORRECT BEHAVIOUR
        if(!player1.isPlayersTurn()) {

            if ((player1.getBullet() != null && player1.getBullet().b2Body.getPosition().x <= mapPixelWidth / 100f - gameCam.viewportWidth / 2) && player1.getBullet().b2Body.getPosition().x >= gameCam.viewportWidth / 2) {
                gameCam.position.x = player1.getBullet().b2Body.getPosition().x;

            } else if (gameCam.position.x > player1.b2Body.getPosition().x && gameCam.position.x < player2.b2Body.getPosition().x && player1.getBullet() == null) {
                gameCam.position.x = Math.max(player1.b2Body.getPosition().x, gameCam.viewportWidth / 2);
            }


        }
        else if(player2.isPlayersTurn()){
            //gameCam.position.x = mapPixelWidth / 100f - gameCam.viewportWidth / 2f;

            if ((player2.getBullet() != null && player2.getBullet().b2Body.getPosition().x <= mapPixelWidth / 100f - gameCam.viewportWidth / 2) && player2.getBullet().b2Body.getPosition().x >= gameCam.viewportWidth / 2) {
                gameCam.position.x = player2.getBullet().b2Body.getPosition().x ;
            }
            else if (gameCam.position.x > player1.b2Body.getPosition().x && gameCam.position.x < player2.b2Body.getPosition().x &&  player2.getBullet() == null ) {
                gameCam.position.x = Math.min(player2.b2Body.getPosition().x, mapPixelWidth / 100f - gameCam.viewportWidth / 2);

            }
        }

        if(isRoundOver()){
            ControllerLogic.roundCount ++;

            if (ControllerLogic.roundCount == 3) {
                game.setScreen(new HighScoreScreen(game, engine));
            }
            else {
                prepareForNextRound();
                game.setScreen(new ShopScreen(game, engine, players));
            }
        }
    }

    //TODO RESET THE NEXT ROUND CORRECTLY, THIS IS JUST A TEST
    //TODO: Use ControllerLogic.roundCount to choose the right map (Changes for each round)
    private void prepareForNextRound(){
        player1 = new PlayerOne(world, game,viewPort.getWorldWidth() / 2  , 0.64f, players.get(0), engine);
        player2 = new PlayerTwo(world, game, viewPort.getWorldWidth() / 2 + 3f , 0.64f, players.get(1), engine);
        // player2 = new PlayerTwo(world, game, mapPixelWidth/100f - viewPort.getWorldWidth() / 2 , 0.64f, players.get(1), engine);

        engine.getSystem(PlayerSystem.class).setHealth(players.get(0),150);
        engine.getSystem(PlayerSystem.class).setHealth(players.get(1),150);

        if (ControllerLogic.roundCount == 1) {
            //TODO: Set first map
        }
        else if (ControllerLogic.roundCount == 2) {
            //TODO: Set second map
        }
        else {
            //TODO: Set third map
        }
    }

    private boolean isRoundOver(){
        if(engine.getSystem(PlayerSystem.class).getHealth(players.get(0)) < 0
                || engine.getSystem(PlayerSystem.class).getHealth(players.get(1)) < 0){
            return true;
        }
        return false;
    }

    // TODO fix so that the settings button is clickable when playing, now the trajectory actor takes priority
    public void createSettingsButton(){
        settingsB = Assets.getTexture(Assets.pauseGameButton);

        //Initialize button to get to SettingsScreen
        final Button settingsButton = new Button(new TextureRegionDrawable(new TextureRegion(settingsB)));
        settingsButton.setSize(50, 50);
        settingsButton.setPosition(Gdx.graphics.getWidth()*85f/90f - settingsButton.getWidth() / 2f , Gdx.graphics.getHeight()* 75f/90f - settingsButton.getHeight() / 2f);

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float xpos, float ypos) {
                // TODO Vi bør ikke lage nye screens hele tiden tror jeg, men heller ha de lagret,
                //  kan bli vanskelig å komme tilbake til playScreen hvis ikke.
                game.setScreen(new SettingScreen(game, engine));
            }
        });

        stage.addActor(settingsButton);
    }


    @Override
    public void resize(int width, int height) {
        viewPort.update(width, height);
        gameCam.update();
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }


    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

}
