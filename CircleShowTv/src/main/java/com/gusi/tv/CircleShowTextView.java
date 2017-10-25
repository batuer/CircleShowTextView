package com.gusi.tv;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

/**
 * 作者：${ylw} on 2017-10-25 10:37
 */
public class CircleShowTextView extends TextView {
  private AnimationSet mInAnim;
  private AnimationSet mOutAnim;
  private CharSequence[] mTexts;
  private int mTimeOut;
  private boolean mIsShow = false;
  private boolean mIsStop = false;
  private int mPosition;

  public CircleShowTextView(Context context) {
    this(context, null);
  }

  public CircleShowTextView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircleShowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public CircleShowTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {

    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleShowTextView);
    int standingTime = array.getInteger(R.styleable.CircleShowTextView_standing_time, 3000);
    mTexts = array.getTextArray(R.styleable.CircleShowTextView_texts);
    int animTime = array.getInteger(R.styleable.CircleShowTextView_anim_time,
        getResources().getInteger(android.R.integer.config_longAnimTime));
    array.recycle();

    int relativeToSelf = Animation.RELATIVE_TO_SELF;
    Animation inTranAnim =
        new TranslateAnimation(relativeToSelf, 0, relativeToSelf, 0, relativeToSelf, 1.0f,
            relativeToSelf, 0);
    Animation outTranAnim =
        new TranslateAnimation(relativeToSelf, 0, relativeToSelf, 0, relativeToSelf, 0,
            relativeToSelf, -1.0f);

    //AlphaAnimation inAlphaAnim = new AlphaAnimation(0.0f, 1.0f);
    //AlphaAnimation outAlphaAnim = new AlphaAnimation(1.0f, 0.0f);

    mInAnim = new AnimationSet(true);
    mOutAnim = new AnimationSet(true);

    //mInAnim.addAnimation(inAlphaAnim);
    mInAnim.addAnimation(inTranAnim);
    //mOutAnim.addAnimation(outAlphaAnim);
    mOutAnim.addAnimation(outTranAnim);

    mInAnim.setDuration(animTime);
    mOutAnim.setDuration(animTime);
    mInAnim.setInterpolator(new LinearInterpolator());
    mOutAnim.setInterpolator(new LinearInterpolator());

    //AnimationSet animationSet = new AnimationSet(true);

    mTimeOut = Math.abs(standingTime + animTime);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    pause();
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    resume();
  }

  /**
   * stop
   */
  public void stop() {
    mIsShow = false;
    mIsStop = true;
    stopAnimation();
  }

  /**
   * restart
   */
  public void restart() {
    mIsShow = true;
    mIsStop = false;
    startAnimation();
    //invalidate();
  }

  private void resume() {
    if (mTexts == null || mTexts.length == 0) return;
    mIsShow = true;
    startAnimation();
  }

  private void pause() {
    stopAnimation();
  }

  /**
   * Get a list of the texts
   *
   * @return the texts array
   */
  public CharSequence[] getTexts() {
    return mTexts;
  }

  /**
   * Sets the texts to be shuffled using a string array resource
   *
   * @param textsRes The string array resource to use for the texts
   */
  public void setTexts(int textsRes) {
    String[] texts = getResources().getStringArray(textsRes);
    if (texts.length < 1) {
      throw new IllegalArgumentException("There must be at least one text");
    } else {
      setTexts(texts);
    }
  }

  public void setTexts(CharSequence[] texts) {
    if (texts == null) {
      throw new NullPointerException("texts is null");
    }
    this.mTexts = texts;
    stopAnimation();
    mPosition = 0;
    startAnimation();
  }

  /**
   * This method should only be used to forcefully apply timeout changes
   * It will dismiss the currently queued animation change and start a new animation
   */
  public void forceRefresh() {
    stopAnimation();
    startAnimation();
  }

  @Override public void startAnimation(Animation animation) {
    if (mIsShow && !mIsStop) {
      super.startAnimation(animation);
    }
  }

  protected void startAnimation() {
    setText(mTexts[mPosition]);
    startAnimation(mInAnim);
    postDelayed(mCircleRunnable, mTimeOut);
  }

  private void stopAnimation() {
    removeCallbacks(mCircleRunnable);
    Animation animation = getAnimation();
    if (animation != null) {
      animation.cancel();
    }
  }

  private Runnable mCircleRunnable = new Runnable() {
    @Override public void run() {
      startAnimation(mOutAnim);
      Animation animation = getAnimation();
      if (animation != null) {
        animation.setAnimationListener(mAnimListener);
      }
    }
  };
  private Animation.AnimationListener mAnimListener = new Animation.AnimationListener() {
    @Override public void onAnimationStart(Animation animation) {

    }

    @Override public void onAnimationEnd(Animation animation) {
      if (mIsShow) {
        mPosition = mPosition == mTexts.length - 1 ? 0 : mPosition + 1;
        startAnimation();
      }
    }

    @Override public void onAnimationRepeat(Animation animation) {
    }
  };
}
