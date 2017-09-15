/*
 * Copyright 2014 Mario Guggenberger <mg@protyposis.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.protyposis.android.spectaculumdemo;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import net.protyposis.android.spectaculum.SpectaculumView;
import net.protyposis.android.spectaculum.effects.Parameter;
import net.protyposis.android.spectaculum.effects.ImmersiveSensorNavigation;
import net.protyposis.android.spectaculum.effects.ContrastBrightnessAdjustmentEffect;
import net.protyposis.android.spectaculum.effects.EffectException;
import net.protyposis.android.spectaculum.effects.FlowAbsSubEffect;
import net.protyposis.android.spectaculum.effects.QrMarkerEffect;
import net.protyposis.android.spectaculum.effects.Effect;
import net.protyposis.android.spectaculum.effects.FlipEffect;
import net.protyposis.android.spectaculum.effects.FlowAbsEffect;
import net.protyposis.android.spectaculum.effects.KernelBlurEffect;
import net.protyposis.android.spectaculum.effects.KernelEdgeDetectEffect;
import net.protyposis.android.spectaculum.effects.KernelEmbossEffect;
import net.protyposis.android.spectaculum.effects.KernelGaussBlurEffect;
import net.protyposis.android.spectaculum.effects.KernelSharpenEffect;
import net.protyposis.android.spectaculum.effects.NoEffect;
import net.protyposis.android.spectaculum.effects.SimpleToonEffect;
import net.protyposis.android.spectaculum.effects.SobelEffect;
import net.protyposis.android.spectaculum.effects.ImmersiveTouchNavigation;
import net.protyposis.android.spectaculum.effects.StackEffect;
import net.protyposis.android.spectaculum.effects.WatermarkEffect;
import net.protyposis.android.spectaculum.gles.GLUtils;
import net.protyposis.android.spectaculum.effects.ColorFilterEffect;
import net.protyposis.android.spectaculum.effects.ImmersiveEffect;
import net.protyposis.android.spectaculumdemo.testeffect.InterlaceEffect;

/**
 * Created by Mario on 18.07.2014.
 *
 * Helper class for easy effect handling in the various Spectaculum views in this demo.
 * Provides a list of effects for the actionbar and displays a parameter control panel for
 * selected effects with parameters that the demo user can player play with.
 */
public class EffectManager implements SpectaculumView.EffectEventListener, Effect.Listener {

    private Activity mActivity;
    private ViewGroup mParameterListView;
    private EffectParameterListAdapter mParameterListAdapter;
    private MenuItem mParameterToggleMenuItem;
    private SpectaculumView mSpectaculumView;
    private List<Effect> mEffects;
    private Effect mSelectedEffect;
    private ImmersiveSensorNavigation mImmersiveSensorNavigation;
    private ImmersiveTouchNavigation mImmersiveTouchNavigation;

    public EffectManager(Activity activity, int parameterListViewId, SpectaculumView glView) {
        mActivity = activity;
        mParameterListView = (ViewGroup) activity.findViewById(parameterListViewId);
        mSpectaculumView = glView;
        mEffects = new ArrayList<>();
        mSpectaculumView.setEffectEventListener(this);

        // Spectaculum-Core filters
        mEffects.add(new NoEffect());
        mEffects.add(new FlipEffect());
        mEffects.add(new SobelEffect());
        mEffects.add(new SimpleToonEffect());
        mEffects.add(new KernelBlurEffect());
        mEffects.add(new KernelGaussBlurEffect());
        mEffects.add(new KernelEdgeDetectEffect());
        mEffects.add(new KernelEmbossEffect());
        mEffects.add(new KernelSharpenEffect());
        mEffects.add(new ContrastBrightnessAdjustmentEffect());
        mEffects.add(new ColorFilterEffect());
        // Create a watermark effect with demo app icon as watermark image
        WatermarkEffect watermarkEffect = new WatermarkEffect();
        watermarkEffect.setWatermark(BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.ic_launcher));
        mEffects.add(watermarkEffect);
        // Create a filter stack with multiple effects
        mEffects.add(new StackEffect("Stack: Toon, Contrast/Brightness, Watermark",
                new SimpleToonEffect(), new ContrastBrightnessAdjustmentEffect(), watermarkEffect));

