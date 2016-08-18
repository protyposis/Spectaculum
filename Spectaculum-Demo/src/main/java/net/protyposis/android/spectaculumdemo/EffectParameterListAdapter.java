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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import net.protyposis.android.spectaculum.SpectaculumView;
import net.protyposis.android.spectaculum.effects.EnumParameter;
import net.protyposis.android.spectaculum.effects.FloatParameter;
import net.protyposis.android.spectaculum.effects.IntegerParameter;
import net.protyposis.android.spectaculum.effects.Parameter;

/**
 * Created by Mario on 06.09.2014.
 */
public class EffectParameterListAdapter extends BaseAdapter {

    private Activity mActivity;
    private SpectaculumView mSpectaculumView;
    public List<Parameter> mParameters;

    public EffectParameterListAdapter(Activity activity, SpectaculumView spectaculumView, List<Parameter> parameters) {
        mActivity = activity;
        mSpectaculumView = spectaculumView;
        mParameters = new ArrayList<>(parameters);
    }

    @Override
    public int getCount() {
        return mParameters.size();
    }

    @Override
    public Parameter getItem(int position) {
        return mParameters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Parameter parameter = getItem(position);
        View view = convertView;

        if(convertView == null || convertView.getTag() != parameter.getClass()) {
            if(parameter instanceof EnumParameter) {
                view = mActivity.getLayoutInflater().inflate(R.layout.list_item_parameter_spinner, parent, false);
            } else {
                view = mActivity.getLayoutInflater().inflate(R.layout.list_item_parameter_seekbar, parent, false);
            }
            view.setTag(parameter.getClass());
        }

        TextView parameterName = (TextView) view.findViewById(R.id.name);
        parameterName.setText(parameter.getName());
        if(parameter.getDescription() != null) {
            parameterName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mActivity, parameter.getDescription(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        final TextView valueView = (TextView) view.findViewById(R.id.value);
        final Button resetButton = (Button) view.findViewById(R.id.reset);

        if (parameter instanceof IntegerParameter) {
            final IntegerParameter p = (IntegerParameter) parameter;
            int interval = p.getMax() - p.getMin();
            seekBar.setMax(interval);
            seekBar.setProgress(p.getValue() - p.getMin());
            SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    final int value = progress + p.getMin();
                    p.setValue(value);
                    valueView.setText(String.format("%d", value));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };
            seekBar.setOnSeekBarChangeListener(changeListener);
            changeListener.onProgressChanged(seekBar, seekBar.getProgress(), false); // init value label
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekBar.setProgress(p.getDefault() - p.getMin());
                }
            });
        } else if (parameter instanceof FloatParameter) {
            final int precision = 100; // 2 digits after comma
            final FloatParameter p = (FloatParameter) parameter;
            float interval = p.getMax() - p.getMin();
            seekBar.setMax((int) (interval * precision));
            seekBar.setProgress((int) ((p.getValue() - p.getMin()) * precision));
            SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                    final float value = (progress / (float) precision) + p.getMin();
                    p.setValue(value);
                    valueView.setText(String.format("%.2f", value));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };
            seekBar.setOnSeekBarChangeListener(changeListener);
            changeListener.onProgressChanged(seekBar, seekBar.getProgress(), false); // init value label
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekBar.setProgress((int) ((p.getDefault() - p.getMin()) * precision));
                }
            });
        } else if (parameter instanceof EnumParameter) {
            final EnumParameter p = (EnumParameter) parameter;
            final ArrayAdapter<Enum> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, p.getEnumValues());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(adapter.getPosition(p.getValue()));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                    p.setValue(p.getEnumValues()[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinner.setSelection(adapter.getPosition(p.getDefault()));
                }
            });
        }

        return view;
    }

    public void clear() {
        mParameters.clear();
        notifyDataSetChanged();
    }
}