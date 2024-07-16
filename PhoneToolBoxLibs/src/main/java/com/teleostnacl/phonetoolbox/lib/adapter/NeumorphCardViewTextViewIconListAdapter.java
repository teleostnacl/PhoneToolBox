package com.teleostnacl.phonetoolbox.lib.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.teleostnacl.common.android.view.recyclerview.ClickedOnceListAdapter;
import com.teleostnacl.common.android.view.recyclerview.DataBindingVH;
import com.teleostnacl.common.android.view.recyclerview.DefaultItemCallback;
import com.teleostnacl.phonetoolbox.lib.R;
import com.teleostnacl.phonetoolbox.lib.databinding.LayoutItemNeumorphCardViewTextWithIconBinding;
import com.teleostnacl.phonetoolbox.lib.model.NeumorphCardViewTextWithIconModel;

public class NeumorphCardViewTextViewIconListAdapter extends ClickedOnceListAdapter<NeumorphCardViewTextWithIconModel,
        DataBindingVH<LayoutItemNeumorphCardViewTextWithIconBinding>> {

    public NeumorphCardViewTextViewIconListAdapter() {
        super(new DefaultItemCallback<>());
    }

    @NonNull
    @Override
    public DataBindingVH<LayoutItemNeumorphCardViewTextWithIconBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DataBindingVH<LayoutItemNeumorphCardViewTextWithIconBinding> viewHolder = new DataBindingVH<>(parent, R.layout.layout_item_neumorph_card_view_text_with_icon);
        setOnClickListener(viewHolder.itemView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull DataBindingVH<LayoutItemNeumorphCardViewTextWithIconBinding> holder, int position) {
        holder.binding.setModel(getItem(position));

        holder.itemView.setTag(getItem(position));
    }

    @Override
    public void onClick(@NonNull View v) {
        NeumorphCardViewTextWithIconModel model = (NeumorphCardViewTextWithIconModel) v.getTag();

        if (model != null) {
            model.runnable.run();
        }
    }
}
