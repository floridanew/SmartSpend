package com.team.smartspend.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.team.smartspend.R
import com.team.smartspend.viewmodel.MonthlyBarData
import com.team.smartspend.viewmodel.StatsSummary
import com.team.smartspend.viewmodel.StatsViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Écran Statistiques (Membre 5).
 *
 * Cet écran observe le [StatsViewModel] et met à jour, en temps réel :
 *   - le résumé textuel (revenus, dépenses, solde, taux d'épargne, catégorie n°1)
 *   - le graphique circulaire (dépenses par catégorie)
 *   - le graphique en barres (revenus vs dépenses par mois)
 */
class StatsActivity : AppCompatActivity() {

    private lateinit var viewModel: StatsViewModel

    // Les vues (récupérées une seule fois dans onCreate)
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    private lateinit var textViewSoldeNet: TextView
    private lateinit var textViewRevenus: TextView
    private lateinit var textViewDepenses: TextView
    private lateinit var textViewTauxEpargne: TextView
    private lateinit var textViewMoyenne: TextView
    private lateinit var textViewCategorieMax: TextView

    // Pour afficher les montants joliment (ex: "12 500")
    private val formatMontant: NumberFormat = NumberFormat.getInstance(Locale.FRANCE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // 1) Récupérer les vues du layout
        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)
        textViewSoldeNet = findViewById(R.id.textViewSoldeNet)
        textViewRevenus = findViewById(R.id.textViewRevenus)
        textViewDepenses = findViewById(R.id.textViewDepenses)
        textViewTauxEpargne = findViewById(R.id.textViewTauxEpargne)
        textViewMoyenne = findViewById(R.id.textViewMoyenne)
        textViewCategorieMax = findViewById(R.id.textViewCategorieMax)

        // 2) Préparer l'apparence générale des graphiques (une seule fois)
        configurerPieChart()
        configurerBarChart()

        // 3) Récupérer le ViewModel
        viewModel = ViewModelProvider(this)[StatsViewModel::class.java]

        // 4) Observer les données : dès qu'elles changent, l'écran se met à jour
        viewModel.summary.observe(this) { resume ->
            afficherResume(resume)
        }
        viewModel.pieEntries.observe(this) { entries ->
            afficherPieChart(entries)
        }
        viewModel.barData.observe(this) { data ->
            afficherBarChart(data)
        }

        // 5) Configurer la barre de navigation du bas
        configurerNavigation()

        // ⚠️ TEMPORAIRE — bouton pour charger des données de test (à retirer plus tard)
        findViewById<android.widget.Button>(R.id.buttonDonneesTest).setOnClickListener {
            viewModel.insererDonneesDeTest()
            android.widget.Toast.makeText(
                this, "Données de test ajoutées !", android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =====================================================================
    //  RÉSUMÉ TEXTUEL
    // =====================================================================

    private fun afficherResume(resume: StatsSummary) {
        textViewSoldeNet.text = "${formatMontant.format(resume.soldeNet)} FCFA"
        textViewRevenus.text = "${formatMontant.format(resume.totalRevenus)} FCFA"
        textViewDepenses.text = "${formatMontant.format(resume.totalDepenses)} FCFA"
        textViewTauxEpargne.text = "Taux d'épargne : ${formatMontant.format(resume.tauxEpargne)} %"
        textViewMoyenne.text = "Dépense moyenne : ${formatMontant.format(resume.moyenneDepenses)} FCFA"
        textViewCategorieMax.text = "Catégorie la plus dépensière : ${resume.categorieLaPlusDepensiere}"
    }

    // =====================================================================
    //  GRAPHIQUE CIRCULAIRE (PieChart)
    // =====================================================================

    private fun configurerPieChart() {
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(android.graphics.Color.WHITE)
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelColor(android.graphics.Color.BLACK)
        pieChart.legend.isEnabled = true
    }

    private fun afficherPieChart(entries: List<com.github.mikephil.charting.data.PieEntry>) {
        if (entries.isEmpty()) {
            pieChart.clear()
            pieChart.setNoDataText("Aucune dépense à afficher")
            pieChart.invalidate()
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = couleursGraphiques()
        dataSet.sliceSpace = 2f
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = android.graphics.Color.BLACK

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart)) // affiche en %

        pieChart.data = data
        pieChart.invalidate() // redessine
    }

    // =====================================================================
    //  GRAPHIQUE EN BARRES (BarChart)
    // =====================================================================

    private fun configurerBarChart() {
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.axisMinimum = 0f

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
    }

    private fun afficherBarChart(data: MonthlyBarData) {
        if (data.labels.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("Aucune donnée à afficher")
            barChart.invalidate()
            return
        }

        // Une série pour les revenus (vert) et une pour les dépenses (rouge)
        val setRevenus = BarDataSet(data.revenus, "Revenus")
        setRevenus.color = ContextCompat.getColor(this, R.color.vert_revenu)

        val setDepenses = BarDataSet(data.depenses, "Dépenses")
        setDepenses.color = ContextCompat.getColor(this, R.color.rouge_depense)

        val barData = BarData(setRevenus, setDepenses)

        // Réglages pour afficher 2 barres groupées par mois
        val groupSpace = 0.3f
        val barSpace = 0.05f
        val barWidth = 0.3f
        // (barWidth + barSpace) * 2 + groupSpace doit faire 1.0
        barData.barWidth = barWidth

        barChart.data = barData

        // Libellés des mois sur l'axe X
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(data.labels)
        barChart.xAxis.axisMinimum = 0f
        barChart.xAxis.axisMaximum = data.labels.size.toFloat()
        barChart.xAxis.setCenterAxisLabels(true)

        // Regroupe les barres à partir de la position 0
        barChart.groupBars(0f, groupSpace, barSpace)
        barChart.invalidate()
    }

    // =====================================================================
    //  COULEURS DES PARTS DU CAMEMBERT
    // =====================================================================

    private fun couleursGraphiques(): List<Int> {
        return listOf(
            R.color.chart_1, R.color.chart_2, R.color.chart_3, R.color.chart_4,
            R.color.chart_5, R.color.chart_6, R.color.chart_7, R.color.chart_8
        ).map { ContextCompat.getColor(this, it) }
    }

    // =====================================================================
    //  BARRE DE NAVIGATION DU BAS
    // =====================================================================

    private fun configurerNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // On marque l'onglet "Statistiques" comme sélectionné (on est dessus)
        bottomNav.selectedItemId = R.id.nav_stats

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0) // transition sans animation brusque
                    finish()
                    true
                }
                R.id.nav_stats -> true // déjà sur cet écran
                R.id.nav_history -> {
                    // Écran de l'historique : développé par un autre membre de l'équipe
                    android.widget.Toast.makeText(
                        this, "Historique — à venir", android.widget.Toast.LENGTH_SHORT
                    ).show()
                    false
                }
                R.id.nav_settings -> {
                    // Écran des paramètres : développé par un autre membre de l'équipe
                    android.widget.Toast.makeText(
                        this, "Paramètres — à venir", android.widget.Toast.LENGTH_SHORT
                    ).show()
                    false
                }
                else -> false
            }
        }
    }
}
