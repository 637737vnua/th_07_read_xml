package androidvnua.vnua.sitemaptimoday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AdapterView extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ArrayUrl> infoList;

    public AdapterView(Context context, int layout, List<ArrayUrl> infoList) {
        this.context = context;
        this.layout = layout;
        this.infoList = infoList;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class viewHolder {
        TextView txtUrl, txtDate, txtPriority, txtChangeFrequency, txtId, txtCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder = new viewHolder();
            holder.txtUrl = (TextView) convertView.findViewById(R.id.txtUrl);
            holder.txtId = (TextView) convertView.findViewById(R.id.txtId);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            holder.txtPriority = (TextView) convertView.findViewById(R.id.txtPriority);
            holder.txtChangeFrequency = (TextView) convertView.findViewById(R.id.txtChangeFrequency);
            holder.txtCount = (TextView) convertView.findViewById(R.id.txtCount);
            convertView.setTag(holder);
        }else {
            holder = (viewHolder) convertView.getTag();
        }

        ArrayUrl arrayUrl = infoList.get(position);

        String s = arrayUrl.getPriority();
        float parser = Float.parseFloat(s)*100;

        holder.txtId.setText("ID: " + arrayUrl.getId());
        holder.txtUrl.setText("URL: " + arrayUrl.getUrl());
        holder.txtDate.setText("DATE: " + arrayUrl.getDate());
        holder.txtPriority.setText("Priority: " + parser + "%");
        holder.txtChangeFrequency.setText("ChangeFrequency: " + arrayUrl.getChangeFrequency());
        holder.txtCount.setText("Count Image: " + arrayUrl.getCount());

        return convertView;
    }
}
