package com.example.whereott.ui.person

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.whereott.R
import com.example.whereott.common.KnownFor
import com.example.whereott.common.Person
import com.example.whereott.common.TV


class PersonAdapter (var persons: MutableList<Person>, var onPersonClick: (person: Person) -> Unit
) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>(){

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.item_person_poster)
        private val personName: TextView = itemView.findViewById(R.id.item_person_name)
        private val departmentView: TextView = itemView.findViewById(R.id.item_DepartmentView)
        private val knownForView: TextView = itemView.findViewById(R.id.movie_watch_provider)
        private val starImageView: ImageView = itemView.findViewById(R.id.starImageView)
        private var isStarFilled = false

        fun bind(person: Person) {
            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w342${person.profilePath}")
                .transform(CenterCrop())
                .into(poster)
            personName.text = person.name
            departmentView.text = person.Department

            // 별 이미지 클릭 시 상태 변경
            starImageView.setOnClickListener {
                isStarFilled = !isStarFilled
                if (isStarFilled) {
                    // 별이 채워져있는 이미지로 변경
                    starImageView.setImageResource(R.drawable.fill_color_star)
                } else {
                    // 비어있는 별 이미지로 변경
                    starImageView.setImageResource(R.drawable.non_color_star)
                }
            }

            itemView.setOnClickListener { onPersonClick.invoke(person) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(view)
    }

    override fun getItemCount(): Int = persons.size

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(persons[position])
    }

    fun appendPerson(persons: List<Person>) {
        this.persons.addAll(persons)
        notifyItemRangeInserted(
            this.persons.size,
            persons.size - 1
        )
    }

    fun clear() {
        persons.clear()
        notifyDataSetChanged()
    }
}
