package com.kuzheevadel.vmplayerv2.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import kotlinx.android.synthetic.main.radio_category_layout.view.*

class RadioFragment: Fragment() {

    private lateinit var fm: FragmentManager
    private var state = RadioState.POPULAR
    private lateinit var popularRadioFragment: PopularRadioFragment
    private lateinit var searchRadioFragment: SearchRadioFragment
    private lateinit var favoriteRadioFragment: FavoriteRadioFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popularRadioFragment = PopularRadioFragment()
        searchRadioFragment = SearchRadioFragment()
        favoriteRadioFragment = FavoriteRadioFragment()
        fm = childFragmentManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.radio_category_layout, container, false)
        setCurrentFragment(view)

        view.category_button.setOnClickListener {
            val popupMenu = PopupMenu(context!!, it)
            popupMenu.setOnMenuItemClickListener {item ->
                when (item.itemId) {
                    R.id.popular_stations -> {

                        if (state != RadioState.POPULAR) {
                            view.category_textview.text = getText(R.string.popular_radio_stations)
                            state = RadioState.POPULAR

                            fm.beginTransaction()
                                .replace(R.id.radio_fragments_container, popularRadioFragment)
                                .commit()
                        }

                        return@setOnMenuItemClickListener true
                    }

                    R.id.search_stations -> {
                        if (state != RadioState.SEARCH) {
                            view.category_textview.text = getText(R.string.search_stations)
                            state = RadioState.SEARCH

                            fm.beginTransaction()
                                .replace(R.id.radio_fragments_container, searchRadioFragment)
                                .commit()
                        }

                        return@setOnMenuItemClickListener true
                    }

                    R.id.favorite_stations -> {
                        view.category_textview.text = getText(R.string.favorite_stations)
                        state = RadioState.FAVORITE
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }
            }

            popupMenu.inflate(R.menu.menu_main)
            popupMenu.setOnDismissListener{
                view.category_button.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
            }

            try {
                val fieldPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopup.isAccessible = true
                val mPopup = fieldPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Menu icons", "Error showing menu icons", e )
            } finally {
                popupMenu.show()
                view.category_button.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
            }


        }

        return view
    }

    private fun setCurrentFragment(view: View) {
        when (state) {
            RadioState.POPULAR -> {
                view.category_textview.text = getText(R.string.popular_radio_stations)

                fm.beginTransaction()
                    .replace(R.id.radio_fragments_container, popularRadioFragment)
                    .commit()
            }

            RadioState.SEARCH -> {
                view.category_textview.text = getText(R.string.search_stations)

                fm.beginTransaction()
                    .replace(R.id.radio_fragments_container, searchRadioFragment)
                    .commit()
            }

            RadioState.FAVORITE -> {
                view.category_textview.text = getText(R.string.favorite_stations)
            }
        }
    }
}

enum class RadioState {
    POPULAR, SEARCH, FAVORITE
}