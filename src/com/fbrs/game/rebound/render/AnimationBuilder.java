package com.fbrs.game.rebound.render;

import com.fbrs.game.rebound.Main;
import com.fbrs.rebound.abstraction.AnimationFactory;
import com.fbrs.utils.math.LPoint;

public class AnimationBuilder extends AnimationFactory{
	
	@Override
	public void newAnimation(String texture, LPoint a, LPoint b, int frames)
	{
		Animation anim = new Animation(texture, (int)a.X, (int)a.Y, (int)b.X, (int)b.Y, frames, false);
		Main.scene.registerUpdateHandler(anim);
	}

	@Override
	public void existingAnimation(String texture, LPoint a, LPoint b, int frames) {
		Animation anim = new Animation(texture, (int)a.X, (int)a.Y, (int)b.X, (int)b.Y, frames, true);
		Main.scene.registerUpdateHandler(anim);
		
	}
	
	

}
