package com.example.whereott.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whereott.R

class ProviderAdapter(private val providers: MutableList<Provider>) : RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder>() {

    inner class ProviderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val providerNameTextView: TextView = itemView.findViewById(R.id.provider_type)
        private val logoImageView: ImageView = itemView.findViewById(R.id.provider_logo)
        private val nameTextView: TextView = itemView.findViewById(R.id.provider_name)

        fun bind(provider: Provider) {

            providerNameTextView.text = provider.type

            // 프로바이더 로고 설정
            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w185${provider.logoPath}")
                .into(logoImageView)

            nameTextView.text = provider.providerName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_provider, parent, false)
        return ProviderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        holder.bind(providers[position])
    }

    override fun getItemCount(): Int = providers.size

    fun appendProviders(providers: List<Provider>) {
        // 기존의 프로바이더 리스트에 새로운 프로바이더를 추가합니다.
        this.providers.addAll(providers)
        notifyDataSetChanged()
    }
}
