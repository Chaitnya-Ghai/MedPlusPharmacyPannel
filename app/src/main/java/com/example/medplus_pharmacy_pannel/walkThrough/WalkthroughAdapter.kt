package com.example.medplus_pharmacy_pannel.walkThrough

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medplus_pharmacy_pannel.R


class WalkthroughAdapter(private val screens: List<WalkthroughModel>) :
    RecyclerView.Adapter<WalkthroughAdapter.WalkthroughViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkthroughViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.walkthrough_item, parent, false)
        return WalkthroughViewHolder(view)
    }

    override fun onBindViewHolder(holder: WalkthroughViewHolder, position: Int) {
        holder.bind(screens[position])
    }

    override fun getItemCount(): Int = screens.size

    class WalkthroughViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView =  itemView.findViewById(R.id.imageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(model: WalkthroughModel) {
            imageView.setImageResource(model.imageRes)
            titleTextView.text = model.title
            descriptionTextView.text = model.description
        }
    }
}