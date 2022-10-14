package com.chuckerteam.chucker.internal.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.databinding.ChuckerActivityEditResponseBinding
import com.chuckerteam.chucker.internal.data.cache.MockApiPackage
import com.chuckerteam.chucker.internal.data.cache.mockApiCache
import com.chuckerteam.chucker.internal.ui.transaction.TransactionViewModel
import com.chuckerteam.chucker.internal.ui.transaction.TransactionViewModelFactory
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal class ChuckerEditResponseActivity : AppCompatActivity() {

    private lateinit var binding: ChuckerActivityEditResponseBinding
    private val viewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory(intent.getLongExtra(TRANSACTION_ID, 0))
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ChuckerActivityEditResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.transaction.observe(this, Observer { transaction ->
            binding.editResponseCode.setText(transaction?.responseCode.toString())
            binding.editResponseBody.setText(transaction?.responseBody ?: "no body found")

            val shortPath = getPathWithoutQueryParams(transaction?.path ?: "")

            binding.titleEditing.text =
                "Editing future responses for requests beginning with path ${shortPath}"

            binding.makeMockResponse.setOnClickListener {
                val currentJson = binding.editResponseBody.text.toString()
                val currentResponseCode = binding.editResponseCode.text.toString().toInt()
                if (isJSONValid(currentJson)) {
                    mockApiCache.put(shortPath, MockApiPackage(currentResponseCode, currentJson))
                    finish()
                } else {
                    Snackbar.make(binding.root, R.string.chucker_json_is_not_valid, LENGTH_SHORT)
                        .show()
                }
            }

            binding.removeMockResponse.setOnClickListener {
                mockApiCache.remove(shortPath)
                finish()
            }
        })
    }

    private fun getPathWithoutQueryParams(path: String) = path.split("?")[0]

    private fun isJSONValid(test: String): Boolean {
        try {
            JSONObject(test)
        } catch (ex: JSONException) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                JSONArray(test)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }

    companion object {
        private const val TRANSACTION_ID = "transaction_id"

        @JvmStatic
        fun start(
            context: Context,
            transactionId: Long,
        ) {
            val intent = Intent(context, ChuckerEditResponseActivity::class.java)
            intent.putExtra(TRANSACTION_ID, transactionId)
            context.startActivity(intent)
        }
    }
}