package it.unimib.dimmitu.Login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.unimib.dimmitu.R;

public class CustomAdapterScelta extends BaseAdapter {

    Context context;
    int colors[];
    String[] colorNames;
    LayoutInflater inflter;

    public CustomAdapterScelta(Context applicationContext, int[] colors, String[] colorNames) {
        this.context = applicationContext;
        this.colors = colors;
        this.colorNames = colorNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.my_dropdown_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        icon.setImageResource(colors[i]);
        names.setText(colorNames[i]);
        return view;
    }

}
