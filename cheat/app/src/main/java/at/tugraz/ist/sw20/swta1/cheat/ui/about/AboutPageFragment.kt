package at.tugraz.ist.sw20.swta1.cheat.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.tugraz.ist.sw20.swta1.cheat.R

class AboutPageFragment : Fragment() {

    companion object {
        fun newInstance() = AboutPageFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.about_page_fragment, container, false)
        return root
    }

}