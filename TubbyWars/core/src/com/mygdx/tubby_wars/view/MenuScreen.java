package com.mygdx.tubby_wars.view;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.tubby_wars.TubbyWars;
import com.badlogic.gdx.audio.Music;
import com.mygdx.tubby_wars.model.Assets;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.mygdx.tubby_wars.model.MusicStateManager;
import com.badlogic.gdx.audio.Sound;

public class MenuScreen extends ScreenAdapter implements ScreenInterface {

    private TubbyWars game;
    private Engine engine;

    //Initialize textures
    private Texture logo;
    private Texture background;
    private Texture playB;
    private Texture highScoreB;
    private Texture settingsB;
    private Texture shopB;

    //Initialize  background music and sound effects
    private Music music;
    private Sound click;

    private SpriteBatch sb;
    private Stage stage;

    public MenuScreen(TubbyWars game, Engine engine){
        super();
        this.game = game;
        this.engine = engine;

        background = Assets.getTexture(Assets.mainBackground);
        logo = Assets.getTexture(Assets.logo);
        playB = Assets.getTexture(Assets.playButton);
        highScoreB = Assets.getTexture(Assets.highScoreButton);
        settingsB = Assets.getTexture(Assets.settingsButton);
        shopB = Assets.getTexture(Assets.shopScreenButton);

        this.music = game.getBackgroundMusic();
        this.game.playMusic(music);

        this.click = game.getClickSound();

        // one-time operations
        create();
    }

    public void create(){
        stage = new Stage(new ScreenViewport());
        sb = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);

        //Initialize button to get GameScreen
        final Button gameButton = new Button(new TextureRegionDrawable(new TextureRegion(playB)));
        gameButton.setSize(100, 39);
        gameButton.setPosition(Gdx.graphics.getWidth() / 2 - gameButton.getWidth() / 2 , Gdx.graphics.getHeight() / 10*3 - gameButton.getHeight() / 2);

        gameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float xpos, float ypos) {
                game.playSound(click);
                game.setScreen(new GameScreen(game, engine));
            }

        });

        //Initialize button to get to SettingsScreen
        final Button settingsButton = new Button(new TextureRegionDrawable(new TextureRegion(settingsB)));
        settingsButton.setSize(100, 39);
        settingsButton.setPosition(Gdx.graphics.getWidth()/4 - settingsButton.getWidth() / 2 , Gdx.graphics.getHeight() / 10*3 - settingsButton.getHeight() / 2);

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float xpos, float ypos) {
                game.playSound(click);
                game.setScreen(new SettingScreen(game, engine));
            }
        });

        //Initialize button to get to HighScoreScreen
        final Button highScoreButton = new Button(new TextureRegionDrawable(new TextureRegion(highScoreB)));
        highScoreButton.setSize(100, 39);
        highScoreButton.setPosition(Gdx.graphics.getWidth()/4*3 - highScoreButton.getWidth() / 2 , Gdx.graphics.getHeight() / 10*3 - highScoreButton.getHeight() / 2);

        highScoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float xpos, float ypos) {
                game.playSound(click);
                game.setScreen(new HighScoreScreen(game, engine));
            }
        });

        //Initialize button to get ShopScreen TODO:Remove later, for access befoure it is done
        final Button shopButton = new Button(new TextureRegionDrawable(new TextureRegion(shopB)));
        shopButton.setSize(100, 39);
        shopButton.setPosition(Gdx.graphics.getWidth() /20*19 - shopButton.getWidth()/2 , Gdx.graphics.getHeight() / 10*3 - shopButton.getHeight() / 2);

        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float xpos, float ypos) {
                game.playSound(click);
                game.setScreen(new ShopScreen(game, engine));
            }

        });

        stage.addActor(gameButton);
        stage.addActor(settingsButton);
        stage.addActor(highScoreButton);
        stage.addActor(shopButton); //TODO:Remove later
    }

    @Override
    public void update(float dt){
        // check for user input
        handleinput();
    }

    @Override
    public void draw(){
        sb.begin(); // Draw elements to Sprite Batch
        sb.draw(background, 0,0, TubbyWars.WIDTH, TubbyWars.HEIGHT); //Draws background photo
        sb.draw(logo, Gdx.graphics.getWidth()/2 - logo.getWidth()/2,
                Gdx.graphics.getHeight()/2, 300,150); //Draws logo
        sb.end();

        stage.draw();
    }

    @Override
    public void handleinput(){
    }

    @Override
    public void render(float dt){
        update(dt);
        draw();
    }

    @Override
    public void dispose(){
        super.dispose();
    }
}