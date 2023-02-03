package com.github.bkmbigo.textclassification.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.bkmbigo.textclassification.R
import com.github.bkmbigo.textclassification.data.PredictionResult
import com.github.bkmbigo.textclassification.databinding.ActivityMainBinding
import com.github.bkmbigo.textclassification.databinding.ItemResultBinding
import com.github.bkmbigo.textclassification.di.ActivityNLClassifier
import com.github.bkmbigo.textclassification.domain.DatabaseRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @ActivityNLClassifier
    @Inject
    lateinit var textClassifier: NLClassifier

    @Inject
    lateinit var databaseRepository: DatabaseRepository
    private val adapter = ResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btPredict.setOnClickListener {
            classify(binding.etText.text.toString())
        }

        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            true
        }

        observeList()

        binding.recyclerView.adapter = adapter

        binding.btClear.setOnClickListener {
            lifecycleScope.launch {
                databaseRepository.clearDatabase()
            }
        }
    }

    private fun classify(text: String) {
        binding.etText.text?.clear()
        lifecycleScope.launch {
            val results = withContext(Dispatchers.IO) {
                textClassifier.classify(text)
            }
            val newPredictionResult = PredictionResult(
                source = PredictionResult.PredictionResultSource.INPUT_TEXT,
                message = text,
                title = null,
                positiveScore = results[1].score,
                negativeScore = results[0].score
            )
            databaseRepository.insertPredictionResult(newPredictionResult)
        }
    }

    private fun observeList() {

        databaseRepository.getAll().observe(this) { results ->
            adapter.submitList(results)
            binding.tvItems.text = getString(R.string.items_value, results.size)
            binding.btClear.isEnabled = results.isNotEmpty()
        }
    }

    class ResultAdapter :
        ListAdapter<PredictionResult, ResultAdapter.ResultViewHolder>(ResultDiffUtil()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
            val binding =
                ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ResultViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class ResultViewHolder(private val binding: ItemResultBinding) : ViewHolder(binding.root) {
            fun bind(result: PredictionResult) {
                binding.tvResult.text = result.message
                binding.tvPositiveConfidence.text = itemView.context.getString(
                    R.string.positive_confidence_value,
                    result.positiveScore * 100
                )
                binding.tvNegativeConfidence.text = itemView.context.getString(
                    R.string.negative_confidence_value,
                    result.negativeScore * 100
                )
            }
        }

        class ResultDiffUtil : DiffUtil.ItemCallback<PredictionResult>() {
            override fun areItemsTheSame(
                oldItem: PredictionResult,
                newItem: PredictionResult
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PredictionResult,
                newItem: PredictionResult
            ): Boolean {
                return oldItem.message == newItem.message &&
                        oldItem.positiveScore == newItem.positiveScore &&
                        oldItem.negativeScore == newItem.negativeScore
            }
        }
    }
}