package com.team.smartspend.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team.smartspend.R
import com.team.smartspend.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * HistoryAdapter — adaptateur RecyclerView de l'écran Historique (Membre 4, adapté).
 *
 * Affiche pour chaque transaction : une icône (emoji) de catégorie, la catégorie,
 * la description, la date formatée et le montant coloré
 * (rouge pour une dépense, vert pour un revenu).
 */
class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.TransactionViewHolder>() {

    private var transactions: List<Transaction> = emptyList()
    private var onClick: ((Transaction) -> Unit)? = null

    fun setTransactions(list: List<Transaction>) {
        transactions = list
        notifyDataSetChanged()
    }

    fun setOnTransactionClickListener(listener: (Transaction) -> Unit) {
        onClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val t = transactions[position]

        holder.tvIcon.text = iconePourCategorie(t.categorie)
        holder.tvCategory.text = t.categorie
        holder.tvDescription.text = if (t.description.isNotEmpty()) t.description else "—"

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(t.date))

        // Montant coloré : rouge pour une dépense, vert pour un revenu
        if (t.type == "DEPENSE") {
            holder.tvAmount.text = String.format(Locale.getDefault(), "-%.0f FCFA", t.montant)
            holder.tvAmount.setTextColor(Color.parseColor("#C62828"))
        } else {
            holder.tvAmount.text = String.format(Locale.getDefault(), "+%.0f FCFA", t.montant)
            holder.tvAmount.setTextColor(Color.parseColor("#2E7D32"))
        }

        holder.itemView.setOnClickListener { onClick?.invoke(t) }
    }

    override fun getItemCount(): Int = transactions.size

    /** Emoji selon la catégorie (repris du travail de Membre 4). */
    private fun iconePourCategorie(categorie: String?): String = when (categorie) {
        "Transport" -> "🚗"
        "Nourriture" -> "🍽️"
        "Loyer" -> "🏠"
        "Loisirs" -> "🎮"
        "Santé" -> "💊"
        "Salaire" -> "💼"
        "Freelance" -> "💻"
        else -> "💰"
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tv_icon)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
    }
}
