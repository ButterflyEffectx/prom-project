package com.hadirahimi.passwordchecker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class PasswordAdapter(
    private val context: Context,
    private var passwordList: ArrayList<PasswordItem>,
    private val onItemClick: (PasswordItem) -> Unit,
    private val onDeleteClick: (PasswordItem) -> Unit
) : RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

    class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPassword: TextView = itemView.findViewById(R.id.tvPasswordText)
        val tvStrength: TextView = itemView.findViewById(R.id.tvStrengthValue)
        val tvDate: TextView = itemView.findViewById(R.id.tvDateValue)
        val cardView: CardView = itemView.findViewById(R.id.cardPassword)
        val btnDelete: View = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val passwordItem = passwordList[position]

        holder.tvPassword.text = passwordItem.password
        holder.tvStrength.text = passwordItem.strength
        holder.tvDate.text = passwordItem.date

        // Set background color based on password strength
        val cardColor = when (passwordItem.strength) {
            "Strong" -> R.color.password_strong
            "Moderate" -> R.color.password_moderate
            else -> R.color.password_weak
        }

        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, cardColor))

        holder.itemView.setOnClickListener {
            onItemClick(passwordItem)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(passwordItem)
        }
    }

    override fun getItemCount() = passwordList.size

    fun updateData(newData: ArrayList<PasswordItem>) {
        passwordList = newData
        notifyDataSetChanged()
    }
}