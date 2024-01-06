package com.example.wodbook.domain

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wodbook.R
import com.example.wodbook.data.WOD

class WodAdapter(private var wods: List<WOD> = emptyList()) : RecyclerView.Adapter<WodAdapter.WodViewHolder>() {

    // ViewHolder class for each WOD item
    class WodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view_wod)
        val textView: TextView = view.findViewById(R.id.text_view_wod)
    }

    // Creates and returns a ViewHolder for the WOD item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WodViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wod_item, parent, false)
        return WodViewHolder(itemView)
    }

    // Binds data to each ViewHolder
    override fun onBindViewHolder(holder: WodViewHolder, position: Int) {
        val currentItem = wods[position]
        // Display the WOD date in the TextView
        holder.textView.text = currentItem.dateTime.toString()

        // Load and display the image from URI
        val uri = Uri.parse(currentItem.picture)
        try {
            holder.itemView.context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val drawable = Drawable.createFromStream(inputStream, uri.toString())
                holder.imageView.setImageDrawable(drawable)
            }
        } catch (e: Exception) {
            // Log error and set a placeholder image if there's an issue loading the image
            Log.e("WodAdapter", "Error loading image", e)
            holder.imageView.setImageResource(R.drawable.ic_placeholder_foreground) // Placeholder image resource
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount() = wods.size

    // Updates the list of WODs and notifies the adapter to refresh
    fun setWods(wods: List<WOD>) {
        this.wods = wods
        notifyDataSetChanged()
    }
}
