/*
 * Copyright (c) 2014 Mario Guggenberger <mg@protyposis.net>
 *
 * This file is part of MediaPlayer-Extended.
 *
 * MediaPlayer-Extended is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MediaPlayer-Extended is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MediaPlayer-Extended.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.protyposis.android.spectaculumdemo;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import net.protyposis.android.spectaculum.SpectaculumView;
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
import net.protyposis.android.spectaculum.gles.GLUtils;
import net.protyposis.android.spectaculumdemo.testeffect.ColorFilterEffect;
import net.protyposis.android.spectaculum.effects.EquirectangularSphereEffect;

/**
 * Created by Mario on 18.07.2014.
 *
 * Helper class for easy effect handling in the Spectaculum views.
 */
public class EffectManager implements SpectaculumView.EffectEventListener {

    private Activity mActivity;
    private ViewGroup mParameterListView;
    private EffectParameterListAdapter mParameterListAdapter;
    private MenuItem mParameterToggleMenuItem;
    private SpectaculumView mSpectaculumView;
    private List<Effect> mEffects;
    private Effect mSelectedEffect;

    public EffectManager(Activity activity, int parameterListViewId, SpectaculumView glView) {
        mActivity = activity;
        mParameterListView = (ViewGroup) activity.findViewById(parameterListViewId);
        mSpectaculumView = glView;
        mEffects = new ArrayList<>();
        mSpectaculumView.setEffectEventListener(this);

        // MediaPlayer-GLES filters
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

        // custom filters
        mEffects.add(new ColorFilterEffect());

        // Immersive filters
        mEffects.add(new EquirectangularSphereEffect());

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

    @Override
    public void onEffectInitialized(int index, final Effect effect) {
        // nothing to do here
    }

    @Override
    public void onEffectSelected(int index, Effect effect) {
        viewEffectParameters(getSelectedEffect());
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
}