        // custom filters
        mEffects.add(new InterlaceEffect());

        // Immersive filters
        mEffects.add(new ImmersiveEffect());

        // FlowAbs filters
        FlowAbsEffect flowAbsEffect = new FlowAbsEffect();
        mEffects.add(flowAbsEffect);
        mEffects.add(flowAbsEffect.getNoiseTextureEffect());
        mEffects.add(flowAbsEffect.getGaussEffect());
        mEffects.add(flowAbsEffect.getSmoothEffect());
        mEffects.add(flowAbsEffect.getBilateralFilterEffect());
        mEffects.add(flowAbsEffect.getColorQuantizationEffect());
        mEffects.add(flowAbsEffect.getDOGEffect());
        mEffects.add(flowAbsEffect.getFDOGEffect());
        mEffects.add(flowAbsEffect.getTangentFlowMapEffect());

        // QrMarker filters
        QrMarkerEffect qrMarkerEffect = new QrMarkerEffect();
        //mEffects.add(qrMarkerEffect);
        mEffects.add(qrMarkerEffect.getCannyEdgeEffect());
    }

    public void addEffects() {
        mSpectaculumView.addEffect(mEffects.toArray(new Effect[mEffects.size()]));
    }

    public List<String> getEffectNames() {
        List<String> effectNames = new ArrayList<>();
        for(Effect effect : mEffects) {
            effectNames.add(effect.getName());
        }
        return effectNames;
    }

    public boolean selectEffect(int index) {
        if(mSelectedEffect != null) {
            // Remove listener from previously selected effect
            mSelectedEffect.removeListener(this);

            if (mSelectedEffect instanceof ImmersiveEffect) {
                if (mImmersiveSensorNavigation != null) {
                    mImmersiveSensorNavigation.deactivate();
                    mImmersiveSensorNavigation.detach();
                    mImmersiveSensorNavigation = null;
                }
                if (mImmersiveTouchNavigation != null) {
                    mImmersiveTouchNavigation.deactivate();
                    mImmersiveTouchNavigation.detach();
                    mImmersiveTouchNavigation = null;
                }
            }
        }
        Effect effect = mEffects.get(index);
        if(effect instanceof FlowAbsEffect || effect instanceof FlowAbsSubEffect) {
            if(GLUtils.HAS_GPU_TEGRA) {
                Toast.makeText(mActivity, "FlowAbs deactivated (the Tegra GPU of this device does not support the required dynamic loops in shaders)", Toast.LENGTH_SHORT).show();
                return false;
            } else if(!GLUtils.HAS_FLOAT_FRAMEBUFFER_SUPPORT) {
                Toast.makeText(mActivity, "FlowAbs effects do not render correctly on this device (GPU does not support fp framebuffer attachments)", Toast.LENGTH_SHORT).show();
            }
        }

        mSelectedEffect = effect;
        mSpectaculumView.selectEffect(index);
        return true;
    }

    public Effect getSelectedEffect() {
        return mSelectedEffect;
    }

    public void addToMenu(Menu menu) {
        SubMenu submenu = menu.findItem(R.id.action_list_effects).getSubMenu();
        int count = 0;
        for(String effectName : getEffectNames()) {
            submenu.add(R.id.action_list_effects, count++, Menu.NONE, effectName);
        }
        mParameterToggleMenuItem = menu.findItem(R.id.action_toggle_parameters);
    }

    private boolean doMenuActionEffect(MenuItem item) {
        if(item.getGroupId() == R.id.action_list_effects) {
            return selectEffect(item.getItemId());
        }
        return false;
    }

    public boolean doMenuActions(MenuItem item) {
        if(doMenuActionEffect(item)) {
            return true;
        } else if(item.getItemId() == R.id.action_toggle_parameters) {
            mParameterListView.setVisibility(mParameterListView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            return true;
        }
        return false;
    }

    public void viewEffectParameters(Effect effect) {
        if(effect.hasParameters()) {
            mParameterListAdapter = new EffectParameterListAdapter(mActivity, mSpectaculumView, effect.getParameters());
            mParameterListView.removeAllViews();
            for(int i = 0; i < mParameterListAdapter.getCount(); i++) {
                mParameterListView.addView(mParameterListAdapter.getView(i, null, null));
            }
            mParameterListView.setVisibility(View.VISIBLE);
            mParameterToggleMenuItem.setEnabled(true);
        } else {
            mParameterListView.setVisibility(View.GONE);
            mParameterListView.removeAllViews();
            if(mParameterListAdapter != null) {
                mParameterListAdapter.clear();
                mParameterListAdapter = null;
            }
            mParameterToggleMenuItem.setEnabled(false);
        }
    }

    public void onPause() {
        if(mImmersiveSensorNavigation != null) {
            mImmersiveSensorNavigation.deactivate();
        }
        if(mImmersiveTouchNavigation != null) {
            mImmersiveTouchNavigation.deactivate();
        }
    }

    public void onResume() {
        if(mImmersiveSensorNavigation != null) {
            mImmersiveSensorNavigation.activate();
        }
        if(mImmersiveTouchNavigation != null) {
            mImmersiveTouchNavigation.activate();
        }
    }

    @Override
    public void onEffectInitialized(int index, final Effect effect) {
        // nothing to do here
    }

    @Override
    public void onEffectSelected(int index, Effect effect) {
        effect.addListener(this); // add listener so callback below gets called
        viewEffectParameters(getSelectedEffect());

        if(effect instanceof ImmersiveEffect) {
            if(mImmersiveSensorNavigation == null) {
                // Create sensor navigation instance in a try/catch block because it fails
                // if no rotation sensor is available.
                try {
                    mImmersiveSensorNavigation = new ImmersiveSensorNavigation(mActivity);
                    mImmersiveSensorNavigation.attachTo((ImmersiveEffect) effect);
                } catch (RuntimeException e) {
                    Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            if(mImmersiveTouchNavigation == null) {
                mImmersiveTouchNavigation = new ImmersiveTouchNavigation(mSpectaculumView);
                mImmersiveTouchNavigation.attachTo((ImmersiveEffect) effect);
            }
        }
    }

    @Override
    public void onEffectError(int index, final Effect effect, final EffectException e) {
        if(e.getCause() != null) {
            Throwable cause = e.getCause();
            Toast.makeText(mActivity, "Effect " + cause.getClass().getSimpleName() + ": " + cause.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mActivity, "EffectException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        selectEffect(0); // select the NoEffect to get rid of the parameter control panel of the failed effect
    }

    @Override
    public void onEffectChanged(Effect effect) {
        /*
         *Because we set this class as the event listener for effects, we overwrite SpectaculumView's
         * internal event listener and must forward the calls to the view. This is true for all following
         * event listener methods.
         * TODO find a solution, maybe permit a list of event listeners on effects? what would be the performance implication?
         */
        mSpectaculumView.onEffectChanged(effect);
    }

    @Override
    public void onParameterAdded(Effect effect, Parameter parameter) {
        mSpectaculumView.onParameterAdded(effect, parameter); // see onEffectChanged

        // refresh the parameter control panel
        viewEffectParameters(getSelectedEffect());
    }

    @Override
    public void onParameterRemoved(Effect effect, Parameter parameter) {
        mSpectaculumView.onParameterAdded(effect, parameter); // see onEffectChanged

        // refresh the parameter control panel
        viewEffectParameters(getSelectedEffect());
    }
}
