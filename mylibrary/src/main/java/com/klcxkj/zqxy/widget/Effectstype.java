package com.klcxkj.zqxy.widget;

import com.klcxkj.zqxy.widget.effects.BaseEffects;
import com.klcxkj.zqxy.widget.effects.FadeIn;
import com.klcxkj.zqxy.widget.effects.Fall;
import com.klcxkj.zqxy.widget.effects.FlipH;
import com.klcxkj.zqxy.widget.effects.FlipV;
import com.klcxkj.zqxy.widget.effects.NewsPaper;
import com.klcxkj.zqxy.widget.effects.RotateBottom;
import com.klcxkj.zqxy.widget.effects.RotateLeft;
import com.klcxkj.zqxy.widget.effects.Shake;
import com.klcxkj.zqxy.widget.effects.SideFall;
import com.klcxkj.zqxy.widget.effects.SlideBottom;
import com.klcxkj.zqxy.widget.effects.SlideLeft;
import com.klcxkj.zqxy.widget.effects.SlideRight;
import com.klcxkj.zqxy.widget.effects.SlideTop;
import com.klcxkj.zqxy.widget.effects.Slit;

/**
 * Created by lee on 2014/7/30.
 */
public enum Effectstype {

	Fadein(FadeIn.class), Slideleft(SlideLeft.class), Slidetop(SlideTop.class), SlideBottom(
			SlideBottom.class), Slideright(SlideRight.class), Fall(Fall.class), Newspager(
			NewsPaper.class), Fliph(FlipH.class), Flipv(FlipV.class), RotateBottom(
			RotateBottom.class), RotateLeft(RotateLeft.class), Slit(Slit.class), Shake(
			Shake.class), Sidefill(SideFall.class);
	private Class<? extends BaseEffects> effectsClazz;

	private Effectstype(Class<? extends BaseEffects> mclass) {
		effectsClazz = mclass;
	}

	public BaseEffects getAnimator() {
		BaseEffects bEffects = null;
		try {
			bEffects = effectsClazz.newInstance();
		} catch (ClassCastException e) {
			throw new Error("Can not init animatorClazz instance");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new Error("Can not init animatorClazz instance");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new Error("Can not init animatorClazz instance");
		}
		return bEffects;
	}
}
