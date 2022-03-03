package app.dealux.docstextinputlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import app.dealux.docstextinputlibrary.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
    View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listener()
    }

    private fun listener() {
        binding.edCpf.setOnClickListener(this)
        binding.btn.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding.btn.id -> {
                binding.edCpf.verifyCpf()
                binding.edPis.verifyPis()
            }
        }
    }
}