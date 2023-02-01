package com.github.bkmbigo.textclassification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.bkmbigo.textclassification.databinding.ActivityMainBinding
import com.github.bkmbigo.textclassification.databinding.ItemResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var textClassifier: NLClassifier
    private val resultList = mutableListOf<Result>()
    private val adapter = ResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textClassifier = NLClassifier.createFromFile(this, "sentiment_analysis.tflite")


        binding.btPredict.setOnClickListener {
            classify(binding.etText.text.toString())
        }
        binding.recyclerView.adapter = adapter
    }

    private fun classify(text: String) {
        lifecycleScope.launch {
            val results = withContext(Dispatchers.IO) {
                textClassifier.classify(text)
            }
            val result = Result(
                text = text,
                positiveConfidence = results[1].score,
                negativeConfidence = results[0].score
            )
            showResult(result)
        }
    }

    private fun showResult(result: Result) {
        binding.etText.text?.clear()
        resultList.add(result)
        adapter.submitList(resultList)

        // The following code is unimportant as it notifies the list to update automatically
        try {
            adapter.notifyItemInserted(resultList.size - 1)
        } catch (_: Exception){}

    }

    data class Result(
        val text: String,
        val positiveConfidence: Float,
        val negativeConfidence: Float
    )

    class ResultAdapter : ListAdapter<Result, ResultAdapter.ResultViewHolder>(ResultDiffUtil()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
            val binding =
                ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ResultViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class ResultViewHolder(private val binding: ItemResultBinding) : ViewHolder(binding.root) {
            fun bind(result: Result) {
                binding.tvResult.text = result.text
                binding.tvPositiveConfidence.text = itemView.context.getString(
                    R.string.value_positive_confidence,
                    result.positiveConfidence * 100
                )
                binding.tvNegativeConfidence.text = itemView.context.getString(
                    R.string.value_negative_confidence,
                    result.negativeConfidence * 100
                )
            }
        }

        class ResultDiffUtil : DiffUtil.ItemCallback<Result>() {
            override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
                return oldItem.text == newItem.text &&
                        oldItem.positiveConfidence == newItem.positiveConfidence &&
                        oldItem.negativeConfidence == newItem.negativeConfidence
            }
        }
    }
}