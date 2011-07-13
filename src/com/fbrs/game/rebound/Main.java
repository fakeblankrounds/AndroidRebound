package com.fbrs.game.rebound;

import java.io.IOException;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.ZoomCamera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.detector.PinchZoomDetector;
import org.anddev.andengine.extension.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ScrollDetector;
import org.anddev.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.anddev.andengine.input.touch.detector.SurfaceScrollDetector;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.widget.Toast;

import com.fbrs.game.rebound.render.AnimationBuilder;
import com.fbrs.game.rebound.render.TextureLoad;
import com.fbrs.rebound.abstraction.AnimationFactory;
import com.fbrs.rebound.abstraction.TextureLoader;
import com.fbrs.rebound.map.MapLoad;

public class Main extends BaseGameActivity implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener{

	public static Main main;

	public static final int CAM_W = 800;
	public static final int CAM_H = 480;  
	public static Engine mEngine;
	public static Scene scene;
	public static HUD hud;
	public static ZoomCamera mZoomCamera;
	private SurfaceScrollDetector mScrollDetector;
	private PinchZoomDetector mPinchZoomDetector;
	private float mPinchZoomStartedCameraZoomFactor;

	@Override
	public Engine onLoadEngine() {
		Main.mZoomCamera = new ZoomCamera(0, 0, CAM_W, CAM_H);
		
		hud = new HUD();
		Main.mZoomCamera.setHUD(hud);

		mEngine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAM_W, CAM_H), Main.mZoomCamera).setNeedsMusic(true));

		main = this;

		try {
			if(MultiTouch.isSupported(this)) {
				mEngine.setTouchController(new MultiTouchController());
			} else {
				Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(No PinchZoom is possible!)", Toast.LENGTH_LONG).show();
			}
		} catch (final MultiTouchException e) {
			Toast.makeText(this, "Sorry your Android Version does NOT support MultiTouch!\n\n(No PinchZoom is possible!)", Toast.LENGTH_LONG).show();
		}

		return mEngine;
	}

	@Override
	public void onLoadResources() {
		//load each sprite img.

		TextureLoader.setTextureLoader(new TextureLoad());
		AnimationFactory.SetImplementer(new AnimationBuilder());
	}

	@Override
	public Scene onLoadScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		scene = new Scene(1);

		scene.setBackground(new ColorBackground(0, 0, 0.35f));

		this.mScrollDetector = new SurfaceScrollDetector(this);
		if(MultiTouch.isSupportedByAndroidVersion()) {
			try {
				this.mPinchZoomDetector = new PinchZoomDetector(this);
			} catch (final MultiTouchException e) {
				this.mPinchZoomDetector = null;
			}
		} else {
			this.mPinchZoomDetector = null;
		}

		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingEnabled(true);

		return scene;
	}

	@Override
	public void onLoadComplete() {

		try {
			MapLoad.ParseFile(Main.main.getAssets().open(
					"scripts/scripts.tct"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		MapLoad.map.RenderMap();
		Main.mZoomCamera.setBounds(0, (MapLoad.map.MapDimX +1)*128, 0, (MapLoad.map.MapDimY + 1)*128);
		Main.mZoomCamera.setBoundsEnabled(true);
		
		//AnimationFactory.StartNewAnimation("commandcenter", new LPoint(0,0), new LPoint(400,400), 60, AnimationType.explonential);
	}
	
	@Override
    public void onScroll(final ScrollDetector pScollDetector, final TouchEvent pTouchEvent, final float pDistanceX, final float pDistanceY) {
            final float zoomFactor = Main.mZoomCamera.getZoomFactor();
            Main.mZoomCamera.offsetCenter(-pDistanceX / (zoomFactor / 2), -pDistanceY / (zoomFactor/2));
            //gamestates.sprite.setPosition(this.mZoomCamera.)
            
            
    }

    @Override
    public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
            this.mPinchZoomStartedCameraZoomFactor = Main.mZoomCamera.getZoomFactor();
    }
    
    private final static float MAX_ZOOM = 0.2f;

    @Override
    public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
            if(mPinchZoomStartedCameraZoomFactor * pZoomFactor > MAX_ZOOM)
            	Main.mZoomCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
            else
            	Main.mZoomCamera.setZoomFactor(MAX_ZOOM);
           
    }

    @Override
    public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
    	if(mPinchZoomStartedCameraZoomFactor * pZoomFactor > MAX_ZOOM)
        	Main.mZoomCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
        else
        	Main.mZoomCamera.setZoomFactor(MAX_ZOOM);
            
    }


    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
            if(this.mPinchZoomDetector != null) {
                    this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

                    if(this.mPinchZoomDetector.isZooming()) {
                            this.mScrollDetector.setEnabled(false);
                    } else {
                            if(pSceneTouchEvent.isActionDown()) {
                                    this.mScrollDetector.setEnabled(true);
                            }
                            this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
                    }
            } else {
                    this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
            }

            return true;
    }
    
    @Override
    public void onBackPressed()
    {
    	super.onBackPressed();
    	this.finish();
    }
    
    @Override
	public void onStart()
	{
		super.onStart();
		
		TextureLoader.ReloadTextures();
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		TextureLoader.ReloadTextures();
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		TextureLoader.ReloadTextures();
	}


}