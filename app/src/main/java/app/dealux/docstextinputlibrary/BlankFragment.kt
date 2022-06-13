package app.dealux.docstextinputlibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.dealux.docs_txtinputs_library.utils.DatePickerFragment
import app.dealux.docstextinputlibrary.databinding.FragmentBlankBinding

class BlankFragment : Fragment(),
    View.OnClickListener {

    private lateinit var binding: FragmentBlankBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBlankBinding.inflate(layoutInflater)
        binding.btnBirth.fragmentActivity = requireActivity()

        listener()

        return binding.root
    }

    private fun listener() {
        binding.btn.setOnClickListener(this@BlankFragment)
        binding.btnBirth.setOnClickListener(this@BlankFragment)
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding.btn.id -> {
                verify()
            }
        }
    }

    private fun verify() {
        binding.edCpf.verifyCpf()
        binding.edPis.verifyPis()
        binding.btnBirth.verifyBirthDay()
        binding.edUf.verifyUF()
        binding.edName.verify()

        if (!binding.edCpf.isValid) {
            Toast.makeText(requireContext(), "Digite um CPF Válido", Toast.LENGTH_SHORT).show()
        } else if (!binding.edPis.isValid) {
            Toast.makeText(requireContext(), "Digite um PIS Válido", Toast.LENGTH_SHORT).show()
        } else if (!binding.btnBirth.isValid) {
            Toast.makeText(requireContext(), "Digite uma Data Válida", Toast.LENGTH_SHORT).show()
        } else if (!binding.edUf.isValid) {
            Toast.makeText(requireContext(), "Digite uma naturalidade Válida", Toast.LENGTH_SHORT).show()
        } else if (!binding.edName.isValid) {
            Toast.makeText(requireContext(), "Digite um nome Válido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Opa são validos", Toast.LENGTH_SHORT).show()
        }
    }

}