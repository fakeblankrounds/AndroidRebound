package com.fbrs.game.rebound.render;

import org.anddev.andengine.engine.handler.IUpdateHandler;

import com.fbrs.game.rebound.Main;

public class Animation implements IUpdateHandler{
	
	private int s_x, s_y, delta_x,delta_y, t_frames, pass_frames;
	private String sprite;
	
	public Animation(String texture, int start_x, int start_y, int finish_x, int finish_y, int frames, boolean existing)
	{
		s_x = start_x;
		s_y = start_y;
		delta_x = (finish_x - start_x) / frames;
		delta_y = (finish_y - start_x) / frames;
		t_frames = frames;
		pass_frames = 0;
		if(existing == false)
			sprite = TextureLoad.newSprite(start_x, start_y, 0, 1, texture, null);
		else
			sprite = texture;
	}	
	
	public int getX()
	{
		return s_x + delta_x * pass_frames;
	}
	
	public int getY()
	{
		return s_y + delta_y * pass_frames;
	}
	
	public void FrameUp()
	{
		pass_frames++;
	}
	
	public boolean Continue()
	{
		if(t_frames <= pass_frames)
			return false;
		return true;
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		
		TextureLoad.moveSprite(sprite, getX(), getY());
		FrameUp();
		if(!Continue())
		{
			Main.scene.unregisterUpdateHandler(this);
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
